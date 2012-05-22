/* =========================================================
 * NotUndoable.java
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
 * This annotation is used to mark an instance variable as
 * not undoable when saving the state.  Any Variables marked
 * with this will be skipped over when saving.
 * 
 * @version     1.0 Jan 9, 2008
 * @author Ken McHugh
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotUndoable {

}
