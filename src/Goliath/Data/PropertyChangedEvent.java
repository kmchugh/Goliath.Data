/* ========================================================
 * PropertyChangedEvent.java
 *
 * Author:      kmchugh
 * Created:     Sep 8, 2010, 10:34:32 AM
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

package Goliath.Data;

import Goliath.Event;
import Goliath.Interfaces.Data.ISimpleDataObject;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Sep 8, 2010
 * @author      kmchugh
**/
public class PropertyChangedEvent extends Event<ISimpleDataObject>
{
    private String m_cPropertyName;

    /**
     * Creates a new instance of PropertyChangedEvent
     */
    public PropertyChangedEvent(ISimpleDataObject toObject, String tcProperty)
    {
        super(toObject);
        m_cPropertyName = tcProperty;
    }

    public String getPropertyName()
    {
        return m_cPropertyName;
    }
}
