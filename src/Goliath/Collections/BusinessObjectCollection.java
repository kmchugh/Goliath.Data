/* ========================================================
 * BusinessObjectCollection.java
 *
 * Author:      kmchugh
 * Created:     Aug 1, 2010, 1:46:47 PM
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */
package Goliath.Collections;

import Goliath.Applications.Application;
import Goliath.Constants.StringFormatType;
import Goliath.Data.BusinessObjects.BusinessObject;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Data.DataObjects.TypeComposition;
import Goliath.Data.DataObjects.ValueList;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.InList;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.DataException;
import Goliath.Exceptions.ObjectNotCreatedException;
import Goliath.Interfaces.Collections.IList;
import Goliath.Interfaces.Collections.IRefreshable;
import Goliath.Interfaces.IContainedClassIdentifiable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Aug 1, 2010
 * @author      kmchugh
 **/
public class BusinessObjectCollection<T extends BusinessObject>
        extends Goliath.Object
        implements IList<T>, IContainedClassIdentifiable, IRefreshable
{

    private SimpleDataObjectCollection m_oBaseList;
    private HashTable<SimpleDataObject, T> m_oCache;
    private boolean m_lLoaded;
    private Class<T> m_oClass;
    private T m_oClassInstance;
    private DataQuery m_oListQuery;
    // A list to know which property has been sorted and to which direction
    private HashTable<String, OrderDirection> m_oSortedField;

    /**
     * Creates a new Business Object Collection that will contain the class specified
     * @param toClass the class this list will contain
     */
    public BusinessObjectCollection(Class<T> toClass)
    {
        m_oClass = toClass;
    }

    /**
     * Gets the type that this collection contains
     * @return the type that is contained by this collection
     */
    @Goliath.Annotations.NotProperty
    @Override
    public final Class<T> getContainedClass()
    {
        return m_oClass;
    }

    /**
     * Checks if the collection is modified
     * @return true if the collection is modified
     */
    public boolean isModified()
    {
        // TODO: Also needs to mark as modified if items have been added or removed from the list
        for (T loObject : this)
        {
            if (loObject.isModified())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function to get an instance of the primary class
     * @return the primary class instance
     */
    protected T getClassInstance()
    {
        if (m_oClassInstance == null)
        {
            try
            {
                m_oClassInstance = m_oClass.newInstance();
            } catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
        return m_oClassInstance;
    }

    @Override
    public void refresh() throws Throwable
    {
        clear();
        loadList(m_oListQuery);
    }

    // TODO: implement load lists with a maxItems like the simple data object collection
    
    /**
     * Loads the list of business objects filtering using the data query
     * if the query is null then no filtering is applied
     * @param toQuery the query 
     * @throws DataException if any errors occur with the retrieval
     */
    public final void loadList(DataQuery toQuery)
            throws DataException
    {
        try
        {
            m_oBaseList = null;
            m_oListQuery = toQuery;
            onLoadList(toQuery);
        } catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            throw new DataException(ex);
        }
    }

    /**
     * Loads the list based on the properties specified
     * @param toObject the object to load
     * @param toProperties the list of properties the object must match to be loaded
     */
    protected void onLoadList(DataQuery toQuery)
    {
        getBaseList().loadList(toQuery);
    }

    /**
     * Gets the entire list of items of type class from the data source
     * The list is limited by the default number of items return in a query
     * @throws DataException if there is a problem with retrieval
     */
    public final void loadList()
            throws DataException
    {
        try
        {
            onLoadList(null);
            m_lLoaded = true;
        } catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            throw new DataException(ex);
        }
    }

    /**
     * Checks if this list has been loaded from the data source, if not, then 
     * it means a call to loadList has not been made, or clear has been called
     */
    public boolean isLoaded()
    {
        return m_lLoaded;
    }

    /**
     * Gets the simple data object collection list that supports this business object list
     * @return the supporting list containing all of the simple data objects
     */
    protected SimpleDataObjectCollection getBaseList()
    {
        if (m_oBaseList == null)
        {
            try
            {
                T loClass = getClassInstance();
                m_oBaseList = new SimpleDataObjectCollection(loClass.getPrimaryClass());
            } catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
        return m_oBaseList;
    }

    /**
     * Gets the cache list, the cache list contains the items that have
     * been fully loaded from the list of simple data objects
     * @return the cache
     */
    private HashTable<SimpleDataObject, T> getCachedList()
    {
        if (m_oCache == null)
        {
            m_oCache = new HashTable<SimpleDataObject, T>();
        }
        return m_oCache;
    }

    /**
     * Helper function to get the simple data object for the specified data object,
     * this can be used to key the cache
     * @param toBusinessObject the business object from this collection to get the simple data object for
     * @return the simple data object for the specified business object
     */
    private SimpleDataObject getSimpleObject(T toBusinessObject)
    {
        return toBusinessObject != null ? toBusinessObject.getPrimaryObject() : null;
    }

    /**
     * Deletes all records in the collection, this just marks the records as deleted,
     * objects are not deleted from the data source until save is called
     */
    public synchronized final void delete()
    {
        // need to load up the rest of the objects if we are going to mark all for delete
        for (T loObject : this)
        {
            loObject.delete();
        }
    }

    /**
     * Adds the specified item to the list, the data object is not saved to the data source
     * until save is called on the collection, or on the data object itself
     * @param toItem the item to add
     * @return true if the collection was changed as a result of this call
     */
    @Override
    public boolean add(T toItem)
    {
        SimpleDataObject loObject = getSimpleObject(toItem);
        boolean llReturn = false;
        if (loObject != null)
        {
            if (!getBaseList().contains(loObject))
            {
                llReturn = m_oBaseList.add(loObject);
                getCachedList().put(loObject, toItem);
            }
        }
        return llReturn;
    }

    /**
     * Adds the object to the specified index in the list
     * @param tnIndex the index to add the item to
     * @param toElement the item to add to the list
     */
    @Override
    public void add(int tnIndex, T toItem)
    {
        SimpleDataObject loObject = getSimpleObject(toItem);
        if (loObject != null)
        {
            getBaseList().add(tnIndex, loObject);
            getCachedList().put(loObject, toItem);
        }
    }

    /**
     * Adds all of the objects in the collection to the list
     * @param toCollection the collection to add
     * @return true if the collection was changed as a result of this call
     */
    @Override
    public boolean addAll(Collection<? extends T> toCollection)
    {
        boolean llReturn = false;
        for (T loObject : toCollection)
        {
            llReturn = add(loObject) || llReturn;
        }
        return llReturn;
    }

    /**
     * Adds all of the objects from the collection, inserting at the specified index
     * @param tnIndex, the index to start adding at
     * @param toCollection the collection to add
     * @return always returns true if there were items in toCollection
     */
    @Override
    public boolean addAll(int tnIndex, Collection<? extends T> toCollection)
    {
        boolean llReturn = false;
        for (T loObject : toCollection)
        {
            add(tnIndex, loObject);
            llReturn = true;
        }
        return llReturn;
    }

    /**
     * Clears all of the items from the list
     */
    @Override
    public void clear()
    {
        if (m_oBaseList != null && m_oBaseList.size() > 0)
        {
            m_oBaseList.clear();
        }

        if (m_oCache != null && m_oCache.size() > 0)
        {
            m_oCache.clear();
        }
        m_lLoaded = false;
    }

    /**
     * Checks if this collection contains the specified item
     * @param the object to check for
     * @return true if the item is contained in this list
     */
    @Override
    public boolean contains(Object toObject)
    {
        return m_oBaseList != null && Java.isEqualOrAssignable(m_oClass, toObject.getClass()) && m_oBaseList.contains(((T) toObject).getPrimaryClass());
    }

    /**
     * Checks if the list contains all of the specified items, if the list is empty this will return true
     * @param toCollection the list of items to check
     * @return true if this list contains all of the items in toCollection
     */
    @Override
    public boolean containsAll(Collection<?> toCollection)
    {
        boolean llReturn = toCollection == this;
        if (!llReturn)
        {
            for (Object loObject : toCollection)
            {
                llReturn = llReturn && contains((T) loObject);
                if (!llReturn)
                {
                    break;
                }
            }
        }
        return llReturn;
    }

    /**
     * Gets the item as the specified index
     * @param tnIndex the index to get the item from
     * @return the item at the index specified
     */
    @Override
    public T get(int tnIndex)
    {
        T loReturn = null;
        if (m_oBaseList != null)
        {
            SimpleDataObject loKey = m_oBaseList.get(tnIndex);
            loReturn = getCachedList().get(loKey);
            if (loReturn == null)
            {
                // If the object is not yet loaded, load it now and cache it
                Constructor<T> loConstructor = Java.getConstructor(m_oClass, new Class[]
                        {
                            loKey.getClass()
                        });
                if (loConstructor != null)
                {
                    try
                    {
                        loReturn = loConstructor.newInstance(loKey);
                        getCachedList().put(loKey, loReturn);
                    } catch (Throwable ex)
                    {
                        Application.getInstance().log(ex);
                    }
                } else
                {
                    throw new ObjectNotCreatedException("Could not create an object of type " + m_oClass.getName() + " because no constructor exists that takes a " + loKey.getClass().getName() + " as an argument");
                }
            }
        }
        return loReturn;
    }

    /**
     * Gets the index of the specified item
     * @param toItem the item to check
     * @return the index of the item specified
     */
    @Override
    public int indexOf(Object toItem)
    {
        return m_oBaseList != null ? m_oBaseList.indexOf(((T) toItem).getPrimaryObject()) : -1;
    }

    /**
     * Checks if this list is empty
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return m_oBaseList == null || m_oBaseList.isEmpty();
    }

    /**
     * Gets the iterator for this list
     * @return the iterator for the business object collection
     */
    @Override
    public Iterator<T> iterator()
    {
        return new BusinessObjectCollectionIterator<T>(this);
    }

    /**
     * Gets the last index of the item specified, as this list can only contain each item
     * one time, this is the same as calling indexOf
     * @param toObject the object of the item to get
     * @return the index of the item specified
     */
    @Override
    public int lastIndexOf(Object toObject)
    {
        return indexOf(toObject);
    }

    /**
     * Gets the iterator for this list
     * @return the iterator for the list
     */
    @Override
    public ListIterator<T> listIterator()
    {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the items specified from the list
     * @param toObject the object to remove
     * @return true if the collection was changed as a result of this call
     */
    @Override
    public boolean remove(Object toObject)
    {
        boolean llReturn = false;
        if (m_oBaseList != null)
        {
            llReturn = m_oBaseList.remove(((T) toObject).getPrimaryObject());
            if (llReturn)
            {
                getCachedList().remove(((T) toObject).getPrimaryObject());
            }
        }
        return llReturn;
    }

    /**
     * Removes the item at the index specified
     * @param tnIndex the index to remove the item from
     * @return the item that has been removed, if it had been fully loaded
     */
    @Override
    public T remove(int tnIndex)
    {
        SimpleDataObject loObject = null;
        if (m_oBaseList != null)
        {
            loObject = getBaseList().remove(tnIndex);
        }
        return loObject != null ? getCachedList().remove(loObject) : null;
    }

    /**
     * Removes all of the items from the collection
     * @param toCollection the list of items to remove
     * @return true if the collection is removed as a result of this call
     */
    @Override
    public boolean removeAll(Collection<?> toCollection)
    {
        boolean llReturn = false;
        if (m_oBaseList != null)
        {
            for (Object loObject : toCollection)
            {
                llReturn = m_oBaseList.remove(((T) loObject).getPrimaryObject()) || llReturn;
                if (llReturn && m_oCache != null)
                {
                    m_oCache.remove(((T) loObject).getPrimaryObject());
                }
            }
        }
        return llReturn;
    }

    /**
     * Keeps only the items in the collection that also exist in toCollection
     * @param toCollection the list of items to use to filter this list
     * @return true if the collection was changed as a result of this call
     */
    @Override
    public boolean retainAll(Collection<?> toCollection)
    {
        boolean llReturn = false;
        if (m_oBaseList != null)
        {
            T[] laItems = null;
            laItems = this.toArray(laItems);

            for (T loItem : laItems)
            {
                if (!toCollection.contains(loItem))
                {
                    llReturn = remove(loItem) || llReturn;
                }
            }
        }
        return llReturn;
    }

    /**
     * Replaces the element at the specified position
     * @param tnIindex the index to replace at
     * @param toItem
     * @return the item that was removed
     */
    @Override
    public T set(int tnIndex, T toElement)
    {
        T loReturn = remove(tnIndex);
        if (loReturn != null)
        {
            add(tnIndex, toElement);
        }
        return loReturn;
    }

    /**
     * Gets the size of this collection
     * @return the collection size
     */
    @Override
    public int size()
    {
        return m_oBaseList != null ? m_oBaseList.size() : 0;
    }

    /**
     * Saves all items in the collection
     * @throws Goliath.Exceptions.Exception
     * @throws Goliath.Exceptions.DataException
     */
    public final void save() throws Goliath.Exceptions.Exception
    {
        // TODO: Implement validation on all the objects before attempting to save

        // Uses index loop because iteration will not return object that is marked as deleted
        for (int i = 0; i < this.size(); i++)
        {
            get(i).save();
        }
    }

    /**
     * Gets the list of items from the specified index to the specified index
     * @param tnFromIndex the starting index
     * @param tnToIndex the ending index
     * @return the list containing the specified items
     */
    @Override
    public List<T> subList(int tnFromIndex, int tnToIndex)
    {
        // TODO: implement this
        throw new UnsupportedOperationException();
    }

    // TODO: Implement subList that takes a dataquery as a parameter
    /**
     * Gets an array from all of the items in this list
     * @return a new array with all of the items in the list
     */
    @Override
    public Object[] toArray()
    {
        Object[] laObjects = new Object[size()];
        int i = 0;
        for (T loObject : this)
        {
            laObjects[i++] = loObject;
        }
        return laObjects;
    }

    /**
     * Gets the array of the paramaterised type containing all of the elements in this list
     * @param <K> 
     * @param taArray the array to use as a template, this array will be filled if it is large enough
     * @return a new array containing all of the items specified
     */
    @Override
    public <K> K[] toArray(K[] taArray)
    {
        Object[] laObjects = taArray != null && taArray.length >= size() ? taArray : new Object[size()];
        int i = 0;
        for (T loObject : this)
        {
            laObjects[i++] = loObject;
        }

        return (K[]) laObjects;
    }

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return this.getClass().getName() + "[" + this.size() + "]";
    }

    /*
    @Goliath.Annotations.NotProperty
    public final ObjectRegistry getPrimaryObject()
    {
    return m_oPrimaryObject;
    }

    @Goliath.Annotations.NotProperty
    public void setAttributeTypeComposition(TypeComposition toAttributeTypeComposition)
    {
    m_oAttributeTypeComposition = toAttributeTypeComposition;
    }

    protected TypeComposition getAttributeTypeComposition()
    {
    return m_oAttributeTypeComposition;
    }
     * 
     */
    /**
     * A method to clear the index. The iteration sequence will then be
     * as when it was first queried from database.
     */
    public void clearOrderedIndex()
    {
        getBaseList().clearOrderedIndex();
    }

    /**
     * To see whether a member is filtered out.
     * @param tnIndex   Index of the object that we are checking.
     * @return          True if the object is filtered out, false if it's not filtered out.
     */
    public boolean isFilteredOut(int tnIndex)
    {
        return getBaseList().isFilteredOut(tnIndex);
    }

    /**
     * A method to sort this list using the given comparator.
     * @param toComparator  The comparator to be used.
     */
    public void sort(Comparator toComparator)
    {
        sort(toComparator, OrderDirection.ASCENDING());
    }

    /**
     * A method to sort this list based on the member's property value
     * @param toTypeComposition     The type composition of the member's property
     */
    public void sort(TypeComposition toTypeComposition)
    {
        sort(toTypeComposition, OrderDirection.ASCENDING());
    }

    /**
     * A method to sort this list using the given comparator. If the direction is OrderDirection.DESCENDING(),
     * the comparator will be reversed.
     * @param toComparator      The comparator to use
     * @param toSortDirection   The direction of sort
     */
    public void sort(Comparator toComparator, OrderDirection toSortDirection)
    {
        List<T> loObjectList = new List<T>();
        for (T loObject : this)
        {
            loObjectList.add(loObject);
        }

        Collections.sort(loObjectList, (toSortDirection == OrderDirection.ASCENDING() ? toComparator : Collections.reverseOrder(toComparator)));

        List<Integer> loOrderedIndexList = new List<Integer>();
        for (T loObject : loObjectList)
        {
            loOrderedIndexList.add(indexOf(loObject));
        }

        getBaseList().setOrderedIndex(loOrderedIndexList);
    }

    /**
     * A helper list to know which field has been sorted
     * @return  A HashTable listing the fields that has been sorted and to which direction
     */
    private HashTable<String, OrderDirection> getSortedField()
    {
        if (m_oSortedField == null)
        {
            m_oSortedField = new HashTable<String, OrderDirection>();
        }
        return m_oSortedField;
    }

    /**
     * A method to sort this list based on the member's property value. If the direction is OrderDirection.DESCENDING(),
     * the comparator used will be reversed.
     * @param toTypeComposition     The type composition of the member's property
     * @param toSortDirection       The direction of the sort
     */
    public void sort(TypeComposition toTypeComposition, OrderDirection toSortDirection)
    {
        boolean llDoSort = false;

        if (!getSortedField().containsKey(toTypeComposition.getGUID()))
        {
            llDoSort = true;
        } else if (getSortedField().get(toTypeComposition.getGUID()) != toSortDirection)
        {
            llDoSort = true;
            m_oSortedField.remove(toTypeComposition.getGUID());
        }

        if (llDoSort)
        {
            List<Long> loObjectRegistryIDList = new List<Long>();
            for (Object loObjectRegistry : getBaseList())
            {
                loObjectRegistryIDList.add(((SimpleDataObject) loObjectRegistry).getID());
            }

            SimpleDataObjectCollection<ValueList> loValueLists = new SimpleDataObjectCollection<ValueList>(ValueList.class);

            DataQuery loMainDataQuery = new DataQuery(
                    new InList("ObjectRegistryID", loObjectRegistryIDList),
                    new InList("TypeCompositionID", new Long[]
                    {
                        toTypeComposition.getID()
                    }));
            DataQuery loOptionalDataQuery = onCreateDataQueryForSort();

            loValueLists.loadList(loOptionalDataQuery != null ? new DataQuery(loMainDataQuery, loOptionalDataQuery) : loMainDataQuery);

            loValueLists.sort(new ValueListValueComparator(), toSortDirection);

            List<Integer> loSortedIndex = new List<Integer>();
            for (ValueList loValueList : loValueLists)
            {
                loSortedIndex.add(loObjectRegistryIDList.indexOf(loValueList.getObjectRegistryID()));
            }
            getBaseList().setOrderedIndex(loSortedIndex);

            m_oSortedField.put(toTypeComposition.getGUID(), toSortDirection);
        }
    }

    /**
     * A hook to allow additional data query, to further filter the value list
     * when querying them to sort by property value.
     * @return  Additional data query
     */
    protected DataQuery onCreateDataQueryForSort()
    {
        return null;
    }
    // TODO: Implement this
    /**
     * Gets a sub set of the current list, that subset will only include
     * items that match the data query specified
     * @param toQuery the data query to use for the filter
     * @return the new list of data objects, this will return an empty list if there are not matches
     */
    //public BusinessObjectCollection<T> subList(DataQuery toQuery);
}
