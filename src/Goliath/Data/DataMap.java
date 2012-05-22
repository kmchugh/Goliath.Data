package Goliath.Data;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.DynamicCode.Java.MethodDefinition;
import Goliath.Interfaces.Data.ISimpleDataObject;

/**
 * The DataMap is a mapping of data objects to data source to allow dynamic
 * retrieval and caching of objects
 *
 * @version     1.0 17-Jun-2008
 * @author      kmchugh
**/
public class DataMap
{
    // TODO: We want to extend this to allow partitioning of tables (multiple maps per class)

    private String m_cPrefix = "";
    private String m_cDefaultConnection = null;
    private boolean m_lUsesPrefix = true;
    private HashTable<String, DataMapItem> m_oMapItems = null;
    private List<DataMapItem> m_oOrderedItems = null;
    private int m_nMaxReturnedItems = 50;


    /**
     * Gets the prefix that has been set for this data map
     * @return the prefix to be used for all data items
     */
    public String getPrefix()
    {
        return m_cPrefix;
    }


    /**
     * Sets the prefix for this data map
     * @param tcPrefix the new prefix
     */
    public void setPrefix(String tcPrefix)
    {
        m_cPrefix = tcPrefix;
    }

    /**
     * Gets if this data map uses a prefix for its data sources
     * @return true if it does
     */
    public boolean getUsePrefix()
    {
        return m_lUsesPrefix;
    }

    /**
     * Sets if this data map should use a prefix for data sources
     * @param tlValue true if the data map should
     */
    public void setUsePrefix(boolean tlValue)
    {
        m_lUsesPrefix = tlValue;
    }

    /**
     * Gets the default connection name, this is the connection to use for any
     * Data mapped items that do not have a connection specified
     * @return the default name of the connection
     */
    public String getDefaultConnection()
    {
        return m_cDefaultConnection;
    }

    /**
     * Sets the default connection that any data mapped items which do not
     * have a connection specifically set will use
     * @param tcConnectionName the name of the new connection to use by default
     */
    public void setDefaultConnection(String tcConnectionName)
    {
        m_cDefaultConnection = tcConnectionName;
    }

    /**
     * Checks if the data map contains a map for the specified class
     * @param toClass the class to check for
     * @return true if the map already contains this class
     */
    public  <T extends ISimpleDataObject> boolean hasMapItem(Class<T> toClass)
    {
        return m_oMapItems != null && m_oMapItems.containsKey(toClass.getName());
    }

    /**
     * Gets the list of data map items in this map
     * @return the list of data map items or an empty list if there was none
     */
    public List<DataMapItem> getDataItems()
    {
        if (m_oOrderedItems == null)
        {
            m_oOrderedItems = createOrderedList();
        }
        return m_oOrderedItems;
    }
    
    /**
     * Sets the default number of items that will be retrieved in a query
     * @param tnValue the new number of items to be returned by default
     */
    public void setMaxQuerySize(int tnValue)
    {
        if (tnValue > 0)
        {
            m_nMaxReturnedItems = tnValue;
        }
    }

    /**
     * Gets the number of items that should be retrieved by default
     * @return the number of items
     */
    public int getDefaultMaxQuerySize()
    {
        return m_nMaxReturnedItems;
    }


    /*
     * Creates the ordered list of DataMapItems.  The ordering is based on dependencies
     */
    private List<DataMapItem> createOrderedList()
    {
        List<DataMapItem> loOrderedItems = new List<DataMapItem>((m_oMapItems != null) ? m_oMapItems.size() : 0);
        if (m_oMapItems != null)
        {
            List<DataMapItem> loItems = new List<DataMapItem>(m_oMapItems.values());
            
            // Loop through all of the classes and insert them into their correct position in the list
            for (DataMapItem loItem : loItems)
            {
                insertClass(loItem, loOrderedItems, new HashTable<String, Class>(m_oMapItems.size()), new List<DataMapItem>());
            }
        }
        return loOrderedItems;
    }

