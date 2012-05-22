/* =========================================================
 * DataObjectCache.java
 *
 * Author:      kmchugh
 * Created:     17-Jun-2008, 15:58:52
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

import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.KeyedObjectCache;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 17-Jun-2008
 * @author      kmchugh
**/
public class DataObjectCache<T extends SimpleDataObject<T>> extends KeyedObjectCache<T>
{

    @Override
    protected boolean canAddObject(T toObject)
    {
        // Can not cache a new object, it must have been saved previously
        return !toObject.isNew();
    }

    @Override
    protected void afterObjectAdded(T toObject)
    {
        if (add((Class<T>)toObject.getClass(), "ID", toObject.getID(), toObject))
        {
            if (toObject.hasGUID())
            {
                add((Class<T>)toObject.getClass(), "GUID", toObject.getGUID(), toObject);
            }
        }
    }

}
