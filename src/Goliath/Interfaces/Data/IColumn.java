/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

/**
 *
 * @author kenmchugh
 */
public interface IColumn 
{
    String getName();
    ITable getTable();
    IDataType getType();
    boolean getAllowNull();
    void setAllowNull(boolean tlAllowNull);
    boolean getAutoIncrement();
    void setAutoIncrement(boolean tlAutoIncrement);
    String getDefault();
    
    // TODO : Make this a constant function list rather than free string
    void setDefault(String tcDefault);
}
