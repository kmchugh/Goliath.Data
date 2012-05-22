/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Constants.LogType;
import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.IConnectionString;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;


class ConnectionReaper extends Thread
{
    private ConnectionPool m_oPool;
    private final long m_nDelay= 300000;
    
    ConnectionReaper(ConnectionPool toPool)
    {
        this.setName("ConnectionReaper");
        this.m_oPool = toPool;
    }
    
    public void run()
    {
        while(true)
        {
            try
            {
                sleep(m_nDelay);
            }
            catch (InterruptedException ex)
            {}
            m_oPool.reapConnections();
        }
    }
}

public class ConnectionPool extends Goliath.Object
{
    private Vector<IConnection> m_oConnections;
    private final long m_nTimeout = 60000;
    private IConnectionString m_oConnectionString;
    private ConnectionReaper m_oReaper;
    private final int m_nPoolSize = 10;
    
    private static HashTable<String, ConnectionPool> g_oPools = new HashTable<String, ConnectionPool>(1);
    public static IConnection getConnection(IConnectionString toConnectionString) throws DataException
    {
        if (!g_oPools.containsKey(toConnectionString.toString()))
        {
            g_oPools.put(toConnectionString.toString(), new ConnectionPool(toConnectionString));
            Application.getInstance().log("Created Connection Pool for " + toConnectionString.toString(), LogType.TRACE());
        }
        return g_oPools.get(toConnectionString.toString()).getConnection();
        
    }
    
    public ConnectionPool(IConnectionString toConnectionString)
    {
        m_oConnections = new Vector<IConnection>(m_nPoolSize);
        m_oConnectionString = toConnectionString;
        m_oReaper = new ConnectionReaper(this);
        m_oReaper.start();
    }
    
    public synchronized void reapConnections()
    {
        long lnStale = System.currentTimeMillis() - m_nTimeout;
        for (IConnection loConn : m_oConnections)
        {
            if (loConn.inUse() && lnStale > loConn.getLastUse() && !loConn.validate())
            {
                removeConnection(loConn);
            }
        }

        // TODO : Need to remove the reaper if no new connections are added after a certain period of time
    }
    
    public synchronized void closeConnections()
    {
        for (IConnection loConn : m_oConnections)
        {
            removeConnection(loConn);
      
        
        }
    }
    
    private synchronized void removeConnection(IConnection toConn)
    {
        m_oConnections.removeElement(toConn);
    }
    
    public synchronized IConnection getConnection() throws DataException
    {
        // TODO: Force closing of long standing connections
        // TODO: Optimise this method
        IConnection loConn;
        for (int i=0; i< m_oConnections.size(); i++)
        {
            loConn = m_oConnections.elementAt(i);
            if (loConn.lease())
            {
                // We also need to make sure the connection is okay.
                if (!loConn.validate())
                {
                    Application.getInstance().log("Clearing stale connection");
                    // This connection was closed, possible by an error or disconnect from the data source
                    removeConnection(loConn);
                    loConn = null;
                }
                else
                {
                    return loConn;
                }
            }
        }
        
        java.sql.Connection loConnection = null;
        try
        {
            loConnection = DriverManager.getConnection(m_oConnectionString.toString());
        }
        catch (SQLException ex)
        {
            StringBuilder loException = new StringBuilder(ex.toString());
            while (ex.getNextException()!= null)
            {
                ex = ex.getNextException();
                loException.append("\n");
                loException.append(ex.toString());
            }

            // TODO : Throw different errors based on sql error
            throw new DataException(loException.toString());
        }
        
        loConn = new Connection(loConnection, this, m_oConnectionString);
        loConn.lease();
        m_oConnections.addElement(loConn);
        Application.getInstance().log("Created new Connection for " + m_oConnectionString.toString() + " count[" + Integer.toString(m_oConnections.size()) + "]", LogType.TRACE());
        return loConn;
    }
    
    public synchronized void returnConnection(Connection loConn)
    {
        loConn.expireLease();
    }
}


