/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Collections.List;
import Goliath.Constants.StringFormatType;
import Goliath.Interfaces.Data.IConnectionString;
import Goliath.Text.StringFormatter;

/**
 *
 * @author kenmchugh
 */
public class DataSourceStringFormatter extends Goliath.Text.StringFormatter<IConnectionString>
{
    /** Creates a new instance of JDBCMySQLStringFormatter */
    public DataSourceStringFormatter()
    {
    }

    @Override
    public String toString(Goliath.Interfaces.Data.IConnectionString toObject)
    {
        StringBuilder loBuilder = new StringBuilder();

        String lcPort = toObject.<String>getParameter("port");
        String lcDatabase = toObject.<String>getParameter("database");

        loBuilder.append(toObject.<String>getParameter("hostname"));

        if (lcPort != null && !lcPort.isEmpty())
        {
            loBuilder.append(":" + lcPort);
        }

        loBuilder.append("/");

        if (lcDatabase != null && !lcDatabase.isEmpty())
        {
            loBuilder.append(lcDatabase);
        }

        loBuilder.append("?");

        boolean llAmp = false;
        for (String lcPropertyName : toObject.getParameters().getPropertyKeys())
        {
            if (lcPropertyName.equalsIgnoreCase("port") &&
                    !lcPropertyName.equalsIgnoreCase("database") &&
                    !lcPropertyName.equalsIgnoreCase("hostname") &&
                    toObject.getParameters().getProperty(lcPropertyName) != null)
            {
                loBuilder.append((llAmp ? "&" : "") + lcPropertyName + "=" + toObject.getParameters().getProperty(lcPropertyName).toString());
                llAmp = true;
            }
            
        }
        return loBuilder.toString();
    }
    
    @Override
    public void appendPrimitiveString(StringBuilder toBuilder, IConnectionString toObject, StringFormatType toType)
    {
        
    }

    @Override
    protected void formatComplexProperty(StringBuilder toBuilder, String tcPropertyName, Object toValue, StringFormatter toFormatter, StringFormatType toType)
    {
        
    }

    @Override
    protected void formatForPropertyCount(StringBuilder toBuilder, int tnIndex, int tnCount, StringFormatType toFormatType)
    {
        
    }

    @Override
    public String formatNullObject()
    {
        return "";
    }

    @Override
    protected String getEndTag(IConnectionString toObject)
    {
        return "";
    }

    @Override
    protected String getStartTag(IConnectionString toObject)
    {
        return "";
    }

    @Override
    public List<StringFormatType> supportedFormats()
    {
        return new List<StringFormatType>(new StringFormatType[]{StringFormatType.DEFAULT()});
    }


}