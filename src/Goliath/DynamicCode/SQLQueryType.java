/* ========================================================
 * SQLQueryType.java
 *
 * Author:      admin
 * Created:     Jul 19, 2011, 12:24:53 PM
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
package Goliath.DynamicCode;

import Goliath.Collections.List;
import Goliath.Data.Query.DataQuery;
import Goliath.Interfaces.Data.IConnection;
import Goliath.Interfaces.Data.ISimpleDataObject;

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
public abstract class SQLQueryType extends CodeType<ISimpleDataObject>
{
    /**
     * Creates a new instance of the query type, this is not publically creatable
     * @param tcValue the unique value of the enumeration
     */
    protected SQLQueryType(String tcValue)
    {
        super(tcValue);
    }

    @Override
    public final String generateCode(ISimpleDataObject toObject)
    {
        throw new UnsupportedOperationException("use generateCode(IConnection toConnection, java.lang.Object toObject, DataQuery toArguments)");
    }
    
    public abstract String generateCode(IConnection toConnection, ISimpleDataObject toObject, DataQuery toArguments, List<Object> toProperties);
}