    // TODO: This function could be refactored into the Goliath.Collections class, as a sorter object
    /*
     * Helper function for the ordering of the list.
     */
    private void insertClass(DataMapItem toItem, List<DataMapItem> toOrderedList, HashTable<String, Class> toClassList, List<DataMapItem>toCurrentTree)
    {
        // If this class is already in the ordered list, then don't process again
        if (toOrderedList.contains(toItem))
        {
            return;
        }

        // If this class is in the class map, then add it now or we will get into circular references
        if (toCurrentTree.contains(toItem))
        {
            toOrderedList.add(toItem);
            return;
        }

        // Add the current item to the map
        toCurrentTree.add(toItem);


        // Get the class that we are working with
        if (!toClassList.containsKey(toItem.getSupportedClassName()))
        {
            toClassList.put(toItem.getSupportedClassName(), Goliath.DynamicCode.Java.getClass(toItem.getSupportedClassName()));
        }
        Class loClass = toClassList.get(toItem.getSupportedClassName());

        // Get all of the methods from the class that have a foreign key annotation
        List<MethodDefinition> loMethods = Goliath.DynamicCode.Java.getMethodDefinitions(loClass, Goliath.Annotations.ForeignKey.class);

        // If there are any methods, then this class has a dependency, so we need to resolve that first
        // If there are no methdods, then we can just add this class to the list
        if (loMethods.size() > 0)
        {
            // Go through all of the methods and find the dependencies
            for(MethodDefinition loMethod:loMethods)
            {
                Goliath.Annotations.ForeignKey loKey = loMethod.getAnnotation(Goliath.Annotations.ForeignKey.class);

                // We only insert dependencies that are not the current class, so if the class depends on itself
                // there is no point in going in to the dependency list.
                if (!loKey.className().getName().equalsIgnoreCase(toItem.getSupportedClassName()))
                {
                    insertClass(getMapItem(loKey.className()), toOrderedList, toClassList, toCurrentTree);
                }
            }
        }

        // This is a double locking check.  The item we are currently on may have been
        // a dependencies of one of it's dependencies (circular reference) so may have been
        // added for us already
        if (toOrderedList.contains(toItem))
        {
            return;
        }

        // It is now safe to add the item
        toOrderedList.add(toItem);

        // We can now remove this item from the tree
        toCurrentTree.remove(toItem);
    }


    /**
     * Gets the associated DataMapItem for the specified class
     * @param <T> must implement ISimpleDataObject
     * @param toClass The class to get the data object for
     * @return the DataMapItem, or null if it doesn't exist
     */
    @Goliath.Annotations.NotProperty
    public <T extends ISimpleDataObject> DataMapItem getMapItem(Class<T> toClass)
    {
        return getMapItem(toClass.getName());
    }

    /**
     * Gets the DataMapItem associated with the specified class
     * @param tcClassName the name of the class to get the DataMapItem for
     * @return the DataMapItem or null
     */
    @Goliath.Annotations.NotProperty
    public DataMapItem getMapItem(String tcClassName)
    {
        return (m_oMapItems != null) ? m_oMapItems.get(tcClassName) : null;
    }
    

    /**
     * Adds the class in to the data map and returns the DataMapItem that was added.
     * If the class is already in the data map then a new DataMapItem is not created, but
     * the DataMapItem that already exists is returned.
     * @param <T> must extend ISimpleDataObject
     * @param toClass the class to add
     * @return the DataMapItem that has been added, or that already existed
     */
    public <T extends ISimpleDataObject> DataMapItem addMapItem(Class<T> toClass)
    {
        if(!hasMapItem(toClass))
        {
            if (m_oMapItems == null)
            {
                m_oMapItems = new HashTable<String, DataMapItem>();
            }
            // Create the new data map item
            DataMapItem loItem = new DataMapItem(this, toClass);
            m_oOrderedItems = null;
            m_oMapItems.put(toClass.getName(), loItem);
        }
        return m_oMapItems.get(toClass.getName());
    }
    
    /**
     * Adds the class specified to the dat map and returns the DataMapItem that was added.
     * If the class is already in the data map then a new DataMapItem is not created, the existing item
     * is adjusted and returned
     * @param <T> must extend ISimpleDataObject
     * @param toClass the class to add
     * @param tlUsePrefix true to use th prefix, false to ignore the prefix, null to take the default action
     * @param tcPrefix the prefix to use for this class, null for the default prefix
     * @param tcDefaultConnectionName the name of the connection to use for this item, the connection name should match a name that 
     * exists in the list of connections
     * @return the DataMapItem that has been added, or that already existed
     */
    public <T extends ISimpleDataObject> DataMapItem addMapItem(Class<T> toClass, String tcName, Boolean tlUsePrefix, String tcPrefix, String tcDefaultConnectionName, Integer tnMaxResults)
    {
        // This will adjust the ordering, so clear out the current order
        m_oOrderedItems = null;
        if(!hasMapItem(toClass))
        {
            if (m_oMapItems == null)
            {
                m_oMapItems = new HashTable<String, DataMapItem>();
            }
            // Create the new data map item
            DataMapItem loItem = new DataMapItem(this, toClass, tcName, tcDefaultConnectionName, tcPrefix, tlUsePrefix, tnMaxResults);
            m_oMapItems.put(toClass.getName(), loItem);
        }
        return m_oMapItems.get(toClass.getName());
    }






}
