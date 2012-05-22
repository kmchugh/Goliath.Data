/* =========================================================
 * DataException.java
 *
 * Author:      kmchugh
 * Created:     29-Jan-2008, 13:42:13
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
 * Should be thrown when an exception occurs in a data query
 *
 * @see         Related Class
 * @version     1.0 29-Jan-2008
 * @author      kmchugh
**/
public class DataException extends Goliath.Exceptions.UncheckedException
{
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param tcMessage   The error message
    */
    public DataException(String tcMessage)
    {
        super(tcMessage);
    }
    
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param tcMessage   The error message
    * @param toException The inner exception
    */
    public DataException(String tcMessage, java.lang.Exception toException)
    {
        super(tcMessage, toException);
    }
    
    /**
    * Creates a new instance of InvalidOperationException
    *
    * @param toException The inner exception
    */
    public DataException(java.lang.Throwable toException)
    {
        super(toException);
    }
    
    /**
    * Creates a new instance of InvalidOperationException
    */
    public DataException()
    {
        super("An error occurred during a data operation");
    }
    
    
   
}
