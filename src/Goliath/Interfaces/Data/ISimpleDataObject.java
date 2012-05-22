/* =========================================================
 * ISimpleDataObject.java
 *
 * Author:      kmchugh
 * Created:     29-Jan-2008, 13:57:52
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Data;

import Goliath.Constants.CacheType;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 29-Jan-2008
 * @author      kmchugh
**/
public interface ISimpleDataObject
{
    /**
     * Gets the ID of the object, this id is unique only across objects
     * of the same type
     * @return the ID
     */
    long getID();
    
    /**
     * Sets the id of the object
     * @param tnID the new id
     */
    void setID(long tnID);
    
    /**
     * Sets the GUID for this data object, generally this should not be called,
     * new data objects should be allowed to create their own guid
     * @param tcGUID the data objects new guid
     */
    void setGUID(String tcGUID);
    
    /**
     * Gets the unique id of the dataobject
     * @return the unique id of the data object
     */
    String getGUID();
    
    /**
     * Checks if this data object makes use of a GUID, all data objects have
     * a getGUID property, but not all data object require the persistance of that guid
     * @return true if this data object makes use of the guid
     */
    boolean hasGUID();
    
    /**
     * Gets the type of caching that this data object will be involved in
     * @return the Cache type that will be applied to this data object
     */
    CacheType getCacheType();
    
    /**
     * Checks if this data object is new, a new data object is one that has not been 
     * retrieved from a data source, but created programatically
     * @return true if this data object is new
     */
    boolean isNew();
    
    /**
     * Check if this data object has been marked as deleted
     * @return true if marked as deleted
     */
    boolean isDeleted();
    
    /**
     * Check if this data object has been modified but not yet saved
     * @return true if this has been modified but the modifications have not
     * yet been saved to the data source
     */
    boolean isModified();
    
}
