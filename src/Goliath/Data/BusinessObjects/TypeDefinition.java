/* =========================================================
 * TypeDefinition.java
 *
 * Author:      kenmchugh
 * Created:     Oct 5, 2010, 2:45:33 PM
 *
 * Description
 * --------------------------------------------------------
 * Type definition is the declaration of a dynamic type
 * this contains the entire definition for the type contained
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.BusinessObjects;

import Goliath.Collections.List;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Constants.CacheType;
import Goliath.Constants.StringFormatType;
import Goliath.Data.DataObjects.ANSIDataType;
import Goliath.Data.DataObjects.EnumerationDefinition;
import Goliath.Data.DataObjects.TypeComposition;
import Goliath.Data.Query.InList;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.DataObjectNotFoundException;
import Goliath.Exceptions.ObjectNotCreatedException;

/**
 * Type definition is the class that contains the definition for dynamic types.
 * @author kenmchugh
 */
public class TypeDefinition extends BusinessObject<Goliath.Data.DataObjects.TypeDefinition>
{
    /**
     * Helper function to get the type definition for the class specified, if it doesn't already
     * exist, this will create it
     * @param <K> the type of the class
     * @param toClass the class to get the definition for
     * @return the type definition for this class
     */
    public static <K extends DynamicDataObject> TypeDefinition getTypeDefinitionFromClass(Class<K> toClass)
    {
        // Get a hash of the class name
        String lcHash = Goliath.Data.DataObjects.TypeDefinition.createHash(toClass);

        // First attempt to load from the data source
        TypeDefinition loDef = null;
        try
        {
            loDef = TypeDefinition.getObjectByGUID(TypeDefinition.class, lcHash, false);
        }
        catch (DataObjectNotFoundException toException)
        {
            // Do nothing here
        }

        if (loDef == null)
        {
            // The type definition did not exist, so create it
            loDef = createTypeDefinitionFromClass(toClass);
        }
        
        return loDef;
    }

    /**
     * Creates the type definition for the class specified
     * @param <K> the type of the class to create the type definition for
     * @param toClass the class to create the type definition for
     * @return the new type definition
     */
    public static <K extends DynamicDataObject> TypeDefinition createTypeDefinitionFromClass(Class<K> toClass)
    {
        // Get a hash of the class name
        Goliath.Data.DataObjects.TypeDefinition loBaseDefinition = Goliath.Data.DataObjects.TypeDefinition.getTypeDefinitionFromClass(toClass);
        if (loBaseDefinition != null)
        {
            TypeDefinition loDef = new TypeDefinition(Goliath.Data.DataObjects.TypeDefinition.getTypeDefinitionFromClass(toClass));
        
            // Loop through all of the properties on the object and save them as a Type composition
            for (String lcProperty : Java.getPropertyMethods(toClass))
            {
                TypeComposition loComposition = TypeComposition.getTypeCompositionFromClass(toClass, lcProperty);
                if (loComposition != null)
                {
                    loDef.addProperty(loComposition);
                }
            }

            // Set up the rest of the object

            if (!loDef.save())
            {
                // TDDO: Throw the list of reasons why the data object could not be saved
                throw new ObjectNotCreatedException("Could not save the data object");
            }
            return loDef;
        }
        throw new ObjectNotCreatedException("Could not save the data object");
    }

    
    // TODO: Cache type on this should be application cache

    private TypeDefinition m_oInheritsFrom;
    private TypeDefinition m_oContainsType;

    private SimpleDataObjectCollection<TypeComposition> m_oPropertyDefinitions;
    private SimpleDataObjectCollection<EnumerationDefinition> m_oEnumerationDefinitions;

    /**
     * Creates a new instance of type definition
     */
    public TypeDefinition()
            throws InstantiationException, IllegalAccessException
    {
        super(Goliath.Data.DataObjects.TypeDefinition.class);
    }

    /**
     * Creates the type definition object loading the definition with the
     * matching GUID, if the GUID does not exist, then creates a new object
     * @param tcGUID the GUID of the definition to load
     */
    public TypeDefinition(String tcGUID)
    {
        super(Goliath.Data.DataObjects.TypeDefinition.class, tcGUID);
    }

    /**
     * Creates the type definition object loading the definition using the supplied TypeDefinition
     * data object
     * @param toDefinition the definition data object
     */
    public TypeDefinition(Goliath.Data.DataObjects.TypeDefinition toDefinition)
    {
        super(toDefinition);
    }
    
    /**
     * Sets the cache type of this data object
     * @return the cache type
     */
    @Override
    @Goliath.Annotations.NotProperty
    public CacheType getCacheType()
    {
        return CacheType.APPLICATION();
    }

    /**
     * Gets the name of this object
     * @return the name of the object
     */
    @Goliath.Annotations.MaximumLength(length=150)
    public String getName()
    {
        return getPrimaryObject().getName();
    }

    /**
     * Sets the name of this object
     * @param tcName the new name of the object
     */
    public void setName(String tcName)
    {
        getPrimaryObject().setName(tcName);
    }

    /**
     * Gets the description of this object
     * @return the description of the object
     */
    @Goliath.Annotations.MaximumLength(length=500)
    public String getDescription()
    {
        return getPrimaryObject().getDescription();
    }

    /**
     * Sets the description of this object
     * @param tcDescription the new description of the object
     */
    public void setDescription(String tcDescription)
    {
        getPrimaryObject().setDescription(tcDescription);
    }

    /**
     * Gets the Ansi data type of this property, the ansi data type may be null if
     * this is not a primitive object
     * @return the ansi data type of this type
     */
    public ANSIDataType getANSIDataType()
    {
        return getPrimaryObject().getANSIDataType();
    }

