/* =========================================================
 * SQLGenerator.java
 *
 * Author:      kmchugh
 * Created:     29-Jan-2008, 09:49:27
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
 * =======================================================*/

package Goliath.DynamicCode;

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Data.DataManager;
import Goliath.Data.DataObjects.EnumerationDefinition;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Data.IndexType;
import Goliath.Data.JoinInfo;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.InList;
import Goliath.Data.Query.JoinQuery;
import Goliath.Data.QueryArguments;
import Goliath.DynamicEnum;
import Goliath.Interfaces.Collections.IList;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.IDataBase;
import Goliath.Interfaces.Data.IDataType;
import Goliath.Interfaces.Data.IIndex;
import Goliath.Interfaces.Data.IIndexType;
import Goliath.Interfaces.Data.IRelation;
import Goliath.Interfaces.Data.ITable;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @param <T>
 * @see         Related Class
 * @version     1.0 29-Jan-2008
 * @author      kmchugh
**/
public abstract class SQLGenerator<T extends Goliath.Data.DataObjects.SimpleDataObject>
        extends Goliath.DynamicCode.CodeGenerator<T>
        implements Goliath.Interfaces.DynamicCode.ISqlGenerator<T>
{

    /** Creates a new instance of SQLGenerator */
    protected SQLGenerator()
    {
    }


    /**
     * Helper function to wrap the string in the boundaries defined for this generator
     * @param tcString the string to wrap
     * @return the wrapped string
     */
    protected String wrapInBounds(String tcString)
    {
            return getLeftBoundry() + tcString + getRightBoundry();
    }

    /**
     * Gets the left hand boundary for any strings that need to be wrapped
     * @return the left boundary
     */
    protected String getLeftBoundry()
    {
        return "[";
    }

    /**
     * Gets the right hand boundary for any strings that need to be wrapped
     * @return the right boundary
     */
    protected String getRightBoundry()
    {
        return "]";
    }

    /**
     * Generates the SQL statement used for creating a database
     * @param toDataBase the database to create the statement for
     * @return the statement
     */
    @Override
    public String generateCreateDataBase(IDataBase toDataBase)
    {
        return "CREATE DATABASE " + wrapInBounds(toDataBase.getName());
    }

    /**
     * Generates the SQL statement used for checking if a table exists
     * @param toTable the table to create the statement for
     * @return the statement
     */
    @Override
    public String generateTableExists(ITable toTable)
    {
        return "SELECT * FROM " + wrapInBounds(toTable.getName());
    }

    /**
     * Generates the SQL statement used for creating a table
     * @param toTable the table to create the statement for
     * @return the statement
     */
    @Override
    public String generateCreateTable(ITable toTable)
    {
        // Generate the create
        StringBuilder loBuilder = new StringBuilder("CREATE TABLE " + wrapInBounds(toTable.getName()) + " (");

        boolean llStart = true;
        // Generate each of the columns
        for (IColumn loColumn : toTable.getColumns())
        {
            if (!llStart)
            {
                loBuilder.append(", ");
            }
            llStart = false;

            loBuilder.append(wrapInBounds(loColumn.getName()));
            loBuilder.append(" ");
            loBuilder.append(dataTypeToString(loColumn.getType()));

            if (!loColumn.getAllowNull())
            {
                loBuilder.append(" NOT NULL ");
            }

            if (loColumn.getAutoIncrement())
            {
                loBuilder.append(getAutoIncrementText());
            }

            if (!Goliath.Utilities.isNullOrEmpty(loColumn.getDefault()))
            {
                loBuilder.append(" default ");
                loBuilder.append(defaultToString(loColumn.getDefault()));
            }
        }

        llStart = true;
        if (toTable.getKeyColumns().size() > 0)
        {
            loBuilder.append(", PRIMARY KEY (");
            // Generate the keys
            for (IColumn loColumn : toTable.getKeyColumns())
            {
                if (!llStart)
                {
                    loBuilder.append(", ");
                }
                llStart = false;
                loBuilder.append(wrapInBounds(loColumn.getName()));
            }
            loBuilder.append(")");
        }


        llStart = true;
        // Generate the Indexes
        for (IIndex loIndex : toTable.getIndexes())
        {
            loBuilder.append(", ");
            loBuilder.append(indexTypeToString(loIndex.getType()));
            loBuilder.append(" (");
            llStart = true;
            for (IColumn loColumn : loIndex.getColumns())
            {
                if (!llStart)
                {
                    loBuilder.append(", ");
                }
                llStart = false;
                loBuilder.append(wrapInBounds(loColumn.getName()));
            }

            loBuilder.append(")");

        }

        // Generate the Foreign Keys
        for (IRelation loRelation : toTable.getRelations())
        {
            loBuilder.append(generateRelationString(loRelation));
        }

        // Finish the statement
        loBuilder.append(")");


        return loBuilder.toString();
    }
    
    /**
     * Generates a select statement using the data object as the base and filtering by
     * the query arguments, if the query arguments are null then no filter is applied
     * @param toDataObject the data object 
     * @param toArgs the query filter
     * @return 
     */
    @Override
    public String generateSelectFromDataObject(T toDataObject, DataQuery toArgs)
    {
        StringBuilder loBuilder = new StringBuilder();

        generateSelect(loBuilder, toDataObject);
        if (toArgs != null)
        {
            generateJoinList(loBuilder, toDataObject, toArgs);
            loBuilder.append(" WHERE ");
            generateFromDataQuery(loBuilder, toDataObject, toArgs);
        }
        
        return loBuilder.toString();
    }
    
    @Override
    public String generateInsertFromDataObject(T toDataObject, DataQuery toArgs)
    {
        StringBuilder loBuilder = new StringBuilder("INSERT INTO " + wrapInBounds(getTableName(toDataObject.getClass())) + "(");
        
        Goliath.Collections.List<String> loProperties = getPropertiesWithOutKey(toDataObject);
        boolean llStarted = false;
        for (String lcString : loProperties)
        {
            loBuilder.append( (llStarted ? ", " : ""));
            loBuilder.append(wrapInBounds(lcString));
            llStarted = true;
        }
        loBuilder.append(") VALUES (");
        
        llStarted = false;
        for (String lcString : loProperties)
        {
            Object loResult = Goliath.DynamicCode.Java.getPropertyValue(toDataObject, lcString);
            loBuilder.append( (llStarted ? ", " : ""));
            loBuilder.append(formatForSQL(loResult));
            llStarted = true;                                    
        }
        
        loBuilder.append(")");
        
        
        return loBuilder.toString();
    }
    
    @Override
    public String generateUpdateFromDataObject(T toDataObject, DataQuery toArgs, List<Object> toProperties)
    {
        StringBuilder loBuilder = new StringBuilder("UPDATE " + wrapInBounds(getTableName(toDataObject.getClass())) + " SET ");
        
        boolean llStarted = false;
        for (Object loProperty : toProperties)
        {
            String lcString = loProperty.toString();
            
            // We don't update GUID, CreatedDate, CreatedBy, Or Rowversion
            if (!lcString.equalsIgnoreCase("GUID")
                    && !lcString.equalsIgnoreCase("CreatedDate")
                    && !lcString.equalsIgnoreCase("CreatedBy") 
                    && !lcString.equalsIgnoreCase("Rowversion")
                    && !lcString.equalsIgnoreCase("ID"))
            {
                Object loResult = Goliath.DynamicCode.Java.getPropertyValue(toDataObject, lcString);
                loBuilder.append( (llStarted ? ", " : ""));
                loBuilder.append(wrapInBounds(lcString));
                loBuilder.append(" = " );
                loBuilder.append(formatForSQL(loResult));
                llStarted = true;                                    
            }
        }
        
        if (toArgs != null)
        {
            generateJoinList(loBuilder, toDataObject, toArgs);
            loBuilder.append(" WHERE ");
            generateFromDataQuery(loBuilder, toDataObject, toArgs);
        }
        
        return loBuilder.toString();
    }
    
    @Override
    public String generateDeleteFromDataObject(T toDataObject, DataQuery toArgs)
    {
        StringBuilder loBuilder = new StringBuilder("DELETE FROM " + wrapInBounds(getTableName(toDataObject.getClass())));
        
        if (toArgs != null)
        {
            generateJoinList(loBuilder, toDataObject, toArgs);
            loBuilder.append(" WHERE ");
            generateFromDataQuery(loBuilder, toDataObject, toArgs);
        }
        
        return loBuilder.toString();
    }
    
    private void getJoinList(DataQuery toArgs, List<JoinQuery> toList)
    {
        if (Java.isEqualOrAssignable(JoinQuery.class, toArgs.getClass()))
        {
            toList.add((JoinQuery)toArgs);
        }
        else
        {
            if (toArgs.getLeftArgument() != null)
            {
                getJoinList(toArgs.getLeftArgument(), toList);
            }
            
            if (toArgs.getRightArgument() != null)
            {
                getJoinList(toArgs.getRightArgument(), toList);
            }
        }
    }
    
    private void generateJoinList(StringBuilder toBuilder, T toDataObject, DataQuery toArgs)
    {
        // First get the join list
        List<JoinQuery> loJoins = new List<JoinQuery>();
        getJoinList(toArgs, loJoins);
        
        StringBuilder loJoinQuery = new StringBuilder();
        // Generate for joins
        for (JoinQuery loJoin : loJoins)
        {
            generateJoinClause(loJoinQuery, toDataObject, toArgs.isInverted(), loJoin);
        }
        
        if (loJoinQuery.length() > 0)
        {
            toBuilder.append(loJoinQuery);
        }
        
    }
    
    /**
     * Builds the filter portion of the query based on the data query passed in
     * @param toBuilder the string builder to attach the generated query to
     * @param toDataObject the data object to base the query on
     * @param toArgs the arguments for this query
     */
    private void generateFromDataQuery(StringBuilder toBuilder, T toDataObject, DataQuery toArgs)
    { 
        // Generate for left
        StringBuilder loLeft = new StringBuilder();
        DataQuery loLeftArg = toArgs.getLeftArgument();
        if (loLeftArg != null && !(Java.isEqualOrAssignable(JoinQuery.class, loLeftArg.getClass())))
        {
            generateFromDataQuery(loLeft, toDataObject, loLeftArg);
        }
        
        // Generate for right
        StringBuilder loRight = new StringBuilder();
        DataQuery loRightArg = toArgs.getRightArgument();
        if (loRightArg != null && !(Java.isEqualOrAssignable(JoinQuery.class, loRightArg.getClass())))
        {
            generateFromDataQuery(loRight, toDataObject, loRightArg);
        }
        
        // TODO: We want to implement a factory here
        if (Java.isEqualOrAssignable(InList.class, toArgs.getClass()))
        {
            // Process the inlist arguments
            generateInClause(toBuilder, toDataObject, toArgs.isInverted(), toArgs.getQueryProperty(), (List)toArgs.getQueryData(), toArgs);
        }
        else if (Java.isEqualOrAssignable(JoinQuery.class, toArgs.getClass()))
        {
            // Do nothing as it has already been done
        }
        else
        {
            // Generic rule
            Goliath.Utilities.appendToStringBuilder(toBuilder, 
                            toArgs.isInverted() ? " NOT " : "",
                            "(",
                            loLeft.toString(),
                            " ",
                            loLeft.length() > 0 && loRight.length() > 0 && toArgs.getOperator() != null ? " " + toArgs.getOperator().getValue() + " " : "",
                            " ",
                            loRight.toString(),
                            ")");
        }
    }
    
    private Class<? extends SimpleDataObject> getContext(T toDataObject, DataQuery toQuery)
    {
        Class<? extends SimpleDataObject> loReturn = toQuery.getContext();
        return loReturn == null ? toDataObject.getClass() : loReturn;
    }
    
    
    /**
     * Generates an in clause for a WHERE statement
     * @param toBuilder the string builder to append the clause to
     * @param toDataObject the data object to base the clause on
     */
    protected void generateJoinClause(StringBuilder toBuilder, T toDataObject, boolean tlInverted, JoinQuery toQuery)
    {
        Class<? extends SimpleDataObject> loPrimary = getContext(toDataObject, toQuery);
        Class<? extends SimpleDataObject> loSecondary = toQuery.getSecondary();
        
        // If there is no join query, then just return
        Goliath.Utilities.appendToStringBuilder(toBuilder, 
                " JOIN ",
                wrapInBounds(getTableName(loSecondary)),
                " ON ",
                tlInverted ? "NOT " : "",
                "(",
                wrapInBounds(getTableName(loPrimary)),
                ".",
                wrapInBounds(toQuery.getForeignKey().equalsIgnoreCase("id") ? getKeyName(loPrimary) : toQuery.getForeignKey()),
                " = ",
                wrapInBounds(getTableName(loSecondary)),
                ".",
                wrapInBounds(toQuery.getProperty().equalsIgnoreCase("id") ? getKeyName(loSecondary) : toQuery.getProperty()),
                ")");
        
    }





    /**
     * Generates an in clause for a WHERE statement
     * @param toBuilder the string builder to append the clause to
     * @param toDataObject the data object to base the clause on
     */
    protected void generateInClause(StringBuilder toBuilder, T toDataObject, boolean tlInverted, String tcProperty, List toValues, DataQuery toQuery)
    {
        Class<? extends SimpleDataObject> loPrimary = getContext(toDataObject, toQuery);
        if (toValues.size() > 0)
        {
            tcProperty = tcProperty.toLowerCase();
            Goliath.Utilities.appendToStringBuilder(toBuilder, 
                            wrapInBounds(getTableName(loPrimary)),
                            ".",
                            wrapInBounds(tcProperty.equalsIgnoreCase("id") ? getKeyName(loPrimary) : tcProperty));
            
            // If there is only one item in the list, then use an equal statement rather than an in
            if (toValues.size() == 1)
            {
                toBuilder.append(tlInverted ? " <> " : " = ");
                toBuilder.append(formatForSQL(toValues.get(0)));
            }
            else
            {
                toBuilder.append(tlInverted ? " NOT " : "");
                toBuilder.append(" IN (");
                
                for (int i=0, lnLength = toValues.size(); i<lnLength; i++)
                {
                    toBuilder.append(formatForSQL(toValues.get(i)));
                    toBuilder.append(i != lnLength -1 ? ", " : "");
                }
                toBuilder.append(")");
            }
        }
    }



    /**
     * Generates a select statement for the data object 
     * @param toBuilder
     * @param toDataObject 
     */
    protected void generateSelect(StringBuilder toBuilder, T toDataObject)
    {
        String lcTableName = wrapInBounds(getTableName(toDataObject.getClass()));

        toBuilder.append("SELECT DISTINCT ");
        boolean llStarted = false;
        Goliath.Collections.List<String> loProperties = getPropertiesWithKey(toDataObject);
        for (String lcString : loProperties)
        {
            Goliath.Utilities.appendToStringBuilder(toBuilder,
                    (llStarted ? ", " : ""),
                    lcTableName,
                    ".",
                    wrapInBounds(lcString)
                    );
            llStarted = true;
        }
        toBuilder.append(" FROM ");

        toBuilder.append(lcTableName);
    }
    
    protected Goliath.Collections.List<String> getPropertiesWithKey(T toDataObject)
    {
        Goliath.Collections.List<String> loProperties = getPropertiesWithOutKey(toDataObject);
        String lcKey = getKeyName(toDataObject.getClass());
        if (!loProperties.contains(lcKey))
        {
            loProperties.add(lcKey);
        }
        return loProperties;
    }
    
    protected Goliath.Collections.List<String> getPropertiesWithOutKey(T toDataObject)
    {
        Goliath.Collections.List<String> loProperties = Goliath.DynamicCode.Java.getPropertyMethods(toDataObject.getClass());

        boolean llHasGUID = toDataObject.hasGUID();

        List<String> loReturn = new List<String>(loProperties.size());
        for (String lcProperty : loProperties)
        {
            lcProperty = lcProperty.toLowerCase();
            if (!lcProperty.equals("id") && (llHasGUID || !lcProperty.equals("guid")))
            {
                loReturn.add(lcProperty);
            }
        }
        return loReturn;
    }
    
    @Override
    public <T extends ISimpleDataObject> String getKeyName(Class<T> toDataObject)
    {
        return DataManager.getInstance().getDataMap().getMapItem(toDataObject).getSourceName() + "ID";
    }
    
    @Override
    public <T extends ISimpleDataObject> String getTableName(Class<T> toDataObject)
    {
        return DataManager.getInstance().getDataMap().getMapItem(toDataObject).getActualSourceName();
    }
    
    @Override
    public String getIDValue(ISimpleDataObject toDataObject)
    {
        long lnID = toDataObject.getID();
        if (lnID <= 0)
        {
            return "NULL";
        }
        else
        {
            return Long.toString(lnID);
        }        
    }

    @Override
    public String formatForSQL(java.lang.Object toValue)
    {
        String lcValue = "";
        if (toValue == null)
        {
            lcValue = "NULL";
        } 
        else if (toValue.getClass().equals(java.lang.Boolean.class))
        {
            lcValue = (((java.lang.Boolean)toValue).booleanValue() ? "1" : "0");
        }
        else if (toValue.getClass().equals(java.lang.Integer.class))
        {
            lcValue = toValue.toString();
        }
        else if (toValue.getClass().equals(java.lang.Double.class))
        {
            lcValue = toValue.toString();
        }
        else if (toValue.getClass().equals(java.lang.Float.class))
        {
            lcValue = toValue.toString();
        }
        else if (toValue.getClass().equals(java.lang.Long.class))
        {
            lcValue = toValue.toString();
        }
        else if (toValue.getClass().equals(Goliath.Date.class))
        {
            lcValue = Long.toString(((Goliath.Date)toValue).getLong());
        }
        else if (toValue.getClass().equals(java.util.Date.class))
        {
            lcValue = Long.toString(((java.util.Date)toValue).getTime());
        }
        else if (Java.isEqualOrAssignable(DynamicEnum.class, toValue.getClass()))
        {
            // Get the EnumerationDefinition value for this DynamicEnum, if it doesn't exist, create it
            EnumerationDefinition loDef = EnumerationDefinition.getEnumerationDefinition((DynamicEnum)toValue);
            return Long.toString(loDef.getID());
        }
        // TODO : Make this specific to byte arrays.
        else if (toValue.getClass().isArray())
        {
            lcValue = "?";
        }
        else
        {
            lcValue = "'" + toValue.toString().replace("'", "''") + "'";
        }
        
        lcValue = lcValue.replace("\"", "\\\"");

        return lcValue;
    }
    
    @Override
    public Object formatFromSQL(java.lang.Object toValue, Class toClass)
    {
        if (toValue == null)
        {
            return null;
        }

        if (toClass.equals(Goliath.Date.class) && toValue.getClass().equals(Long.class))
        {
            return new Goliath.Date((Long)toValue);
        }
        else if (toValue.getClass().equals(java.util.Date.class))
        {
            return new Goliath.Date((java.util.Date)toValue);
        }
        else if (Java.isEqualOrAssignable(DynamicEnum.class, toClass))
        {
            // Need to load the type definition
            EnumerationDefinition loDef = EnumerationDefinition.getObjectByID(EnumerationDefinition.class, (Long)toValue);
            return loDef != null ? DynamicEnum.getEnumeration(toClass, loDef.getValueCharacter()) : null;
        }
        else if (toValue.getClass().equals(javax.sql.rowset.serial.SerialBlob.class))
        {
            try
            {
                javax.sql.rowset.serial.SerialBlob loBlob = (javax.sql.rowset.serial.SerialBlob)toValue;
                byte[] laBytes = new byte[(int)loBlob.length()];

                loBlob.getBinaryStream().read(laBytes);

                return laBytes;
            }
            catch(Throwable ex)
            {
                return new byte[]{};
            }
        }
        else if (toValue.getClass().equals(java.sql.Timestamp.class))
        {
            if (((java.sql.Timestamp)toValue).getNanos() == 0)
            {
                return new Goliath.Date(0);
            }
            return new Goliath.Date((java.util.Date)toValue);                        
        }
        else if ((toClass.getName().equalsIgnoreCase("boolean") || toClass.equals(Boolean.class)) && (toValue.getClass().equals(Integer.class) || toValue.getClass().equals(Short.class)))
        {
            return toValue.toString().equals("1");
        }
        else if (toClass.getSimpleName().equalsIgnoreCase("float") && (toValue != null && toValue.getClass().getSimpleName().equalsIgnoreCase("double")))
        {
            return ((Double)toValue).floatValue();
        }
        else
        {
            return toValue;
        }
    }

    
    @Override
    public String generateColumnExists(IColumn toColumn)
    {
        return "SELECT " + toColumn.getName() + " FROM " + toColumn.getTable().getName();
    }
    
    @Override
    public String generateColumnInfo(IColumn toColumn)
    {
        return "SELECT " + toColumn.getName() + " FROM " + toColumn.getTable().getName();
    }

    protected String getUTF8String()
    {
        return " character set utf8";
    }
    
    protected String dataTypeToString(IDataType toType)
    {
        String lcDataType = toType.getName() + (toType.getLength() != 0 ? "(" + Integer.toString(toType.getLength()) + ")" : "");
        if (toType.getName().equalsIgnoreCase("varchar"))
        {
            lcDataType = lcDataType + getUTF8String();
        }
        return lcDataType.toUpperCase();
    }
    
    protected String indexTypeToString(IIndexType toType)
    {
        if (toType == IndexType.UNIQUE())
        {
            return "UNIQUE";
        }
        return "";
    }

    protected String defaultToString(String tcDefault)
    {
        return tcDefault;
    }

    




    protected final String getAutoIncrementText()
    {
        return " " + onGetAutoIncrementText() + " ";
    }

    protected String onGetAutoIncrementText()
    {
        return "INCREMENT(1, 1)";
    }

    protected abstract String generateRelationString(IRelation toRelation);



    private void generateWhereClause(StringBuilder toBuilder, T toDataObject, QueryArguments toArgs)
    {
        if (toArgs == null)
        {
            return;
        }

        PropertySet loFilter = toArgs.getFilter();
        if (loFilter != null && loFilter.size() > 0)
        {
            toBuilder.append(" WHERE ");
            int lnCount = 0;
            for(String lcColumn : loFilter.getPropertyKeys())
            {
                lcColumn = lcColumn.toLowerCase();
                
                toBuilder.append(wrapInBounds(lcColumn.equalsIgnoreCase("id") ? getKeyName(toDataObject.getClass()) : lcColumn));
                
                Object loObject = null;
                if (lcColumn.equalsIgnoreCase(getKeyName(toDataObject.getClass())))
                {
                    loObject = formatForSQL(toDataObject.getID());
                }
                else
                {
                    loObject = formatForSQL(loFilter.getProperty(lcColumn));
                }
                if (loObject.toString().equalsIgnoreCase("null"))
                {
                    toBuilder.append(" IS ");
                }
                else
                {
                    toBuilder.append(" = ");
                }
                toBuilder.append(loObject);

                if (lnCount != loFilter.size()-1)
                {
                    toBuilder.append(" AND ");
                }
                lnCount ++;
            }
        }
    }

    protected void generateJoin(StringBuilder toBuilder, T toDataObject, QueryArguments toArgs)
    {
        IList<JoinInfo> loInfoList = toArgs.getJoinInfo();
        if (loInfoList != null && loInfoList.size() > 0)
        {
            for (JoinInfo loInfo : loInfoList)
            {
                Goliath.Utilities.appendToStringBuilder(toBuilder, 
                        " JOIN ",
                        wrapInBounds(getTableName(loInfo.getSecondaryObject())),
                        " ON ");

                int lnCount = 0;
                IList<JoinInfo.PredicateInfo> loPredicates = loInfo.getPredicates();
                for (JoinInfo.PredicateInfo loPredicate : loPredicates)
                {
                    Class loClass = loPredicate.getPredicateClass();
                    String lcField = loPredicate.getPredicateField();
                    Object loValue = loPredicate.getPredicateValue();
                    boolean llValueIsColumn = (loValue.getClass().equals(String.class)) ? isPropertyOf(loValue.toString(), (loClass.equals(loInfo.getSecondaryObject()) ? loInfo.getPrimaryObject() : loInfo.getSecondaryObject())) : false;
                    
                    Goliath.Utilities.appendToStringBuilder(toBuilder,
                            wrapInBounds(getTableName(loClass)),
                            ".",
                            wrapInBounds(lcField.equalsIgnoreCase("id") ? getKeyName(loClass) : lcField));
                            
                    
                    if (loValue ==  null || loValue.toString().equalsIgnoreCase("null"))
                    {
                        toBuilder.append(" IS ");
                    }
                    else
                    {
                        toBuilder.append(" = ");
                    }

                    if (llValueIsColumn)
                    {
                        Goliath.Utilities.appendToStringBuilder(toBuilder,
                            wrapInBounds(getTableName(loClass.equals(loInfo.getSecondaryObject()) ? loInfo.getPrimaryObject() : loInfo.getSecondaryObject())),
                            ".",
                            wrapInBounds(loValue.toString().equalsIgnoreCase("id") ? getKeyName(loInfo.getSecondaryObject()) : loValue.toString()));
                    }
                    else
                    {
                        toBuilder.append(formatForSQL(loValue));
                    }

                    // Currently this makes the first Predicate And and the rest OR, this should be updated to allow definition of predicate

                    if (lnCount != loPredicates.size()-1)
                    {
                        if (lnCount == 0)
                        {
                            toBuilder.append(" AND ");
                            if (loPredicates.size() > 2)
                            {
                                toBuilder.append("(");
                            }
                        }
                        else
                        {
                            toBuilder.append(" OR ");
                        }
                    }
                    else
                    {
                        if (lnCount >= 2)
                        {
                            toBuilder.append(")");
                        }
                    }
                    lnCount++;
                }
            }
        }
    }

    private boolean isPropertyOf(String tcProperyField, Class toClass)
    {
        return Java.getPropertyType(toClass, tcProperyField) != null;
    }

    private void generateSelectCount(StringBuilder toBuilder, T toDataObject)
    {
        // TODO: We shouldn't use *, need to build a proper list of fields here
        toBuilder.append("SELECT DISTINCT COUNT(*) as Count");
        toBuilder.append(" FROM ");
        toBuilder.append(wrapInBounds(getTableName(toDataObject.getClass())));
    }

    private void generateOrderClause(StringBuilder toBuilder, QueryArguments toArguments)
    {
        if (toArguments == null)
        {
            return;
        }

        String[] laOrder = toArguments.getOrder();
        if (laOrder != null && laOrder.length > 0)
        {
            toBuilder.append(" ORDER BY");
            for(String lcColumn : laOrder)
            {
                toBuilder.append(" ");
                toBuilder.append(wrapInBounds(lcColumn));
                if (!lcColumn.equals(laOrder[laOrder.length -1]))
                {
                    toBuilder.append(",");
                }
            }
        }
    }

    @Override
    public String generateSelectCountFromDataObject(T toDataObject, QueryArguments toArgs)
    {
        StringBuilder loBuilder = new StringBuilder();

        generateSelectCount(loBuilder, toDataObject);

        generateJoin(loBuilder, toDataObject, toArgs);

        generateWhereClause(loBuilder, toDataObject, toArgs);
        
        generateOrderClause(loBuilder, toArgs);
        
        return loBuilder.toString();
    }

    
    
    @Override
    public String generateCode(SQLQueryType toQueryType, IConnection toConnection, T toObject, DataQuery toArguments)
    {
        throw new UnsupportedOperationException("Use the SQL Query types to generate code");
        //return toQueryType.generateCode(toConnection, toObject, toArguments, null);
    }
    
    

    @Override
    public String generateCode(CodeType toCodeType, T toObject)
    {
        throw new UnsupportedOperationException("Use the SQL Query types to generate code");
        //return toCodeType.generateCode(toObject);
        
        /*
        if (toCodeType.equalsIgnoreCase("insert"))
        {
            return generateInsertFromDataObject(toObject);
        }
        else if (toCodeType.equalsIgnoreCase("update"))
        {
            return generateUpdateFromDataObject(toObject);
        }
        else if (toCodeType.equalsIgnoreCase("delete"))
        {
            return generateDeleteFromDataObject(toObject);
        }
        else if (toCodeType.equalsIgnoreCase("select"))
        {
            PropertySet loFilter = new PropertySet();
            loFilter.setProperty(getKeyName(toObject.getClass()), Goliath.DynamicCode.Java.getPropertyValue(toObject, getKeyName(toObject.getClass())));
            return generateSelectFromDataObject(toObject, new QueryArguments(loFilter));
        }
        else
        {
            throw new Goliath.Exceptions.InvalidParameterException("Incorrect Code Type for generateString", "tcCodeType");
        }
         * 
         */
    }
}
