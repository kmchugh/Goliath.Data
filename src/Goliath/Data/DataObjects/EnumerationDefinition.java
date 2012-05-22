/* =========================================================
 * EnumerationDefinition.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 12:48:48 PM
 *
 * Description
 * --------------------------------------------------------
 * The EnumerationDefintion class defines what values a type
 * is allowed to have.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.DynamicEnum;
import Goliath.Exceptions.ObjectNotCreatedException;
import Goliath.Interfaces.Data.ISimpleDataObject;


// TODO: Implement this dynamically so that the list of valid values could be based on values from a query for example a list of users

/**
 *
 * @author kenmchugh
 */
public class EnumerationDefinition extends UndoableDataObject<EnumerationDefinition>
{
    /**
     * Gets the GUID that will be used for this object
     * @param toEnum the enumeration to get the hash for
     * @return the hash
     */
    public static String getHashFromObject(DynamicEnum toEnum)
    {
        return Goliath.Utilities.encryptMD5(toEnum.getClass().getName() + toEnum.getValue());
    }
    
    
    /**
     * Loads the dynamic enumeration from the database, or if it does not already exist, creates one
     * @param toEnum the enumeration to load
     * @return the DataObject referencing the Dynamic Enumeration
     */
    public static EnumerationDefinition getEnumerationDefinition(DynamicEnum toEnum)
    {
       EnumerationDefinition loEnum = EnumerationDefinition.getObjectByGUID(EnumerationDefinition.class, getHashFromObject(toEnum));
       if (loEnum == null)
       {
           // Get the type definition for this enumeration
           Class loEnumClass = toEnum.getClass();
           TypeDefinition loTypeDef = TypeDefinition.getTypeDefinitionFromClass(loEnumClass.isMemberClass() ? loEnumClass.getDeclaringClass() : loEnumClass);
           
           // Create the enumeration
           loEnum = new EnumerationDefinition();
           loEnum.setGUID(getHashFromObject(toEnum));
           loEnum.setValue(toEnum.getValue());
           loEnum.setSystem(true);
           loEnum.setTypeDefinition(loTypeDef);
           loEnum.save();
           
           // Throw an error if we could not save this
           if (loEnum.isNew())
           {
               throw new ObjectNotCreatedException("Could not create the Enumeration Definition for " + toEnum.getClass() + "." + toEnum.getValue());
           }
       }
       return loEnum;
    }
    
    // TODO: Implement validation, only one of the value fields can have a value
    
    private long m_nTypeDefinitionID;
    
    private String m_cValueCharacter;
    private Long m_nValueInteger;
    private Float m_nValueFloat;
    private Boolean m_lValueBoolean;
    private Goliath.Date m_dValueDateTime;
    private Long m_nValueObjectID;
    
    private long m_nSequence;
    private boolean m_lSystem;
    private boolean m_lIsDefault;


    private TypeDefinition m_oTypeDefinition;

    // TODO: Possibly implement lazyload get and set methods for ObjectRegistry object

    public EnumerationDefinition()
    {
    }

    /**
     * Helper function to ensure that the value of this item is null
     */
    private void clearValue()
    {
        m_cValueCharacter = null;
        m_nValueInteger = null;
        m_nValueFloat = null;
        m_lValueBoolean = null;
        m_dValueDateTime = null;
        m_nValueObjectID = null;
    }


