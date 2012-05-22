/* ========================================================
 * Utilities.java
 *
 * Author:      christinedorothy
 * Created:     Jun 13, 2011, 11:43:44 AM
 *
 * Description
 * --------------------------------------------------------
 * This is a utilities class that contains methods for different
 * XML functions that requires objects in Goliath.Data package.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Data.XML;

import Goliath.Exceptions.DataException;


        
/**
 * This is a utilities class that contains methods for different
 * XML functions that requires objects in Goliath.Data package.
 *
 * @version     1.0 Jun 13, 2011
 * @author      christinedorothy
**/
public final class Utilities extends Goliath.Object
{
    /**
     * Attempts to creates an object of type T from the xml provided.
     * If more than one item is in the XML provided, only the first one is created
     * @param toDataObject The data object to populate from the xml file
     * @param toDocument the xml document containing the data
     */
    public static <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> void fromXML(T toDataObject,  org.w3c.dom.Document toDocument) throws DataException
    {
        Goliath.Data.DataObjects.SimpleDataObject.fromXML(toDataObject, toDocument);
    }
}
