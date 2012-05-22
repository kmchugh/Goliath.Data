 /* =========================================================
 * DataMapper.java
 *
 * Author:      kmchugh
 * Created:     11-Feb-2008, 09:33:22
 * 
 * Description
 * --------------------------------------------------------
 * Used for mapping data from the data source to the java objects
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Data;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Exceptions.CriticalException;
import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Collections.ISimpleDataObjectCollection;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.IDataMapper;
import Goliath.Interfaces.Security.IPermission;
import Goliath.Security.AccessType;
import Goliath.Session;

/**
 *
 * @param <T> 
 * @param <S> 
 * @author kenmchugh
 */
public abstract class DataMapper<T extends ISimpleDataObject, S> extends Goliath.Object
        implements IDataMapper<T, S>
{
    private static HashTable<Class, IDataMapper> m_oMappers = new HashTable<Class, IDataMapper>();
    
    protected DataMapper()
    {
    }

    public static <K extends ISimpleDataObject, L> IDataMapper<K, L> getDataMapper(K toDataObject)
    {
        return getDataMapper((Class<K>)toDataObject.getClass());
    }
    
    public static <K extends ISimpleDataObject, L> IDataMapper<K, L> getDataMapper(Class<K> toClass)
    {
        if (m_oMappers == null || m_oMappers.size() == 0)
        {
            loadMappers();
        }
        if (m_oMappers.containsKey(toClass))
        {
            return m_oMappers.get(toClass);
        }
        IDataMapper<K, L> loMapper = onGetDefaultDataMapper();
        if (loMapper == null)
        {
            throw new CriticalException("No data mapper has been provided for the class " + toClass.getName());
        }
        return loMapper;
    }
    
    protected static <K extends ISimpleDataObject, L> IDataMapper<K, L> onGetDefaultDataMapper()
    {
        return null;
    }
    
    private static void loadMappers()
    {
        List<Class<IDataMapper>> loMapperClasses = Application.getInstance().getObjectCache().getClasses(IDataMapper.class);
        
        // Load all of the available data mapper types
        for (Class<IDataMapper> loMapperClass : loMapperClasses)
        {
            try
            {
                IDataMapper loMapper = loMapperClass.newInstance();
                for (Object loDataObjectClass : loMapper.getSupportedClasses())
                {
                    // Register all of their mappings
                    m_oMappers.put((Class)loDataObjectClass, loMapperClass.newInstance());
                }
            }
            catch (Throwable ignore)
            {}
        }
    }
    
    @Override
    public final List<Class<T>> getSupportedClasses()
    {
        List<Class<T>> loReturn = onGetSupportedClasses();
        if (loReturn == null)
        {
            loReturn = new List<Class<T>>(0);
        }
        return loReturn;
    }
    
    protected abstract List<Class<T>> onGetSupportedClasses();


    @Override
    public final T getObjectByKey(T toParameters, String tcKey, boolean tlThrowError)
    {
        Throwable loError = null;
        T loItem = null;
        try
        {
            loItem = onGetObjectByKey(toParameters, tcKey);
            if (loItem != null)
            {
                Application.getInstance().log("Retrieved " + loItem.getClass().getName() + "[" + (!Goliath.Utilities.isNullOrEmpty(loItem.getGUID()) ? loItem.getGUID() : Long.toString(loItem.getID())) + "] from data source", LogType.TRACE());
            }
        }
        catch (Throwable ex)
        {
            loError = ex;
        }
        if ((loError != null || loItem == null) && tlThrowError)
        {
            throw new Goliath.Exceptions.DataObjectNotFoundException("Could not find " + toParameters.getClass().getSimpleName() + " with " + tcKey + " = [" + Goliath.DynamicCode.Java.getPropertyValue(toParameters, tcKey) + "]");
        }
        return loItem;
    }
    
    protected T onGetObjectByKey(T toParameters, String tcKey) throws Goliath.Exceptions.Exception
    {
        return null;
    }
    
    @Override
    public final T getObjectByID(T toParameters, boolean tlThrowError)
    {
        Throwable loError = null;
        T loItem = null;
        try
        {
            loItem = onGetObjectByID(toParameters);
            if (loItem != null)
            {
                Application.getInstance().log("Retrieved " + loItem.getClass().getName() + "[" + (!Goliath.Utilities.isNullOrEmpty(loItem.getGUID()) ? loItem.getGUID() : Long.toString(loItem.getID())) + "] from data source", LogType.TRACE());
            }
        }
        catch (Throwable ex)
        {
            loError = ex;
        }
        if ((loError != null || loItem == null) && tlThrowError)
        {
            throw new Goliath.Exceptions.DataObjectNotFoundException("Could not find " + toParameters.getClass().getSimpleName() + " with ID = [" + Long.toString(toParameters.getID()) + "]");
        }
        return loItem;
    }
    
    protected T onGetObjectByID(T toParameters) throws Goliath.Exceptions.Exception
    {
        return null;
    }
    
    @Override
    public final T getObjectByID(Class<T> toClass, long tnKey, boolean tlThrowError)
    {
        T loItem = null;
        try
        {
            loItem = toClass.newInstance();
        }
        catch (Throwable ignore)
        {
            
        }
        loItem.setID(tnKey);
        return getObjectByID(loItem, tlThrowError);
    }

    @Override
    public final java.lang.Object[] getListIn(T toParameters, ISimpleDataObjectCollection<?> toSearchCollection, String[] taInFields, String[] taInDataFields, String[] taOrderFields, boolean tlThrowError) throws DataException
    {
        DataException loError = null;
        Object[] toResultCache = null;
        try
        {
           toResultCache = onGetListIn(toParameters,  toSearchCollection, taInFields, taInDataFields, taOrderFields, tlThrowError);
        }
        catch (DataException ex)
        {
            loError = ex;
        }
        if ((loError != null || toResultCache == null) && tlThrowError)
        {
            throw loError;
        }
        return toResultCache;
    }
    
    protected java.lang.Object[] onGetListIn(T toParameters, ISimpleDataObjectCollection<?> toSearchCollection, String[] taInFields, String[] taInDataFields, String[] taOrderFields, boolean tlThrowError) throws DataException
    {
        return null;
    }
    
    protected java.lang.Object[] onGetList(T toParameters, String[] taWhereFields, String[] taOrderFields, boolean tlThrowError) throws DataException
    {
        return null;
    }
    
    @Override
    public final java.lang.Object[] getList(T toParameters, String[] taWhereFields, String[] taOrderFields, boolean tlThrowError) throws DataException
    {
        DataException loError = null;
        Object[] toResultCache = null;
        try
        {
           toResultCache = onGetList(toParameters, taWhereFields, taOrderFields, tlThrowError);
        }
        catch (DataException ex)
        {
            loError = ex;
        }
        if ((loError != null || toResultCache == null) && tlThrowError)
        {
            if (loError == null)
            {
                throw new DataException("No items returned when getting list for " + toParameters.getClass().getName());
            }
            throw loError;
        }
        return toResultCache;
    }

    /**
     * Checks if the current user has the specified permission to the entire resource list specified
     * @param toObject the object to check access to
     * @param toType the access type
     * @return true if the user is allowed access
     */
    protected boolean checkAccess(T toObject, AccessType toType)
    {
        IPermission loPermission = new Goliath.Security.ResourcePermission(toObject.getClass().getSimpleName(), null, Goliath.Security.ResourceType.TABLE(), toType);
        if (!Session.getCurrentSession().getUser().hasPermission(loPermission, toType))
        {
            /*
            // The user doesn't have the permission on the table, check for permission on the specific object
            if (!toObject.checkPermission(new Goliath.Security.ResourcePermission(toObject.getClass().getSimpleName(), toObject.getID(), Goliath.Security.ResourceType.TABLEROW(), AccessType.READ()), AccessType.READ()))
            {
                throw new PermissionDeniedException(loPermission, toType);
            }
             * 
             */
        }
        return true;
    }
    
    @Override
    public final void createFromDataSource(T toObject, S toSource) throws Goliath.Exceptions.DataException
    {
        onCreateFromDataSource(toObject, toSource);
        //toObject.markOld();
    }
    
    protected abstract void onCreateFromDataSource(T toObject, S toSource) throws Goliath.Exceptions.DataException;
    
    //TODO : Secure this method so it can only be called from simpledataobject.save
    @Override
    public final void save(T toObject) throws Goliath.Exceptions.DataException
    {
        // All checks have been done in SimpleDataObject.save
        String lcMethod = "";
        if (toObject.isDeleted())
        {
            // Check for delete permission on the table first
            if (checkAccess(toObject, AccessType.DELETE()))
            {
                // Need to delete the object
                onDelete(toObject);
            }
        }
        else
        {
            if (toObject.isNew())
            {
                if (checkAccess(toObject, AccessType.WRITE()))
                {
                    // need to insert the object
                    onInsert(toObject);
                }
            }
            else
            {
                if (checkAccess(toObject, AccessType.CREATE()))
                {
                    // need to update the object
                    onUpdate(toObject);
                }
            }
        }
    }

    @Override
    public final T convertObject(T toParameter, Object toSource)
    {
        // If either the parameter or the source is null, then we won't be able to convert
        if (toSource == null || toParameter == null)
        {
            return null;
        }
        
        // If the object is already the right type then we don't need to convert
        if (toParameter.getClass().isAssignableFrom(toSource.getClass()))
        {
            return (T)toSource;
        }
        return onConvertObject(toParameter, toSource);
    }

    protected abstract T onConvertObject(T toParameter, java.lang.Object toSource);

    protected abstract T onDelete(T toObject) throws Goliath.Exceptions.DataException;
    protected abstract T onInsert(T toObject) throws Goliath.Exceptions.DataException;
    protected abstract T onUpdate(T toObject) throws Goliath.Exceptions.DataException;
    
    
}
