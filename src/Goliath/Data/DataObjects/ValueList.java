/* =========================================================
 * ValueList.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 5:32:10 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Applications.Application;
import Goliath.Arguments.SingleParameterArguments;
import Goliath.Constants.StringFormatType;
import Goliath.Exceptions.ObjectNotCreatedException;
import Goliath.Interfaces.Data.BusinessObjects.IBusinessObject;
import Goliath.Interfaces.Data.ISimpleDataObject;

/**
 *
 * @author kenmchugh
 */
public class ValueList extends UndoableDataObject<ValueList>
{
    private long m_nObjectRegistryID;
    private Long m_nTypeDefinitionID;       // This is here for help with quick lookups
    private Long m_nTypeCompositionID;
    private String m_cPropertyName;

    // TODO: Implement validation, only one of the following fields can be filled
    private String m_cValueCharacter;
    private Long m_nValueInteger;
    private Float m_nValueFloat;
    private Boolean m_lValueBoolean;
    private Goliath.Date m_dValueDateTime;
    private Long m_nValueObjectID;
    private Long m_nValueResourceID;

    private ObjectRegistry m_oObjectRegistry;
    private TypeDefinition m_oTypeDefinition;
    private TypeComposition m_oTypeComposition;

    // TODO: Possibly implement lazyload get and set methods for ObjectRegistry and Resource objects

