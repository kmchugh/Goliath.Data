/* ========================================================
 * DataQueryOperation.java
 *
 * Author:      admin
 * Created:     Jul 19, 2011, 9:50:44 AM
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
 * @version     1.0 Jul 19, 2011
 * @author      admin
 **/
public class DataQueryOperation extends DynamicEnum
{
    
    private static DataQueryOperation g_oAnd;
    public static DataQueryOperation AND()
    {
        if (g_oAnd == null)
        {
            g_oAnd = createEnumeration(DataQueryOperation.class, "AND");
        }
        return g_oAnd;
    }

    private static DataQueryOperation g_oOr;
    public static DataQueryOperation OR()
    {
        if (g_oOr == null)
        {
            g_oOr = createEnumeration(DataQueryOperation.class, "OR");
        }
        return g_oOr;
    }
    
    private static DataQueryOperation g_oNot;
    public static DataQueryOperation NOT()
    {
        if (g_oNot == null)
        {
            g_oNot = createEnumeration(DataQueryOperation.class, "NOT");
        }
        return g_oNot;
    }

    /**
     * Creates a new instance of the data query operation, this is not 
     * publically creatable
     * @param tcValue the unique identifier for this operation
     */
    protected DataQueryOperation(String tcValue)
    {
        super(tcValue);
    }
    
    
}
