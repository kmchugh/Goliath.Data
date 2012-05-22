package Goliath.Annotations;

/**
 * Marks a property as being immutable, meaning that after the record
 * has been saved, that property can not be modified
 * @author admin
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Immutable
{
}
