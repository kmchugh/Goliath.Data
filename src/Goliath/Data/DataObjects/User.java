/* =========================================================
 * User.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:27:55 PM
 *
 * Description
 * --------------------------------------------------------
 * Represents a user loaded from a data source
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 * Represents a user in the system, a user can have an identity within the system
 * @author kenmchugh
 */
public class User extends UndoableDataObject<User>
{
    private String m_cName;
    private String m_cDisplayName;
    private String m_cPassword;
    private long m_nTimePeriodID;
    private long m_nLoginCount;
    private boolean m_lLocked;
    private boolean m_lAnonymous;
    private String m_cEmail;
    
    private TimePeriod m_oTimePeriod;


    /**
     * Every user has a unique ID
     * @return the unique ID of the user
     */
    @Override
    public final boolean hasGUID()
    {
        return true;
    }

    /**
     * Gets the name of this user, this is the name the user would
     * use to log in with
     * @return the user name
     */
    public final String getName()
    {
        canReadProperty();
        return m_cName;
    }

    /**
     * Sets the name of this user, must be unique
     * @param tcValue the new name
     */
    @Goliath.Annotations.UniqueIndex
    @Goliath.Annotations.MaximumLength(length=100)
    @Goliath.Annotations.NoNulls
    public final void setName(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cName, tcValue))
        {
            m_cName = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the display name of this user, this is the name that
     * would be displayed in any output
     * @return the user display name
     */
    public final String getDisplayName()
    {
        canReadProperty();
        return m_cDisplayName;
    }

    /**
     * Sets the user display name
     * @param tcValue the new display name
     */
    @Goliath.Annotations.MaximumLength(length=150)
    @Goliath.Annotations.NoNulls
    public final void setDisplayName(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cDisplayName, tcValue))
        {
            m_cDisplayName = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the users password
     * @return the user password
     */
    public final String getPassword()
    {
        canReadProperty();
        return m_cPassword;
    }

    /**
     * Sets the new password for the user
     * @param tcValue the new user password
     */
    @Goliath.Annotations.MinimumLength(length=4)
    @Goliath.Annotations.NoNulls
    public final void setPassword(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cPassword, tcValue))
        {
            m_cPassword = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the email address for this user
     * @return the email address
     */
    public final String getEmail()
    {
        canReadProperty();
        return m_cEmail;
    }

    /**
     * Sets the email address for this user
     * @param tcValue the email address
     */
    @Goliath.Annotations.RegEx(matchString="^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @Goliath.Annotations.MaximumLength(length=255)
    public final void setEmail(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cEmail, tcValue))
        {
            m_cEmail = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Checks if this user is expired
     * @return true if the user is expired
     */
    public final boolean isExpired()
    {
        return getTimePeriod() == null || !getTimePeriod().isCurrent();
    }
    
    /**
     * Gets the user login count
     * @return the user login count
     */
    public final long getLoginCount()
    {
        canReadProperty();
        return m_nLoginCount;
    }

    /**
     * Sets the user login count
     * @param tnValue the user login count
     */
    @Goliath.Annotations.MinimumValue(value=0)
    public final void setLoginCount(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nLoginCount, tnValue))
        {
            m_nLoginCount = tnValue;
            propertyHasChanged();
        }
    }

    /**
     * Helper function to increment the login count,
     */
    public final void incrementLoginCount()
    {
        m_nLoginCount++;
        propertyHasChanged();
    }

    /**
     * Checks if this user is locked or not
     * @return locked users can not log in
     */
    public final boolean getLocked()
    {
        canReadProperty();
        return m_lLocked;
    }

    /**
     * Sets if this user is locked or not
     * @param tlValue true to lock the user
     */
    public final void setLocked(boolean tlValue)
    {
        canWriteProperty();
        if (isDifferent(m_lLocked, tlValue))
        {
            m_lLocked = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Checks if this user is an anonymous user
     * @return true if this is an anonymous user
     */
    public final boolean getAnonymous()
    {
        canReadProperty();
        return m_lAnonymous;
    }

    /**
     * Sets if this is an anonymous user or not
     * @param tlValue true to set as anonymous
     */
    public final void setAnonymous(boolean tlValue)
    {
        canWriteProperty();
        if (isDifferent(m_lAnonymous, tlValue))
        {
            m_lAnonymous = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the ID of the Timeperiod related to this user
     * @return the time period for this user
     */
    public final long getTimePeriodID()
    {
        canReadProperty();
        if (m_oTimePeriod != null)
        {
            return m_oTimePeriod.getID();
        }
        return m_nTimePeriodID;
    }

    /**
     * Sets the TimePeriod for this user, a user requires a time period
     * @param tnValue the time period for this user
     */
    @Goliath.Annotations.ForeignKey(className=TimePeriod.class, fieldName="ID")
    @Goliath.Annotations.MinimumValue(1)
    @Goliath.Annotations.NoNulls
    public final void setTimePeriodID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nTimePeriodID, tnValue))
        {
            m_nTimePeriodID = tnValue;
            if (m_oTimePeriod != null && m_oTimePeriod.getID() != tnValue)
            {
                m_oTimePeriod = null;
            }
            propertyHasChanged();
        }
    }

    /**
     * Gets the time period related to this user
     * @return the time period for this user
     */
    @Goliath.Annotations.NotProperty
    public final TimePeriod getTimePeriod()
    {
        canReadProperty();
        if (m_oTimePeriod == null)
        {
            m_oTimePeriod = lazyLoad(TimePeriod.class, m_nTimePeriodID);
        }
        return m_oTimePeriod;
    }

    /**
     * Sets the time period for this user
     * @param toType the user time period
     */
    @Goliath.Annotations.NotProperty
    public final void setTimePeriod(TimePeriod toType)
    {
        canWriteProperty();
        if (isDifferent(m_oTimePeriod, toType))
        {
            m_oTimePeriod = toType;
            setTimePeriodID(m_oTimePeriod.getID());
        }
    }

    @Override
    protected boolean onBeforeSave()
    {
        if (isDeleted())
        {
            // We are deleting this object, so delete the TimePeriod as well
            TimePeriod loPeriod = getSimpleDataObject(TimePeriod.class, m_nTimePeriodID, m_oTimePeriod);
            if (loPeriod != null)
            {
                loPeriod.delete();
                loPeriod.save();
            }
        }
        else
        {
            // We are saving or adjusting, so save the TimePeriod First
            TimePeriod loPeriod = getSimpleDataObject(TimePeriod.class, m_nTimePeriodID, m_oTimePeriod);
            if (loPeriod != null)
            {
                loPeriod.save();
            }
        }
        return super.onBeforeSave();
    }


    


}
