package Goliath.Security;

import Goliath.Applications.Application;
import Goliath.SecurityEntity;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Commands.CreateDataObjectCommand;
import Goliath.Commands.CreateDataObjectCommandArgs;
import Goliath.Commands.GetDataObjectByKeyCommand;
import Goliath.Commands.GetDataObjectCommandArgs;
import Goliath.Data.DataObjects.LoginInfo;
import Goliath.Data.DataObjects.TimePeriod;
import Goliath.Date;
import Goliath.Exceptions.ProcessNotComplete;
import Goliath.Interfaces.Security.IPermission;
import Goliath.Session;

/**
 *
 * @author kenmchugh
 */
public class DataSecurityManager extends SecurityManager
{
    // TODO: A Serious revisit to this class is needed!!!!
    
    private HashTable<String, Goliath.Data.DataObjects.User> m_oUserCache;
    private HashTable<String, Goliath.Data.DataObjects.Group> m_oGroupCache;

    @Override
    public boolean addAsMember(SecurityEntity toEntity, Group toGroup)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean addPermission(SecurityEntity toEntity, IPermission toPermission)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean authenticate(Goliath.Security.User toUser, String tcPassword)
    {
        Goliath.Data.DataObjects.User loUser = getUserDataObject(toUser);
        if (loUser.getPassword().equals(encryptPasswordString(toUser, tcPassword)))
        {
            loUser.incrementLoginCount();
            // Make sure the login info exists for this session
            LoginInfo.getLoginInfo(Session.getCurrentSession());
            return true;
        }
        return false;
    }

    @Override
    public long getLoginCount(User toUser)
    {
        Goliath.Data.DataObjects.User loUser = getUserDataObject(toUser);
        return loUser != null ? loUser.getLoginCount() : 0;
    }

    @Override
    public String getDescription(Group toGroup)
    {
        Goliath.Data.DataObjects.Group loGroup = getGroupDataObject(toGroup);
        return loGroup != null ? loGroup.getDescription() : "";
    }

    @Override
    public void setDescription(Group toGroup, String tcDescription)
    {
        Goliath.Data.DataObjects.Group loGroup = getGroupDataObject(toGroup);
        if (loGroup != null)
        {
            loGroup.setDescription(tcDescription);
        }
    }



    @Override
    public String getDisplayName(Goliath.Security.User toUser)
    {
        Goliath.Data.DataObjects.User loUser = getUserDataObject(toUser);
        return (loUser != null) ? loUser.getDisplayName() : "";
    }

