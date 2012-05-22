/* =========================================================
 * TypeDefinition.java
 *
 * Author:      Ken McHugh
 * Created:     Feb 19, 2008, 10:21:32 PM
 *
 * Description
 * --------------------------------------------------------
 * TypeDefinition is the base of the dyncamic typing system
 * This defines all of the types that are available in the sytem
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Constants.CacheType;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.ObjectNotCreatedException;
import java.lang.reflect.Type;

/**
 *
 * @author kmchugh
 */
public class TypeDefinition extends UndoableDataObject<TypeDefinition>
{
    /**
     * Gets a hash that can be used as the GUID for this type
     * @param toClass the class to get the hash for
     * @return the hash for the class
     */
    public static String createHash(Type toClass)
    {
        Class loClass = Java.getClassFromType(toClass);
        Class loContained = Java.getParameterizedClassFromType(toClass);
        
        String lcPackage = loClass.getPackage() == null ? "none" : loClass.getPackage().getName();
        
        String lcHashString = lcPackage + loClass.getName();
        // If this is a list class, we need to also include the contained type
        if (loContained != null)
        {
            lcHashString += loContained.getPackage().getName() + loContained.getName();
        }
        return Goliath.Utilities.encryptMD5(lcHashString);
    }
    
    /**
     * Gets the Type definition representing the specified class, if it does not exist, it is created here
     * @param toClass the class to get the type definition for
     * @return the type definition
     */
    public static TypeDefinition getTypeDefinitionFromClass(Type toClass)
    {
        // Get a hash of the class name
        String lcHash = createHash(toClass);

        // First attempt to load from the data source
        TypeDefinition loDef = TypeDefinition.getObjectByGUID(TypeDefinition.class, lcHash);
        if (loDef == null)
        {
            loDef = createTypeDefinitionFromClass(toClass);
        }
        return loDef;
    }
    
    /**
     * Creates the Type definition from the specified class
     * @param toClass the class to create the type definition from
     * @return the new type definiton, or null if it could not be saved
     */
    public static TypeDefinition createTypeDefinitionFromClass(Type toClass)
    {
        // Create a hash for the class
        String lcHash = createHash(toClass);
        TypeDefinition loType = new TypeDefinition();
        loType.setGUID(lcHash);
        
        Class loClass = Java.getClassFromType(toClass);
        Class loContainedClass = Java.getParameterizedClassFromType(toClass);
        
        loType.setName(loClass.getName() + (loContainedClass != null ? "<" + loContainedClass.getName() + ">" : ""));
        loType.setDescription("Dynamic data type definition for the object type " + loClass.getSimpleName());
        loType.setSystem(true);

        // If the class is a primitive, use or create an ansi data type
        if (Java.isPrimitive(loClass))
        {
            loType.setANSIDataType(ANSIDataType.getFromClass(loClass));
        }

        loType.setArrayType(loContainedClass != null || loClass.isArray());
        if (loType.getArrayType())
        {
            if (loClass.isArray())
            {
                loType.setContainsType(getTypeDefinitionFromClass(loClass.getComponentType()));
            }
            else
            {
                loType.setContainsType(getTypeDefinitionFromClass(loContainedClass));
            }
        }
        
        if (loType.save())
        {
            return loType;
        }
        throw new ObjectNotCreatedException("Could not create the Type Definition for " + loClass.getName());
    }
    
    
    
    
    

    public static Class getClassFromTypeDefinition(TypeDefinition toType)
    {
        return Class.class;
    }

    


    // TODO: Implement rule - System type protection
    // TODO: Implement rule - ArrayType true or has an enumeration defined then ContainsType required
    // TODO: Implement rule - ArrayType false, then ContainsType must be null
    // TODO: Implement rule = InheritsFrom not null, then ANSIDataType must be null

    
    private String m_cName;
    private String m_cDescription;
    private Long m_nInheritsFromID;
    private boolean m_lSystem;
    private Long m_nAnsiDataTypeID;
    private boolean m_lArrayType;
    private Long m_nContainsTypeID;

    private ANSIDataType m_oAnsiDataType;
    private TypeDefinition m_oInheritsFrom;
    private TypeDefinition m_oContainsType;
    
    public TypeDefinition()
    {
    }
    
    @Override
    public boolean hasGUID()
    {
        return true;
    }
    
    /**
     * Lets the data manager know what caching method to use for this class
     */
    @Override
    @Goliath.Annotations.NotProperty
    public CacheType getCacheType()
    {
        return CacheType.APPLICATION();
    }


    /**
     * Determines if this is a system TypeDefintion or not
     * @return true if this is a system definition
     */
    public boolean getSystem()
    {
        canReadProperty();
        return m_lSystem;
    }

