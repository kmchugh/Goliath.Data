/* ========================================================
 * MaximumValue.java
 *
 * Author:      kenmchugh
 * Created:     Apr 30, 2011, 10:27:02 AM
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
 * Specifies the maximum numeric value that the property should
 * be allowed
 *
 * @see         Related Class
 * @version     1.0 Apr 30, 2011
 * @author      kenmchugh
**/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MaximumValue
{
    long value();
}

