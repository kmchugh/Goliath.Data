/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Data.IConnectionString;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author kenmchugh
 */
public class ConnectionDriver extends Goliath.Object
        implements Driver
{
    private ConnectionPool m_oPool;
    
    public ConnectionDriver(IConnectionString toConnectionString)
            throws SQLException
    {
        m_oPool = new ConnectionPool(toConnectionString);
    }
    
    public java.sql.Connection connect() throws DataException 
    {
        return m_oPool.getConnection();
    }

    
    @Override
    public java.sql.Connection connect(String url, Properties props) 
    {
        try
        {
            return connect();
        }
        catch (DataException ex)
        {
            return null;
        }
    }

    public boolean acceptsURL(String url) 
    {
        return true;
    }

    public int getMajorVersion() 
    {
        return 1;
    }

    public int getMinorVersion() 
    {
        return 0;
    }

    public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) 
    {
        return new DriverPropertyInfo[0];
    }

    public boolean jdbcCompliant() 
    {
        return false;
    }

    //@Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return Logger.getLogger("ConnectionDriver");
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
