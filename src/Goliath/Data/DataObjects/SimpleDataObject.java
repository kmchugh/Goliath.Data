/* =========================================================
 * SimpleDataObject.java
 *
 * Author:      Ken McHugh
 * Created:     Jan 9, 2008, 7:11:37 PM
 * 
 * Description
 * --------------------------------------------------------
 * A simple data object is a lightweight data object.  It doesn't
 * have and validation or know about parent child relationships
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Constants.CacheType;
import Goliath.Constants.StringFormatType;
import Goliath.Data.DataManager;
import Goliath.Date;
import Goliath.DynamicCode.Java.MethodDefinition;
import Goliath.Exceptions.DataException;
import Goliath.Exceptions.DataObjectLockedException;
import Goliath.Exceptions.DataObjectNotFoundException;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Exceptions.ObjectNotCreatedException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A simple data object is a lightweight data object.  It doesn't
 * have and validation, authentication,  or know about parent child relationships
 *
 * @param <T> The type of the data object
 * @version     1.0 Jan 9, 2008
 * @author      Ken McHugh
**/
public abstract class SimpleDataObject<T extends SimpleDataObject> extends Goliath.Object
        implements Goliath.Interfaces.Data.ISimpleDataObject
{

    /**
     * Helper function to get an object by GUID, if the object could not be found this method will 
     * return null rather than throw an exception
     * @param <K> must extend simple data object
     * @param toClass The class of the object that we are retrieving
     * @param tcGUID The GUID of the object to get
     * @return The object requested, or null if an object does not exist
     */
    @Goliath.Annotations.NotProperty
    public static <K extends SimpleDataObject<K>> K getObjectByGUID(Class<K> toClass, String tcGUID)
    {
        K loTemplate = null;
        try
        {
            loTemplate = toClass.newInstance();
        }
        catch (Throwable ex)
        {
            throw new ObjectNotCreatedException(toClass);
        }
        
        // Make sure the class actually uses GUIDs, there is no point in doing the search if it doesn't
        if (!loTemplate.hasGUID())
        {
            throw new InvalidParameterException("Objects of type " + toClass.getSimpleName() + " do not use GUIDS.", "toClass");
        }

        // TODO: Convert this to use a dataQuery with EQUALS
        PropertySet loFilter = new PropertySet();
        loFilter.setProperty("GUID", tcGUID);
        SimpleDataObjectCollection<K> loList = getObjectsByProperty(toClass, loFilter);
        return loList != null && loList.size() == 1 ? loList.get(0) : null;
    }


    /**
     * Gets an object of the specified type.  The object to find will have the same value in the tcKey property
     * as the template has specified.  If multiple objects match, only the first object will be returned
     * @param <K> must extend from SimpleDataObject
     * @param toTemplate The template object, this object should have the properties that will be searched for set
     * @param tcKey the property from toTemplate to use for the search
     * @return the found object, or null if there was a problem
     * @throws DataObjectNotFoundException if the object was not found
     */
    // TODO: Remove this method
    @Goliath.Annotations.NotProperty
    public static <K extends SimpleDataObject<K>> SimpleDataObjectCollection<K> getObjectsByProperty(Class<K> toClass, PropertySet toPropertySet)
            throws DataObjectNotFoundException
    {
        return DataManager.getInstance().getDataItems(toClass, toPropertySet);
    }

    /**
     * Helper function for easily retrieving an object by a specific property when only one properties is being
     * checked
     * @param <K> The Type must extend the SimpleDataObject type
     * @param toClass the type of the object being retrieved
     * @param tcProperty the property the check is on
     * @param toValue the value the property must equal to be retrieved
     * @return the object or null if there was a problem
     * @throws DataObjectNotFoundException if the object was not found
     */
    public static <K extends SimpleDataObject<K>> SimpleDataObjectCollection<K> getObjectsByProperty(Class<K> toClass, String tcProperty, Object toValue)
    {
        PropertySet loProperties = new PropertySet(1);
        loProperties.setProperty(tcProperty, toValue);
        return getObjectsByProperty(toClass, loProperties);
    }


    

    /**
     * Helper function to get an object by ID
     * @param <K> must extend simple data object
     * @param toClass The class of the object that we are retrieving
     * @param tnID The ID of the object to get
     * @return The object requested or null if the object does not exist
     */
    @Goliath.Annotations.NotProperty
    public static <K extends SimpleDataObject<K>> K getObjectByID(Class<K> toClass, long tnID)
    {
        // Make sure the id is valid
        if (tnID <= 0)
        {
            throw new InvalidParameterException("An object of type " + toClass.getName() + " does not exist with an ID of less than 1", "tnID", tnID);
        }

        // TODO: Change this to a DataQuery using EQUALS
        PropertySet loFilter = new PropertySet();
        loFilter.setProperty("ID", tnID);
        
        SimpleDataObjectCollection<K> loCollection = getObjectsByProperty(toClass, loFilter);
        return loCollection != null && loCollection.size() == 1 ? loCollection.get(0) : null;
    }

    


    private boolean m_lIsNew;
    private boolean m_lIsDeleted;
    private boolean m_lIsModified;
    private boolean m_lLocked;
    private long m_nID;
    private long m_nCreatedStamp;
    private String m_cGUID;
    
    /**
     * Creates a new object of type SimpleDataObject
     */
    public SimpleDataObject()
    {
        m_lIsNew = true;
        m_lIsDeleted = false;
        m_lIsModified = true;
        m_lLocked = false;
        m_nID = 0;
        m_nCreatedStamp = new Date().getLong();
        
        m_cGUID = (hasGUID() ? Goliath.Utilities.generateStringGUID() : null);
    }

    /**
     * This is used to mark the object as an old, or loaded, rather than a new object
     */
    public void markOld()
    {
        m_lIsNew = false;
        m_lIsModified = false;
    }
    
    /**
     * Gets the cache type for this object
     * @return the cache type that should be applied to this object
     */
    @Goliath.Annotations.NotProperty
    @Override
    public CacheType getCacheType()
    {
        return CacheType.NONE();
    }
    
    /**
     * This is used to mark if a data object uses a GUID.  This is called during object construction so not all
     * data is going to be available
     * @return true if a GUID is used, false if not
     */
    @Override
    public boolean hasGUID()
    {
        return false;
    }


    @Override
    public final String getGUID()
    {
        return m_cGUID;
    }

    /**
     * Helper function to load a data object if it has not yet been loaded, but to return the data object
     * if it is already available.  This will ensure processing time is not lost if the ID is not set or if
     * the Object has already been loaded.
     *
     * General usage of this method should be when getting a linked object, and example of usage would be:
     *
     * // We are saving or adjusting, so save the TimePeriod First
        TimePeriod loPeriod = getSimpleDataObject(TimePeriod.class, m_nTimePeriodID, m_oTimePeriod);
        if (loPeriod != null)
        {
            loPeriod.save();
        }
     * 
     *
     * @param <K> The type of object being loaded
     * @param toClass the class of the object being loaded
     * @param tnID the ID of the object to load
     * @param toObject the object if it exists
     * @return the object, or null if tnID has not been set
     */
    protected <K extends SimpleDataObject<K>> K getSimpleDataObject(Class<K> toClass, long tnID, K toObject)
    {
        // There is no object loaded, and there is no ID identifying an object, so no point in trying anything
        if (tnID <= 0 && toObject == null)
        {
            return null;
        }

        return tnID > 0 && toObject == null ? SimpleDataObject.getObjectByID(toClass, tnID) : toObject;
    }

    @Override
    @Goliath.Annotations.UniqueIndex
    @Goliath.Annotations.MaximumLength(length=40)
    @Goliath.Annotations.NoNulls
    public final void setGUID(String tcGUID)
    {
        // If we are using a guid, we are not allowed to set the guid to null or empty
        if (Goliath.Utilities.isNullOrEmpty(tcGUID) && hasGUID())
        {
            throw new InvalidParameterException("The GUID value must not be null or empty", "tcParameter");
        }
        if (isDifferent(m_cGUID, tcGUID))
        {
            m_cGUID = tcGUID;
            onGUIDChanged();
        }
    }

    /**
     * A hook to allow subclasses to be notified on changes to the guid
     */
    protected void onGUIDChanged()
    {
    }

    @Override
    public final long getID()
    {
        return m_nID;
    }

    @Goliath.Annotations.PrimaryKey
    @Override
    public final void setID(long tnID)
    {
        // TODO: Protected this so it can not be called except for by the framework
        // The identity must always be greater than zero
        // A zero id can only be set in the constructor and means that the object is new
        // and has not been saved before
        if (tnID <= 0)
        {
            throw new InvalidParameterException("The ID value must be greater than zero", "tnID");
        }
        if (isDifferent(m_nID,tnID))
        {
            m_nID = tnID;
            onIDChanged();
        }
    }

    /**
     * A hook to allow subclasses to be notified when the ID changes
     */
    protected void onIDChanged()
    {
    }
    
    /**
     * Checks if this object has been modified in any way
     * @return true if there have been any modifications
     */
    @Override
    public final boolean isModified()
    {
        return m_lIsModified;
    }
    
    /**
     * Checks if this object is new, if the id is 0 then this is always considered new
     * @return
     */
    @Override
    public final boolean isNew()
    {
        return m_nID == 0 || m_lIsNew;
    }

    /**
     * Checks if this object has been marked for deletion
     * @return
     */
    @Override
    public final boolean isDeleted()
    {
        return m_lIsDeleted;
    }

    /**
     * Sets the modified flag for this object
     * @param tlValue the new modified state
     */
    protected final void setDirtyFlag(boolean tlValue)
    {
        if (m_lIsModified != tlValue)
        {
            m_lIsModified = tlValue;
            onDirtyFlagChanged();
        }
    }

    /**
     * A hook to allow subclasses to react when the dirty flag changes
     */
    protected void onDirtyFlagChanged()
    {
    }


    /**
     *  Standard CRUD methods for data objects
     */


    /**
     * Saves this data object to the data source
     * @return true if the save was successful, false if not
     * @throws Goliath.Exceptions.PermissionDeniedException if the user was not allowed to save
     */
    public synchronized final boolean save()
            throws Goliath.Exceptions.PermissionDeniedException
    {
        // First check if we need to bother saving
        if (isModified() || isNew())
        {
            // If this object is locked then we want to make sure not to proceed.
            if (m_lLocked)
            {
                throw new DataObjectLockedException();
            }

            // The object has been changed or it is new, so we will need to do something.
            try
            {
                // We will ask the data manager to do the actual saving as we don't know how
                if (onBeforeSave())
                {
                    long lnID = this.getID();
                    DataManager.getInstance().saveDataObject(this);
                    if (getID() > 0)
                    {
                        if (this.isDeleted())
                        {
                            //this object has been successfully deleted from the data source, so lock it to prevent further changes.
                            // There is no way to unlock a data object
                            // TODO: Write up some use cases to decide if unlocking is a valid and useful action
                            m_lLocked = true;
                        }
                        else
                        {
                            // The object was saved successfully
                            onSaveCompleted();
                            this.m_lIsModified = false;
                            this.m_lIsNew = false;

                            // Final hook after the object has been cleaned
                            this.onAfterUpdate();
                        }
                    }
                    else
                    {
                        // The save failed, give the subclass a chance to do something
                        onSaveFailed();
                        return false;
                    }
                }
            }
            catch(Throwable ex)
            {
                // The creation of the exception will log it automatically for us.
                new Goliath.Exceptions.Exception("Error saving " + this.getClass().getSimpleName(), ex);
                return false;
            }
        }
        return true;
    }

    /**
     * This method only marks the item for deletion, after being marked, it must be saved for the
     * object to be deleted from the data source
     */
    public final void delete()
    {
        this.m_lIsDeleted = true;
        this.m_lIsModified = true;
    }

    /**
     * This will unmark an object for deletion, however the object will still be marked as modified
     * This is just to ensure that if any modifications were made while the object was deleted, they
     * will still be saved.
     */
    public final void undelete()
    {
        // TODO: If the data object has already been saved and deleted, then this should throw an error
        m_lIsDeleted = false;
    }

    /**
     * A hook to allow the subclasses to interact with the save method
     * This method will be called when the object needs to be saved, but before
     * the DataManager has been asked to save.
     * @return true to allow the save to continue, false to stop the save
     */
    protected boolean onBeforeSave()
    {
        return true;
    }

    /**
     * This method is a hook to allow subclasses to take action when the save completed
     * After this hook the class is cleaned (set as not new, and not modified. Any changes
     * made to the object in this method will not cause the object to.
     * The updated ID will have already been set by the time this method is called
     */
    protected void onSaveCompleted()
    {
    }

    /**
     * This method is a hook to allow subclasses to take action after the save completed AND
     * after the cleaning has been completed, any changes made to the object in this method
     * will cause the object to become modified
     */
    protected void onAfterUpdate()
    {

    }

    /**
     * This method is a hook to allow subclasses to take action when the save failed
     */
    protected void onSaveFailed()
    {
    }

    /**
     *  END Standard CRUD methods for data objects
     */

    /**
     * Helper method for comparing two data objects
     * @param <K> the type of the objects being compared, both must be the same
     * @param toObject the first object to compare
     * @param toOther the second object to compare
     * @return true if the objects are different, false otherwise
     */
    protected final <K> boolean isDifferent(K toObject, K toOther)
    {
        // Check if these are actually the same objects
        if (toObject == toOther || (toObject == null && toOther == null))
        {
            return false;
        }

        if ((toObject != null && toOther == null) || (toOther != null && toObject == null))
        {
            return true;
        }

        // If we got to here, we know that the objects are different and at least one of them is not null
        return (toObject == null) ? !toOther.equals(toObject) : !toObject.equals(toOther);
    }

    /**
     * Helper method for comparing two boolean values, prevents boxing when comparing primitives
     * @param <T>
     * @param toObject the first object to compare
     * @param toOther the second object to compare
     * @return true if the objects are different, false otherwise
     */
    protected final boolean isDifferent(boolean tlObject, boolean tlOther)
    {
        return tlObject != tlOther;
    }

    /**
     * Helper method for comparing two integer values, prevents boxing when comparing primitives
     * @param <T>
     * @param toObject the first object to compare
     * @param toOther the second object to compare
     * @return true if the objects are different, false otherwise
     */
    protected final boolean isDifferent(int tnObject, int tnOther)
    {
        return tnObject != tnOther;
    }
    
    /**
     * Helper method for comparing two long values, prevents boxing when comparing primitives
     * @param <T>
     * @param toObject the first object to compare
     * @param toOther the second object to compare
     * @return true if the objects are different, false otherwise
     */
    protected final boolean isDifferent(long tnObject, long tnOther)
    {
        return tnObject != tnOther;
    }
    
    /**
     * Helper method for comparing two long values, prevents boxing when comparing primitives
     * @param <T>
     * @param toObject the first object to compare
     * @param toOther the second object to compare
     * @return true if the objects are different, false otherwise
     */
    protected final boolean isDifferent(float tnObject, float tnOther)
    {
        return tnObject != tnOther;
    }

    /**
     * Loads the object of the specified type
     * @param <T>
     * @param toClass The class to load
     * @param tnID The identity of the object to load
     * @return the new loaded object, or null if the object did not exist
     */
    protected final <T extends SimpleDataObject<T>> T lazyLoad(Class<T> toClass, Long tnID)
    {
        T loReturn = null;

        if (tnID != null && tnID > 0)
        {
            try
            {
                loReturn = SimpleDataObject.getObjectByID(toClass, tnID);
            }
            catch (DataObjectNotFoundException ex)
            {
                Application.getInstance().log(ex);
            }
        }

        return loReturn;
    }




    /**
     * This is a helper function for getting the value that is used as the identity of this object
     * If the object has a GUID then that is to be used, otherwise the ID numerical value is used
     * @return the GUID or the ID if a GUID is not used by this object
     */
    @Goliath.Annotations.NotProperty
    protected final Object getIDValue()
    {
        return hasGUID() ? getGUID() : getID();
    }



    @Override
    protected String formatString(StringFormatType toFormat)
    {
        if (toFormat == StringFormatType.DEFAULT())
        {
            return getIDValue().toString();
        }
        return super.formatString(toFormat);
    }

    /**
     * Compares two items and returns true if they are equal.
     * This compares the classes, then the guids.  If the
     * guids are null or empty, it compares the value from
     * getID().  Because new objects will all have an id value
     * of 0, if both objects have 0 as the id value they are only
     * considered equal if they are in the same address space
     *
     * @param  obj         The object to compare with
     * @return  true if the items are the same
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final SimpleDataObject<T> other = (SimpleDataObject<T>) obj;

        if (this.hasGUID())
        {
            if (this.m_cGUID != other.m_cGUID && (this.m_cGUID == null || !this.m_cGUID.equals(other.m_cGUID)))
            {
                return false;
            }
        }
        else
        {
            if (this.m_nID != other.m_nID)
            {
                return false;
            }

            // If both items are new, then we need to check the hash codes to determine if they are equal or not
            if (this.m_nID == 0 && other.m_nID == 0)
            {
                return this.hashCode() == other.hashCode();
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 11 * hash + (int) (this.m_nID ^ (this.m_nID >>> 32));
        hash = 11 * hash + (hasGUID() ? (this.m_cGUID != null ? this.m_cGUID.hashCode() : 0) : (int)m_nCreatedStamp);
        return hash;
    }

     
    @Override
    protected final Object clone() throws CloneNotSupportedException
    {
        return getClone();
    }

    public final T getClone()
    {
        return this.copy();
    }


    /**
     * Creates a copy of this item, this is not a deep copy
     * @return the copy of the item is returned
     */
    public T copy()
    {
        try
        {
            T loReturn = (T)this.getClass().newInstance();

            List<String> loProperties = Goliath.DynamicCode.Java.getPropertyMethods(this.getClass());
            for (String lcProperty: loProperties)
            {
                // If the property is not the id or guid
                if (!lcProperty.equalsIgnoreCase("id") && !lcProperty.equalsIgnoreCase("guid")&& !lcProperty.equalsIgnoreCase("rowversion"))
                {
                    Goliath.DynamicCode.Java.setPropertyValue(loReturn, lcProperty, Goliath.DynamicCode.Java.getPropertyValue(this, lcProperty, true), true);
                }
            }

            return loReturn;
        }
        catch (Throwable ex)
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Could not copy object", ex);
        }
    }




















    

    
    /**
     * Attempts to creates an object of type T from the xml provided.
     * If more than one item is in the XML provided, only the first one is created
     * @param <T>
     * @param toDataObject The data object to populate from the xml file
     * @param toDocument the xml document containing the data
     * @throws DataException
     */
    public static <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> void fromXML(T toDataObject,  org.w3c.dom.Document toDocument) throws DataException
    {
        try
        {
            NodeList loList = toDocument.getElementsByTagName(toDataObject.getClass().getSimpleName());
            if (loList.getLength() < 1)
            {
                throw new Goliath.Exceptions.DataException("No items found in XML of type " + toDataObject.getClass().getSimpleName());
            }

            // We only want to create one object, so we always create the first
            Node loDataName = loList.item(0);
            // We want to go through each of the inner nodes and populate the properties of the data object
            loList = loDataName.getChildNodes();
            for (int i=0; i<loList.getLength(); i++)
            {
                Node loNode = loList.item(i);
                String lcProperty = loNode.getNodeName();
                String lcValue = loNode.getTextContent();

                // If the property is isNew, isDeleted, or isDirty, need to take special actions
                if (Goliath.Utilities.inList(true, lcProperty, "isNew", "isDeleted", "isDirty"))
                {
                    boolean llValue = Boolean.parseBoolean(lcValue);
                    if (lcProperty.equalsIgnoreCase("isNew"))
                    {
                        if (llValue)
                        {
                            //toDataObject.markNew();
                        }
                        else
                        {
                            //toDataObject.markOld();
                        }
                    }
                    else if (lcProperty.equalsIgnoreCase("isDeleted"))
                    {
                        if (llValue)
                        {
                            //toDataObject.markDeleted();
                        }
                    }
                    else if (lcProperty.equalsIgnoreCase("isDirty"))
                    {
                        if (llValue)
                        {
                            //toDataObject.markDirty();
                        }
                        else
                        {
                            //toDataObject.markClean();
                        }
                    }
                    continue;
                }

                lcProperty = "set" + lcProperty;
                // Check if the method actually exists
                MethodDefinition loMethod = Goliath.DynamicCode.Java.getMethodDefinition(toDataObject.getClass(), lcProperty);
                if (loMethod != null)
                {
                    // At least one method exists, try to find the correct one
                    for (AccessibleObject loFunction : loMethod.getFunctionsWithParameterCount(1))
                    {
                        // This method only takes one parameter so it might be acceptable
                        Class loParameterType = ((Method)loFunction).getParameterTypes()[0];
                        try
                        {
                            Goliath.DynamicCode.Java.setPropertyValue(toDataObject, lcProperty, Goliath.Utilities.fromString(loParameterType, lcValue));
                        }
                        catch (Exception ex)
                        {
                            // Do nothing, the exception will be logged automatically
                        }
                    }
                }
            }
        }
        catch(DOMException ex)
        {
            new Goliath.Exceptions.Exception(ex);
        }
    }
    
}
