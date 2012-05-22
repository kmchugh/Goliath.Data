 /* =========================================================
 * SimpleDataObjectCollection.java
 *
 * Author:      kmchugh
 * Created:     11-Feb-2008, 09:33:22
 * 
 * Description
 * --------------------------------------------------------
 * Simple list of data objects
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Collections;

import Goliath.Constants.StringFormatType;
import Goliath.Data.DataManager;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.JoinQuery;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Collections.IRefreshable;
import Goliath.Interfaces.Collections.ISimpleDataObjectCollection;
import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Simple list of data objects 
 *
 * @param <T>   The type of objects allowed in the data object
 * @see         Related Class
 * @version     1.0 11-Feb-2008
 * @author      kmchugh
 **/
public class SimpleDataObjectCollection<T extends Goliath.Data.DataObjects.SimpleDataObject<T>>
        extends Goliath.Object
        implements Goliath.Interfaces.Collections.ISimpleDataObjectCollection<T>, IRefreshable
{

    private IDataLayerAdapter m_oAdapter;

    // This is the object used to hold the datasource representation of objects before load
    private List m_oResultCache;

    // Number of records from record set to load into collection
    private int m_nMaxFill;
    private boolean m_lLoaded;
    
    // Search Parameters for refreshing the list if needed
    private Class<T> m_oClass;
    private DataQuery m_oListQuery;

    // A sorted and/or filtered list
    private List<Integer> m_oOrderedIndex;
    
    // Helper list to know which field has been sorted and to which direction
    private HashTable<String, OrderDirection> m_oSortedField;

    /** Creates a new instance of SimpleDataObjectCollection */
    public SimpleDataObjectCollection(Class<T> toClass)
    {
        m_nMaxFill = 1000;
        m_oClass = toClass;
    }

    /**
     * Gets the class that this collection contains
     * @return the type that is contained by this list
     */
    @Override
    public Class getContainedClass()
    {
        return m_oClass;
    }

    /**
     * Gets the entire list of items of type class from the data source.
     * The list is limited by the default number of items that are allowed to be returned in a query for this type
     * @throws DataException if there is problem with retrieval
     */
    @Override
    public void loadList() 
            throws DataException
    {
        loadList(DataManager.getInstance().getDataMap().getMapItem(getContainedClass()).getDefaultQuerySize());
    }

    /**
     * Gets the entire list of items of type class from the data source.
     * The list is limited by the tnMaxItems
     * @param tnMaxItems the maximum number of items to get
     * @throws DataException if there is problem with retrieval
     */
    @Override
    public void loadList(int tnMaxItems) 
            throws DataException
    {
        // Ensure there is no data in the class before attempting to load the list
        loadList(null, tnMaxItems);
    }

    /**
     * Populates the list of data objects based on the data query that is supplied.
     * This list will be limited to the default maximum items
     * @param toQuery the query that we are running
     * @throws DataException if there are any issues with the query or data retrieval
     */
    @Override
    public void loadList(DataQuery toQuery)
            throws DataException
    {
        loadList(toQuery, DataManager.getInstance().getDataMap().getMapItem(getContainedClass()).getDefaultQuerySize());
    }

    /**
     * Populates the list of data objects based on the data query that is supplied.
     * The list is limited by the tnMaxItems
     * @param toQuery the query that we are running
     * @param tnMaxItems the maximum number of items to get
     * @throws DataException if there is problem with retrieval
     */
    @Override
    public void loadList(DataQuery toQuery, int tnMaxItems)
            throws DataException
    {
        clear();
        m_oListQuery = toQuery;
        m_nMaxFill = tnMaxItems;
        m_oAdapter = DataManager.getInstance().getDataAdapter(getContainedClass());
        m_oResultCache = m_oAdapter.getList(m_oClass, toQuery, tnMaxItems);
        m_lLoaded = true;
    }
    
     /**
     * Populates the list of data objects based on the data query and joins that are supplied
     * @param toJoins the list of join query objects,
     * generally this should also include the data query objects that are related to the joined classes
     * @param toQuery the query that we are running
     * @throws DataException if there is problem with retrieval
     */
    @Override
    public void loadList(List<JoinQuery> toJoins, DataQuery toQuery) throws DataException
    {
        if (toJoins != null && toJoins.size() > 0)
        {
            if (toJoins.get(0) != null)
            {
                toQuery.setLeftArgument(toJoins.get(0));
            }
            if (toJoins.get(1) != null)
            {
                toQuery.setRightArgument(toJoins.get(1));
            }
        }
        loadList(toQuery, DataManager.getInstance().getDataMap().getMapItem(getContainedClass()).getDefaultQuerySize());
    }

    /**
     * Populates the list of data objects based on the data query and joins that are supplied
     * @param toJoins the list of join query objects,
     * generally this should also include the data query objects that are related to the joined classes
     * @param toQuery the query that we are running
     * @param tnMaxItems tnMaxItems the maximum number of items to get
     * @throws DataException if there is problem with retrieval
     */
    @Override
    public void loadList(List<JoinQuery> toJoins, DataQuery toQuery, int tnMaxItems) throws DataException
    {
        if (toJoins != null && toJoins.size() > 0)
        {
            if (toJoins.get(0) != null)
            {
                toQuery.setLeftArgument(toJoins.get(0));
            }
            if (toJoins.get(1) != null)
            {
                toQuery.setRightArgument(toJoins.get(1));
            }
        }
        loadList(toQuery, tnMaxItems);
    }
    
    /**
     * Checks if this list has been loaded from the data source, if not, then 
     * it means a call to loadList has not been made, or clear has been called
     */
    @Override
    public boolean isLoaded()
    {
        return m_lLoaded;
    }
    
    /**
     * This creates a collection with a copy of all the items that are in this collection
     * @return Returns a copy of the collection with a copy of the objects
     */
    public SimpleDataObjectCollection<T> copy()
    {
        SimpleDataObjectCollection<T> loCollection = new SimpleDataObjectCollection<T>(m_oClass);
        for (int i = 0; i < this.size(); i++)
        {
            loCollection.add(this.get(i));
        }
        return loCollection;
    }

    /**
     * Refreshes the list from the data source, you will lose any changes to the list by doing this
     * @throws DataException if there is an error with getList
     */
    @Override
    public void refresh() throws DataException
    {
        clear();
        loadList(m_oListQuery, m_nMaxFill);
    }

    /**
     * Creates an iterator for this collection
     * This iterator will not include items deleted from the collection
     * @return iterator of this collection
     */
    @Override
    public Iterator<T> iterator()
    {
        return new Goliath.Collections.DataObjectCollectionIterator(this);
    }

    /**
     * Set the maximum number of rows from recordset to load into array at a time
     * for those methods where applicable. E.g. can set before the loadarray
     * @param tnMaxFill The maximum number of rows from recordset to load at a time
     */
    public void setMaxFill(int tnMaxFill)
    {
        if (tnMaxFill > 0)
        {
            m_nMaxFill = tnMaxFill;
        }
    }

    /**
     * Return the maximum number of rows to load into the array at a time
     * @return the number of maximum rows to load into collection array
     */
    public int getMaxFill()
    {
        return m_nMaxFill;
    }

    /**
     * The number of items in this collection
     * @return the size of the collection
     */
    @Override
    public int size()
    {
        return (m_oResultCache == null) ? 0 : m_oResultCache.size();
    }

    /**
     * Checks if the collection is empty
     * @return whether the collection is currently empty
     */
    @Override
    public boolean isEmpty()
    {
        return (m_oResultCache == null || m_oResultCache.size() == 0);
    }

    private List getResultCache()
    {
        if (m_oResultCache == null)
        {
            m_oResultCache = new List();
        }
        return m_oResultCache;
    }

    /**
     * Test whether an Object is in the collection
     * @param toObject the object to check for
     * @return boolean indicating whether the object is in the collection
     */
    @Override
    public boolean contains(java.lang.Object toObject)
    {
        // TODO: Need to implement this correctly so that it will convert objects to check
        return m_oResultCache == null ? false : m_oResultCache.contains(toObject);
    }

    /**
     * Return an array containing all entries in the collection
     * @return Array containing the records in the collection
     */
    @Override
    public Object[] toArray()
    {
        return toArray(new Object[0]);
    }

    /**
     * Returns an array of the runtype of array a
     * @param <K> Type array
     * @param a Array of type T indicating the return type of the returned array
     * @return An array of the same type as the type of array a containing all records in the collection
     */
    @Override
    public <K> K[] toArray(K[] a)
    {
        Object[] loArray = new Object[size()];
        for (int i = 0; i < size(); i++)
        {
            loArray[i] = get(i);
        }
        return (K[]) loArray;
    }

    /**
     * Add an entry to the array
     * @param toObject - the object to be added to the array
     * @return - the array (with new entry added)
     */
    @Override
    public boolean add(T toObject)
    {
        if (contains(toObject))
        {
            // If the item was deleted and we are re adding the item, then we need to undelete
            if (toObject.isDeleted())
            {
                toObject.undelete();
                return true;
            }
        }
        else
        {
            return getResultCache().add(toObject);
        }
        return false;
    }

    /**
     * Deletes all records in the collection
     */
    @Override
    public void delete()
    {
        // need to load up the rest of the objects if we are going to mark all for delete
        for (T loObject : this)
        {
            loObject.delete();
        }
    }

    /**
     * Saves all items in the collection
     * @return true if all the items were saved successfully
     * @throws Goliath.Exceptions.Exception
     * @throws Goliath.Exceptions.DataException
     */
    @Override
    public boolean save() throws Goliath.Exceptions.Exception
    {
        return DataManager.getInstance().saveDataList(this);
    }

    /**
     * Remove the first occurrence of an object from the collection
     * @param toObject Object to be removed from the collection
     * @return true if the collection contained the specific element
     */
    @Override
    public boolean remove(Object toObject)
    {
        return getResultCache().remove(toObject);
    }

    /**
     * Determines if the records in the passed collection are in this collection
     * @param toObjects Collection to check if in this collection
     * @return true if all the passed collection objects exist in this collection
     */
    @Override
    public boolean containsAll(Collection<?> toObjects)
    {
        return getResultCache().containsAll(toObjects);
    }

    /**
     * Add all entries in passed collection to this collection
     * @param toObjects The entries to be added to this collection
     * @return true if this collection changed as a result of the add
     */
    @Override
    public boolean addAll(Collection<? extends T> toObjects)
    {
        return getResultCache().addAll(toObjects);
    }

    /**
     * Add all entries in passed collection starting from position index in this collection
     * @param tnIndex Position from which inserted records begin, pushing subsequent entries back
     * @param toObjects Collection to be added to this collection
     * @return true if this collection has changed as a result of the add
     */
    @Override
    public boolean addAll(int tnIndex, Collection<? extends T> toObjects)
    {
        return getResultCache().addAll(tnIndex, toObjects);
    }

    /**
     * Remove all entries in the passed collection from this collection
     * @param toCollection Collection containing records to be removed
     * @return true if this collection has changed as a result of the remove
     */
    @Override
    public boolean removeAll(Collection<?> toCollection)
    {
        return getResultCache().removeAll(toCollection);
    }

    /**
     * Remove all records in this collection NOT contained in the passed collection
     * @param toCollection Collection of records to be retained from this collection
     * @return true if this collection has changed as a result of the retain
     */
    @Override
    public boolean retainAll(Collection<?> toCollection)
    {
        return getResultCache().removeAll(toCollection);
    }

    /**
     * Clear the collection, any changes to the collection are lost
     */
    @Override
    public void clear()
    {
        m_oResultCache = null;
        m_lLoaded = false;
    }

    /**
     * Get an item from the collection at passed position 
     * This index includes deleted items
     * @param tnIndex Position for returning item from collection
     * @return Returned item from collection
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T get(int tnIndex)
    {
        if (tnIndex < 0 || tnIndex >= size())
        {
            throw new IndexOutOfBoundsException();
        }


        synchronized (getResultCache())
        {

            Object loObject = m_oResultCache.get(getOrderedIndex().size() > 0 ? m_oOrderedIndex.get(tnIndex) : tnIndex);

            // Decide if the object needs to be converted or not
            if (m_oClass.isAssignableFrom(loObject.getClass()))
            {
                return (T) loObject;
            }

            // TODO: Delete the DataMapper and IDataMapper class and interface

            // Need to convert it here
            loObject = m_oAdapter.convertObject(m_oClass, loObject);
            ((T) loObject).markOld();

            m_oResultCache.remove(tnIndex);
            m_oResultCache.add(tnIndex, (T) loObject);

            return (T) loObject;
        }
    }

    /**
     * replaces the entry in this collection at passed position with passed element
     * @param tnIndex Position to update entry in collection
     * @param toObject New element to be updated into passed position
     * @return The element previously at this position
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T set(int tnIndex, T toObject)
    {
        T loObject = get(tnIndex);

        synchronized (getResultCache())
        {
            m_oResultCache.remove(tnIndex);
            m_oResultCache.add(tnIndex, loObject);
        }

        return loObject;
    }

    /**
     * Add an element to collection at position passed - rest of entries pushed back
     * @param tnIndex Index position to add new entry
     * @param toObject Element to add to the collection
     * @throws IndexOutOfBoundsException
     */
    @Override
    public void add(int tnIndex, T toObject)
    {
        getResultCache().add(tnIndex, toObject);
    }

    /**
     * Remove the entry in this collection at passed position
     * @param tnIndex Position of entry to remove
     * @return The entry that has been removed
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T remove(int tnIndex)
    {
        T loObject = get(tnIndex);
        getResultCache().remove(tnIndex);
        return loObject;
    }

    /**
     * Return the first index position in collection of the passed object
     * @param toObject Object to be checked in collection
     * @return Position found in collection of first occurrence, -1 if not found
     */
    @Override
    public int indexOf(Object toObject)
    {
        return getResultCache().indexOf(toObject);
    }

    /**
     * Return the last index position in collection of the passed object
     * @param toObject Object to be checked in collection
     * @return Position found in collection of last occurrence, -1 if not found
     */
    @Override
    public int lastIndexOf(Object toObject)
    {
        return getResultCache().lastIndexOf(toObject);
    }

    /**
     * Gets a list iterator over the elements in this list, this will include the deleted items
     * @return a list iterator
     */
    @Override
    public ListIterator<T> listIterator()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a list iterator over the elements in this list, starting at the specified index.
     * This iterator will include deleted items
     * @param tnIndex the index to start at
     * @return a list iterator
     * @throws IndexOutOfBoundsException
     */
    @Override
    public ListIterator<T> listIterator(int tnIndex)
    {
        throw new UnsupportedOperationException();
        /*
        getRest();
        return getLoadedItems().listIterator(tnIndex);
         * */
    }

    /**
     * Returns a sublist of this collection
     * @param tnFromIndex From index for the sublist
     * @param tnToIndex To index for the sublist
     * @return A list view of the collection within from and to index
     */
    @Override
    public List<T> subList(int tnFromIndex, int tnToIndex)
    {
        throw new UnsupportedOperationException();
        /*
        getRest();
        return new List<T>(getLoadedItems().subList(tnFromIndex, tnToIndex));
         * */
    }

    /**
     * Checks if the collection is dirty
     * @return true if the collection is dirty
     */
    @Override
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

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return this.getClass().getName() + "[" + this.size() + "]";
    }

    /**
     * Gets the list of index which has been sorted and/or filtered
     * @return  List of indexes
     */
    private List<Integer> getOrderedIndex()
    {
        if (m_oOrderedIndex == null)
        {
            m_oOrderedIndex = new List<Integer>();
        }
        return m_oOrderedIndex;
    }

    /**
     * A method to set the list of index as a filter
     * @param toOrderedIndexList    List of index as a filter
     */
    public void setOrderedIndex(List<Integer> toOrderedIndexList)
    {
        if (toOrderedIndexList != null)
        {
            m_oOrderedIndex = toOrderedIndexList;
            m_oSortedField = null;
        }
    }

    /**
     * A method to clear the index. The iteration sequence will then be
     * as when it was first queried from database.
     */
    public void clearOrderedIndex()
    {
        m_oOrderedIndex = null;
        getOrderedIndex();
    }

    /**
     * To find out whether a member is filtered out
     * @param tnIndex   Index of the member to check
     * @return      True if it's filtered out, False if not.
     */
    // TODO: Check where this is being used
    public boolean isFilteredOut(int tnIndex)
    {
        if (getOrderedIndex().size() == 0 || (getOrderedIndex().size() > 0 && getOrderedIndex().contains(tnIndex)))
        {
            return false;
        }
        return true;
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
    public void sort(String tcPropertyName)
    {
        sort(tcPropertyName, OrderDirection.ASCENDING());
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
        clearOrderedIndex();

        for (T loObject : loObjectList)
        {
            m_oOrderedIndex.add(indexOf(loObject));
        }
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
     * @param tcPropertyName     The name of the member's property
     * @param toSortDirection    The direction of the sort
     */
    public void sort(String tcPropertyName, OrderDirection toSortDirection)
    {
        boolean llDoSort = false;

        if (!getSortedField().containsKey(tcPropertyName))
        {
            llDoSort = true;
        }
        else if (getSortedField().get(tcPropertyName) != toSortDirection)
        {
            llDoSort = true;
            m_oSortedField.remove(tcPropertyName);
        }

        if (llDoSort)
        {
            List loValueList = new List();
            List<T> loNullValueList = new List<T>();

            HashTable<Object, List<T>> loValueObjectList = new HashTable<Object, List<T>>();

            // Create a list consisting the value of the parameter from all object
            for (T loObject : this)
            {
                Object loPropertyValue = Java.getPropertyValue(loObject, tcPropertyName);

                // HashTable will not accept null as the key
                if (loPropertyValue == null)
                {
                    loNullValueList.add(loObject);
                    continue;
                }

                if (loValueObjectList.containsKey(loPropertyValue))
                {
                    if (!loValueObjectList.get(loPropertyValue).contains(loObject))
                    {
                        loValueObjectList.get(loPropertyValue).add(loObject);
                    }
                }
                else
                {
                    loValueList.add(loPropertyValue);
                    List<T> loObjectList = new List<T>();
                    loObjectList.add(loObject);
                    loValueObjectList.put(loPropertyValue, loObjectList);
                }
            }

            // Use default sort from Java
            if (toSortDirection == OrderDirection.ASCENDING())
            {
                Collections.sort(loValueList);
            }
            else
            {
                Collections.sort(loValueList, Collections.reverseOrder());
            }

            // TODO: Handles when the property is non primitive Java object
            // Should we use ComparatorFactory?

            if (getOrderedIndex().size() > 0)
            {
                clearOrderedIndex();
            }

            // Create the sorted index based on the property value
            if (toSortDirection == OrderDirection.ASCENDING())
            {
                for (T loObject : loNullValueList)
                {
                    m_oOrderedIndex.add(indexOf(loObject));
                }
            }
            for (Object loValue : loValueList)
            {
                for (T loObject : loValueObjectList.get(loValue))
                {
                    m_oOrderedIndex.add(indexOf(loObject));
                }
            }
            if (toSortDirection == OrderDirection.DESCENDING())
            {
                for (T loObject : loNullValueList)
                {
                    m_oOrderedIndex.add(indexOf(loObject));
                }
            }

            m_oSortedField.put(tcPropertyName, toSortDirection);
        }
    }

    @Override
    public ISimpleDataObjectCollection<T> subList(DataQuery toQuery)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
