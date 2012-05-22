/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Annotations.ForeignKey;
import Goliath.Annotations.NoNulls;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Data.DataObjects.DataObject;
import Goliath.Data.DataObjects.EnumerationDefinition;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.DynamicCode.Java;
import Goliath.DynamicCode.Java.MethodDefinition;
import Goliath.DynamicEnum;
import Goliath.Exceptions.DataException;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;
import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IDataType;
import Goliath.Interfaces.Data.IIndex;
import Goliath.Interfaces.Data.IRelation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 *
 * @author kenmchugh
 */
public class Table extends Goliath.Object
        implements Goliath.Interfaces.Data.ITable
{
    // TODO: Add table versioning in order to allow updating from one table version to another.
    // TODO: Implement loading this structure from existing tables in a data source
    
    private String m_cName;
    private HashTable<String, IColumn> m_oColumns;
    private List<IIndex> m_oIndexes;
    private List<String> m_oKeys;
    private List<IRelation> m_oRelations;
    private Boolean m_lExists;
    private IDataLayerAdapter m_oDataAdapter;
    private SimpleDataObject m_oBaseObject;


    /**
     * Creates a new empty table structure that will use the specified connection string
     * @param tcName the name of the new table
     */
    public Table(String tcName)
    {
        if (Goliath.Utilities.isNullOrEmpty(tcName))
        {
            throw new InvalidParameterException("tcName", tcName);
        }
        m_cName = tcName;
    }

    /**
     * Creates a new table structure from the specified class.
     * @param toClass The class to base the table structure on
     * @param tcName The name of the table
     */
    public <T extends SimpleDataObject<T>> Table(Class<T> toClass, IDataLayerAdapter toAdapter)
    {
        T loBaseObject = null;
        try
        {
            loBaseObject = toClass.newInstance();
        }
        catch (Throwable ex)
        {
            throw new UnsupportedOperationException(ex);
        }

        m_oDataAdapter = toAdapter;
        m_cName = m_oDataAdapter.getDataObjectSourceName(toClass);
        m_oBaseObject = loBaseObject;
    }

    /**
     * Gets the name of this table
     * @return the name of the table
     */
    @Override
    public String getName()
    {
        return m_cName;
    }


    /**
     * Initialises this table structure from the specified object
     * @param <T>
     * @param toObject The object to initialise from
     */
    private <T extends SimpleDataObject<T>> void initialiseFromObject(T toObject)
    {
        // First get the list of available columns from the object;
        createColumnsFromObject(toObject);

        // Add the primary key columns
        // At the moment each table can only have a single primary key
        List<String> loKeyColumns = m_oDataAdapter.getKeyColumns(toObject.getClass());
        for (String lcKey : loKeyColumns)
        {
            this.addKey(getColumn(lcKey));
        }
        
        // Add any foreign key relations
        createRelationsFromObject(toObject);

        // Add any indexes that have been specified for this object
        createIndexesFromObject(toObject);
    }

    /**
     * Checks if the table exists in the data source specified in the connection string
     * @return true if the table exists, false if not.
     */
    @Override
    public boolean exists()
    {
        if (m_lExists == null)
        {
            m_lExists = m_oDataAdapter.exists(this);
        }
        return m_lExists;
    }


    /**
     * Creates the table in the data source specified by the connection string
     * This will always check exist first, if the table already exists, this method will
     * return false as the table was not created by this method
     * @return true if the table was created, false if not
     */
    @Override
    public boolean create()
    {
        if (!exists())
        {
            if (m_oBaseObject != null)
            {
                initialiseFromObject(m_oBaseObject);
            }
            m_lExists = m_oDataAdapter.create(this);
            return m_lExists;
        }
        return false;
    }

    /**
     * Adds a column to this table structure
     * @param toColumn the column to add
     * @return true if the column was actually added
     */
    @Override
    public boolean addColumn(IColumn toColumn)
    {
        if (m_oColumns == null)
        {
            m_oColumns = new HashTable<String, IColumn>();
        }

        if (!hasColumn(toColumn))
        {
            if (toColumn.getTable() != this)
            {
                throw new InvalidParameterException("Column being added must belong to the table", "toColumn", toColumn);
            }
            return m_oColumns.put(toColumn.getName().toLowerCase(), toColumn) == null;
        }
        return false;
    }

    /**
     * Checks if a column already exists within this
     * @param toColumn
     * @return
     */
    @Override
    public boolean hasColumn(IColumn toColumn)
    {
        return m_oColumns != null && m_oColumns.containsKey(toColumn.getName().toLowerCase());
    }

    /**
     * Removes the specified column from the table structure
     * @param toColumn the column to remove
     * @return true if the column was removed, false if the column never existed
     */
    @Override
    public boolean removeColumn(IColumn toColumn)
    {
        if (hasColumn(toColumn))
        {
            return m_oColumns.remove(toColumn.getName().toLowerCase()) != null;
        }
        return false;
    }

    @Override
    public List<IColumn> getColumns()
    {
        return new List<IColumn>(m_oColumns.values());
    }

    /**
     * Gets the column specified, this is not case sensitive
     * @param tcName the name of the column to get.
     * @return the column with the name tcName or null if the column does not exist
     */
    @Override
    public final IColumn getColumn(String tcName)
    {
        if (m_oColumns != null && m_oColumns.containsKey(tcName.toLowerCase()))
        {
            return m_oColumns.get(tcName.toLowerCase());
        }
        return null;
    }

    /**
     * Adds the specified key column to the table
     * @param toKeyColumn the column that is to be used as the key column
     * @return true if the key was added
     * @throws DataException if the column does not exist within this table
     */
    @Override
    public boolean addKey(IColumn toKeyColumn) throws DataException
    {
        if (m_oKeys == null)
        {
            m_oKeys = new List<String>();
        }
        
        if (!hasKey(toKeyColumn))
        {
            if (!hasColumn(toKeyColumn))
            {
                throw new DataException("Column " + toKeyColumn.getName() + " does not exist in table " + this.getName());
            }
            return m_oKeys.add(toKeyColumn.getName().toLowerCase());
        }
        return false;
    }

    /**
     * Checks if the key column already exists in the table
     * @param toKeyColumn the key column to check for
     * @return true if it already exists, false otherwise
     */
    @Override
    public boolean hasKey(IColumn toKeyColumn)
    {
        return m_oKeys != null && m_oKeys.contains(toKeyColumn.getName().toLowerCase());
    }

    /**
     * Removes the specified key column as a key, this does not remove the column from the table
     * @param toKeyColumn the key column to remove
     * @return true if the column was removed, false if the column was not a key
     */
    @Override
    public boolean removeKey(IColumn toKeyColumn)
    {
        if (hasKey(toKeyColumn))
        {
            return m_oKeys.remove(toKeyColumn.getName().toLowerCase());
        }
        return false;
    }

    /**
     * Gets the list of columns that are key columns in this table
     * @return the list of key columns, if there are no key columns then an empty list
     */
    @Override
    public List<IColumn> getKeyColumns()
    {
        List<IColumn> loReturn = new List<IColumn>(m_oKeys != null ? m_oKeys.size() : 0);
        if (m_oKeys != null)
        {
            for (String lcColumn : m_oKeys)
            {
                loReturn.add(getColumn(lcColumn));
            }
        }
        return loReturn;
    }

    /**
     * Adds the specified index to the table, this does not make changes to any columns
     * @param toIndex the Index to add
     * @return true if it was added, false if it already existed
     * @throws DataException if the column specified in the index does not exist in the table
     */
    @Override
    public boolean addIndex(IIndex toIndex) throws DataException
    {
        if (m_oIndexes == null)
        {
            m_oIndexes = new List<IIndex>();
        }
        if (!hasIndex(toIndex))
        {
            // Indexes can be multi column so we need to check all of the columns
            for (IColumn loColumn : toIndex.getColumns())
            {
                if (!hasColumn(loColumn))
                {
                    throw new DataException("Column " + loColumn.getName() + " does not exist in table " + this.getName());
                }
            }
            return m_oIndexes.add(toIndex);
        }
        return false;
    }

    /**
     * Checks if the specified index exists in the table
     * @param toIndex the index to check
     * @return true if it exists, false otherwise
     */
    @Override
    public boolean hasIndex(IIndex toIndex)
    {
        return m_oIndexes != null && m_oIndexes.contains(toIndex);
    }

    /**
     * Removes the specified index from the table, does not make changes to columns
     * @param toIndex the index to remove
     * @return true if it was removed, false if it did not exist
     */
    @Override
    public boolean removeIndex(IIndex toIndex)
    {
        if (hasIndex(toIndex))
        {
            return m_oIndexes.remove(toIndex);
        }
        return false;
    }

    /**
     * Gets the list of indexes for this table
     * @return the list of indexes or an empty list if there are none
     */
    @Override
    public List<IIndex> getIndexes()
    {
        List<IIndex> loReturn = new List<IIndex>(m_oIndexes != null ? m_oIndexes.size() : 0);
        if (m_oIndexes != null)
        {
            for (IIndex loIndex : m_oIndexes)
            {
                loReturn.add(loIndex);
            }
        }
        return loReturn;
    }

    /**
     * Adds the specified relation to the table
     * @param toRelation the relation to add
     * @return true if the relation was added, false if it already existed
     * @throws DataException if the colum for the relation does not exist on this table
     */
    @Override
    public boolean addRelation(IRelation toRelation) throws DataException
    {
        if (m_oRelations == null)
        {
            m_oRelations = new List<IRelation>();
        }
        if (!hasRelation(toRelation))
        {
            if (!hasColumn(toRelation.getColumn()))
            {
                throw new DataException("Column " + toRelation.getColumn().getName() + " does not exist in table " + this.getName());
            }
            return m_oRelations.add(toRelation);
        }
        return false;
    }

    /**
     * Checks if this relation already exists in the table
     * @param toRelation the relation to check
     * @return
     */
    @Override
    public boolean hasRelation(IRelation toRelation)
    {
        return m_oRelations != null && m_oRelations.contains(toRelation);
    }

    /**
     * Removes the specified relation from the table, this does not remove any columns
     * @param toRelation the relation to remove
     * @return true if the relation was removed, false if it did not exist
     */
    @Override
    public boolean removeRelation(IRelation toRelation)
    {
        if (hasRelation(toRelation))
        {
            return m_oRelations.remove(toRelation);
        }
        return false;
    }

    /**
     * Gets the list of relations for this table
     * @return the list of relations or an empty list if there are none
     */
    @Override
    public List<IRelation> getRelations()
    {
        List<IRelation> loReturn = new List<IRelation>((m_oRelations != null) ? m_oRelations.size() : 0);
        if (m_oRelations != null)
        {
            for (IRelation loRelation : m_oRelations)
            {
                loReturn.add(loRelation);
            }
        }
        return loReturn;
    }


    /**
     * Adds all of the indexes that have been specified in the object
     * @param <T>
     * @param toObject the object to get the indexes from
     */
    private <T extends SimpleDataObject<T>> void createIndexesFromObject(T toObject)
    {
        Class<T> loClass = (Class<T>)toObject.getClass();

        List<MethodDefinition> loIndexMethods = Goliath.DynamicCode.Java.getMethodDefinitions(loClass, Goliath.Annotations.UniqueIndex.class);

        for (MethodDefinition loMethod : loIndexMethods)
        {
            String lcMethodName = loMethod.getName();

            // If we do not use the GUID, then we shouldn't be indexing it
            if (lcMethodName.equalsIgnoreCase("GUID") && !toObject.hasGUID())
            {
                continue;
            }
            addIndex(new Index(IndexType.UNIQUE(), this, lcMethodName));
        }
    }

    /**
     * Adds all of the foreign keys that have been specified in the object
     * @param <T>
     * @param toObject the object to use to create the foreign keys from
     */
    private <T extends SimpleDataObject<T>> void createRelationsFromObject(T toObject)
    {
        Class<T> loClass = (Class<T>)toObject.getClass();
        Class loForeignClass;
        String lcKeyName;
        
        // Add in the foreign keys, foreign keys are either marked as foreign key, extend from simple data object, or extend from DynamicEnum
        List<String> loProperties = Java.getPropertyMethods(loClass);
        for (String lcMethod : loProperties)
        {
            MethodDefinition loMethod = Java.getMethodDefinition(loClass, lcMethod);
            ForeignKey loFK = loMethod.getAnnotation(ForeignKey.class);
            Type loReturn = loMethod.getReturnType();
            
            if (loFK != null)
            {
                loForeignClass = loFK.className();
                lcKeyName = loFK.fieldName();
                // Check that the Field actually exists in the foreign class
                if (Goliath.DynamicCode.Java.getMethodDefinition(loForeignClass, "get" + lcKeyName) == null)
                {
                    throw new UnsupportedOperationException("Could not determine the correct field for " + lcKeyName + " on the class " + loForeignClass.getName() + " when trying to create a foreign key from " + loClass.getName() + " " + loMethod.getName());
                }
            }
            else if (Java.isEqualOrAssignable(SimpleDataObject.class, (Class)loReturn) ||
                    Java.isEqualOrAssignable(DynamicEnum.class, (Class)loReturn))
            {
                lcKeyName = "ID";
                loForeignClass = Java.isEqualOrAssignable(DynamicEnum.class, (Class)loReturn) ? EnumerationDefinition.class :
                        (Class)loReturn;
            }
            else
            {
                // Not a foreign key
                continue;
            }
            
            
            // Create the temp table
            Table loTable = (loForeignClass == loClass) 
                    ? this 
                    : new Table(loForeignClass, m_oDataAdapter);
            
            // Check if the table exists, if not, we need to create it
            if (loTable != this && !loTable.exists())
            {
                loTable.create();
            }

            Relation loRelation = new Relation(getColumn(loMethod.getName()),
                    new Column(
                            lcKeyName.equalsIgnoreCase("ID") ?
                                m_oDataAdapter.getIdentityKeyName(loForeignClass) :
                                lcKeyName,
                            loTable,
                            DataType.BIGINT()));
            addRelation(loRelation);
        }
    }


    /**
     * Adds all of the columns from the object to this table structure.
     * @param <T>
     * @param toObject The object to copy the fields from
     */
    private <T extends SimpleDataObject<T>> void createColumnsFromObject(T toObject)
    {
        Class<T> loClass = (Class<T>)toObject.getClass();

        // Each of the properties in the class becomes a field in the table
        List<String> loProperties = Goliath.DynamicCode.Java.getPropertyMethods(loClass);
        for (String lcField : loProperties)
        {
            // Check for the GUID field if the object is not marked as using GUID, we will ignore
            if (lcField.equalsIgnoreCase("GUID") && !toObject.hasGUID())
            {
                continue;
            }

            // If we are working with a DataObject, then we can also check for the 5 fields that it adds
            if (Goliath.DynamicCode.Java.isEqualOrAssignable(DataObject.class, loClass))
            {
                DataObject loDO = (DataObject)toObject;
                if ((lcField.equalsIgnoreCase("createdBy") && !loDO.hasCreatedBy())
                        || (lcField.equalsIgnoreCase("createdDate") && !loDO.hasCreatedDate())
                        || (lcField.equalsIgnoreCase("modifiedBy") && !loDO.hasModifiedBy())
                        || (lcField.equalsIgnoreCase("modifiedDate") && !loDO.hasModifiedDate())
                        || (lcField.equalsIgnoreCase("rowVersion") && !loDO.hasRowVersion()))
                {
                    continue;
                }
            }

            // We must have both get and set methods available for this to be a valid field.
            Method loGetMethod = null;
            Method loSetMethod = null;

            MethodDefinition loMethod = Goliath.DynamicCode.Java.getMethodDefinition(loClass, lcField);
            if (loMethod == null)
            {
                throw new UnsupportedOperationException("The field " + lcField + " was listed as a property on the class " + loClass.getName() + " but does not exist.");
            }
            else
            {
                loGetMethod = loMethod.getAccessor();
                loSetMethod = loMethod.getMutator();
                
                if (loGetMethod == null || loSetMethod == null)
                {
                    throw new UnsupportedOperationException("Either a mutator or an accessor does not exist on the class " + loClass.getName() + " for the property " + lcField);
                }
            }

            // Get the value from the maximum length annotation if there is one
            long lnMaxLength = -1;
            Goliath.Annotations.MaximumLength loMaxLengthAnnotation =
                    (loGetMethod.isAnnotationPresent(Goliath.Annotations.MaximumLength.class) ?
                        loGetMethod.getAnnotation(Goliath.Annotations.MaximumLength.class) :
                            loSetMethod.getAnnotation(Goliath.Annotations.MaximumLength.class));

            lnMaxLength = (loMaxLengthAnnotation != null) ? loMaxLengthAnnotation.length() : 1000;

            // Get the return type of the method so we know the type of the field
            IDataType loDataType = null;
            try
            {
                loDataType = DataType.createFromClass(loGetMethod.getReturnType(), lnMaxLength);
            }
            catch(Throwable ex)
            {
                throw new UnsupportedOperationException("Could not create field " + lcField + " from class " + loClass, ex);
            }

            // Get the auto increment value if there is one
            // For the moment, the auto increment is only allowed on the key field, of which there is only one
            boolean llAutoIncrement = lcField.equalsIgnoreCase("ID");

            // The key field is not called ID in the database, so convert it here before creating the column
            if (llAutoIncrement)
            {
                lcField = m_oDataAdapter.getIdentityKeyName(loClass);
            }

            // Now create the actual column
            Column loColumn = new Column((lcField), this, loDataType);

            // Only allow null on columns where the data type is a primitive type
            loColumn.setAllowNull(!loGetMethod.getReturnType().isPrimitive() &&
                    (loSetMethod.getAnnotation(NoNulls.class) == null && loGetMethod.getAnnotation(NoNulls.class) == null));
            loColumn.setAutoIncrement(llAutoIncrement);

            addColumn(loColumn);
        }
    }

    
    /**
     * Helper function for adding the created by column
     * @return true if the column was added
     */
    @Override
    public boolean addCreatedByColumn()
    {
        return addColumn(new Column("CreatedBy", this, DataType.VARCHAR(40)));
    }

    /**
     * Helper function for adding the created date column
     * @return true if the column was added
     */
    @Override
    public boolean addCreatedDateColumn()
    {
        return addColumn(new Column("CreatedDate", this, DataType.DATETIME()));
    }

    /**
     * Helper function for adding the guid column
     * @return true if the column was added
     */
    @Override
    public boolean addGUIDColumn()
    {
        return addColumn(new Column("GUID", this, DataType.VARCHAR(40)));
    }

    /**
     * Helper function for adding the modified by column
     * @return true if the column was added
     */
    @Override
    public boolean addModifiedByColumn()
    {
        return addColumn(new Column("ModifiedBy", this, DataType.VARCHAR(40)));
    }

    /**
     * Helper function for adding the modified date column
     * @return true if the column was added
     */
    @Override
    public boolean addModifiedDateColumn()
    {
        return addColumn(new Column("ModifiedDate", this, DataType.DATETIME()));
    }

    /**
     * Helper function for adding the row version column
     * @return true if the column was added
     */
    @Override
    public boolean addRowVersionColumn()
    {
        return addColumn(new Column("RowVersion", this, DataType.DATETIME()));
    }
    
}
