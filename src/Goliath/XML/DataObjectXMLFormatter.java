/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.XML;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.XMLFormatType;
import Goliath.Data.DataObjects.SimpleDataObject;

/**
 *
 * @author kenmchugh
 */
public class DataObjectXMLFormatter extends Goliath.XML.XMLFormatter<SimpleDataObject>
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
    protected boolean allowContent(SimpleDataObject toObject, XMLFormatType toFormat)
    {
        return false;
    }

    @Override
    protected List<String> getAttributeList(SimpleDataObject toObject)
    {
        return getAttributesFromMap(toObject);
    }

    private List<String> getAttributesFromMap(SimpleDataObject toObject)
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
        return  SimpleDataObject.class;
    }

}
