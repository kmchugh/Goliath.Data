/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Interfaces.Data.IColumn;
import Goliath.Interfaces.Data.IRelation;
import Goliath.Interfaces.Data.ITable;

/**
 *
 * @author kenmchugh
 */
public class Relation extends Goliath.Object
        implements IRelation
{
    private IColumn m_oColumn = null;
    private IColumn m_oForeignColumn = null;
    
    public Relation(IColumn toColumn, IColumn toForeignColumn)
    {
        Goliath.Utilities.checkParameterNotNull("toColumn", toColumn);
        Goliath.Utilities.checkParameterNotNull("toForeignColumn", toForeignColumn);
        
        m_oColumn = toColumn;
        m_oForeignColumn = toForeignColumn;
    }

    @Override
    public IColumn getColumn()
    {
        return m_oColumn;
    }

    @Override
    public IColumn getForeignColumn()
    {
        return m_oForeignColumn;
    }

    @Override
    public ITable getForeignTable()
    {
        return m_oForeignColumn.getTable();
    }

    @Override
    public String getName()
    {
        return "FK_" + Goliath.Utilities.generateStringGUID().replaceAll("-", "");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Relation other = (Relation) obj;
        if (this.m_oColumn != other.m_oColumn && (this.m_oColumn == null || !this.m_oColumn.equals(other.m_oColumn))) {
            return false;
        }
        if (this.m_oForeignColumn != other.m_oForeignColumn && (this.m_oForeignColumn == null || !this.m_oForeignColumn.equals(other.m_oForeignColumn))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.m_oColumn != null ? this.m_oColumn.hashCode() : 0);
        hash = 41 * hash + (this.m_oForeignColumn != null ? this.m_oForeignColumn.hashCode() : 0);
        return hash;
    }

    
    

}