    /**
     * Sets if this type is a system definition or not
     * @param tnValue true if this will become a system definition, false otherwise.
     */
    public void setSystem(boolean tnValue)
    {
        // TODO: implement system variable protection
        canWriteProperty();
        if (isDifferent(m_lSystem, tnValue))
        {
            m_lSystem = tnValue;
            propertyHasChanged();
        }
    }

    /**
     * Determines if this object is an array type or not.  An array type contains
     * object of type ContainsType.
     * @return true if this is an array type, false otherwise
     */
    public boolean getArrayType()
    {
        canReadProperty();
        return m_lArrayType;
    }

    /**
     * Sets if this object is an array type or not
     * @param tnValue true if it is, false otherwise
     */
    public void setArrayType(boolean tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_lArrayType, tnValue))
        {
            m_lArrayType = tnValue;
            propertyHasChanged();
        }
    }

    public String getName()
    {
        canReadProperty();
        return m_cName;
    }

    @Goliath.Annotations.UniqueIndex
    @Goliath.Annotations.MaximumLength(length=150)
    @Goliath.Annotations.NoNulls
    public void setName(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(tcValue, m_cName))
        {
            m_cName = tcValue;
            propertyHasChanged();
        }
    }

    public String getDescription()
    {
        canReadProperty();
        return m_cDescription;
    }

    @Goliath.Annotations.MaximumLength(length=500)
    public void setDescription(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cDescription, tcValue))
        {
            m_cDescription = tcValue;
            propertyHasChanged();
        }
    }

    public Long getInheritsFromID()
    {
        canReadProperty();
        if (m_oInheritsFrom != null)
        {
            return m_oInheritsFrom.getID();
        }
        return m_nInheritsFromID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setInheritsFromID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nInheritsFromID, tnValue))
        {
            m_nInheritsFromID = tnValue;
            if (m_oInheritsFrom != null && m_oInheritsFrom.getID() != tnValue)
            {
                m_oInheritsFrom = null;
            }
            propertyHasChanged();
        }
    }
    
    public Long getContainsTypeID()
    {
        canReadProperty();
        if (m_oContainsType != null)
        {
            return m_oContainsType.getID();
        }
        return m_nContainsTypeID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setContainsTypeID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nContainsTypeID, tnValue))
        {
            m_nContainsTypeID = tnValue;
            if (m_oContainsType != null && m_oContainsType.getID() != tnValue)
            {
                m_oContainsType = null;
            }
            propertyHasChanged();
        }
    }

    public Long getANSIDataTypeID()
    {
        canReadProperty();
        if (m_oAnsiDataType != null)
        {
            return m_oAnsiDataType.getID();
        }
        return m_nAnsiDataTypeID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.ANSIDataType.class, fieldName="ID")
    public void setANSIDataTypeID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nAnsiDataTypeID, tnValue))
        {
            m_nAnsiDataTypeID = tnValue;
            if (m_oAnsiDataType != null && m_oAnsiDataType.getID() != tnValue)
            {
                m_oAnsiDataType = null;
            }
            propertyHasChanged();
        }
    }

    @Goliath.Annotations.NotProperty
    public ANSIDataType getANSIDataType()
    {
        if (m_oAnsiDataType == null)
        {
            m_oAnsiDataType = lazyLoad(ANSIDataType.class, m_nAnsiDataTypeID);
        }
        return m_oAnsiDataType;
    }

    @Goliath.Annotations.NotProperty
    public void setANSIDataType(ANSIDataType toType)
    {
        if (isDifferent(m_oAnsiDataType, toType))
        {
            m_oAnsiDataType = toType;
            setANSIDataTypeID((m_oAnsiDataType != null) ? m_oAnsiDataType.getID() : null);
        }
    }

    @Goliath.Annotations.NotProperty
    public TypeDefinition getInheritsFrom()
    {
        if (m_oInheritsFrom == null)
        {
            m_oInheritsFrom = lazyLoad(TypeDefinition.class, m_nInheritsFromID);
        }
        return m_oInheritsFrom;
    }

    @Goliath.Annotations.NotProperty
    public void setInheritsFrom(TypeDefinition toType)
    {
        if (isDifferent(m_oInheritsFrom, toType))
        {
            m_oInheritsFrom = toType;
            setInheritsFromID((m_oInheritsFrom != null) ? m_oInheritsFrom.getID() : null);
        }
    }

    @Goliath.Annotations.NotProperty
    public TypeDefinition getContainsType()
    {
        if (m_oContainsType == null)
        {
            m_oContainsType = lazyLoad(TypeDefinition.class, m_nContainsTypeID);
        }
        return m_oContainsType;
    }

    @Goliath.Annotations.NotProperty
    public void setContainsType(TypeDefinition toType)
    {
        if (isDifferent(m_oContainsType, toType))
        {
            m_oContainsType = toType;
            setContainsTypeID((m_oContainsType != null) ? m_oContainsType.getID() : null);
        }
    }
}
