/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Interfaces.Data.IConnectionString;
import Goliath.PropertyHandlers.PropertyHelper;
import Goliath.PropertyHandlers.PropertyHelpers.DefaultPropertyHelper;
import org.w3c.dom.Element;

/**
 *
 * @author kenmchugh
 */
public class ConnectionStringPropertyHelper extends DefaultPropertyHelper
{

    @Override
    public Class getObjectType()
    {
        return IConnectionString.class;
    }

    @Override
    protected <T> T onReadProperty(Element toSource, Class<T> toClass)
    {
        try
        {
            IConnectionString loConnectionString = (IConnectionString)getType(toSource).newInstance();

            // Get the type in the collection
            for (int i=0; i<toSource.getChildNodes().getLength(); i++)
            {
                if (!Element.class.isAssignableFrom(toSource.getChildNodes().item(i).getClass()))
                {
                    continue;
                }
                Element loItem = (Element)toSource.getChildNodes().item(i);
                Object loObject = PropertyHelper.create(getBehaviour(), loItem).readProperty(loItem, getType(loItem));
                if (toSource.getChildNodes().item(i).getNodeName().equalsIgnoreCase("name"))
                {
                    loConnectionString.setName(loObject.toString());
                }
                else
                {
                    loConnectionString.setParameter(toSource.getChildNodes().item(i).getNodeName(), loObject);
                }
            }

            return (T)loConnectionString;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    @Override
    protected boolean onWriteProperty(Element toSource, Object toNewValue)
    {
        // Clear the current collection
        for (int i= toSource.getChildNodes().getLength() -1; i > 0; i--)
        {
            toSource.removeChild(toSource.getChildNodes().item(i));
        }

        IConnectionString loConnectionString = (IConnectionString)toNewValue;

        // Write the name
        Element loElement = toSource.getOwnerDocument().createElement("Name");
        toSource.appendChild(loElement);
        PropertyHelper.create(getBehaviour(), loConnectionString.getName()).writeProperty(loElement, loConnectionString.getName());

        // Write all of the set properties
        for (String loParameter : loConnectionString.getParameters().getPropertyKeys())
        {
            Object loValue = loConnectionString.getParameter(loParameter);
            if (loValue != null)
            {
                loElement = toSource.getOwnerDocument().createElement(loParameter);
                toSource.appendChild(loElement);
                PropertyHelper.create(getBehaviour(), loValue).writeProperty(loElement, loValue);
            }
        }
        return true;
    }
}
