/* =========================================================
 * DataCommand.java
 *
 * Author:      kmchugh
 * Created:     20-Jan-2008, 21:49:42
 * 
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 20-Jan-2008
 * @author      kmchugh
**/
public class DataCommand extends Goliath.Commands.Command
{
    private java.sql.PreparedStatement m_oStatement;

    public DataCommand(java.sql.Connection toConnection, String tcSQLStatement)
    {
        super(false);
        try
        {
            m_oStatement = toConnection.prepareStatement(tcSQLStatement);
        }
        catch (Exception e)
        {
            throw new Goliath.Exceptions.CriticalException(e);
        }
    }

    @Override
    public Object doExecute() throws Throwable
    {
        return m_oStatement.execute();
    }

    public void setURL(int parameterIndex, URL x) throws SQLException
    {
        m_oStatement.setURL(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
        m_oStatement.setTimestamp(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
        m_oStatement.setTimestamp(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException
    {
        m_oStatement.setTime(parameterIndex, x, cal);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException
    {
        m_oStatement.setTime(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException
    {
        m_oStatement.setString(parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException
    {
        m_oStatement.setShort(parameterIndex, x);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException
    {
        m_oStatement.setSQLXML(parameterIndex, xmlObject);
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException
    {
        m_oStatement.setRowId(parameterIndex, x);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
        m_oStatement.setRef(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException
    {
        m_oStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException
    {
        m_oStatement.setObject(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
    {
        m_oStatement.setObject(parameterIndex, x, targetSqlType);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        m_oStatement.setNull(parameterIndex, sqlType, typeName);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException
    {
        m_oStatement.setNull(parameterIndex, sqlType);
    }

    public void setNString(int parameterIndex, String value) throws SQLException
    {
        m_oStatement.setNString(parameterIndex, value);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException
    {
        m_oStatement.setNClob(parameterIndex, reader);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        m_oStatement.setNClob(parameterIndex, reader, length);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException
    {
        m_oStatement.setNClob(parameterIndex, value);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
    {
        m_oStatement.setNCharacterStream(parameterIndex, value);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException
    {
        m_oStatement.setNCharacterStream(parameterIndex, value, length);
    }

    public void setLong(int parameterIndex, long x) throws SQLException
    {
        m_oStatement.setLong(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException
    {
        m_oStatement.setInt(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException
    {
        m_oStatement.setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException
    {
        m_oStatement.setDouble(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
    {
        m_oStatement.setDate(parameterIndex, x, cal);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException
    {
        m_oStatement.setDate(parameterIndex, x);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException
    {
        m_oStatement.setClob(parameterIndex, reader);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException
    {
        m_oStatement.setClob(parameterIndex, reader, length);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
        m_oStatement.setClob(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException
    {
        m_oStatement.setCharacterStream(parameterIndex, reader);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException
    {
        m_oStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException
    {
        m_oStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException
    {
        m_oStatement.setBytes(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException
    {
        m_oStatement.setByte(parameterIndex, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
        m_oStatement.setBoolean(parameterIndex, x);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
    {
        m_oStatement.setBlob(parameterIndex, inputStream);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException
    {
        m_oStatement.setBlob(parameterIndex, inputStream, length);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
        m_oStatement.setBlob(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException
    {
        m_oStatement.setBinaryStream(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        m_oStatement.setBinaryStream(parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        m_oStatement.setBinaryStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
        m_oStatement.setBigDecimal(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException
    {
        m_oStatement.setAsciiStream(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException
    {
        m_oStatement.setAsciiStream(parameterIndex, x, length);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        m_oStatement.setAsciiStream(parameterIndex, x, length);
    }

    public void setArray(int parameterIndex, Array x) throws SQLException
    {
        m_oStatement.setArray(parameterIndex, x);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        return m_oStatement.getParameterMetaData();
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        return m_oStatement.getMetaData();
    }

    public int executeUpdate() throws SQLException
    {
        return m_oStatement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException
    {
        return m_oStatement.executeQuery();
    }

    public void clearParameters() throws SQLException
    {
        m_oStatement.clearParameters();
    }

    public void addBatch() throws SQLException
    {
        m_oStatement.addBatch();
    }

    public Object Execute(Object... taArgs)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    
}
