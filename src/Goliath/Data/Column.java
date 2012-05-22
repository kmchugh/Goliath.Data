/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IDataType;
import Goliath.Interfaces.Data.ITable;

/**
 *
 * @author kenmchugh
 */
public class Column extends Goliath.Object
        implements IColumn
{
    private String m_cName = null;
    private ITable m_oTable = null;
    private boolean m_lAllowNull = false;
    private boolean m_lAutoIncrement = false;
    private String m_cDefault = null;
    private IDataType m_oDataType = null;
    
    public Column(String tcName, ITable toTable, IDataType toType, boolean tlAutoIncrement)
    {
        this(tcName, toTable, toType, false, null);
        m_lAutoIncrement = tlAutoIncrement;
    }
    
    public Column(String tcName, ITable toTable, IDataType toType, boolean tlAllowNull, String tcDefault)
    {
        this(tcName, toTable, toType);
        m_lAllowNull = tlAllowNull;
        m_cDefault = tcDefault;
    }
    
    public Column(String tcName, ITable toTable, IDataType toType)
    {
        if (Goliath.Utilities.isNullOrEmpty(tcName))
        {
            throw new InvalidParameterException("tcName", tcName);
        }
        if (toTable == null)
        {
            throw new InvalidParameterException("toTable", toTable);
        }
        if (toType == null)
        {
            throw new InvalidParameterException("toType", toType);
        }
        m_cName = tcName;
        m_oTable = toTable;
        m_oDataType = toType;
    }
    
    @Override
    public String getName()
    {
        return m_cName;
    }
    
    @Override
    public ITable getTable()
    {
        return m_oTable;
    }
    
    public boolean exists()
    {
        return Utilities.checkColumnExists(this);
    }

    @Override
    public boolean getAllowNull()
    {
        return m_lAllowNull;
    }

    @Override
    public boolean getAutoIncrement()
    {
        return m_lAutoIncrement;
    }

    @Override
    public String getDefault()
    {
        return m_cDefault;
    }

    @Override
    public IDataType getType()
    {
        return m_oDataType;
    }

    @Override
    public void setAllowNull(boolean tlAllowNull)
    {
        m_lAllowNull = tlAllowNull;
    }

    @Override
    public void setAutoIncrement(boolean tlAutoIncrement)
    {
        m_lAutoIncrement = tlAutoIncrement;
    }

    @Override
    public void setDefault(String tcDefault)
    {
        m_cDefault = tcDefault;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Column other = (Column) obj;
        if ((this.m_cName == null) ? (other.m_cName != null) : !this.m_cName.equals(other.m_cName)) {
            return false;
        }
        if (this.m_oTable != other.m_oTable && (this.m_oTable == null || !this.m_oTable.equals(other.m_oTable))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.m_cName != null ? this.m_cName.hashCode() : 0);
        hash = 89 * hash + (this.m_oTable != null ? this.m_oTable.hashCode() : 0);
        return hash;
    }

    
    
    

}
