/* =========================================================
 * ISqlGenerator.java
 *
 * Author:      kmchugh
 * Created:     29-Jan-2008, 14:12:50
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.DynamicCode;

import Goliath.Collections.List;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.QueryArguments;
import Goliath.DynamicCode.SQLQueryType;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.IDataBase;
import Goliath.Interfaces.Data.ITable;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 29-Jan-2008
 * @author      kmchugh
**/
public interface ISqlGenerator<T extends ISimpleDataObject>
        extends Goliath.Interfaces.DynamicCode.ICodeGenerator<T>
{
    /**
     * Generates the data query using the arguments as a filter, if no arguments are 
     * supplied then there no filters are applied to the query
     * @param tcCodeType the type of 
     * @param toObject
     * @param toArguments
     * @return 
     */
    String generateCode(SQLQueryType toQueryType, IConnection toConnection, T toObject, DataQuery toArguments);
    
    /**
     * Generates a select statement using the data object as the base for the select
     * @param toDataObject the data object to use as the base for this level of the data query
     * @param toArgs the arguments for the query
     * @return the generated statement
     */
    String generateSelectFromDataObject(T toDataObject, DataQuery toArgs);
    
    /**
     * Generates an insert statement using the data object as the base for the insert
     * @param toDataObject the data object to use as the base for this level of the data query
     * @param toArgs the arguments for the query
     * @return the generated statement
     */
    String generateInsertFromDataObject(T toDataObject, DataQuery toArgs);
    
    /**
     * Generates an update statement using the data object as the base for the update
     * @param toDataObject the data object to use as the base for this level of the data query
     * @param toArgs the arguments for the query
     * @param toProperties the list of properties to update
     * @return the generated statement
     */
    String generateUpdateFromDataObject(T toDataObject, DataQuery toArgs, List<Object> toProperties);
    
    
    /**
     * Generates a delete statement using the data object as the base for the delete
     * @param toDataObject the data object to use as the base for this level of the data query
     * @param toArgs the arguments for the query
     * @return the generated statement
     */
    String generateDeleteFromDataObject(T toDataObject, DataQuery toArgs);
    
    
    
    
    
    
    <T extends ISimpleDataObject> String getKeyName(Class<T> toDataObject);
    <T extends ISimpleDataObject> String getTableName(Class<T> toDataObject);
    String getIDValue(ISimpleDataObject toDataObject);
    String generateTableExists(ITable toTable);
    String generateCreateDataBase(IDataBase toDataBase);
    String generateCreateTable(ITable toTable);
    String generateColumnExists(IColumn toColumn);
    String generateColumnInfo(IColumn toColumn);
    String formatForSQL(java.lang.Object toValue);
    Object formatFromSQL(java.lang.Object toValue, Class toClass);
    String generateSelectCountFromDataObject(T toDataObject, QueryArguments toArgs);
}
