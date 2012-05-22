/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.XML;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.CacheType;
import Goliath.Constants.XMLFormatType;
import Goliath.Data.DataMap;
import Goliath.Data.DataMapItem;
import java.util.Arrays;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author kmchugh
 */
public class DataMapXMLFormatter extends Goliath.XML.XMLFormatter<DataMap>
{

    @Override
    public Class supports()
    {
        return DataMap.class;
    }

    @Override
    protected boolean allowContent(DataMap toObject, XMLFormatType toFormatType)
    {
        return true;
    }

    @Override
    protected List<String> getAttributeList(DataMap toObject)
    {
        return new Goliath.Collections.List<String>(new String[]{"prefix", "defaultConnection", "usePrefix"});
    }

    @Override
    protected void onWriteContent(XMLStreamWriter toStream, DataMap toObject, XMLFormatType toFormatType)
    {
        if (toObject == null)
        {
            return;
        }

        HashTable<String, DataMapItem> loItemLookup = new HashTable<String, DataMapItem>();

        for (DataMapItem loItem : toObject.getDataItems())
        {
            loItemLookup.put(loItem.getSourceName(), loItem);
        }

        Object[] loKeys = loItemLookup.keySet().toArray();
        Arrays.sort(loKeys);
        for (int i = 0; i < loKeys.length; i++)
        {
            String loKey = (String)loKeys[i];
            DataMapItem loValue = loItemLookup.get(loKey);

            if (loValue != null)
            {
                try
                {
                    toStream.writeStartElement("DataMapItem");

                    toStream.writeStartElement("SupportedClassName");
                    toStream.writeAttribute("type", loValue.getSupportedClassName().getClass().getName());

                    XMLFormatter.appendToXMLStream(loValue.getSupportedClassName(), toFormatType, toStream, "SupportedClassName");
                    toStream.writeEndElement();
                    
                    XMLFormatter.appendToXMLStream(loValue.getCacheType(), toFormatType, toStream, "CacheType");

                    toStream.writeEndElement();
                }
                catch(Throwable ex)
                {}
            }
        }
    }

    @Override
    protected void onStartedElement(XMLStreamReader toReader, String tcNodeName, PropertySet toAttributes, Object toObject, XMLFormatType toFormatType)
    {
        if (tcNodeName.equalsIgnoreCase("DataMapItem"))
        {
            String lcPrefix = toAttributes.containsKey("prefix") ? toAttributes.<String>getProperty("prefix") : null;
            String lcDefaultConnection = toAttributes.containsKey("defaultConnection") ? toAttributes.<String>getProperty("defaultConnection") : null;
            String lcName = toAttributes.containsKey("name") ? toAttributes.<String>getProperty("name") : null;
            Boolean llUsePrefix = toAttributes.containsKey("usePrefix") ? toAttributes.<String>getProperty("usePrefix").equalsIgnoreCase("true") : false;
            Integer lnMaxResults = toAttributes.containsKey("maxResults") ? new Integer(Integer.parseInt(toAttributes.<String>getProperty("maxResults"))) : null;
            
            // We have found an item, so we need to start processing
            iterateUntilStarting(toReader, "SupportedClassName");
            Object loSupportedClass = fromXMLReader(toReader, toFormatType, null);

            // Extract the Key and the Value
            iterateUntilStarting(toReader, "CacheType");
            Object loCacheType = fromXMLReader(toReader, toFormatType, null);
            
            try
            {
                Class loClass = Class.forName(loSupportedClass.toString());
                DataMapItem loItem = ((DataMap)toObject).addMapItem(loClass, lcName, llUsePrefix, lcPrefix, lcDefaultConnection, lnMaxResults);
                loItem.setCacheType((CacheType)loCacheType);
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
    }

    @Override
    protected DataMap onCreateObject(Class<DataMap> toClass, PropertySet toAttributes, Object toObject)
    {
        DataMap loReturn = new DataMap();

        String lcPrefix = toAttributes.<String>getProperty("prefix");
        String lcDefaultConnection = toAttributes.<String>getProperty("defaultConnection");
        boolean llUsePrefix = toAttributes.containsKey("usePrefix") && toAttributes.<String>getProperty("usePrefix").equalsIgnoreCase("true");

        loReturn.setDefaultConnection(lcDefaultConnection);
        loReturn.setPrefix(lcPrefix);
        loReturn.setUsePrefix(llUsePrefix);

        return loReturn;
    }


}
