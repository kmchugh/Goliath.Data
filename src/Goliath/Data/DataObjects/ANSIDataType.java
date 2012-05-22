/* =========================================================
 * ANSIDataType.java
 *
 * Author:      Ken McHugh
 * Created:     Feb 19, 2008, 10:21:32 PM
 * 
 * Description
 * --------------------------------------------------------
 * ANSIDataType stores all of the datatypes that are defined
 * within the ANSI standard, this allows mapping between
 * the dataobjects and actual objects
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Constants.CacheType;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Exceptions.ObjectNotCreatedException;


/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Feb 19, 2008
 * @author      Ken McHugh
**/
public class ANSIDataType extends LookupType<ANSIDataType>
{
    /**
     * Creates a hash for this class that can be used as a guid
     * @param toClass the class to get the hash for
     * @return the has that can be used as a GUID
     */
    public static String createHash(Class toClass)
    {
        return Goliath.Utilities.encryptMD5(toClass.getName().toLowerCase());
    }
    
    /**
     * Gets an ANSI data type from the specified class, if the type does not exist, then it is created here
     * @param toClass the class to get the type for
     * @return the ANSIDataType
     */
    public static ANSIDataType getFromClass(Class toClass)
    {
        String lcHash = createHash(toClass);
        ANSIDataType loType = ANSIDataType.getObjectByGUID(ANSIDataType.class, lcHash);
        if (loType == null)
        {
            loType = createFromClass(toClass);
        }
        return loType;
    }
    
    /**
     * Creates the ANSIDataType from the class specified
     * @param toClass the class to create the ansi data type from, this must be a primitive class
     * @return the new ansi data type
     */
    public static ANSIDataType createFromClass(Class toClass)
    {
        // Can only create an ansi data type from primitive classes
        if (!Java.isPrimitive(toClass))
        {
            throw new InvalidParameterException("The class " + toClass + " is not a primitive type", "tcClass");
        }

        // Create a hash for the class
        String lcHash = createHash(toClass);
        ANSIDataType loType = new ANSIDataType();
        loType.setGUID(lcHash);
        loType.setName(toClass.getSimpleName());
        loType.setDescription("ANSI Data type representing the " + toClass.getSimpleName() + " class");
        loType.setSystem(true);
        if (loType.save())
        {
            return loType;
        }
        throw new ObjectNotCreatedException("Could not create the ANSIDataType using the class " + toClass.getName());
    }

    @Override
    public boolean hasGUID()
    {
        return true;
    }
    
    /**
     * Lets the data manager know what caching method to use for this class
     */
    @Override
    @Goliath.Annotations.NotProperty
    public CacheType getCacheType()
    {
        return CacheType.APPLICATION();
    }

}
