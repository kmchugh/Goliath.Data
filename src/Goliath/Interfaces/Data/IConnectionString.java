/* =========================================================
 * IConnectionString.java
 *
 * Author:      kmchugh
 * Created:     12-Dec-2007, 15:24:15
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Data;

import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 12-Dec-2007
 * @author      kmchugh
**/
public interface IConnectionString 
{

    /**
     * Gets the type of connection
     * @return the Connection Type
     */
    IDataLayerAdapter getDataLayerAdapter();
    
    /**
     * Gets the propertybag of parameters 
     *
     * @return  the property bag with all the parameters for this connectionstring
     */
    Goliath.Interfaces.Collections.IPropertySet getParameters();
    
    /**
     * Sets a parameter value, throws an error if that parameter does not 
     * already exist
     *
     * @param  tcName the parameter to set
     * @param  toValue the value to set the parameter to
     * @return true if a parameter was changed due to this call
     */
    public <T> boolean setParameter(String tcName, T toValue)
            throws Goliath.Exceptions.InvalidParameterException;
    
    public <T> T getParameter(String tcName);

    public void setName(String tcName);
    public String getName();
            
}
