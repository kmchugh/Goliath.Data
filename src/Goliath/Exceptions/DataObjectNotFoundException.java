/* =========================================================
 * DataObjectNotFoundException.java
 *
 * Author:      kmchugh
 * Created:     22-Feb-2008, 14:41:11
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
 * =======================================================*/

package Goliath.Exceptions;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 22-Feb-2008
 * @author      kmchugh
**/
public class DataObjectNotFoundException extends Goliath.Exceptions.UncheckedException
{
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param tcMessage   The error message
    */
    public DataObjectNotFoundException(String tcMessage)
    {
        super(tcMessage);
    }
    
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param tcMessage   The error message
    * @param toException The inner exception
    */
    public DataObjectNotFoundException(String tcMessage, java.lang.Exception toException)
    {
        super(tcMessage, toException);
    }
    
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param toException The inner exception
    */
    public DataObjectNotFoundException(java.lang.Exception toException)
    {
        super(toException);
    }
    
}