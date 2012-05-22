/* =========================================================
 * UndoableDataObjectCollection.java
 *
 * Author:      kmchugh
 * Created:     11-Feb-2008, 09:37:04
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

package Goliath.Collections;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 11-Feb-2008
 * @author      kmchugh
**/
public class UndoableDataObjectCollection<T extends Goliath.Data.DataObjects.UndoableDataObject<T>>
        extends Goliath.Collections.DataObjectCollection<T> 
{
    /** Creates a new instance of UndoableDataObjectCollection */
    public UndoableDataObjectCollection(Class<T> toClass)
    {
        super(toClass);
    }
}
