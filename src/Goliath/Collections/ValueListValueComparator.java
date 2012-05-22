/* ========================================================
 * ValueListComparator.java
 *
 * Author:      christinedorothy
 * Created:     Aug 11, 2011, 4:20:57 PM
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

package Goliath.Collections;

import java.util.Comparator;
import Goliath.Data.DataObjects.ValueList;

        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Aug 11, 2011
 * @author      christinedorothy
**/
public class ValueListValueComparator<T extends ValueList> implements Comparator<T>

{
    @Override
    public int compare(T toValueList1, T toValueList2)
    {
        // Check which value type.
        // If both object's values are not null, compare the values.
        // If object1's value is not and object2's value is null, it should be exchanged (return 1).

        if (toValueList1.getValueBoolean() != null || toValueList2.getValueBoolean() != null)
        {
            if (toValueList1.getValueBoolean() != null && toValueList2.getValueBoolean() != null)
            {
                return (toValueList1.getValueBoolean() == false) && (toValueList2.getValueBoolean() == true) ? 0 : 1;
            }
            else if(toValueList1.getValueBoolean() != null && toValueList2.getValueBoolean() == null)
            {
                return 1;
            }
        }
        else if (toValueList1.getValueFloat() != null || toValueList2.getValueFloat() != null)
        {
            if (toValueList1.getValueFloat() != null && toValueList2.getValueFloat() != null)
            {
                return toValueList1.getValueFloat() <= toValueList2.getValueFloat() ? 0 : 1;
            }
            else if (toValueList1.getValueFloat() != null && toValueList2.getValueFloat() == null)
            {
                return 1;
            }
        }
        else if (toValueList1.getValueInteger() != null || toValueList2.getValueInteger() != null)
        {
            if (toValueList1.getValueInteger() != null && toValueList2.getValueInteger() != null)
            {
                return toValueList1.getValueInteger() <= toValueList2.getValueInteger() ? 0 : 1;
            }
            else if (toValueList1.getValueInteger() != null && toValueList2.getValueInteger() == null)
            {
                return 1;
            }
        }
        else if (toValueList1.getValueDateTime() != null || toValueList2.getValueDateTime() != null)
        {
            if (toValueList1.getValueDateTime() != null && toValueList2.getValueDateTime() != null)
            {
                return toValueList1.getValueDateTime().compareTo(toValueList2.getValueDateTime());
            }
            else if (toValueList1.getValueDateTime() != null && toValueList2.getValueDateTime() == null)
            {
                return 1;
            }
        }
        else if (toValueList1.getValueCharacter() != null || toValueList2.getValueCharacter() != null)
        {
            if (toValueList1.getValueCharacter() != null && toValueList2.getValueCharacter() != null)
            {
                return toValueList1.getValueCharacter().compareTo(toValueList2.getValueCharacter());
            }
            else if (toValueList1.getValueCharacter() != null && toValueList2.getValueCharacter() == null)
            {
                return 1;
            }
        }
        else if (toValueList1.getValueObjectID() != null || toValueList2.getValueObjectID() != null)
        {
            // TODO: handles object as the property value
            return 0;
        }
        else if (toValueList1.getValueResourceID() != null || toValueList2.getValueResourceID() != null)
        {
            // TODO: handles object as the property value
            return 0;
        }
        return 0;
    }
}
