/* ========================================================
 * MinimumValue.java
 *
 * Author:      kenmchugh
 * Created:     Apr 30, 2011, 10:27:07 AM
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


        
import java.lang.annotation.*;


/**
 * Specifies the minimum value allowed for this method
 *
 * @see         Related Class
 * @version     1.0 Apr 30, 2011
 * @author      kenmchugh
**/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MinimumValue
{
    long value();
}

