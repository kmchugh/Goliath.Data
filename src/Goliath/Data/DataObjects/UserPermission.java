/* =========================================================
 * UserPermission.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:57:13 PM
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
public class UserPermission extends UndoableDataObject<UserPermission>
{
    // TODO: Add validation - only one of m_cGroupGUID or m_cUserGUID must be set
    // TODO: Add validation - only one of m_nPermissionID or m_nResourcePermissionID must be set

    private String m_cGroupGUID;
    private String m_cUserGUID;
    private String m_cHash;
    private long m_nTimePeriod;
    private Long m_nPermissionID;
    private Long m_nResourcePermissionID;

    private Permission m_oPermission;
    private ResourcePermission m_oResourcePermission;
    private TimePeriod m_oTimePeriod;

    public String getGroupGUID()
    {
        canReadProperty();
        return m_cGroupGUID;
    }

    @Goliath.Annotations.MaximumLength(length=40)
    public void setGroupGUID(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cGroupGUID, tcValue))
        {
            m_cGroupGUID = tcValue;
            propertyHasChanged();
        }
    }

    public String getUserGUID()
    {
        canReadProperty();
        return m_cUserGUID;
    }

    @Goliath.Annotations.MaximumLength(length=40)
    public void setUserGUID(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cUserGUID, tcValue))
        {
            m_cUserGUID = tcValue;
            propertyHasChanged();
        }
    }



    public long getTimePeriodID()
    {
        canReadProperty();
        if (m_oTimePeriod != null)
        {
            return m_oTimePeriod.getID();
        }
        return m_nTimePeriod;
    }
    
    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.TimePeriod.class, fieldName="ID")
    public void setTimePeriodID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nTimePeriod, tnValue))
        {
            m_nTimePeriod = tnValue;
            if (m_oTimePeriod != null && m_oTimePeriod.getID() != tnValue)
            {
                m_oTimePeriod = null;
            }
            propertyHasChanged();
        }
    }
    
    @Goliath.Annotations.NotProperty
    public TimePeriod getTimePeriod()
    {
        canWriteProperty();
        if (m_oTimePeriod == null)
        {
            m_oTimePeriod = lazyLoad(TimePeriod.class, m_nTimePeriod);
        }
        return m_oTimePeriod;
    }

    @Goliath.Annotations.NotProperty
    public void setTimePeriod(TimePeriod toTimePeriod)
    {
        canReadProperty();
        if (isDifferent(m_oTimePeriod, toTimePeriod))
        {
            m_oTimePeriod = toTimePeriod;
            setTimePeriodID(m_oTimePeriod.getID());
        }
    }
    
    public String getHash()
    {
        canReadProperty();
        return m_cHash;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setHash(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cHash, tcValue))
        {
            m_cHash = tcValue;
            propertyHasChanged();
        }
    }

    public Long getPermissionID()
    {
        canReadProperty();
        if (m_oPermission != null)
        {
            return m_oPermission.getID();
        }
        return m_nPermissionID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Permission.class, fieldName="ID")
    public void setPermissionID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nPermissionID, tnValue))
        {
            m_nPermissionID = tnValue;
            if (m_oPermission != null && m_oPermission.getID() != tnValue)
            {
                m_oPermission = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public Permission getPermission()
    {
        if (m_oPermission == null)
        {
            m_oPermission = lazyLoad(Permission.class, m_nPermissionID);
        }
        return m_oPermission;
    }

    @Goliath.Annotations.NotProperty
    public void setPermission(Permission toGroup)
    {
        if (isDifferent(m_oPermission, toGroup))
        {
            m_oPermission = toGroup;
            setPermissionID(m_oPermission.getID());
        }
    }

    public Long getResourcePermissionID()
    {
        canReadProperty();
        if (m_oResourcePermission != null)
        {
            return m_oResourcePermission.getID();
        }
        return m_nResourcePermissionID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.ResourcePermission.class, fieldName="ID")
    public void setResourcePermissionID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nResourcePermissionID, tnValue))
        {
            m_nResourcePermissionID = tnValue;
            if (m_oResourcePermission != null && m_oResourcePermission.getID() != tnValue)
            {
                m_oResourcePermission = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public ResourcePermission getResourcePermission()
    {
        if (m_oResourcePermission == null)
        {
            m_oResourcePermission = lazyLoad(ResourcePermission.class, m_nResourcePermissionID);
        }
        return m_oResourcePermission;
    }

    @Goliath.Annotations.NotProperty
    public void setResourcePermission(ResourcePermission toPermission)
    {
        if (isDifferent(m_oResourcePermission, toPermission))
        {
            m_oResourcePermission = toPermission;
            setResourcePermissionID(m_oResourcePermission.getID());
        }
    }


}
