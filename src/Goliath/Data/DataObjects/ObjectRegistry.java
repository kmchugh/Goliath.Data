/* =========================================================
 * ObjectRegistry.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 5:31:58 PM
 *
 * Description
 * --------------------------------------------------------
 * This represents an object in the system.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 *
 * @author kenmchugh
 */
public class ObjectRegistry extends UndoableDataObject<ObjectRegistry>
{
    private long m_nTypeDefinitionID;

    private TypeDefinition m_oTypeDefinition;

    @Override
    public boolean hasGUID()
    {
        return true;
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
}