    @Override
    public Date getExpiry(SecurityEntity toEntity)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            return getUserDataObject((User)toEntity).getTimePeriod().getEndDate();
        }
    }

    @Override
    public String getGUID(SecurityEntity toEntity)
    {
        return toEntity.getLookupID();
    }

    @Override
    public Group getGroup(String tcGUID)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getID(SecurityEntity toEntity)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            return getUserDataObject((User)toEntity).getID();
        }
    }

    @Override
    public boolean getLocked(Goliath.Security.User toUser)
    {
        return getUserDataObject(toUser).getLocked();
    }

    @Override
    public List<Group> getMembershipList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName(SecurityEntity toEntity)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            return getUserDataObject((User)toEntity).getName();
        }
    }

    @Override
    public List<IPermission> getPermissionList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPrimaryEmail(Goliath.Security.User toUser)
    {
        Goliath.Data.DataObjects.User loUser = getUserDataObject(toUser);
        return (loUser != null) ? loUser.getEmail() : "";
    }

    @Override
    protected Goliath.Security.User onGetUser(String tcUserName)
    {
        PropertySet loProperties = new PropertySet();
        loProperties.setProperty("Name", tcUserName);

        // We will use the system user to get this user
        GetDataObjectByKeyCommand<Goliath.Data.DataObjects.User> loCommand = 
                new GetDataObjectByKeyCommand<Goliath.Data.DataObjects.User>(
                    new GetDataObjectCommandArgs<Goliath.Data.DataObjects.User>(
                        Goliath.Data.DataObjects.User.class,
                        loProperties), Session.getSystemSession());

        Goliath.Utilities.waitForCommand(loCommand);

        try
        {
            return (loCommand.getResult() != null) ? new User(this, loCommand.getResult().getGUID()) : null;
        }
        catch (ProcessNotComplete ex)
        {
        }
        return null;
    }

    @Override
    public boolean isExpired(SecurityEntity toEntity)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            return getUserDataObject((User)toEntity).getTimePeriod().getEndDate().getLong() < new Date().getLong();
        }
    }

    @Override
    public void storeEntity(SecurityEntity toEntity)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            Goliath.Data.DataObjects.User loUser = getUserDataObject((User)toEntity);
            loUser.save();
        }
    }

    @Override
    public boolean isAnonymous(User toUser)
    {
        return getUserDataObject(toUser).getAnonymous();
    }

    @Override
    public void setAnonymous(User toUser)
    {
        getUserDataObject(toUser).setAnonymous(true);
    }

    @Override
    protected User onCreateAnonymousUser()
    {
        Goliath.Data.DataObjects.User loActual = new Goliath.Data.DataObjects.User();
        loActual.setDisplayName("Anonymous");
        loActual.setEmail("anonymous@user.sys");
        loActual.setTimePeriod(new TimePeriod(new Date(), 60000 * 60 * 24));
        loActual.setName(Goliath.Utilities.generateStringGUID());
        loActual.setAnonymous(true);

        User loReturn = new User(this, loActual.getGUID());

        loActual.setPassword(encryptPasswordString(loReturn, Goliath.Security.Utilities.generatePassword()));
        
        // Store the user that has been created
        storeInCache(loActual);
        return loReturn;
    }

    @Override
    protected Group onCreateGroup(String tcGUID)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Goliath.Security.User onCreateSystemUser(String tcUserName, String tcEmail, String tcPassword)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean onDeleteGroup(Group toGroup)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean onResetPassword(Goliath.Security.User toUser, String tcNewPassword)
    {
        Goliath.Data.DataObjects.User loActual = getUserDataObject(toUser);
        loActual.setPassword(encryptPasswordString(toUser, tcNewPassword));
        return loActual.save();
    }

    @Override
    protected void onSetLocked(Goliath.Security.User toUser, boolean tlLocked)
    {
        getUserDataObject(toUser).setLocked(tlLocked);
    }

    @Override
    public void setDisplayName(Goliath.Security.User toUser, String tcDisplayName)
    {
        getUserDataObject(toUser).setDisplayName(tcDisplayName);
    }

    @Override
    public void setExpiry(SecurityEntity toEntity, Date toExpiry)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            getUserDataObject((User)toEntity).setTimePeriod(new TimePeriod(new Date(), toExpiry));
        }
    }

    @Override
    public void setGUID(SecurityEntity toEntity, String tcGUID)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            getUserDataObject((User)toEntity).setGUID(tcGUID);
        }
    }

    @Override
    public void setName(SecurityEntity toEntity, String tcValue)
    {
        if (isGroup(toEntity))
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        else
        {
            getUserDataObject((User)toEntity).setName(tcValue);
        }
    }

    @Override
    public void setPassword(Goliath.Security.User toUser, String tcPassword)
    {
        getUserDataObject(toUser).setPassword(encryptPasswordString(toUser, tcPassword));
    }

    @Override
    public void setPrimaryEmail(Goliath.Security.User toUser, String tcEmail)
    {
        getUserDataObject(toUser).setEmail(tcEmail);
    }

    @Override
    public boolean unauthenticate(Goliath.Security.User toUser)
    {
        Application.getInstance().log("Unauthenticating user " + toUser.getGUID());
        // Make sure the login info exists for this session
        LoginInfo loInfo = LoginInfo.getLoginInfo(Session.getCurrentSession());
        // loInfo could be null if this is an anonymous session
        if (loInfo != null)
        {
            loInfo.setLogOut(new Date());
            loInfo.save();
        }
        return true;
    }

    @Override
    protected Goliath.Security.User onCreateUser(String tcUserName, String tcEmail, String tcPassword, String tcDisplayName, String tcDescription, boolean tlUseSystem)
    {
        Goliath.Data.DataObjects.User loUser = new Goliath.Data.DataObjects.User();
        loUser.setName(Goliath.Utilities.isNullOrEmpty(tcUserName) ? tcEmail : tcUserName);
        loUser.setDisplayName(tcDisplayName);
        loUser.setEmail(tcEmail);
        loUser.setTimePeriod(new TimePeriod((long)(Long.MAX_VALUE * .05)));
        User loReturn = new User(this, loUser.getGUID());
        loUser.setPassword(encryptPasswordString(loReturn, tcPassword));
        if (!tlUseSystem)
        {
            loUser.save();
        }
        else
        {
            CreateDataObjectCommand loCommand = new CreateDataObjectCommand(
                    new CreateDataObjectCommandArgs(loUser), 
                    Session.getSystemSession());
            Goliath.Utilities.waitForCommand(loCommand);
        }
        return loUser.isNew() ? null : loReturn;
    }

    @Override
    protected boolean onDeleteUser(Goliath.Security.User toUser)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private HashTable<String, Goliath.Data.DataObjects.User> m_oCachedUsers;
    private Goliath.Data.DataObjects.User getCachedUser(String tcGUID)
    {
        return (m_oCachedUsers != null) ? m_oCachedUsers.get(tcGUID) : null;
    }
    private void cacheUser(Goliath.Data.DataObjects.User toUser)
    {
        if (m_oCachedUsers == null)
        {
            m_oCachedUsers = new HashTable<String, Goliath.Data.DataObjects.User>();
        }
        m_oCachedUsers.put(toUser.getGUID(), toUser);
    }

    // TODO: Implement data object caching properly in the DataManager
    private Goliath.Data.DataObjects.Group getGroupDataObject(Group toGroup)
    {
        Goliath.Data.DataObjects.Group loReturn = getGroupFromCache(toGroup);
        if (loReturn == null)
        {
            // We need to look this up in the database, we will use the System user to do this
            loReturn = Goliath.Data.DataObjects.Group.getObjectByGUID(Goliath.Data.DataObjects.Group.class, toGroup.getLookupID());
        }
        if (loReturn != null)
        {
            storeInCache(loReturn);
        }
        return loReturn;
    }


    // TODO: Implement data object caching properly in the DataManager
    private Goliath.Data.DataObjects.User getUserDataObject(User toUser)
    {
        Goliath.Data.DataObjects.User loReturn = getUserFromCache(toUser);
        if (loReturn == null)
        {
            // We need to look this up in the database, we will use the System user to do this
            loReturn = Goliath.Data.DataObjects.User.getObjectByGUID(Goliath.Data.DataObjects.User.class, toUser.getLookupID());
        }
        if (loReturn != null)
        {
            storeInCache(loReturn);
        }
        return loReturn;
    }

    @Override
    public User getUserByGUID(String tcGUID)
    {
        Goliath.Data.DataObjects.User loUser = Goliath.Data.DataObjects.User.getObjectByGUID(Goliath.Data.DataObjects.User.class, tcGUID);
        return loUser != null ? getUser(loUser.getName()) : null;
    }

    private Goliath.Data.DataObjects.Group getGroupFromCache(Group toGroup)
    {
        return m_oGroupCache == null ? null : m_oGroupCache.get(toGroup.getLookupID());
    }

    private Goliath.Data.DataObjects.User getUserFromCache(User toUser)
    {
        return m_oUserCache == null ? null : m_oUserCache.get(toUser.getLookupID());
    }

    public void storeInCache(Goliath.Data.DataObjects.User toUser)
    {
        // TODO: need to set up an interval to clean out expired users, or users with no session (use the session event listener)
        if (m_oUserCache == null)
        {
            m_oUserCache = new HashTable<String, Goliath.Data.DataObjects.User>();
        }
        m_oUserCache.put(toUser.getGUID(), toUser);
    }

    public void storeInCache(Goliath.Data.DataObjects.Group toGroup)
    {
        // TODO: need to set up an interval to clean out expired users, or users with no session (use the session event listener)
        if (m_oGroupCache == null)
        {
            m_oGroupCache = new HashTable<String, Goliath.Data.DataObjects.Group>();
        }
        m_oGroupCache.put(toGroup.getGUID(), toGroup);
    }
}
