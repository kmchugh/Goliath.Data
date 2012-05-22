/* ========================================================
 * ContainsType.java
 *
 * Author:      kenmchugh
 * Created:     Jan 18, 2011, 2:57:03 PM
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
 * Specifies the type that the method contains
 *
 * @see         Related Class
 * @version     1.0 Jan 18, 2011
 * @author      kenmchugh
**/
import java.lang.annotation.*;

// TODO: May be able to remove this annotation

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ContainsType
{
    Class contains();
}
