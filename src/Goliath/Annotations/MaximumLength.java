/* ========================================================
 * MaximumLength.java
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

/**
 * Specifies the Maximum length of the method value
 * @author kenmchugh
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MaximumLength
{
    long length();
}
