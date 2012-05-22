/* =========================================================
 * IConnectionType.java
 *
 * Author:      kmchugh
 * Created:     14-Dec-2007, 18:48:09
 * 
 * Description
 * --------------------------------------------------------
 * The Data Layer Adapter is the class that controls interaction
 * Between the data objects and the data source
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Data.ConnectionTypes;

import Goliath.Collections.List;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Data.Query.DataQuery;
import Goliath.Interfaces.Data.IDataBase;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.ITable;
import Goliath.Interfaces.DynamicCode.ISqlGenerator;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 14-Dec-2007
 * @author      kmchugh
**/
public interface IDataLayerAdapter
{
    /**
     * Creates and saves the specified data object to the data source
     * @param toDataObject the data object to store
     * @return true is saved, false otherwise
     */
    boolean create(ISimpleDataObject toDataObject);
    
    /**
     * Deletes the specified data object from the data store
     * @param toDataObject the data object to delete
     * @return true if successfully deleted
     */
    boolean delete(ISimpleDataObject toDataObject);
    
    /**
     * Deletes all of the items that are in the data query
     * @param toDataObject the data object type to delete
     * @param toQuery the query to use to filter the delete
     * @return true if the items were deleted successfully
     */
    boolean delete(ISimpleDataObject toDataObject, DataQuery toQuery);
    
    /**
     * Deletes the specified data object from the data store
     * @param toDataObject the data object to delete
     * @return true if successfully updated
     */
    boolean update(ISimpleDataObject toDataObject);
    
    /**
     * Updates all of the data items that fall into the query filter, using the data object passed as the template for
     * the update values
     * @param toDataObject the object to use for the template
     * @param toProperties the properties to update
     * @param toQuery the filter to select which objects to update
     * @return true if the objects were updated successfully
     */
    boolean update(ISimpleDataObject toDataObject, List<String> toProperties, DataQuery toQuery);
    
    /**
     * Checks if the specified data object exists in the data store
     * @param toDataObject the data object to check for existance
     * @return true if it exists
     */
    boolean exists(ISimpleDataObject toDataObject);
    
    /**
     * Gets the full list of items, up to tnMaxItems of the specified class from the data store.
     * The list returned is a raw list of items, it may or may not be the type of the class that
     * has been passed in.  If toQuery is null then there is no filtering applied on the query
     * @param <T> the type of the item to get
     * @param toClass the class of the items to get
     * @param toQuery the data filter to apply to the query
     * @param tnMaxItems the maximum number of items to get
     * @return the raw list of items that exist in the database
     */
    <K extends ISimpleDataObject> List<K> getList(Class<K> toClass, DataQuery toQuery, long tnMaxItems);
    
    /**
     * Gets the object of the specified class from the data store
     * @param <T> the type of the object to get
     * @param toClass the class of the object to get
     * @param tnID the id of the object to get
     * @return the object from the data store
     */
    <K extends ISimpleDataObject> K getByID(Class<K> toClass, long tnID);
    
    
    /**
     * Gets the object of the specified class from the data store
     * @param <T> the type of the object to get
     * @param toClass the class of the object to get
     * @param tcGUID the guid of the object to get
     * @return the object from the data store
     */
    <K extends ISimpleDataObject> K getByGUID(Class<K> toClass, String tcGUID);
    
    
    
    
    

    <T extends ISimpleDataObject> T convertObject(Class<T> toClass, Object toObject);

    /**
     * Gets a reference to the string formatter for this class 
     *
     * @return  the String formatter object for this class
     */
    Goliath.Interfaces.IStringFormatter<Goliath.Interfaces.Data.IConnectionString> getFormatter();
    
    /**
     * Gets the list of parameters that are available to this connection type 
     *
     * @return the list of parameters that are available
     */
    List<String> getParameters();

    boolean create(IDataBase toDataBase);

    

    
    boolean exists(IDataBase toDataBase);


    <T extends SimpleDataObject<T>> String getDataObjectSourceName(Class<T> toClass);

    <T extends SimpleDataObject<T>> List<String> getKeyColumns(Class<T> toClass);

    <T extends SimpleDataObject<T>> String getIdentityKeyName(Class<T> toClass);

    /**
     * Gets the query generator that is used for creating queries for this type of data source
     * @return the query generator
     */
    ISqlGenerator getQueryGenerator();

    boolean exists(ITable toTable);
    boolean create(ITable toTable);

}
