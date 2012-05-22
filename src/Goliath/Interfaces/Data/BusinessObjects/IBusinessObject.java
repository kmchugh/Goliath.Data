/* ========================================================
 * IBusinessObject.java
 *
 * Author:      kmchugh
 * Created:     Jul 31, 2010, 4:51:46 PM
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
 * ===================================================== */

package Goliath.Interfaces.Data.BusinessObjects;

import Goliath.Constants.EventType;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Event;
import Goliath.Interfaces.IEventDispatcher;
import Goliath.Interfaces.IValidatable;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 $(date)
 * @author      kmchugh
**/
public interface IBusinessObject<T extends SimpleDataObject<T>>
        extends IEventDispatcher<EventType, Event<IBusinessObject<T>>>, IValidatable
{
    long getID();
    boolean isModified();
    
    
}
