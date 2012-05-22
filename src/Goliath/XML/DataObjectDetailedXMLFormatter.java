/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.XML;

import Goliath.XML.DataObjectXMLFormatter;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.XMLFormatType;
import Goliath.Data.DataObjects.SimpleDataObject;

/**
 *
 * @author kenmchugh
 */
public class DataObjectDetailedXMLFormatter extends DataObjectXMLFormatter
{
    private static HashTable<String, List<String>> g_oMap;

    /**
     * Allow singleton for speed, otherwise this object could be created thousands of times.
     */
    private static DataObjectDetailedXMLFormatter g_oFormatter;
    public static DataObjectDetailedXMLFormatter instance()
    {
        if (g_oFormatter == null)
        {
            g_oFormatter = new DataObjectDetailedXMLFormatter();
        }
        return g_oFormatter;
    }

    @Override
    protected boolean allowContent(SimpleDataObject toObject, XMLFormatType toFormat)
    {
        return true;
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

        if (!g_oMap.containsKey(toObject.getClass().getName()))
        {
            List<String> loList = new List<String>(2);
            for (String lcProperty : Goliath.DynamicCode.Java.getPropertyMethods(toObject.getClass()))
            {
                if (lcProperty.equals("GUID") && !toObject.hasGUID())
                {
                    continue;
                }

                Class loClass = Goliath.DynamicCode.Java.getPropertyType(toObject, lcProperty);

                // Strings are not to be considered primitives here, except for GUID
                if (Goliath.DynamicCode.Java.isPrimitive(loClass))
                {
                    if (!java.lang.String.class.isAssignableFrom(loClass) || lcProperty.equals("GUID"))
                    {
                        loList.add(lcProperty);
                    }
                }
            }
            g_oMap.put(toObject.getClass().getName(), loList);
        }
        return g_oMap.get(toObject.getClass().getName());
    }
}
