/* =========================================================
 * Membership.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:40:27 PM
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
public class Membership extends UndoableDataObject<Membership>
{
    // TODO: Implement validation - only one of MemberGroup or MemberUser can be null, and one must be null
    private long m_nParentGroupID;
    private long m_nTimePeriod;
    private Long m_nMemberGroupID;
    private Long m_nMemberUserID;
    private String m_cHash;

    private Group m_oParentGroup;
    private Group m_oMemberGroup;
    private User m_oMemberUser;
    private TimePeriod m_oTimePeriod;

    public long getParentGroupID()
    {
        canReadProperty();
        if (m_oParentGroup != null)
        {
            return m_oParentGroup.getID();
        }
        return m_nParentGroupID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Group.class, fieldName="ID")
    public void setParentGroupID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nParentGroupID, tnValue))
        {
            m_nParentGroupID = tnValue;
            if (m_oParentGroup != null && m_oParentGroup.getID() != tnValue)
            {
                m_oParentGroup = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public Group getParentGroup()
    {
        if (m_oParentGroup == null)
        {
            m_oParentGroup = lazyLoad(Group.class, m_nParentGroupID);
        }
        return m_oParentGroup;
    }

    @Goliath.Annotations.NotProperty
    public void setParentGroup(Group toGroup)
    {
        if (isDifferent(m_oParentGroup, toGroup))
        {
            m_oParentGroup = toGroup;
            setParentGroupID(m_oParentGroup.getID());
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

    public Long getMemberGroupID()
    {
        canReadProperty();
        if (m_oMemberGroup != null)
        {
            return m_oMemberGroup.getID();
        }
        return m_nMemberGroupID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Group.class, fieldName="ID")
    public void setMemberGroupID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nMemberGroupID, tnValue))
        {
            clearMember();
            m_nMemberGroupID = tnValue;
            if (m_oMemberGroup != null && m_oMemberGroup.getID() != tnValue)
            {
                m_oMemberGroup = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public Group getMemberGroup()
    {
        if (m_oMemberGroup == null)
        {
            m_oMemberGroup = lazyLoad(Group.class, m_nMemberGroupID);
        }
        return m_oMemberGroup;
    }

    @Goliath.Annotations.NotProperty
    public void setMemberGroup(Group toGroup)
    {
        if (isDifferent(m_oMemberGroup, toGroup))
        {
            clearMember();
            m_oMemberGroup = toGroup;
            setMemberGroupID((m_oMemberGroup != null) ? m_oMemberGroup.getID() : null);
        }
    }

    public Long getMemberUserID()
    {
        canReadProperty();
        if (m_oMemberUser != null)
        {
            return m_oMemberUser.getID();
        }
        return m_nMemberUserID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.User.class, fieldName="ID")
    public void setMemberUserID(Long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nMemberUserID, tnValue))
        {
            clearMember();
            m_nMemberUserID = tnValue;
            if (m_oMemberUser != null && m_oMemberUser.getID() != tnValue)
            {
                m_oMemberUser = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public User getMemberUser()
    {
        if (m_oMemberUser == null)
        {
            m_oMemberUser = lazyLoad(User.class, m_nMemberUserID);
        }
        return m_oMemberUser;
    }

    @Goliath.Annotations.NotProperty
    public void setMemberUser(User toUser)
    {
        if (isDifferent(m_oMemberUser, toUser))
        {
            clearMember();
            m_oMemberUser = toUser;
            setMemberUserID((m_oMemberUser != null) ? m_oMemberUser.getID() : null);
        }
    }

    @Goliath.Annotations.NotProperty
    public void setMember(Object toObject)
    {
        if (toObject.getClass().equals(User.class))
        {
            setMemberUser((User)toObject);
        }
        else if (toObject.getClass().equals(Group.class))
        {
            setMemberGroup((Group)toObject);
        }
    }

    @Goliath.Annotations.NotProperty
    public Object getMember()
    {
        return (m_oMemberUser == null && m_nMemberUserID == null) ? getMemberGroup() : getMemberUser();
    }

    private void clearMember()
    {
        m_nMemberUserID = null;
        m_nMemberGroupID = null;
        m_oMemberGroup = null;
        m_oMemberUser = null;
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
}
