/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Collections;

/**
 *
 * @author Peter
 */
public interface IDataObjectCollection<T extends Goliath.Data.DataObjects.DataObject<T>>
        extends Goliath.Interfaces.Collections.ISimpleDataObjectCollection<T>, java.lang.Iterable<T>, java.util.List<T>
{

}