    public long getObjectRegistryID()
    {
        canReadProperty();
        if (m_oObjectRegistry != null)
        {
            return m_oObjectRegistry.getID();
        }
        return m_nObjectRegistryID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.ObjectRegistry.class, fieldName="ID")
    public void setObjectRegistryID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nObjectRegistryID, tnValue))
        {
            m_nObjectRegistryID = tnValue;
            if (m_oObjectRegistry != null && m_oObjectRegistry.getID() != tnValue)
            {
                m_oObjectRegistry = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public ObjectRegistry getObjectRegistry()
    {
        if (m_oObjectRegistry == null)
        {
            m_oObjectRegistry = lazyLoad(ObjectRegistry.class, m_nObjectRegistryID);
        }
        return m_oObjectRegistry;
    }

    @Goliath.Annotations.NotProperty
    public void setObjectRegistry(ObjectRegistry toValue)
    {
        if (isDifferent(m_oObjectRegistry, toValue))
        {
            m_oObjectRegistry = toValue;
            setObjectRegistryID(m_oObjectRegistry.getID());
        }
    }



    public Long getTypeDefinitionID()
    {
        canReadProperty();
        if (m_oTypeDefinition != null)
        {
            return m_oTypeDefinition.getID();
        }
        return m_nTypeDefinitionID;
    }


    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setTypeDefinitionID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nTypeDefinitionID, tnValue))
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
            setTypeDefinitionID(m_oTypeDefinition.getID());
        }
    }

    public Long getTypeCompositionID()
    {
        canReadProperty();
        if (m_oTypeComposition != null)
        {
            return m_oTypeComposition.getID();
        }
        return m_nTypeCompositionID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeComposition.class, fieldName="ID")
    public void setTypeCompositionID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nTypeCompositionID, tnValue))
        {
            m_nTypeCompositionID = tnValue;
            if (m_oTypeComposition != null && m_oTypeComposition.getID() != tnValue)
            {
                m_oTypeComposition = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public TypeComposition getTypeComposition()
    {
        if (m_oTypeComposition == null)
        {
            m_oTypeComposition = lazyLoad(TypeComposition.class, m_nTypeCompositionID);
        }
        return m_oTypeComposition;
    }

    @Goliath.Annotations.NotProperty
    public void setTypeComposition(TypeComposition toValue)
    {
        if (isDifferent(m_oTypeComposition, toValue))
        {
            m_oTypeComposition = toValue;
            setTypeCompositionID(m_oTypeComposition.getID());
        }
    }

    public String getPropertyName()
    {
        canReadProperty();
        return m_cPropertyName;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setPropertyName(String tcPropertyName)
    {
        canWriteProperty();
        if (isDifferent(m_cPropertyName, tcPropertyName))
        {
            m_cPropertyName = tcPropertyName;
            propertyHasChanged();
        }
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
        m_nValueResourceID = null;
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
            setValueInteger(((java.lang.Integer)toValue).longValue());
        }
        else if (toValue.getClass().equals(Goliath.Date.class))
        {
            setValueDateTime((Goliath.Date)toValue);
        }
        else if (toValue.getClass().equals(java.util.Date.class))
        {
            setValueDateTime(new Goliath.Date((java.util.Date)toValue));
        }
        else if (toValue.getClass().equals(String.class))
        {
            setValueCharacter((String)toValue);
        }
        else if (Goliath.DynamicCode.Java.isEqualOrAssignable(Goliath.Data.BusinessObjects.Resource.class, toValue.getClass()))
        {
            setValueResourceID(((Goliath.Data.BusinessObjects.Resource)toValue).getPrimaryObject().getID());
        }
        else if (Goliath.DynamicCode.Java.isEqualOrAssignable(ISimpleDataObject.class, toValue.getClass()))
        {
            setValueObjectID(((ISimpleDataObject)toValue).getID());
        }
        else if (Goliath.DynamicCode.Java.isEqualOrAssignable(IBusinessObject.class, toValue.getClass()))
        {
            setValueObjectID(((IBusinessObject)toValue).getID());
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
            ObjectRegistry loObject = lazyLoad(ObjectRegistry.class, m_nValueObjectID);
            TypeDefinition loTypeDefinition = loObject.getTypeDefinition();
            java.lang.Object loReturn = null;
            Class loClass = null;

            if (loTypeDefinition.getArrayType())
            {
                String loClassName = loTypeDefinition.getName();
                loClassName = loClassName.substring(0, loClassName.indexOf("<"));
                loClass = Goliath.DynamicCode.Java.getClass(loClassName);

                java.lang.reflect.Constructor loConstructor = Goliath.DynamicCode.Java.getConstructor(loClass, new Class[]{String.class});
                if (loConstructor != null)
                {
                    try
                    {
                        loReturn = loConstructor.newInstance(loObject.getGUID());
                    }
                    catch (Throwable ex)
                    {
                        Application.getInstance().log(ex);
                    }
                }
                else
                {
                    throw new ObjectNotCreatedException("Could not create an object of type " + loClass.getName() + " because no constructor exists that takes a " + loObject.getClass().getName() + " as an argument");
                }
            }
            else
            {
                loClass = Goliath.DynamicCode.Java.getClass(loTypeDefinition.getName());
                java.lang.reflect.Constructor loConstructor = Goliath.DynamicCode.Java.getConstructor(loClass, new Class[]{loObject.getClass()});
                if (loConstructor != null)
                {
                    try
                    {
                        loReturn = loConstructor.newInstance(loObject);
                    }
                    catch (Throwable ex)
                    {
                        Application.getInstance().log(ex);
                    }
                }
                else
                {
                    throw new ObjectNotCreatedException("Could not create an object of type " + loClass.getName() + " because no constructor exists that takes a " + loObject.getClass().getName() + " as an argument");
                }
            }

            return loReturn;
        }
        if (m_nValueResourceID != null)
        {
            Goliath.Data.DataObjects.Resource loResourceDataObject = lazyLoad(Goliath.Data.DataObjects.Resource.class, m_nValueResourceID);
            Goliath.Data.BusinessObjects.Resource loReturn = new Goliath.Data.BusinessObjects.Resource(loResourceDataObject);
            return loReturn;
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
     * Gets the ID value of this enum.  It is recommended that getValue be used instead
     * @return the ID value, if not set, returns null
     */
    public Long getValueResourceID()
    {
        canReadProperty();
        return m_nValueResourceID;
    }

    /**
     * Sets this value as an ID value, it is recommended that setValue be used instead
     * @param toValue the new value
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Resource.class, fieldName="ID")
    public void setValueResourceID(Long toValue)
    {
        canWriteProperty();
        if (isDifferent(toValue, m_nValueResourceID))
        {
            clearValue();
            m_nValueResourceID = toValue;
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

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        Object loValue = getValue();
        return super.formatString(toFormat) + " - " + (loValue == null ? "" : loValue.toString());
    }
    
    

    @Override
    public int hashCode()
    {
        long hash = 7;
        hash = 11 * hash + (int) (this.getID() ^ (this.getID() >>> 32));
        hash = 11 * hash + (m_oTypeDefinition != null ? m_oTypeDefinition.getID() : m_nTypeDefinitionID);
        hash = 11 * hash + (m_oTypeComposition != null ? m_oTypeComposition.getID() : m_nTypeCompositionID);
        return (int)hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final ValueList other = (ValueList) obj;
        if (this.m_nObjectRegistryID != other.m_nObjectRegistryID)
        {
            return false;
        }
        if (this.m_nTypeDefinitionID != other.m_nTypeDefinitionID)
        {
            return false;
        }
        if (this.m_nTypeCompositionID != other.m_nTypeCompositionID)
        {
            return false;
        }
        if ((this.m_cValueCharacter == null) ? (other.m_cValueCharacter != null) : !this.m_cValueCharacter.equals(other.m_cValueCharacter))
        {
            return false;
        }
        if (this.m_nValueInteger != other.m_nValueInteger && (this.m_nValueInteger == null || !this.m_nValueInteger.equals(other.m_nValueInteger)))
        {
            return false;
        }
        if (this.m_nValueFloat != other.m_nValueFloat && (this.m_nValueFloat == null || !this.m_nValueFloat.equals(other.m_nValueFloat)))
        {
            return false;
        }
        if (this.m_lValueBoolean != other.m_lValueBoolean && (this.m_lValueBoolean == null || !this.m_lValueBoolean.equals(other.m_lValueBoolean)))
        {
            return false;
        }
        if (this.m_dValueDateTime != other.m_dValueDateTime && (this.m_dValueDateTime == null || !this.m_dValueDateTime.equals(other.m_dValueDateTime)))
        {
            return false;
        }
        if (this.m_nValueObjectID != other.m_nValueObjectID && (this.m_nValueObjectID == null || !this.m_nValueObjectID.equals(other.m_nValueObjectID)))
        {
            return false;
        }
        if (this.m_nValueResourceID != other.m_nValueResourceID && (this.m_nValueResourceID == null || !this.m_nValueResourceID.equals(other.m_nValueResourceID)))
        {
            return false;
        }
        return true;
    }

    @Override
    protected void onAddClassValidationRules()
    {
        addClassValidationRule(Goliath.Validation.Rules.NoNullIfEmptyRule.class, "PropertyName", new SingleParameterArguments<String>("TypeCompositionID"));
    }


}
