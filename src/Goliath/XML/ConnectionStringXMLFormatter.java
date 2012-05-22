/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.XML;

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.XMLFormatType;
import Goliath.Data.ConnectionString;
import Goliath.Interfaces.Collections.IPropertySet;
import java.util.Arrays;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author kmchugh
 */
public class ConnectionStringXMLFormatter extends Goliath.XML.XMLFormatter<ConnectionString>
{

    @Override
    public Class supports()
    {
        return ConnectionString.class;
    }

    @Override
    protected boolean allowContent(ConnectionString toObject, XMLFormatType toFormatType)
    {
        return true;
    }

    @Override
    protected List<String> getAttributeList(ConnectionString toObject)
    {
        return new Goliath.Collections.List<String>();
    }

    @Override
    protected void onWriteContent(XMLStreamWriter toStream, ConnectionString toObject, XMLFormatType toFormatType)
    {
        if (toObject == null)
        {
            return;
        }
        
        
        try
        {


            toStream.writeStartElement("Name");
            toStream.writeAttribute("type", toObject.getName().getClass().getName());
            XMLFormatter.appendToXMLStream(toObject.getName(), toFormatType, toStream, null);
            toStream.writeEndElement();

            IPropertySet loProperties = toObject.getParameters();
            Object[] loKeys = loProperties.keySet().toArray();
            Arrays.sort(loKeys);

            if (loProperties != null && loProperties.size() > 0)
            {
                toStream.writeStartElement("Parameters");

                for (int i = 0; i < loKeys.length; i++)
                {
                    String loKey = (String)loKeys[i];
                    Object loValue = loProperties.getProperty(loKey);

                    if (loValue != null)
                    {
                        try
                        {
                            toStream.writeStartElement("Parameter");

                            toStream.writeStartElement("Name");

                            if (Goliath.DynamicCode.Java.isPrimitive(loKey) && toFormatType == XMLFormatType.TYPED())
                            {
                                toStream.writeAttribute("type", loKey.getClass().getName());
                            }

                            XMLFormatter.appendToXMLStream(loKey, toFormatType, toStream, null);
                            toStream.writeEndElement();


                            toStream.writeStartElement("Value");

                            if (Goliath.DynamicCode.Java.isPrimitive(loValue) && toFormatType == XMLFormatType.TYPED())
                            {
                                toStream.writeAttribute("type", loValue.getClass().getName());
                            }


                            XMLFormatter.appendToXMLStream(loValue, toFormatType, toStream, null);
                            toStream.writeEndElement();

                            toStream.writeEndElement();
                        }
                        catch(Throwable ex)
                        {}
                    }
                }


                toStream.writeEndElement();

            }
        }
        catch(Throwable ex)
        {}
    }

    @Override
    protected void onStartedElement(XMLStreamReader toReader, String tcNodeName, PropertySet toAttributes, Object toObject, XMLFormatType toFormatType)
    {
        if(tcNodeName.equalsIgnoreCase("Parameter"))
        {
            // We have found an item, so we need to start processing

            // Extract the Key and the Value
            iterateUntilStarting(toReader, "Name");
            Object loKey = fromXMLReader(toReader, toFormatType, null);
            iterateUntilStarting(toReader, "Value");
            Object loValue = fromXMLReader(toReader, toFormatType, null);

            if (loKey != null && loValue != null)
            {
                ((ConnectionString) toObject).setParameter((String)loKey, loValue);
            }
        }
    }
}