    /**
     * Sets the ansi data type of this type
     * @param toType the ansi data type
     */
    public void setANSIDataType(ANSIDataType toType)
    {
        getPrimaryObject().setANSIDataType(toType);
    }

    /**
     * Gets the type that this type inherits from, only singly inheritance is allowed
     * @return the type this type inherits from
     */
    public TypeDefinition getInheritsFrom()
    {
        if (m_oInheritsFrom == null)
        {
            Long lnType = getPrimaryObject().getInheritsFromID();
            if (lnType != null)
            {
                m_oInheritsFrom = TypeDefinition.getObjectByID(TypeDefinition.class, Goliath.Data.DataObjects.TypeDefinition.class, lnType);
            }
        }
        return m_oInheritsFrom;
    }

    /**
     * Sets the inheritance of this type
     * @param toDefinition the type this type should inherit from
     */
    public void setInheritsFrom(TypeDefinition toDefinition)
    {
        getPrimaryObject().setInheritsFrom(toDefinition.getPrimaryObject());
        m_oInheritsFrom = null;
    }

    /**
     * If this is a list type, then this represents the type that is contained in the list
     * @return the type contained in the list
     */
    public TypeDefinition getContainsType()
    {
        if (m_oContainsType == null)
        {
            Long lnTypeID = getPrimaryObject().getContainsTypeID();
            if (lnTypeID != null)
            {
                m_oContainsType = TypeDefinition.getObjectByID(TypeDefinition.class, Goliath.Data.DataObjects.TypeDefinition.class, lnTypeID);
            }
        }
        return m_oContainsType;
    }

    /**
     * Sets the type contained within the list for list types
     * @param toDefinition the type contained within the list type
     */
    public void setContainsType(TypeDefinition toDefinition)
    {
        getPrimaryObject().setContainsType(toDefinition.getPrimaryObject());
        m_oContainsType = null;
    }

    /**
     * Gets the list of TypeCompositions that make up this type, this is basically the
     * entire list of properties available to object of this type
     * @return the list of property definitions
     */
    public List<TypeComposition> getProperties()
    {
        if (m_oPropertyDefinitions == null)
        {
            m_oPropertyDefinitions = new SimpleDataObjectCollection<TypeComposition>(TypeComposition.class);
            m_oPropertyDefinitions.loadList(new InList<Long, Long>("ParentTypeID", new Long[]{getID()}));
        }
        return new List(m_oPropertyDefinitions);
    }

    /**
     * Adds a property to the type definition
     * @param toTypeComposition
     */
    public void addProperty(TypeComposition toTypeComposition)
    {
        if (toTypeComposition == null)
        {
            return;
        }
        if (m_oPropertyDefinitions == null)
        {
            getProperties();
        }
        m_oPropertyDefinitions.add(toTypeComposition);
    }


    /**
     * If the property is only allowed specific values, then this will get that list
     * of values
     * @return the list of values that classes of this type are allowed to have
     */
    public List<EnumerationDefinition> getEnumerations()
    {
        if (m_oEnumerationDefinitions == null)
        {
            m_oEnumerationDefinitions = new SimpleDataObjectCollection<EnumerationDefinition>(EnumerationDefinition.class);
            m_oEnumerationDefinitions.loadList(new InList<Long, Long>("TypeDefinitionID", new Long[]{getID()}));
        }
        return new List(m_oEnumerationDefinitions);
    }

    /**
     * Adds an enumeration to the type definition
     * @param toEnumeration
     */
    public void addEnumeration(EnumerationDefinition toEnumeration)
    {
        if (toEnumeration == null)
        {
            return;
        }
        if (m_oEnumerationDefinitions == null)
        {
            getEnumerations();
        }
        m_oEnumerationDefinitions.add(toEnumeration);
    }


    @Override
    protected boolean onIsModified()
    {
        return getPrimaryObject().isModified() ||
                (m_oPropertyDefinitions != null && m_oPropertyDefinitions.isModified()) ||
                (m_oEnumerationDefinitions != null && m_oEnumerationDefinitions.isModified());
    }




    @Override
    protected void onSave() throws Goliath.Exceptions.Exception
    {
        // Check if we are deleting or adding/updating this object
        if (isDeleted())
        {
            // We need to delete the object here

            // Delete the type compositions
            if (m_oPropertyDefinitions != null && m_oPropertyDefinitions.size() > 0)
            {
                m_oPropertyDefinitions.delete();
                m_oPropertyDefinitions.save();
            }

            // Delete the enumeration definitions
            if (m_oEnumerationDefinitions != null && m_oEnumerationDefinitions.size() > 0)
            {
                m_oEnumerationDefinitions.delete();
                m_oEnumerationDefinitions.save();
            }

            // Delete the TypeDefinition
            getPrimaryObject().save();

            // Leave the AnsiDataType
        }
        else
        {
            // We are either updating or adding the business object

            // Need to save the TypeDefinition
            getPrimaryObject().save();

            // Then save any properties
            if (m_oPropertyDefinitions != null && m_oPropertyDefinitions.size() > 0)
            {
                m_oPropertyDefinitions.save();
            }

            // Need to Save any Enumerations
            if (m_oEnumerationDefinitions != null && m_oEnumerationDefinitions.size() > 0)
            {
                m_oEnumerationDefinitions.save();
            }
        }
    }
    
    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return getName();
    }

    @Override
    protected BusinessObject<Goliath.Data.DataObjects.TypeDefinition> onCopy()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onDelete()
    {
        throw new UnsupportedOperationException("Not supported yet.");
        
    }

}
