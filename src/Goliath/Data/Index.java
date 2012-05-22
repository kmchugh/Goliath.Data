/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Collections.List;
import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IIndex;
import Goliath.Interfaces.Data.IIndexType;
import Goliath.Interfaces.Data.ITable;

/**
 *
 * @author kenmchugh
 */
public class Index extends Goliath.Object
        implements IIndex
{
    IIndexType m_oType;
    List<IColumn> m_oColumns;
    
    public Index(IIndexType toType, ITable toTable, String[] taColumns)
    {
        Goliath.Utilities.checkParameterNotNull("toType", toType);
        Goliath.Utilities.checkParameterNotNull("toTable", toTable);
        Goliath.Utilities.checkParameterNotNull("taColumns", taColumns);
        
        m_oColumns = new List<IColumn>(taColumns.length);
        m_oType = toType;
        for (String lcColumn : taColumns)
        {
            addColumn(toTable.getColumn(lcColumn));
        }
    }
    
    public Index(IIndexType toType, ITable toTable, String tcColumnName)
    {
        Goliath.Utilities.checkParameterNotNull("toType", toType);
        Goliath.Utilities.checkParameterNotNull("toTable", toTable);
        Goliath.Utilities.checkParameterNotNull("tcColumnName", tcColumnName);
        
        m_oColumns = new List<IColumn>(1);
        m_oType = toType;
        addColumn(toTable.getColumn(tcColumnName));
    }

    @Override
    public boolean addColumn(IColumn toColumn)
    {
        if (!hasColumn(toColumn))
        {
            return m_oColumns.add(toColumn);
        }
        return false;
    }

    @Override
    public boolean hasColumn(IColumn toColumn)
    {
        return m_oColumns.contains(toColumn);
    }

    @Override
    public boolean removeColumn(IColumn toColumn)
    {
        return m_oColumns.remove(toColumn);
    }
    
    @Override
    public List<IColumn> getColumns()
    {
        return new List<IColumn>(m_oColumns);
    }

    @Override
    public String getName()
    {
        StringBuilder loBuilder = new StringBuilder("IX_" + m_oColumns.get(0).getTable().getName() + "_");
        for (IColumn loColumn : m_oColumns)
        {
            loBuilder.append(loColumn.getName() + "_");
        }
        return loBuilder.toString();
    }

    @Override
    public IIndexType getType()
    {
        return m_oType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Index other = (Index) obj;
        if (this.m_oType != other.m_oType && (this.m_oType == null || !this.m_oType.equals(other.m_oType))) {
            return false;
        }
        if (this.m_oColumns != other.m_oColumns && (this.m_oColumns == null || !this.m_oColumns.equals(other.m_oColumns))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.m_oType != null ? this.m_oType.hashCode() : 0);
        hash = 73 * hash + (this.m_oColumns != null ? this.m_oColumns.hashCode() : 0);
        return hash;
    }

    
    

}
