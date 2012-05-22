/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.CacheType;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Collections.DataObjectCache;
import Goliath.Collections.PropertySet;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Data.Query.InList;
import Goliath.Data.Query.PropertyQuery;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.CriticalException;
import Goliath.Exceptions.PermissionDeniedException;
import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.IConnectionString;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Security.IPermission;
import Goliath.KeyedObjectCache;
import Goliath.Security.AccessType;
import Goliath.Security.User;
import Goliath.Session;

/**
 *
 * @author kmchugh
 */
public class DataManager extends Goliath.Object
{
    // TODO: Reimplement caching by ID and GUID
    
    private static DataManager g_oInstance = null;
    
    /**
     * Singleton DataManager
     * @return the global data manager
     */
    public static DataManager getInstance()
    {
        if (g_oInstance == null)
        {
            g_oInstance = new DataManager();
        }
        return g_oInstance;
    }
    
    private HashTable<String, IConnectionString> m_oConnections;
    private List<IConnectionString> m_oValidConnections;
    private DataMap m_oDataMap;
    private HashTable<IConnectionString, DataBase> m_oDatabases;
    
    /* not publicly creatable*/
    private DataManager()
    {

    }

    /**
     * Gets the list of connections that have been specified
     * @return the full list of connection strings that are available to the system
     */
    private synchronized List<IConnectionString> getConnections()
    {
        if (m_oConnections == null || m_oConnections.size() == 0)
        {
            m_oConnections = m_oConnections == null ? new HashTable<String, IConnectionString>() : m_oConnections;
            List<IConnectionString> loConnections = Application.getInstance().getPropertyHandlerProperty("Application.Data.DataSources", new List<IConnectionString>());
            if (loConnections.size() == 0)
            {
                // Try to create a JDBC by default just to show an example
                IConnectionString loConnString = null;

                try
                {
                    loConnString = (IConnectionString)Java.getClass("Goliath.Data.JDBC.JavaDB.ConnectionString").newInstance();
                }
                catch (Throwable ex)
                {
                    // TODO: Rather than just throw here, try to create other types of connection strings using the object cache to load them.  Only throw if none can be created
                    throw new CriticalException("Could not create a connection string", ex);
                }

                String lcBase = Application.getInstance().getName().replaceAll(" ", "");
                loConnString.setName("default");
                loConnString.setParameter("database", lcBase);
                loConnString.setParameter("user", "A"+ Goliath.Utilities.generateStringGUID().substring(0, 15).replaceAll("-", ""));
                loConnString.setParameter("password", Goliath.Utilities.generateStringGUID().substring(0, 10));
                loConnections.add(loConnString);

                Application.getInstance().setPropertyHandlerProperty("Application.Data.DataSources", loConnections);
            }

            for (IConnectionString loConnString : loConnections)
            {
                try
                {
                    m_oConnections.put(loConnString.getName().toLowerCase(), loConnString);
                }
                catch (Throwable ex)
                {
                    
                }
            }
        }
        return new List<IConnectionString>(m_oConnections.values());
    }


    /**
     * Makes sure the source for this connection exists ,if it does not, it will attempt
     * to create it.
     * @param toConnection
     * @return true if it has been created or exists
     */
    public boolean ensureConnectionSourceExists(IConnectionString toConnection)
    {
        // Check if the source exists if not attempt to create it
        if (m_oValidConnections == null || !m_oValidConnections.contains(toConnection))
        {
            // We need to check this connection as it has not been checked already
            if (m_oValidConnections == null)
            {
                m_oValidConnections = new List<IConnectionString>();
            }
            DataBase loDataBase = getDatabase(toConnection);
            if (!loDataBase.exists())
            {
                loDataBase.create();
            }
            m_oValidConnections.add(toConnection);
        }
        return m_oValidConnections.contains(toConnection);
    }

