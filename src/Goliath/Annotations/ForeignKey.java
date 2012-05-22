/* ========================================================
 * ForeignKey.java
 *
 * Author:      kmchugh
 * Created:     Dec 13, 2010, 12:20:05 PM
 *
 * Description
 * --------------------------------------------------------
 * Defines the maximum length allowed for the specified method
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Annotations;

/**
 * Defines the class and field that this used as the foreign key for this method
 * @author kenmchugh
 */
import Goliath.Data.DataObjects.SimpleDataObject;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ForeignKey
{
    Class<? extends SimpleDataObject> className();
    String fieldName();
}
