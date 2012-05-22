/* =========================================================
 * UniqueIndex.java
 *
 * Author:      Ken McHugh
 * Created:     Jan 9, 2008, 7:11:37 PM
 * 
 * Description
 * --------------------------------------------------------
 * This annotation is used to mark an instance variable as
 * not undoable when saving the state.  Any Variables marked
 * with this will be skipped over when saving.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Annotations;

/**
 * Used to mark this method as requiring a unique value across all objects
 * of the same type
 * @author kenmchugh
 */
import java.lang.annotation.*;

// TODO: Allow for the implementation of other index types
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UniqueIndex
{
    String name() default "";
}
