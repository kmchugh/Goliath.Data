/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

/**
 *
 * @author kenmchugh
 */
public interface IDataBase
{
    public String getName();
    public boolean exists();
    public boolean create();
}
