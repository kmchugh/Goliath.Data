/* ========================================================
 * PrimaryKey.java
 *
 * Author:      kmchugh
 * Created:     Dec 13, 2010, 12:20:05 PM
 *
 * Description
 * --------------------------------------------------------
 * Specifies that the method is the primary key for the simple data object
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Annotations;

/**
 * Specifies that the method is the primary key for the simple data object
 * @author kenmchugh
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PrimaryKey
{
    // TODO: Use this to define the primary key names within the datasource
    String name() default "";
}