    /**
     * Makes sure the specified data item exists within it's mapped data source
     * If it does not exist, this will attempt to create it.
     * This will also call ensureConnectionSourceExists to make sure the data source is valid
     * @param toDataItem the data item to make sure exists
     * @throws  CriticalException if either the data source or data item did not exist and could not be created
     * @return true
     */
    public boolean ensureDataObjectExists(DataMapItem toDataItem)
    {
        IConnectionString loConnString = getConnection(toDataItem.getActualConnectionName());
        // Need to make sure the Data source exists first
        if (!ensureConnectionSourceExists(loConnString))
        {
            throw new CriticalException("The Data Source for the connection " + loConnString.toString() + " does not exist and could not be created.");
        }

        try
        {
            Table loTable = new Table((Class<SimpleDataObject>)Class.forName(toDataItem.getSupportedClassName()), loConnString.getDataLayerAdapter());
            if (!loTable.exists())
            {
                loTable.create();
            }
        }
        catch (ClassNotFoundException ex)
        {
            throw new CriticalException("The class " + toDataItem.getSupportedClassName() + " could not be found to map the data source");
        }
        catch (Exception ex)
        {
            throw new CriticalException("The class " + toDataItem.getSupportedClassName() + " could not be created as a data source", ex);
        }

        return true;
    }

    /**
     * Gets the database object for the specified Connection String
     * @param toConnString, the database to get
     * @return the database object
     */
    public DataBase getDatabase(IConnectionString toConnString)
    {
        if (m_oDatabases == null)
        {
            m_oDatabases = new HashTable<IConnectionString, DataBase>(1);
        }

        if (!m_oDatabases.containsKey(toConnString))
        {
            // TODO: toConnString.getParameter("database") should be changed to a getDataSourceName method
            m_oDatabases.put(toConnString, new DataBase(toConnString.getParameter("database").toString(), toConnString.getDataLayerAdapter()));
        }
        return m_oDatabases.get(toConnString);
    }

    /**
     * Gets the specified connection
     * @param tcConnectionName the name of the connection to get
     * @return the connection or null if it did not exist
     */
    public IConnectionString getConnection(String tcConnectionName)
    {
        if (m_oConnections == null || m_oConnections.size() == 0)
        {
            getConnections();
        }
        return m_oConnections.get(tcConnectionName.toLowerCase());
    }

    /**
     * Gets the connection for the specified class
     * @param <T> must implement ISimpleDataObject
     * @param toClass the class to get the mapped connection for
     * @return the mapped connection for this class
     */
    public <T extends ISimpleDataObject> IConnectionString getConnection(Class<T> toClass)
    {
        if (m_oConnections == null || !m_oConnections.containsKey(toClass.getName()))
        {
            // There was no specific connection set up, so we will use the default
            if (m_oConnections == null)
            {
                m_oConnections = new HashTable<String, IConnectionString>();
            }
            m_oConnections.put(toClass.getName(), getConnection("default"));
        }
        return m_oConnections.get(toClass.getName());
    }

    /**
     * Gets the global data map, this will be loaded from the application settings if it is null
     * @return the global data map
     */
    public DataMap getDataMap()
    {
        if (m_oDataMap == null)
        {
            // Each time this is run we want to make sure everything exists.
            m_oDataMap = Application.getInstance().getPropertyHandlerProperty("Application.Data.DataMap", null);

            if (m_oDataMap == null)
            {
                // We need to re create the datamap
                m_oDataMap = new DataMap();
                // By default all of the data objects in the system will be prefixed with "gdo_", for Goliath Data Object
                m_oDataMap.setPrefix("gdo_");
                // Make the default connection the connection that is named default
                m_oDataMap.setDefaultConnection("default");

                saveDataMap();
            }
        }
        return m_oDataMap;
    }

    /**
     * Forces a save of the data map to the application settings
     */
    public void saveDataMap()
    {
        if (m_oDataMap != null)
        {
            Application.getInstance().setPropertyHandlerProperty("Application.Data.DataMap", m_oDataMap);
        }
    }
    
