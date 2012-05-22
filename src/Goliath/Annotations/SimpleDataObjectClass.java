/* ========================================================
 * SimpleDataObjectClass.java
 *
 * Author:      admin
 * Created:     Jul 18, 2011, 5:42:16 PM
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

import Goliath.Data.DataObjects.SimpleDataObject;
import java.lang.annotation.*;

/**
 * Specifies the simple data object class that is the backing
 * class for the method specified.  This is used to create joins
 * when selecting business objects dynamically
 *
 * @see         Related Class
 * @version     1.0 Jul 18, 2011
 * @author      admin
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleDataObjectClass
{
    Class<? extends SimpleDataObject> className();
    String methodName();
}