/* =========================================================
 * ReferenceType.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 5:32:52 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Constants.CacheType;
import Goliath.Exceptions.ObjectNotCreatedException;

/**
 *
 * @author kenmchugh
 */
public class MimeType extends LookupType<MimeType>
{
    /**
     * Helper function to create a hash for the specified mime type, this hash
     * can be used as a GUID
     * @param toMimeType the mime type to get the hash for
     * @return the string hash 
     */
    public static String createHash(Goliath.Constants.MimeType toMimeType)
    {
        return Goliath.Utilities.encryptMD5(Goliath.Constants.MimeType.class.getName() + "." + toMimeType.getValue());
    }

    /**
     * Helper function to get the data object for the specified mime type constant, if
     * it doesn't already exist, this will create it
     * @param toMimeType the mime type constant
     * @return the data object representation of the Mime Type
     */
    public static Goliath.Data.DataObjects.MimeType getMimeTypeFor(Goliath.Constants.MimeType toMimeType)
    {
        String lcHash = createHash(toMimeType);
        MimeType loMimeType = MimeType.getObjectByGUID(MimeType.class, lcHash);

        if (loMimeType == null)
        {
            loMimeType = createMimeTypeFor(toMimeType);
        }

        return loMimeType;
    }

    /**
     * Creates the mime type data object representation for the mime type constant specified
     * @param toMimeType
     * @return 
     */
    public static Goliath.Data.DataObjects.MimeType createMimeTypeFor(Goliath.Constants.MimeType toMimeType)
    {
        MimeType loMimeType = new MimeType();
        loMimeType.setGUID(createHash(toMimeType));
        loMimeType.setName(toMimeType.getValue());
        loMimeType.setSystem(true);
        loMimeType.setDescription(toMimeType.getValue());
        
        if (!loMimeType.save())
        {
            throw new ObjectNotCreatedException("Could not create the Mime Type for " + toMimeType.getValue());
        }
        return loMimeType;
    }

    /**
     * A Mime type makes use of a GUID
     * @return 
     */
    @Override
    public boolean hasGUID()
    {
        return true;
    }

    /**
     * This will be a small number of objects, so this can be cached as part of the 
     * Application cache
     * @return 
     */
    @Override
    public CacheType getCacheType()
    {
        return CacheType.APPLICATION();
    }
}
