/* =========================================================
 * Resource.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 5:32:26 PM
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

/**
 *
 * @author kenmchugh
 */
public class Resource extends UndoableDataObject<Resource>
{
    private Long m_nResourceValueID;
    private ResourceValue m_oResourceValue;

    
    /**
     * Gets the resource value record id
     * @return the id of the resource value record
     */
    public Long getResourceValueID()
    {
        canReadProperty();
        if (m_oResourceValue != null)
        {
            return m_oResourceValue.getID();
        }
        return m_nResourceValueID;
    }

    /**
     * Sets the resource value record id
     * @param tnValue the new resource value id
     */
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.ResourceValue.class, fieldName="ID")
    public void setResourceValueID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nResourceValueID, tnValue))
        {
            m_nResourceValueID = tnValue;
            if (m_oResourceValue != null && m_oResourceValue.getID() != tnValue)
            {
                m_oResourceValue = null;
            }
            propertyHasChanged();
        }
    }


    /**
     * Gets the resource value object
     * @return the resource value object
     */
    @Goliath.Annotations.NotProperty
    public ResourceValue getResourceValue()
    {
        if (m_oResourceValue == null)
        {
            m_oResourceValue = lazyLoad(ResourceValue.class, m_nResourceValueID);
        }
        return m_oResourceValue;
    }

    /**
     * Sets the resource value object
     * @param toValue the resource value object
     */
    @Goliath.Annotations.NotProperty
    public void setResourceValue(ResourceValue toValue)
    {
        if (isDifferent(m_oResourceValue, toValue))
        {
            m_oResourceValue = toValue;
            setResourceValueID((m_oResourceValue != null) ? m_oResourceValue.getID() : null);
        }
    }

    @Override
    public boolean hasGUID()
    {
        return true;
    }


}