    /**
     * Sets the value of this Enumeration item.  This will set the correct field for the db storage
     * @param toValue the value that is stored in the enumeration
     */
    @Goliath.Annotations.NotProperty
    public void setValue(java.lang.Object toValue)
    {
        // If we are setting the value to null, we don't know what the type is so just clear
        if (toValue == null)
        {
            Object loValue = getValue();
            if (loValue != null)
            {
                clearValue();
                propertyHasChanged();
            }
            return;
        }
        if (toValue.getClass().equals(java.lang.Boolean.class))
        {
            setValueBoolean((java.lang.Boolean)toValue);
        }
        else if (toValue.getClass().equals(java.lang.Long.class))
        {
            setValueInteger((java.lang.Long)toValue);
        }
        else if (toValue.getClass().equals(java.lang.Double.class))
        {
            setValueFloat(new Float((float)((java.lang.Double)toValue).doubleValue()));
        }
        else if (toValue.getClass().equals(java.lang.Float.class))
        {
            setValueFloat((java.lang.Float)toValue);
        }
        else if (toValue.getClass().equals(java.lang.Integer.class))
        {
            setValueInteger((java.lang.Long)toValue);
        }
        else if (toValue.getClass().equals(Goliath.Date.class))
        {
            setValueDateTime((Goliath.Date)toValue);
        }
        else if (toValue.getClass().equals(java.util.Date.class))
        {
            setValueDateTime(new Goliath.Date((java.util.Date)toValue));
        }
        else if (Goliath.DynamicCode.Java.isEqualOrAssignable(toValue.getClass(), ISimpleDataObject.class))
        {
            setValueObjectID(((ISimpleDataObject)toValue).getID());
        }
        else
        {
            setValueCharacter(toValue.toString());
        }
    }

    /**
     * Gets the value that has been set for this Enumeration definition.
     * If no value has been set then null will be returned
     * @return the value that has been set, or null
     */
    @Goliath.Annotations.NotProperty
    public java.lang.Object getValue()
    {
        if (m_lValueBoolean != null)
        {
            return getValueBoolean();
        }
        if (m_nValueFloat != null)
        {
            return getValueFloat();
        }
        if (m_nValueInteger != null)
        {
            return getValueInteger();
        }
        if (m_dValueDateTime != null)
        {
            return getValueDateTime();
        }
        if (m_cValueCharacter != null)
        {
            return getValueCharacter();
        }
        if (m_nValueObjectID != null)
        {
            return getValueObjectID();
        }
        return null;
    }

    /**
     * Gets the ID value of this enum.  It is recommended that getValue be used instead
     * @return the ID value, if not set, returns null
     */
    public Long getValueObjectID()
    {
        canReadProperty();
        return m_nValueObjectID;
    }

    /**
     * Sets this value as an ID value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.ObjectRegistry.class, fieldName="ID")
    public void setValueObjectID(Long toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_nValueObjectID))
        {
            clearValue();
            m_nValueObjectID = toValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the date time value of this enum.  It is recommended that getValue be used instead
     * @return the date time value, if not set, returns null
     */
    public Goliath.Date getValueDateTime()
    {
        canReadProperty();
        return m_dValueDateTime;
    }

    /**
     * Sets this value as a date time value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    public void setValueDateTime(Goliath.Date toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_dValueDateTime))
        {
            clearValue();
            m_dValueDateTime = toValue;
            propertyHasChanged();

        }
    }

    /**
     * Gets the boolean value of this enum.  It is recommended that getValue be used instead
     * @return the boolean value, if not set, returns null
     */
    public Boolean getValueBoolean()
    {
        canReadProperty();
        return m_lValueBoolean;
    }


    /**
     * Sets this value as a boolean value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    public void setValueBoolean(Boolean toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_lValueBoolean))
        {
            clearValue();
            m_lValueBoolean = toValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the float value of this enum.  It is recommended that getValue be used instead
     * @return the float value, if not set, returns null
     */
    public Float getValueFloat()
    {
        canReadProperty();
        return m_nValueFloat;
    }


    /**
     * Sets this value as a float value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    public void setValueFloat(Float toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_nValueFloat))
        {
            clearValue();
            m_nValueFloat = toValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the Integer value of this enum.  It is recommended that getValue be used instead
     * @return the float value, if not set, returns null
     */
    public Long getValueInteger()
    {
        canReadProperty();
        return m_nValueInteger;
    }

