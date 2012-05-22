/* ========================================================
 * UserObjectXMLFormatter.java
 *
 * Author:      manamimajumdar
 * Created:     Sep 22, 2011, 5:25:36 PM
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
package Goliath.XML;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.XMLFormatType;
import Goliath.Data.DataObjects.User;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Sep 22, 2011
 * @author      manamimajumdar
 **/
public class UserObjectXMLFormatter extends Goliath.XML.XMLFormatter<User>
{

    private static HashTable<String, List<String>> g_oMap;
    /**
     * Allow singleton for speed, otherwise this object could be created thousands of times.
     */
    private static DataObjectXMLFormatter g_oFormatter;

    public static DataObjectXMLFormatter instance()
    {
        if (g_oFormatter == null)
        {
            g_oFormatter = new DataObjectXMLFormatter();
        }
        return g_oFormatter;
    }

    @Override
    protected boolean allowContent(User toObject, XMLFormatType toFormat)
    {
        return true;
    }

    @Override
    protected List<String> getAttributeList(User toObject)
    {
        return getAttributesFromMap(toObject);
    }

    private List<String> getAttributesFromMap(User toObject)
    {
        if (g_oMap == null)
        {
            g_oMap = new HashTable<String, List<String>>();
        }

        if (!g_oMap.containsKey(toObject.getClass().getSimpleName()))
        {
            List<String> loList = new List<String>(2);
            if (toObject.hasGUID())
            {
                loList.add("GUID");
            }
            loList.add("ID");
            g_oMap.put(toObject.getClass().getSimpleName(), loList);
        }
        return g_oMap.get(toObject.getClass().getSimpleName());
    }

    @Override
    public Class supports()
    {
        // TODO: Force this by using the generic T
        return User.class;
    }

    @Override
    protected List<String> onGetPropertyList(User toObject)
    {
        List<String> loList = new List(super.onGetPropertyList(toObject));

        for (String loProperty : loList)
        {
            if (loProperty.equals("password"))
            {
                loList.remove(loProperty);
                break;
            }
        }
        return loList;
    }
}
