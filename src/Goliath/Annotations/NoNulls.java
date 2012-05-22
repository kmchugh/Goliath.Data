/* ========================================================
 * NoNulls.java
 *
 * Author:      kmchugh
 * Created:     Dec 12, 2010, 4:05:42 PM
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

package Goliath.Annotations;


        
/**
 * Specifies that this method is not allowed to store a null value
 *
 * @see         Related Class
 * @version     1.0 Dec 12, 2010
 * @author      kmchugh
**/
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NoNulls
{
}