    /**
     * Sets this value as a integer value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    public void setValueInteger(Long toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_nValueInteger))
        {
            clearValue();
            m_nValueInteger = toValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the Integer value of this enum.  It is recommended that getValue be used instead
     * @return the float value, if not set, returns null
     */
    public String getValueCharacter()
    {
        canReadProperty();
        return m_cValueCharacter;
    }


    /**
     * Sets this value as a integer value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    @Goliath.Annotations.MaximumLength(length=2500)
    public void setValueCharacter(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cValueCharacter, tcValue))
        {
            clearValue();
            m_cValueCharacter = tcValue;
            propertyHasChanged();
        }
    }


    /**
     * Determines if this is a default value or not.
     * An example of default value use would be when the lookup types are
     * displayed to the user in a dropdown list.  The expectation would be that
     * the default value would be the selected value when the list was first
     * displayed
     * @return true if this is a default value, false otherwise
     */
    public boolean getIsDefault()
    {
        canReadProperty();
        return m_lIsDefault;
    }

    /**
     * Sets this value as being (or not being) a default value for this lookup type
     * @param tlValue true if this value should be a default, false otherwise
     */
    public void setIsDefault(boolean tlValue)
    {
        canWriteProperty();
        if (isDifferent(m_lIsDefault, tlValue))
        {
            m_lIsDefault = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Determines if this lookup is a system lookup.  System lookups are protected
     * and are not allowed to be changed unless it is the system user doing the changes.
     * System lookups are generally used and coded in to the system.
     * @return true if this is a system lookup, false otherwise
     */
    public boolean getSystem()
    {
        canReadProperty();
        return m_lSystem;
    }

    /**
     * Sets if this is a system value or not.  Anyone can make a value a system value,
     * but once made a system value, only the system user is then able to modify the value
     * @param tlValue true if this should be a system value, false otherwise.
     */
    public void setSystem(boolean tlValue)
    {
        // TODO: implement system variable protection
        canWriteProperty();
        if (isDifferent(m_lSystem, tlValue))
        {
            m_lSystem = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the sequence of this lookup type.  An example of sequence use, would be
     * when a list of lookup types are displayed.  They would be displayed in the order
     * presented here.
     * @return the sequence of this lookup type
     */
    public long getSequence()
    {
        canReadProperty();
        return m_nSequence;
    }

    /**
     * Sets the sequence of this lookup type
     * @param tnValue
     */
    public void setSequence(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nSequence, tnValue))
        {
            m_nSequence = tnValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the id of the type definition for this enumeration
     * @return the type definition id
     */
    public Long getTypeDefinitionID()
    {
        canReadProperty();
        if (m_oTypeDefinition != null)
        {
            return m_oTypeDefinition.getID();
        }
        return m_nTypeDefinitionID;
    }

    /**
     * Sets the type definition, the type definition is the type of the entire enumeration
     * @param tnValue the new type definition
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setTypeDefinitionID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent((Long)m_nTypeDefinitionID, (Long)tnValue))
        {
            m_nTypeDefinitionID = tnValue;
            if (m_oTypeDefinition != null && m_oTypeDefinition.getID() != tnValue)
            {
                m_oTypeDefinition = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public TypeDefinition getTypeDefinition()
    {
        if (m_oTypeDefinition == null)
        {
            m_oTypeDefinition = lazyLoad(TypeDefinition.class, m_nTypeDefinitionID);
        }
        return m_oTypeDefinition;
    }

    @Goliath.Annotations.NotProperty
    public void setTypeDefinition(TypeDefinition toType)
    {
        if (isDifferent(m_oTypeDefinition, toType))
        {
            m_oTypeDefinition = toType;
            setTypeDefinitionID((m_oTypeDefinition != null) ? m_oTypeDefinition.getID() : null);
        }
    }

    @Override
    public boolean hasGUID()
    {
        return true;
    }
    
    

}
