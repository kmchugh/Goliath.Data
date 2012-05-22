/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data.DataAdapters;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Data.ConnectionPool;
import Goliath.Data.DataManager;
import Goliath.Data.DataMapItem;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Data.DataSourceStringFormatter;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.InList;
import Goliath.DynamicCode.Java;
import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.IConnectionString;
import Goliath.Interfaces.Data.IDataBase;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.ITable;
import Goliath.Interfaces.DynamicCode.ISqlGenerator;

/**
 *
 * @author kenmchugh
 */
public abstract class DataLayerAdapter extends Goliath.Object
        implements Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter
{

    // TODO: This class needs to be revisited and cleaned
    private List<String> m_oParameters;
    private Goliath.Interfaces.IStringFormatter<Goliath.Interfaces.Data.IConnectionString> m_oFormatter = null;
    private ISqlGenerator m_oGenerator;
    private IConnectionString m_oConnectionString;

    /**
     * Creates a new instance of the data adapter
     */
    public DataLayerAdapter(IConnectionString toString)
    {
        addParameters();
        m_oConnectionString = toString;
    }

    @Override
    public final boolean create(ISimpleDataObject toDataObject)
    {
        return onCreateDataObject(toDataObject);
    }
    
    /**
     * Stores the specified data object to the data store as a new record
     * @param toDataObject the object to store
     * @return true if created successfully
     */
    protected abstract boolean onCreateDataObject(ISimpleDataObject toDataObject);
    
    @Override
    public final boolean delete(ISimpleDataObject toDataObject)
    {
        return onDeleteDataObject(toDataObject, new InList<Long, Long>("ID", new Long[]{toDataObject.getID()}));
    }
    
    @Override
    public final boolean delete(ISimpleDataObject toDataObject, DataQuery toQuery)
    {
        return onDeleteDataObject(toDataObject, toQuery);
    }
    
    /**
     * Deletes the specified data object from the data store
     * @param toDataObject the data object to delete
     * @return true if deleted successfully
     */
    protected abstract boolean onDeleteDataObject(ISimpleDataObject toDataObject, DataQuery toQuery);

    @Override
    public final boolean exists(ISimpleDataObject toDataObject)
    {
        return getByID(toDataObject.getClass(), toDataObject.getID()) != null;
    }
    
    @Override
    public final boolean update(ISimpleDataObject toDataObject)
    {
        return onUpdateDataObject(toDataObject, Java.getPropertyMethods(toDataObject.getClass()), new InList<Long, Long>("ID", new Long[]{toDataObject.getID()}));
    }
    
    @Override
    public final boolean update(ISimpleDataObject toDataObject, List<String> toProperties, DataQuery toQuery)
    {
        return onUpdateDataObject(toDataObject, toProperties, toQuery);
    }

    /**
     * Updates the data object in the data store
     * @param toDataObject the data object to update or use as a template for the update
     * @param toProperties the list of properties to update
     * @return true if updated successfully
     */
    public abstract boolean onUpdateDataObject(ISimpleDataObject toDataObject, List<String> toProperties, DataQuery toQuery);
    
    @Override
    public <T extends ISimpleDataObject> List getList(Class<T> toClass, DataQuery toQuery, long tnMaxItems)
    {
        return onGetRawList(toClass, toQuery, tnMaxItems);
    }
    
    /**
     * Hook method to load all of the items that adhere to the query specified in raw form.
     * If toQuery is null, then there is no where clause on the query
     * @param <T> the type of the items
     * @param toClass the class of the items
     * @param toQuery the query parameters of the items
     * @param tnMaxItems the maximum number of items to load
     * @return the list of raw items
     */
    protected abstract <T extends ISimpleDataObject> List onGetRawList(Class<T> toClass, DataQuery toQuery, long tnMaxItems);
    
    @Override
    public final <T extends ISimpleDataObject> T getByGUID(Class<T> toClass, String tcGUID)
    {
        Goliath.Utilities.checkParameterNotNull("tcGUID", tcGUID);
        return onGetByGUID(toClass, tcGUID);
    }
    
    /**
     * Gets the specified object by GUID
     * @param <T> the class of the item go get
     * @param toClass the class of the item being retrieved
     * @param tcGUID the guid to get the item by
     * @return the item that was retrieved
     */
    protected abstract <T extends ISimpleDataObject> T onGetByGUID(Class<T> toClass, String tcGUID);

    @Override
    public final <T extends ISimpleDataObject> T getByID(Class<T> toClass, long tnID)
    {
        return onGetByID(toClass, tnID);
    }
    
    /**
     * Gets the specified object by ID
     * @param <T> the class of the item go get
     * @param toClass the class of the item being retrieved
     * @param tcGUID the guid to get the item by
     * @return the item that was retrieved
     */
    protected abstract <T extends ISimpleDataObject> T onGetByID(Class<T> toClass, long tnID);
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    


    @Override
    public final <T extends ISimpleDataObject> T convertObject(Class<T> toClass, Object toObject)
    {
        return onConvertObject(toClass, toObject);
    }
    protected abstract <T extends ISimpleDataObject> T onConvertObject(Class<T> toClass, Object toObject);



































    /**
     * Adding of the database name property
     */
    private void addParameters()
    {
        // The database to connect to
        this.addParameter("database");
        
        onAddParameters();
    }

    protected IConnection getConnection()
    {
        IConnection loReturn = null;
        try
        {
            loReturn = ConnectionPool.getConnection(m_oConnectionString);
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            try
            {
                if (loReturn != null)
                {
                    loReturn.close();
                }
            }
            catch(Throwable ignore)
            {
            }
            loReturn = null;
        }
        return loReturn;
    }

    /**
     * Hook to allow subclasses to add parameters
     */
    protected abstract void onAddParameters();

    /**
     * The method for creating the query generator.
     * @return the new query generator
     */
    protected abstract ISqlGenerator onCreateQueryGenerator();

    /**
     * Hook for before creation of the database is started
     * @param toDataBase the database to execute the operation over
     * @return true to continue, false to fail
     */
    protected abstract boolean onBeforeCreateDataBase(IDataBase toDataBase);

    /**
     * The method for creating the database
     * @param toDataBase the database to execute the operation over
     * @return true to continue, false to fail
     */
    protected abstract boolean onCreateDataBase(IDataBase toDataBase);

    /**
     * Hook method for after creating the database
     * @param toDataBase the database to execute the operation over
     * @return true to continue, false to fail
     */
    protected abstract boolean onAfterCreateDataBase(IDataBase toDataBase);


    /**
     * Hook method of failure of database creation
     * @param toDataBase the database to execute the operation over
     */
    protected abstract void onCreateDataBaseFailed(IDataBase toDataBase);

    /**
     * Hook method of failure of database initialisation
     * @param toDataBase the database to execute the operation over
     */
    protected abstract void onInitialiseDataBaseFailed(IDataBase toDataBase);

    /**
     * Hook method to create any db users that are required
     * @param toDataBase the database to execute the operation over
     * @return true to continue with initialisation, false to fail
     */
    protected abstract boolean onCreateUsers(IDataBase toDataBase);

    /**
     * Hook for database initialisation code
     * @param toDataBase the database to execute the operation over
     * @return true to continue, false to fail
     */
    protected abstract boolean onInitialiseDataBase(IDataBase toDataBase);

    /**
     * Hook method for actually checking if the data source exists
     * @param toDataBase the database to execute the operation over
     * @return
     */
    public abstract boolean onCheckDBExists(IDataBase toDataBase);

    /**
     * Gets the query generator for this adapter type
     * @return the query generator
     */
    @Override
    public final ISqlGenerator getQueryGenerator()
    {
        if (m_oGenerator == null)
        {
            m_oGenerator = onCreateQueryGenerator();
        }
        return m_oGenerator;
    }

    /**
     * Checks if the data source exists
     * @param tcName the database name
     * @param toConnectionString the connection string to create the database in
     * @return true if the data source does exist
     */
    @Override
    public final boolean exists(IDataBase toDataBase)
    {
        return onCheckDBExists(toDataBase);
    }

    /**
     * Gets the list of parameters that are available to this connection type
     *
     * @return the list of parameters that are available
     */
    @Override
    public final List<String> getParameters()
    {
        return m_oParameters == null ? new List<String>() : m_oParameters;
    }

    /**
     * Gets a reference to the string formatter for this class, the string formatter is what writes a connection string
     *
     * @return  the String formatter object for this class
     */
    @Override
    public final Goliath.Interfaces.IStringFormatter<Goliath.Interfaces.Data.IConnectionString> getFormatter()
    {
        if (m_oFormatter == null)
        {
            m_oFormatter = onCreateFormatter();
        }
        return m_oFormatter;
    }

    /**
     * Creates the string formatter for this adapter type
     * @return the new string formatter
     */
    protected Goliath.Interfaces.IStringFormatter<Goliath.Interfaces.Data.IConnectionString> onCreateFormatter()
    {
        return new DataSourceStringFormatter();

    }

    /**
     * Adds a parameter to the list of available parameters for this connection type
     *
     * @param  toParameter the parameter to add
     * @return true if the object was changed as a result of this call
     */
    protected final boolean addParameter(String tcName)
    {
        tcName = tcName.toLowerCase();
        if (m_oParameters == null)
        {
            m_oParameters = new List<String>();
        }
        if (!m_oParameters.contains(tcName))
        {
            return m_oParameters.add(tcName);
        }
        return false;
    }

    /**
     * Template method for creating a data source.
     * This method will call the following methods in order:
     * - onBeforeCreateDataBase
     * - onCreateDataBase
     * - onAfterCreateDataBase
     *
     * if any of the above methods fails execution will stop and onCreateDataBaseFailed will be called
     *
     * if the three methods are successful then initialiseDataBase will be called
     *
     *
     * @param toDataBase the database to create
     * @param toConnectionString the connection string to use to create the database
     * @return true if the database was created successfully and exists after all the methods in the template have been called
     */

    @Override
    public final boolean create(IDataBase toDataBase)
    {
        // If the database already exists, we can not create
        if (toDataBase.exists())
        {
            return false;
        }

        boolean llReturn = onBeforeCreateDataBase(toDataBase) &&
               onCreateDataBase(toDataBase) &&
               onAfterCreateDataBase(toDataBase);

        if (!llReturn)
        {
            onCreateDataBaseFailed(toDataBase);
            Application.getInstance().log("Unable to create database " + toDataBase.getName() + " using connection string " + getConnectionString().toString(), LogType.ERROR());
        }
        else
        {
            Application.getInstance().log("Created database " + toDataBase.getName(), LogType.EVENT());
            llReturn = initialise(toDataBase) && exists(toDataBase);
        }
        return llReturn;
    }

    /**
     * Template method for initialising a data source, this method happens after onAfterCreateDataBase returns if it has returned true
     *
     * This method will call the following methods in order
     * - onCreateUsers
     * - onInitialiseDataBase
     *
     * If either of the methods fails execution will stop and onInitialiseDataBaseFailed will be called
     *
     * @param tcName the name of the database to create
     * @param toConnectionString the connection string to use to create the database
     * @return
     */
    protected final boolean initialise(IDataBase toDataBase)
    {
        boolean llReturn = onCreateUsers(toDataBase) && onInitialiseDataBase(toDataBase);
        if (!llReturn)
        {
            onInitialiseDataBaseFailed(toDataBase);
            Application.getInstance().log("Unable to initialise database " + toDataBase.getName() + " using connection string " + getConnectionString().toString(), LogType.ERROR());
        }
        return llReturn;
    }


    protected final IConnectionString getConnectionString()
    {
        return m_oConnectionString;
    }


    /**
     * Template method for creating an entity within a data source
     * This method will call each of the following methods in order, stopping execution immediately
     * if one of the methods fails.
     *
     * - onBeforeCreateTable
     * - onCreateTable
     * - onAfterCreateTable
     *
     * @param toTable the entity to create
     * @return true if the entity was created, false if it was not created
     */
    @Override
    public final boolean create(ITable toTable)
    {
        // If the table exists, we can not create it
        if (toTable.exists())
        {
            return false;
        }

        boolean llReturn = onBeforeCreateTable(toTable) &&
               onCreateTable(toTable) &&
               onAfterCreateTable(toTable);

        if (!llReturn)
        {
            onCreateTableFailed(toTable);
            Application.getInstance().log("Unable to create table " + toTable.getName() + " using connection string " + getConnectionString().toString(), LogType.ERROR());
        }
        else
        {
            Application.getInstance().log("Created table " + toTable.getName(), LogType.EVENT());
        }
        return llReturn;
    }

    
    
    /**
     * Template method for checking if a table exists, this will call onTableExists and return the value returned
     * from that method
     * @param toTable the table to check
     * @return true if the table exists, false if it does not
     */
    @Override
    public final boolean exists(ITable toTable)
    {
        return onTableExists(toTable);
    }

    /**
     * Hook method for before the table is created
     * @param toTable the table to execute the operation over
     */
    protected abstract boolean onBeforeCreateTable(ITable toTable);

    /**
     * Hook method for creating the table
     * @param toTable the table to execute the operation over
     */
    protected abstract boolean onCreateTable(ITable toTable);

    /**
     * Hook method for after the table is created
     * @param toTable the table to execute the operation over
     */
    protected abstract boolean onAfterCreateTable(ITable toTable);

    /**
     * Hook method for notifying failure
     * @param toTable the table to execute the operation over
     */
    protected abstract void onCreateTableFailed(ITable toTable);

    /**
     * Hook method for before the table is created
     * @param toTable the table to execute the operation over
     */
    protected abstract boolean onTableExists(ITable toTable);

    @Override
    public <T extends SimpleDataObject<T>> String getDataObjectSourceName(Class<T> toClass)
    {
        // TODO: When we need things like File based names, this should be turned into a template method
        DataMapItem loItem = DataManager.getInstance().getDataMap().getMapItem(toClass);

        if (loItem != null)
        {
            return loItem.getActualSourceName();
        }
        return toClass.getSimpleName();
    }

    /**
     * Gets the identity key name using the query generator
     * @param <T>
     * @param toClass the class to get the key name for
     * @return the name of the identity key field
     */
    @Override
    public <T extends SimpleDataObject<T>> String getIdentityKeyName(Class<T> toClass)
    {
        return getQueryGenerator().getKeyName(toClass);
    }


    /**
     * Gets the list of key columns for this table, at the moment there is only one key allowed per
     * table, this will be extended later on to allow for multiple primary keys
     * @param <T>
     * @param toClass the class to get the list of key fields for
     * @return the list of key fields, or an empty list of there are none
     */
    @Override
    public <T extends SimpleDataObject<T>> List<String> getKeyColumns(Class<T> toClass)
    {
        List<String> loReturn = new List<String>();
        loReturn.add(getIdentityKeyName(toClass));
        return loReturn;
    }
    
}
