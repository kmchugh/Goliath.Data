package Goliath.Data.DataObjects;

import Goliath.Applications.Application;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Date;
import Goliath.Interfaces.ISession;
import Goliath.Session;

/**
 * The login info class is intended to store login history for the users
 * sessions.  This class is purposely not directly linked to the user object so
 * that it is possible to remove users from the system while maintaining a historical
 * view
 * 
 * @author kmchugh
 */
public class LoginInfo extends UndoableDataObject<LoginInfo>
{
    /**
     * Gets the login info for the specified session, if there is no login info for the session
     * one will be created, populated, and saved by this call
     * @param toSession the session to get the login info for
     */
    public static LoginInfo getLoginInfo(ISession toSession)
    {
        // If the user is Anonymous, there shouldn't be a login info
        if (!toSession.getUser().isAnonymous())
        {
            LoginInfo loInfo = toSession.getProperty("SessionLoginInfo");
            if (loInfo == null)
            {
                try
                {
                    SimpleDataObjectCollection<LoginInfo> loCollection = getObjectsByProperty(LoginInfo.class, "SessionID", Session.getCurrentSession().getSessionID());
                    loInfo = loCollection != null && loCollection.size() == 1 ? loCollection.get(0) : null;
                    if (loInfo == null)
                    {
                        // The info object never existed, so create and save as a user is being authenticated
                        loInfo = new LoginInfo(toSession);
                        loInfo.save();
                    }
                    toSession.setProperty("SessionLoginInfo", loInfo);
                }
                catch (Throwable ex)
                {
                    // Currently this error happens because of the Property query not being handled, when it is handled property we can remove this try catch
                    Application.getInstance().log(ex);
                }
            }
            return loInfo;
        }
        return null;
    }

    private Date m_dLogIn;
    private Date m_dLogOut;
    private String m_cSessionID;
    private String m_cIPAddress;
    private String m_cUserGUID;

    /**
     * Creates an instance of login info.
     */
    public LoginInfo()
    {
        this(Session.getCurrentSession());
    }

    public LoginInfo(ISession toSession)
    {
        m_dLogIn = new Date();
        m_cSessionID = toSession.getSessionID();
        m_cUserGUID = toSession.getUserGUID();
        m_cIPAddress = toSession.getSessionIP();
    }

    /**
     * Gets the IP Address for this session
     * @return the session IP or null if not known
     */
    public final String getIPAddress()
    {
        canReadProperty();
        return m_cIPAddress;
    }

    /**
     * Sets the session ip for this information record
     * @param tcValue the session ip
     */
    @Goliath.Annotations.MaximumLength(length=50)
    public final void setIPAddress(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cIPAddress, tcValue))
        {
            m_cIPAddress = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the User guid for this information record
     * @return the session id
     */
    public final String getUserGUID()
    {
        canReadProperty();
        return m_cUserGUID;
    }

    /**
     * Sets the user guid for this information record
     * @param tcValue the user guid
     */
    @Goliath.Annotations.MaximumLength(length=50)
    @Goliath.Annotations.NoNulls
    public final void setUserGUID(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cUserGUID, tcValue))
        {
            m_cUserGUID = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the Session id for this information record
     * @return the session id
     */
    public final String getSessionID()
    {
        canReadProperty();
        return m_cSessionID;
    }

    /**
     * Sets the session id for this information record
     * @param tcValue the session id
     */
    @Goliath.Annotations.MaximumLength(length=255)
    @Goliath.Annotations.NoNulls
    public final void setSessionID(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cSessionID, tcValue))
        {
            m_cSessionID = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the last time this user logged in
     * @return the last login
     */
    public final Goliath.Date getLogIn()
    {
        canReadProperty();
        return m_dLogIn;
    }

    /**
     * Sets the last time this user logged in
     * @param toDate the last login
     */
    // TODO: Implement a time based validation rule such as cant be later than today, or cant be later than next week
    @Goliath.Annotations.NoNulls
    public final void setLogIn(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dLogIn, toDate))
        {
            m_dLogIn = toDate;
            propertyHasChanged();
        }
    }

    /**
     * Gets the log in time of this session
     * @return the last login
     */
    public final Goliath.Date getLogOut()
    {
        canReadProperty();
        return m_dLogOut;
    }

    /**
     * Sets the logout time of this session
     * @param toDate the last login
     */
    // TODO: Implement a time based validation rule such as cant be later than today, or cant be later than next week
    public final void setLogOut(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dLogOut, toDate))
        {
            m_dLogOut = toDate;
            propertyHasChanged();
        }
    }

}
