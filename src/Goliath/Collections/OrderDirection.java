/* ========================================================
 * OrderType.java
 *
 * Author:      christinedorothy
 * Created:     Aug 10, 2011, 12:29:11 PM
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


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Aug 10, 2011
 * @author      christinedorothy
**/
public class OrderDirection extends Goliath.DynamicEnum
{
    private static OrderDirection g_oAscending;
    public static OrderDirection ASCENDING()
    {
        if (g_oAscending == null)
        {
            g_oAscending = createEnumeration(OrderDirection.class, "Ascending");
        }
        return g_oAscending;
    }

    private static OrderDirection g_oDescending;
    public static OrderDirection DESCENDING()
    {
        if (g_oDescending == null)
        {
            g_oDescending = createEnumeration(OrderDirection.class, "Descending");
        }
        return g_oDescending;
    }

    /**
     * Creates a new instance of OrderType
     */
    public OrderDirection(String tcValue)
    {
        super(tcValue);
    }
}
