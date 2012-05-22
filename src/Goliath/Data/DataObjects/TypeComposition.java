/* =========================================================
 * TypeComposition.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 12:48:36 PM
 *
 * Description
 * --------------------------------------------------------
 * TypeComposition represents the collection of types that
 * compose the TypeDefinition being defined
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Annotations.MaximumLength;
import Goliath.Annotations.MinimumLength;
import Goliath.Annotations.NoNulls;
import Goliath.Constants.CacheType;
import Goliath.Constants.StringFormatType;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.ObjectNotCreatedException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author kenmchugh
 */
public class TypeComposition extends UndoableDataObject<TypeComposition>
{
    /**
     * Creates a hash string that can be used as a guid for the type composition
     * @param toClass the class to get the hash for
     * @param lcProperty the specific property to get the hash for
     * @return the hash string
     */
    public static String createHash(Class toClass, String tcProperty)
    {
        TypeDefinition loParentType = TypeDefinition.getTypeDefinitionFromClass(toClass);
        
        if (loParentType != null)
        {
            TypeDefinition loPropertyType = getTypeDefinitionForProperty(toClass, tcProperty);
            
            if (loPropertyType != null)
            {
                // Create the hash
                return Goliath.Utilities.encryptMD5(loParentType.getGUID() + loPropertyType.getGUID() + tcProperty.toLowerCase());
            }
        }
        return null;
    }

    private static TypeDefinition getTypeDefinitionForProperty(Class toClass, String tcProperty)
    {
        TypeDefinition loReturn = null;
        Java.ClassDefinition loClassDef = Java.getClassDefinition(toClass);
        Java.MethodDefinition loMethod = loClassDef.getMethod(tcProperty);
        if (loMethod != null)
        {
            loReturn = TypeDefinition.getTypeDefinitionFromClass(loMethod.getReturnType()); 
        }
        return loReturn;
    }

    /**
     * Gets a type composition representing the property specified on the class give, if it does not exist already, it will be created here
     * @param toClass the class the property is attached to
     * @param tcProperty the property to create the composition for
     * @return the type composition
     */
    public static TypeComposition getTypeCompositionFromClass(Class toClass, String tcProperty)
    {
        // Get a hash of the class name
        String lcHash = createHash(toClass, tcProperty);
        if (lcHash != null)
        {

            // First attempt to load from the data source
            TypeComposition loComp = TypeComposition.getObjectByGUID(TypeComposition.class, lcHash);
            if (loComp == null)
            {
                loComp = createTypeCompositionFromClass(toClass, tcProperty);
            }
            return loComp;
        }
        throw new ObjectNotCreatedException("Could not create the Type composition for " + toClass.getName() + "." + tcProperty);
    }

    /**
     *
     * @param toClass
     * @param tcProperty
     * @return
     */
    public static TypeComposition createTypeCompositionFromClass(Class toClass, String tcProperty)
    {
        // Create a hash for the class
        String lcHash = createHash(toClass, tcProperty);
        if (lcHash != null)
        {
        
            TypeComposition loType = new TypeComposition();
            loType.setGUID(lcHash);
            TypeDefinition loParentType = TypeDefinition.getTypeDefinitionFromClass(toClass);

            TypeDefinition loPropertyType = getTypeDefinitionForProperty(toClass, tcProperty);

            if (loPropertyType != null)
            {
                Java.ClassDefinition loClassDef = Java.getClassDefinition(toClass);
                Java.MethodDefinition loMethodDef = loClassDef.getMethod(tcProperty);
                Type loReturnType = loMethodDef.getReturnType();
                Class loRType = (Java.isEqualOrAssignable(Class.class, loReturnType.getClass())) ?
                    (Class)loReturnType :
                    (Class)((ParameterizedType)loReturnType).getActualTypeArguments()[0];

                loType.setName(loParentType.getName() + "." + tcProperty);
                loType.setDescription("Property " + tcProperty + " on the type " + loParentType.getName());
                loType.setParentType(loParentType);
                loType.setTypeDefinition(loPropertyType);
                loType.setRequired((loReturnType != null && (loRType).isPrimitive()) || loMethodDef.getAnnotation(NoNulls.class) != null);
                
                MaximumLength loMaxAnnotation = loMethodDef.getAnnotation(MaximumLength.class);
                loType.setMaxAllowed(loMaxAnnotation == null ? null : loMaxAnnotation.length());

                MinimumLength loMinAnnotation = loMethodDef.getAnnotation(MinimumLength.class);
                loType.setMinAllowed(loMinAnnotation == null ? null : loMinAnnotation.length());

                loType.setReadOnly(!loMethodDef.canWrite());
                loType.setWriteOnly(!loMethodDef.canRead());

                // TODO: Implement regular expression matching from the annotations
                loType.setRegexMatch(null);

                if (loType.save())
                {
                    return loType;
                }
            }
        }
        throw new ObjectNotCreatedException("Could not create the Type composition for " + toClass.getName() + "." + tcProperty);
    }



