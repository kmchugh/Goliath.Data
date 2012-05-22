/* =========================================================
 * IDataObject.java
 *
 * Author:      kmchugh
 * Created:     22-Feb-2008, 12:20:32
 * 
 * Description
 * --------------------------------------------------------
 * This interface is used to to identify DataObjects.
 *
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Data;

import Goliath.Constants.EventType;
import Goliath.Event;
import Goliath.Interfaces.IEventDispatcher;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 22-Feb-2008
 * @author      kmchugh
**/
public interface IDataObject
        extends ISimpleDataObject, IEventDispatcher<EventType, Event<ISimpleDataObject>>
{
    boolean save();
}
