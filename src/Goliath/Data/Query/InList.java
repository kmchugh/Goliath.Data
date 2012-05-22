/* ========================================================
 * InList.java
 *
 * Author:      admin
 * Created:     Jul 19, 2011, 10:04:22 AM
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
package Goliath.Data.Query;

import Goliath.Collections.List;
import Goliath.DynamicCode.Java;

/**
 * This data query checks if the data is in the specified list
 * <T> The type of the items in the list passed in
 * <P> The type of the property that is being checked
 * @see         Related Class
 * @version     1.0 Jul 19, 2011
 * @author      admin
 **/
public class InList<T, P> extends DataQuery<java.util.List<P>>
{

    /**
     * Used for primitive classes such as a list if integers, this will
     * check if tcProperty is equal to a value in toList, the list type should be the same as
     * the property type
     * @param tcProperty the property from the class being queried that we are checking against
     * @param toList the list of values to check
     */
    public InList(String tcProperty, java.util.List<T> toList)
    {
        // TODO: Parse for primitives
        super(tcProperty, (List<P>)toList);
    }
    
    /**
     * Used for primitive classes such as a list if integers, this will
     * check if tcProperty is equal to a value in toList, the list type should be the same as
     * the property type
     * @param tcProperty the property from the class being queried that we are checking against
     * @param taList the list of values to check
     */
    public InList(String tcProperty, T[] taList)
    {
        // TODO: Parse for primitives
        super(tcProperty, (List<P>)new List<T>(taList));
    }
    
    /**
     * Used for primitive classes such as integers, this will
     * check if tcProperty is equal to a value toParameter, the parameter type should be the same as
     * the property type
     * @param tcProperty the property from the class being queried that we are checking against
     * @param toParameter the value to check
     */
    public InList(String tcProperty, T toParameter)
    {
        this(tcProperty, (T[])new Object[]{toParameter});
    }
    
    /**
     * Used for lists of complex objects, extracts the property tcListObjectProperty value 
     * from each item in the list, and uses that data for the query
     * @param tcProperty the property from the class being queried that we are checking against
     * @param toList the list of values to check
     * @param tcListObjectProperty the property from the item in the list to check
     */
    public InList(String tcProperty, java.util.List<T> toList, String tcListObjectProperty)
    {
        super(tcProperty);
        
        // Parse the values
        List<P> loList = new List<P>(toList.size());
        
        for (T loItem : toList)
        {
            P loValue = Java.<P>getPropertyValue(loItem, tcListObjectProperty, true);
            if (loValue != null)
            {
                loList.add(loValue);
            }
        }
        setQueryData(loList);
    }
}
