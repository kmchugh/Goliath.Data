/* =========================================================
 * ConnectionString.java
 *
 * Author:      kmchugh
 * Created:     12-Dec-2007, 15:29:24
 * 
 * Description
 * --------------------------------------------------------
 * This class controls the creation of connection strings
 * for any server connections
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data;

import Goliath.Collections.PropertySet;
import Goliath.Constants.StringFormatType;
import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;
import Goliath.Interfaces.Data.IConnectionString;
import Goliath.Interfaces.IStringFormatter;

/**
 * This class controls the creation of connection strings
 * for any server connections
 *
 * @version     1.0 12-Dec-2007
 * @author      kmchugh
**/
public abstract class ConnectionString extends Goliath.Object implements Goliath.Interfaces.Data.IConnectionString
{
    private IDataLayerAdapter m_oDataLayerAdapter;
    private Goliath.Interfaces.Collections.IPropertySet m_oParameters;
    private String m_cName;
    
    /** Creates a new instance of ConnectionString */
    public ConnectionString()
    {
    }

    /** Creates a new instance of ConnectionString */
    public ConnectionString(String tcName)
    {
        m_cName = tcName;
    }

    /**
     * Returns the name of this connection string
     * @return the name of this string
     */
    @Override
    public String getName()
    {
        return m_cName;
    }

    /**
     * Sets the name of this connection string
     * @param tcName the new name of this string
     */
    @Override
    public void setName(String tcName)
    {
        m_cName = tcName;
    }
    
    /**
     * Gets the Property Set with the parameters and their settings
     *
     * @return  the property set with all of the properties for this string
     */
    @Override
    public final Goliath.Interfaces.Collections.IPropertySet getParameters()
    {
        return m_oParameters == null ? new PropertySet(0) : m_oParameters;
    }

    /**
     * Gets the Connection string formatter from the data adapter
     * @return the String formatter for this connection string
     */
    @Goliath.Annotations.NotProperty
    public final IStringFormatter<IConnectionString> getFormatter()
    {
        return getDataLayerAdapter().getFormatter();
    }

    /**
     * Gets the data layer adapter for this type of connection string
     * @return the data layer adapter
     */
    @Goliath.Annotations.NotProperty
    @Override
    public final IDataLayerAdapter getDataLayerAdapter()
    {
        if (m_oDataLayerAdapter == null)
        {
            m_oDataLayerAdapter = onCreateDataLayerAdapter();
        }
        return m_oDataLayerAdapter;
    }

    /**
     * Method to create the data layer for this connection string
     * @return
     */
    protected abstract IDataLayerAdapter onCreateDataLayerAdapter();

    
    /**
     * Sets a parameter value, throws an error if that parameter does not 
     * already exist.  This method will call onSetParameter to allow for hooking
     * for every parameter that is set
     *
     * @param <T>
     * @param  tcName the parameter to set
     * @param  toValue the value to set the parameter to
     * @return true if a parameter was changed due to this call
     * @throws Goliath.Exceptions.InvalidParameterException
     */
    @Override
    public final <T> boolean setParameter(String tcName, T toValue)
            throws Goliath.Exceptions.InvalidParameterException
    {
        if ((m_oParameters != null && m_oParameters.containsKey(tcName)) || getDataLayerAdapter().getParameters().contains(tcName.toLowerCase()))
        {
            if (m_oParameters == null)
            {
                m_oParameters = new PropertySet();
            }
            if (onSetParameter(tcName, toValue))
            {
                return m_oParameters.setProperty(tcName, toValue);
            }
            return false;
        }
        else
        {
            throw new Goliath.Exceptions.InvalidParameterException("Invalid connection parameter [" + tcName + "] for connection string " + this.getClass().getName(), tcName, toValue);
        }
    }

    /**
     * Hook to allow subclasses to interact with the setting of parameters
     * @param <T>
     * @param tcName the name of the parameter being set
     * @param toValue the value of the parameter
     * @return true to allow setting, false to stop setting of the parameter
     */
    protected <T> boolean onSetParameter(String tcName, T toValue)
    {
        return true;
    }

    /**
     * Gets the value of the specified parameter.  This will call onGetParameter to allow the
     * subclasses to interact if needed
     * @param <T>
     * @param tcName the name of the parameter
     * @return the parameter to get
     */
    @Override
    public final <T> T getParameter(String tcName)
    {
        return (m_oParameters != null) ?
            this.<T>onGetParameter(tcName) : null;
    }

    /**
     * Hook method for getting parameters
     * @param <T>
     * @param tcName the name of the parameter to get
     * @return the value to return for the parameter
     */
    protected <T> T onGetParameter(String tcName)
    {
        return (T)m_oParameters.getProperty(tcName);
    }

    /**
     * Uses the formatter for all toString calls
     * @param toFormat irrelevant, not used
     * @return the formatted string
     */
    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return getFormatter().toString(this, toFormat);
    }
}
