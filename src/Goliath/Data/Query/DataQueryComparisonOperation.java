/* ========================================================
 * DataQueryComparisonOperation.java
 *
 * Author:      archana
 * Created:     Sep 27, 2011, 2:28:00 PM
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

package Goliath.Data.Query;

import Goliath.DynamicEnum;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Sep 27, 2011
 * @author      archana
**/
public class DataQueryComparisonOperation extends DynamicEnum
{
    /**
     * Creates a new instance of DataQueryComparisonOperation
     */
    public DataQueryComparisonOperation(String tcValue)
    {
        super(tcValue);
    }

    private static DataQueryComparisonOperation g_oEqualto;
    public static DataQueryComparisonOperation EQUALTO()
    {
        if (g_oEqualto == null)
        {
            g_oEqualto = createEnumeration(DataQueryComparisonOperation.class, "==");
        }
        return g_oEqualto;
    }

    private static DataQueryComparisonOperation g_oGreaterThan;
    public static DataQueryComparisonOperation GREATERTHAN()
    {
        if (g_oGreaterThan == null)
        {
            g_oGreaterThan = createEnumeration(DataQueryComparisonOperation.class, ">");
        }
        return g_oGreaterThan;
    }

    private static DataQueryComparisonOperation g_oLesserThan;
    public static DataQueryComparisonOperation LESSERTHAN()
    {
        if (g_oLesserThan == null)
        {
            g_oLesserThan = createEnumeration(DataQueryComparisonOperation.class, "<");
        }
        return g_oLesserThan;
    }

    private static DataQueryComparisonOperation g_oGreaterThanOrEqualTo;
    public static DataQueryComparisonOperation GREATERTHANOREQUALTO()
    {
        if (g_oGreaterThanOrEqualTo == null)
        {
            g_oGreaterThanOrEqualTo = createEnumeration(DataQueryComparisonOperation.class, ">=");
        }
        return g_oGreaterThanOrEqualTo;
    }

    private static DataQueryComparisonOperation g_oLesserThanOrEqualTo;
    public static DataQueryComparisonOperation LESSERTHANOREQUALTO()
    {
        if (g_oLesserThanOrEqualTo == null)
        {
            g_oLesserThanOrEqualTo = createEnumeration(DataQueryComparisonOperation.class, "<=");
        }
        return g_oLesserThanOrEqualTo;
    }
}
