/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

/**
 *
 * @author kenmchugh
 */
public interface IRelation
{
    // TODO: Implement multiple column relationships and column sequencing
    String getName();
    IColumn getColumn();
    ITable getForeignTable();
    IColumn getForeignColumn();
}