    /**
     * This method saves each data object within the list provided
     * @param toList the list of items to save
     * @return true if everything worked correctly
     */
    public final boolean saveDataList(java.util.List<? extends SimpleDataObject> toList)
    {
        boolean llReturn = true;
        if (toList != null && toList.size() > 0)
        {
            // TODO : Implement transactions on the list save
            
            // Cache deletion of the objects as it will execute faster
            List<SimpleDataObject> loDeletionList = new List<SimpleDataObject>();
            
            // The iterator may not include deleted items in the list, so we are using a for loop instead
            for (int i=0, lnLength = toList.size(); i < lnLength; i++)
            {
                SimpleDataObject loObject = toList.get(i);
                if (loObject.isDeleted())
                {
                    loDeletionList.add(loObject);
                }
                else
                {
                    llReturn = loObject.save() && llReturn;
                } 
            }
            
            // Delete all of the deleted items together
            if (llReturn && loDeletionList.size() > 0)
            {
                llReturn = getDataAdapter((Class<SimpleDataObject>)loDeletionList.get(0).getClass()).delete(loDeletionList.get(0), 
                    new InList<SimpleDataObject, Long>("ID", loDeletionList, "ID")) && llReturn;
             
            }
        }
        return llReturn;
    }

    /**
     * This method saves the data object and returns the updated identity of the object, or
     * returns the original identity of the object if this was an update rather than a create.
     * If this returns a value of less than 1 then it indicates the save did not work
     * @param toDataObject the data object to save
     * @return the identity of the data object after it has been saved, or a value less than 1 if the save failed
     * @throws Goliath.Exceptions.PermissionDeniedException if the user does not have permission to save
     */
    public final boolean saveDataObject(ISimpleDataObject toDataObject)
            throws Goliath.Exceptions.PermissionDeniedException
    {

        // Figure out what we are trying to do first
        AccessType loAction = toDataObject.isDeleted() ?
                                AccessType.DELETE() :
                                    toDataObject.isNew() ? AccessType.CREATE() : AccessType.WRITE();

        // First check if we are even allowed to save
        checkPermission(
                new Goliath.Security.ResourcePermission(
                toDataObject.getClass().getSimpleName(),
                toDataObject.getID(),
                Goliath.Security.ResourceType.TABLEROW(),
                loAction),
                loAction);

        // Remove from the cache on any update
        clearCachedDataObject(toDataObject);

        IDataLayerAdapter loAdapter = getConnection(toDataObject.getClass()).getDataLayerAdapter();
        if (loAction == AccessType.DELETE())
        {
            return loAdapter.delete(toDataObject);
        }
        else if (loAction == AccessType.CREATE())
        {
            return loAdapter.create(toDataObject);
        }
        else if (loAction == AccessType.WRITE())
        {
            return loAdapter.update(toDataObject);
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Gets all of the items of the list type from the data store.  This
     * method sets the data adapter of the list that is passed in to allow the
     * list to populate itself
     * @param <K> the type of the simple data object
     * @param toList the list to set the data adapter for
     * @param tnMaxItems the maximum number of items to query from the data adapter
     * @return the List of items
     */
    public final <K extends SimpleDataObject<K>> IDataLayerAdapter getDataAdapter(Class<K> toClass)
    {
        // Check if the user can access the data object, if not there is no point in giving back the adapter
        checkPermission(
                    new Goliath.Security.ResourcePermission(
                    toClass.getSimpleName(),
                    null,
                    Goliath.Security.ResourceType.TABLE(),
                    AccessType.READ()),
                    AccessType.READ());
        
        return getConnection(toClass).getDataLayerAdapter();
    }

    // TODO: This needs to be changed to use the DataQuery
    public final <K extends SimpleDataObject<K>> SimpleDataObjectCollection<K> getDataItems(Class<K> toClass, PropertySet toProperties)
    {
        IDataLayerAdapter loAdapter = getConnection(toClass).getDataLayerAdapter();
        if (toProperties.hasProperty("GUID") || toProperties.hasProperty("ID") && toProperties.size() == 1)
        {
            K loReturn = getCachedDataObject(toClass, toProperties);
            if (loReturn == null)
            {
                if (toProperties.hasProperty("ID"))
                {
                    long lnID = (Long)(toProperties.getProperty("ID"));
                    checkPermission(
                        new Goliath.Security.ResourcePermission(
                        toClass.getSimpleName(),
                        lnID,
                        Goliath.Security.ResourceType.TABLEROW(),
                        AccessType.READ()),
                        AccessType.READ());

                    loReturn = loAdapter.getByID(toClass, lnID);

                }
                else
                {
                    String lcGUID = (String)(toProperties.getProperty("GUID"));
                    checkPermission(
                        new Goliath.Security.ResourcePermission(
                        toClass.getSimpleName(),
                        lcGUID,
                        Goliath.Security.ResourceType.TABLEROW(),
                        AccessType.READ()),
                        AccessType.READ());

                    loReturn = loAdapter.getByGUID(toClass, lcGUID);
                }
                if (loReturn != null)
                {
                    loReturn.markOld();
                    putCachedDataObject(loReturn);
                }
            }
            SimpleDataObjectCollection loCollection = new SimpleDataObjectCollection<K>(toClass);
            if (loReturn != null)
            {
                loCollection.add(loReturn);
            }
            return loCollection;
        }

        // Retrieve the object, then if we could not check permission before, check now
        SimpleDataObjectCollection<K> loCollection = new SimpleDataObjectCollection<K>(toClass);
        loCollection.loadList(new PropertyQuery(toProperties));
        return loCollection;
        
    }

    /**
     * This method gets the object version from the datasource for the object specified
     * This object version can then be used to detect change conflicts.
     * Once the version has been read, it will be cleared, so a second read will return a
     * null value.  The old versions will also be cleared periodically so we don't end up
     * with a memory leak here
     * @param toDataObject The object to get the version for
     * @return the version of the object in the data source, or null
     */
    public final Goliath.Date getObjectVersion(ISimpleDataObject toDataObject)
    {
        // TODO: Implement this properly based on the description
        return new Goliath.Date();
    }



    /**
     * Data security related methods
     */
    /**
     * Check if the current user has permission for the object
     * @param toPermission
     * @param toAccessType the type of access to check for
     * @return true if the user has permission
     */
    private boolean checkPermission(IPermission toPermission, AccessType toAccessType)
    {
        return checkPermission(Session.getCurrentSession().getUser(), toPermission, toAccessType);
    }

    /**
     * Check if a user has permission for the object
     * @param toUser the user to check permissions for
     * @param toPermission
     * @param toAccessType the type of access to check for
     * @return true if the user has permission
     */
    private boolean checkPermission(User toUser, IPermission toPermission, AccessType toAccessType)
    {
        if (!onCheckPermission(toUser, toPermission, toAccessType))
        {
            throw new PermissionDeniedException(toUser, toPermission, toAccessType);
        }
        return true;
    }

    protected boolean onCheckPermission(User toUser, IPermission toPermission, AccessType toAccessType)
    {
        return toUser.hasPermission(toPermission, toAccessType);
    }

    /**
     * END Data security related methods
     */

    private DataObjectCache getApplicationDataObjectCache()
    {
        DataObjectCache loCache = Application.getInstance().getProperty("ApplicationDataObjectCache");
        if (loCache == null)
        {
            loCache = new DataObjectCache();
            Application.getInstance().setProperty("ApplicationDataObjectCache", loCache);
        }
        return loCache;
    }

    private DataObjectCache getSessionDataObjectCache()
    {
        ISession loSession = Session.getCurrentSession();
        DataObjectCache loCache = loSession.getProperty("SessionDataObjectCache");
        if (loCache == null)
        {
            loCache = new DataObjectCache();
            loSession.setProperty("SessionDataObjectCache", loCache);
        }
        return loCache;
    }

    private <K extends SimpleDataObject<K>> K getCachedDataObject(Class<K> toClass, PropertySet toProperties)
            throws PermissionDeniedException
    {
        K loReturn = null;

        // First check what type of cache this class is meant to be using
        KeyedObjectCache loObjectCache = null;
        CacheType loCache = getDataMap().getMapItem(toClass).getCacheType();
        if (loCache == CacheType.APPLICATION())
        {
            loObjectCache = getApplicationDataObjectCache();
        }
        else if (loCache == CacheType.SESSION())
        {
            loObjectCache = getSessionDataObjectCache();
        }
        else
        {
            return null;
        }

        if (toProperties.size() == 1)
        {
            if (toProperties.hasProperty("ID"))
            {
                loReturn = (K)loObjectCache.getObjectForKey(toClass, "ID", toProperties.getProperty("ID"));
            }
            else if (toProperties.hasProperty("GUID"))
            {
                loReturn = (K)loObjectCache.getObjectForKey(toClass, "GUID", toProperties.getProperty("GUID"));
            }
            else
            {
                List<K> loItems = loObjectCache.getObjectsWithProperty(toClass, toProperties.getPropertyKeys().get(0), toProperties.getProperty(toProperties.getPropertyKeys().get(0)), 1);
                if (loItems.size() == 1)
                {
                    loReturn = loItems.get(0);
                }
            }
        }
        else
        {
            List<K> loItems = loObjectCache.getObjectsWithProperties(toClass, toProperties);
            if (loItems.size() == 1)
            {
                loReturn = loItems.get(0);
            }
        }

        // We only need to check permission if this was the application cache
        if (loReturn != null && loCache == CacheType.APPLICATION())
        {
            checkPermission(
                        new Goliath.Security.ResourcePermission(
                        loReturn.getClass().getSimpleName(),
                        loReturn.getID(),
                        Goliath.Security.ResourceType.TABLEROW(),
                        AccessType.READ()),
                        AccessType.READ());
        }
        return loReturn;
    }

    /**
     * Puts the specified object in to the cache if it is cacheable
     * @param <K> the type of the object
     * @param toObject the object to cache
     */
    public <K extends SimpleDataObject<K>> void putCachedDataObject(K toObject)
    {
        // First check what type of cache this class is meant to be using
        CacheType loCache = getDataMap().getMapItem(toObject.getClass()).getCacheType();

        if (loCache == CacheType.APPLICATION())
        {
            getApplicationDataObjectCache().add(toObject.getClass(), toObject);
        }
        else if (loCache == CacheType.SESSION())
        {
            getSessionDataObjectCache().add(toObject.getClass(), toObject);
        }
    }

    @Goliath.Annotations.NotProperty
    private void clearCachedDataObject(ISimpleDataObject toObject)
    {
        // First check what type of cache this class is meant to be using
        CacheType loCache = getDataMap().getMapItem(toObject.getClass()).getCacheType();

        if (loCache == CacheType.APPLICATION())
        {
            getApplicationDataObjectCache().remove(toObject);
        }
        else if (loCache == CacheType.SESSION())
        {
            getSessionDataObjectCache().remove(toObject);
        }
    }

    /*
    
    public final <K extends SimpleDataObject<K>> K getDataObjectByProperty(Class<K> toClass, String tcProperty, java.lang.Object toValue)
            throws DataObjectNotFoundException
    {
        PropertySet loSet = new PropertySet();
        loSet.addProperty(tcProperty, toValue);
        return getDataObjectByProperties(toClass, loSet);
    }

    public final <K extends SimpleDataObject<K>> K getDataObjectByProperties(Class<K> toClass, PropertySet toProperties)
            throws DataObjectNotFoundException
    {
        SimpleDataObjectCollection<K> loList = getDataObjectsByProperties(toClass, toProperties, 1);
        if (loList.size() == 1)
        {
            // Put the object in the cache
            putCachedDataObject(loList.get(0));
            return loList.get(0);
        }
        // TODO: Write out a proper error message
        throw new DataObjectNotFoundException("Object not found");
    }

    public final <K extends SimpleDataObject<K>> SimpleDataObjectCollection<K> getDataObjectsByProperties(Class<K> toClass, PropertySet toProperties)
            throws DataObjectNotFoundException
    {
        return getDataObjectsByProperties(toClass, toProperties, m_oDataMap.getMapItem(toClass).getDefaultQuerySize());
    }

    public final <K extends SimpleDataObject<K>> SimpleDataObjectCollection<K> getDataObjectsByProperties(Class<K> toClass, PropertySet toProperties, long tnMaxItems)
            throws DataObjectNotFoundException
    {
        // First check if the user has read permission on this object
        checkPermission(
                    new Goliath.Security.ResourcePermission(
                    toClass.getSimpleName(),
                    null,
                    Goliath.Security.ResourceType.TABLE(),
                    AccessType.READ()),
                    AccessType.READ());

        // If the security check failed, then we would not reach this point as an exception would be thrown
        
        // Before going to the datasource, check if we have cached the object first
        // We only want to check this if we are looking for a single object, if we are looking for multiples then we will have to go to the source in case
        // Something has been added.
        // TODO: Add the ability to cache lists of objects
        if (tnMaxItems == 1)
        {
            K loItem = getCachedDataObject(toClass, toProperties);
            if (loItem != null)
            {
                Application.getInstance().log("Retrieved " + loItem.getClass().getName() + "[" + (loItem.hasGUID()? loItem.getGUID() : loItem.getID()) + "] from cache", LogType.TRACE());
                SimpleDataObjectCollection<K> loList = new SimpleDataObjectCollection<K>();
                loList.add(loItem);
                return loList;
            }
        }

        SimpleDataObjectCollection<K> loCollection = new SimpleDataObjectCollection<K>();

        // TODO : Implement max items on the getlist
        //loCollection.getList(toTemplate, taProperties, tnMaxItems);
        
        return loCollection;

        / *

        // Item was not cached so ask the data mapper to get the item
        IDataMapper<K, Object> loMapper = DataMapper.getDataMapper(toParameters);

        // If you have full access to the table, don't bother checking the permission later
        // If you are denied access to the table, just get out of here throwing permission denied.
        // Check if the user has read access on the table
        boolean llCheckSecurity = !checkAccess(toParameters.getClass(), AccessType.READ());

        if (llCheckSecurity && (tcKey.equalsIgnoreCase("guid")))
        {
            llCheckSecurity = !toParameters.checkPermission(new Goliath.Security.ResourcePermission(toParameters.getClass().getSimpleName(), toParameters.getGUID(), Goliath.Security.ResourceType.TABLEROW(), AccessType.READ()), AccessType.READ());
        }


        loItem = loMapper.getObjectByKey(toParameters, tcKey, tlThrowError);

        if (loItem == null)
        {
            return loItem;
        }

        if (llCheckSecurity)
        {
            // Check if we actually have permission to view this object
            if (toParameters.hasGUID())
            {
                loItem.checkPermission(new Goliath.Security.ResourcePermission(loItem.getClass().getSimpleName(), loItem.getGUID(), Goliath.Security.ResourceType.TABLEROW(), AccessType.READ()), AccessType.READ());
            }
            else
            {
                loItem.checkPermission(new Goliath.Security.ResourcePermission(loItem.getClass().getSimpleName(), loItem.getID(), Goliath.Security.ResourceType.TABLEROW(), AccessType.READ()), AccessType.READ());
            }
        }


        // Because we have found an item, we put it to the cache
        putCachedItem(loItem);

        return loItem;
         *
         * /

    }
*/

}
