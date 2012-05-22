/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Exceptions;

/**
 *
 * @author kmchugh
 */
public class DataObjectLockedException extends DataException
{
    /**
    * Creates a new instance of InvalidOperationException
    */
    public DataObjectLockedException()
    {
        super("You have attempted to modify a data object that has been locked");
    }
}
