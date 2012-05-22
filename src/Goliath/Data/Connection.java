/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.IConnectionString;
import Goliath.Interfaces.DynamicCode.ISqlGenerator;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 *
 * @author kenmchugh
 */
public class Connection extends Goliath.Object
        implements java.sql.Connection, IConnection
{

    private ConnectionPool m_oPool;
    private java.sql.Connection m_oConn;
    private boolean m_lInUse;
    private long m_nTimestamp;
    private ISqlGenerator m_oQueryGenerator;
    private IConnectionString m_oConnectionString;


    public Connection(java.sql.Connection toConn, ConnectionPool toPool, IConnectionString toConnectionString)
    {
        m_oConn=toConn;
        m_oPool=toPool;
        m_lInUse=false;
        m_nTimestamp=0;
        m_oQueryGenerator = toConnectionString.getDataLayerAdapter().getQueryGenerator();
        m_oConnectionString = toConnectionString;
    }

    @Override
    public IConnectionString getConnectionString()
    {
        return m_oConnectionString;
    }



    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException
    {
        return m_oConn.isWrapperFor(arg0);
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException
    {
        return m_oConn.unwrap(arg0);
    }

    @Override
    public ISqlGenerator getQueryGenerator()
    {
        return m_oQueryGenerator;
    }


    
    public synchronized boolean lease() 
    {
       if(m_lInUse)
       {
           return false;
       } 
       else 
       {
          m_lInUse = true;
          m_nTimestamp = System.currentTimeMillis();
          return true;
       }
    }
    
    public boolean validate() 
    {
	try 
        {
            return !m_oConn.isClosed() && testConnection(m_oConn);
        }
        catch (Throwable e) 
        {
	    return false;
	}
    }
    
    private boolean testConnection(java.sql.Connection toConnection)
    {
        try
        {
            return toConnection.isValid(1);
        }
        catch (Throwable ex)
        {
            return false;
        }
        /*
        ResultSet loResults = null;
        Statement loStatement = null;
        try 
        {
            loStatement = toConnection.createStatement();
            if (loStatement == null)
            {
                return false;
            }
            loResults = loStatement.executeQuery("SELECT 1");
            if (loResults == null)
            {
                return false;
            }
            return loResults.next();
        }
        catch(Throwable ex)
        {
            return false;
        }
        finally
        {
            try
            {
                loResults.close();
                loStatement.close();
                
                // Notice that we do not close the connection, it is placed on the connection pool
            }
            catch (Throwable ignore)
            {}  
        }
         * */

    }

    public boolean inUse() 
    {
        return m_lInUse;
    }

    public long getLastUse() 
    {
        return m_nTimestamp;
    }

    @Override
    public void close() 
    {
        m_oPool.returnConnection(this);
    }

    
    protected void expireLease() 
    {
        m_lInUse = false;
    }

    protected java.sql.Connection getConnection() 
    {
        return m_oConn;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
        return m_oConn.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException
    {
        return m_oConn.prepareCall(sql);
    }

    public Statement createStatement() throws SQLException
    {
        return m_oConn.createStatement();
    }

    public String nativeSQL(String sql) throws SQLException
    {
        return m_oConn.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        m_oConn.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException
    {
        return m_oConn.getAutoCommit();
    }

    public void commit() throws SQLException
    {
        m_oConn.commit();
    }

    public void rollback() throws SQLException
    {
        m_oConn.rollback();
    }

    public boolean isClosed() throws SQLException
    {
        return m_oConn.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        return m_oConn.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException
    {
        m_oConn.setReadOnly(readOnly);
    }
  
    public boolean isReadOnly() throws SQLException
    {
        return m_oConn.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException
    {
        m_oConn.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException
    {
        return m_oConn.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException
    {
        m_oConn.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException
    {
        return m_oConn.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return m_oConn.getWarnings();
    }

    public void clearWarnings() throws SQLException
    {
        m_oConn.clearWarnings();
    }

    public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException
    {
        m_oConn.setTypeMap(arg0);
    }

    public Savepoint setSavepoint(String arg0) throws SQLException
    {
        return m_oConn.setSavepoint(arg0);
    }

    public Savepoint setSavepoint() throws SQLException
    {
        return m_oConn.setSavepoint();
    }

    public void setHoldability(int arg0) throws SQLException
    {
        m_oConn.setHoldability(arg0);
    }

    public void setClientInfo(Properties arg0) throws SQLClientInfoException
    {
        m_oConn.setClientInfo(arg0);
    }

    public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException
    {
        m_oConn.setClientInfo(arg0, arg1);
    }

    public void rollback(Savepoint arg0) throws SQLException
    {
        m_oConn.rollback(arg0);
    }

    public void releaseSavepoint(Savepoint arg0) throws SQLException
    {
        m_oConn.releaseSavepoint(arg0);
    }

    public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException
    {
        return m_oConn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException
    {
        return m_oConn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException
    {
        return m_oConn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException
    {
        return m_oConn.prepareStatement(arg0, arg1, arg2, arg3);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException
    {
        return m_oConn.prepareStatement(arg0, arg1, arg2);
    }

    public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException
    {
        return m_oConn.prepareCall(arg0, arg1, arg2, arg3);
    }

    public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException
    {
        return m_oConn.prepareCall(arg0, arg1, arg2);
    }

    public boolean isValid(int arg0) throws SQLException
    {
        return m_oConn.isValid(arg0);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return m_oConn.getTypeMap();
    }

    public int getHoldability() throws SQLException
    {
        return m_oConn.getHoldability();
    }

    public Properties getClientInfo() throws SQLException
    {
        return m_oConn.getClientInfo();
    }

    public String getClientInfo(String arg0) throws SQLException
    {
        return m_oConn.getClientInfo(arg0);
    }

    public Struct createStruct(String arg0, Object[] arg1) throws SQLException
    {
        return m_oConn.createStruct(arg0, arg1);
    }

    public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException
    {
        return m_oConn.createStatement(arg0, arg1, arg2);
    }

    public Statement createStatement(int arg0, int arg1) throws SQLException
    {
        return m_oConn.createStatement(arg0, arg1);
    }

    public SQLXML createSQLXML() throws SQLException
    {
        return m_oConn.createSQLXML();
    }

    public NClob createNClob() throws SQLException
    {
        return m_oConn.createNClob();
    }

    public Clob createClob() throws SQLException
    {
        return m_oConn.createClob();
    }

    public Blob createBlob() throws SQLException
    {
        return m_oConn.createBlob();
    }

    public Array createArrayOf(String arg0, Object[] arg1) throws SQLException
    {
        return m_oConn.createArrayOf(arg0, arg1);
    }

   // @Override
    public int getNetworkTimeout() throws SQLException
    {
        return 0;
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
    {
       // throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public void abort(Executor executor) throws SQLException
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public String getSchema() throws SQLException
    {
        return null;
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    //@Override
    public void setSchema(String schema) throws SQLException
    {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    

    





}

