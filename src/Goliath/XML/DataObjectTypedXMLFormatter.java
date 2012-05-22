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
public class DataObjectTypedXMLFormatter extends DataObjectXMLFormatter
{
    /**
     * Allow singleton for speed, otherwise this object could be created thousands of times.
     */
    private static DataObjectTypedXMLFormatter g_oFormatter;
    public static DataObjectTypedXMLFormatter instance()
    {
        if (g_oFormatter == null)
        {
            g_oFormatter = new DataObjectTypedXMLFormatter();
        }
        return g_oFormatter;
    }

    /*
    protected void onWriteContent(StringBuilder toBuilder, SimpleDataObject toObject)
    {
        List<String> loAttributes = getAttributeList(toObject);
        for (String lcProperty : Goliath.DynamicCode.Java.getProperties(toObject.getClass()))
        {
            if (!loAttributes.contains(lcProperty))
            {
                java.lang.Object loValue = Goliath.DynamicCode.Java.getPropertyValue(toObject, lcProperty);
                if (loValue != null)
                {
                    toBuilder.append("<" + lcProperty + " type=\"" + loValue.getClass().getName() + "\">");
                    appendObjectString(toBuilder, loValue);
                    toBuilder.append("</" + lcProperty + ">");
                }
            }
        }
    }
     * 
     */

    @Override
    protected boolean allowContent(SimpleDataObject toObject, XMLFormatType toFormat)
    {
        return true;
    }

}
