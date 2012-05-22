/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Annotations;

/**
 *
 * @author pStanbridge
 */
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegEx
{
    String matchString();
}