    private String m_cName;
    private String m_cDescription;
    private long m_nTypeDefinitionID;
    private long m_nParentTypeID;
    private boolean m_lRequired;
    private Long m_nMaxAllowed;
    private Long m_nMinAllowed;
    private String m_cRegexMatch;
    private boolean m_lReadOnly;
    private boolean m_lWriteOnly;

    private TypeDefinition m_oTypeDefinition;
    private TypeDefinition m_oParentType;

    /**
     * This does not have a guid
     * @return false
     */
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
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cName, tcValue))
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

    public long getTypeDefinitionID()
    {
        canReadProperty();
        if (m_oTypeDefinition != null)
        {
            return m_oTypeDefinition.getID();
        }
        return m_nTypeDefinitionID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setTypeDefinitionID(long tnValue)
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

    public long getParentTypeID()
    {
        canReadProperty();
        if (m_oParentType != null)
        {
            return m_oParentType.getID();
        }
        return m_nParentTypeID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TypeDefinition.class, fieldName="ID")
    public void setParentTypeID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nParentTypeID, tnValue))
        {
            m_nParentTypeID = tnValue;
            if (m_oParentType != null && m_oParentType.getID() != tnValue)
            {
                m_oParentType = null;
            }
            propertyHasChanged();
        }
    }

    @Goliath.Annotations.NotProperty
    public TypeDefinition getParentType()
    {
        if (m_oParentType == null)
        {
            m_oParentType = lazyLoad(TypeDefinition.class, m_nParentTypeID);
        }
        return m_oParentType;
    }

    @Goliath.Annotations.NotProperty
    public void setParentType(TypeDefinition toType)
    {
        if (isDifferent(m_oParentType, toType))
        {
            m_oParentType = toType;
            setParentTypeID(m_oParentType.getID());
        }
    }

    public boolean getRequired()
    {
        canReadProperty();
        return m_lRequired;
    }

    public void setRequired(boolean tlValue)
    {
        // TODO: implement system variable protection
        canWriteProperty();
        if (isDifferent(m_lRequired, tlValue))
        {
            m_lRequired = tlValue;
            propertyHasChanged();
        }
    }

    public Long getMaxAllowed()
    {
        canReadProperty();
        return m_nMaxAllowed;
    }

    public void setMaxAllowed(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nMaxAllowed, tnValue))
        {
            m_nMaxAllowed = tnValue;
            propertyHasChanged();
        }
    }

    public Long getMinAllowed()
    {
        canReadProperty();
        return m_nMinAllowed;
    }

    public void setMinAllowed(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nMinAllowed, tnValue))
        {
            m_nMinAllowed = tnValue;
            propertyHasChanged();
        }
    }

    public String getRegexMatch()
    {
        canReadProperty();
        return m_cRegexMatch;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setRegexMatch(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cRegexMatch, tcValue))
        {
            m_cRegexMatch = tcValue;
            propertyHasChanged();
        }
    }

    public boolean getReadOnly()
    {
        return m_lReadOnly;
    }

    public void setReadOnly(boolean tlReadOnly)
    {
        m_lReadOnly = tlReadOnly;
    }

    public boolean getWriteOnly()
    {
        return m_lWriteOnly;
    }

    public void setWriteOnly(boolean tlWriteOnly)
    {
        m_lWriteOnly = tlWriteOnly;
    }
    
    @Override
    protected String formatString(StringFormatType toFormat)
    {
        if (toFormat == StringFormatType.DEFAULT())
        {
            return getName();
        }
        return super.formatString(toFormat);
    }
    
    

}
