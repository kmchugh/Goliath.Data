/* =========================================================
 * DataObject.java
 *
 * Author:      kmchugh
 * Created:     22-Feb-2008, 12:20:49
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
 * =======================================================*/

package Goliath.Data.BusinessObjects;

import Goliath.Applications.Application;
import Goliath.Constants.CacheType;
import Goliath.Constants.LogType;
import Goliath.Constants.StringFormatType;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Exceptions.Exception;
import Goliath.Validation.BrokenRule;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 22-Feb-2008
 * @author      kmchugh
**/
public abstract class DataObject extends Goliath.Object
    implements Goliath.Interfaces.Data.IDataObject
{
    private Class m_oPrimaryClass = null;
    private boolean m_lDeleted = false;
    private Goliath.Data.DataObjects.SimpleDataObject m_oPrimaryObject = null;
    private Goliath.Validation.BrokenRulesCollection m_oBrokenRules = new Goliath.Validation.BrokenRulesCollection();
    
    /** Creates a new instance of DataObject
     * @param <T> 
     * @param toPrimary The name of the class that acts as the primary object for this data object
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(Class<T> toPrimary)
    {
        this(toPrimary, true);
    }
    
    /** Creates a new instance of DataObject
     * @param <T> 
     * @param toPrimary The name of the class that acts as the primary object for this data object
     * @param tlCreate if true, then an attempt will be made to create the primary class
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(Class<T> toPrimary, boolean tlCreate)
    {
        m_oPrimaryClass = toPrimary;
        if (tlCreate)
        {
            try
            {
                m_oPrimaryObject = toPrimary.newInstance();
            }
            catch (Throwable e)
            {
                throw new Goliath.Exceptions.ObjectNotCreatedException("Could not create object", e);
            }
        }
    }

    /** Creates a new instance of DataObject
     * @param <T>
     * @param toPrimary The name of the class that acts as the primary object for this data object
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(T toPrimary)
    {
        m_oPrimaryObject = toPrimary;
    }
    
    /** Loads the dataobject from the database where the guids match
     * This will not create the new object
     * @param toPrimary The name of the class that acts as the primary object for this data object
     * @param tcGUID The guid of the object to load from the database
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(Class<T> toPrimary, String tcGUID)
    {
        this(toPrimary, tcGUID, false);
    }
    
    /** Loads the dataobject from the database where the guids match
     * @param toPrimary The name of the class that acts as the primary object for this data object
     * @param tcGUID The guid of the object to load from the database
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(Class<T> toPrimary, long tnIDValue)
    {
        m_oPrimaryClass = toPrimary;
        m_oPrimaryObject = Goliath.Data.DataObjects.SimpleDataObject.getObjectByID(toPrimary, tnIDValue);
        if (m_oPrimaryObject == null)
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Could not create object");
        }
    }
    
    /** Loads the dataobject from the database where the guids match, if the object is not found, and tlCreate is false, this will throw an error
     * @param toPrimary The name of the class that acts as the primary object for this data object
     * @param tcGUID The guid of the object to load from the database
     * @param tlCreate Creates the object if it doesn't exist
     * @throws Goliath.Exceptions.ObjectNotCreatedException if tlCreate is false, and the object is not found in the database
     */
    protected <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> DataObject(Class<T> toPrimary, String tcGUID, boolean tlCreate)
    {
        m_oPrimaryClass = toPrimary;
        try
        {
            m_oPrimaryObject = toPrimary.newInstance();
            m_oPrimaryObject.setGUID(tcGUID);
            try
            {
                m_oPrimaryObject = (T)SimpleDataObject.getObjectByGUID(toPrimary, tcGUID);
            }
            catch (Throwable e)
            {
                if (tlCreate)
                {
                    m_oPrimaryObject = toPrimary.newInstance();
                    m_oPrimaryObject.setGUID(tcGUID);
                }
                else
                {
                    throw new Goliath.Exceptions.ObjectNotCreatedException("Could not find object", e);
                }
            }
        }
        catch (Throwable e)
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Could not create object", e);
        }
    }
    
    @Override
    public CacheType getCacheType()
    {
        return CacheType.NONE();
    }
    
    protected Goliath.Data.DataObjects.SimpleDataObject getPrimaryObject()
    {
        return m_oPrimaryObject;
    }
    protected void setPrimaryObject(Goliath.Data.DataObjects.SimpleDataObject toObject)
    {
        m_oPrimaryObject = toObject;        
    }    
    
    @Override
    public String getGUID()
    {
        return m_oPrimaryObject.getGUID();
    }
    
    protected abstract DataObject onCopy();
    protected abstract void onSave() throws Exception;
    protected abstract void onDelete();
    
    protected abstract void onValidate();
    protected abstract boolean onIsDirty();
    
    public final void validate()
    {
        // Clear all the current broken rules
        m_oBrokenRules.clear();
        onValidate();
    }
    
    @Override
    public long getID()
    {
        return m_oPrimaryObject.getID();
    }
    
    protected void validate(Goliath.Data.DataObjects.DataObject toObject)
    {
        if (!toObject.isValid())
        {
            getBrokenRules().addAll(toObject.getBrokenRules());
        }
    }
    
    @Goliath.Annotations.NotProperty
    public Goliath.Validation.BrokenRulesCollection getBrokenRules()
    {
        return m_oBrokenRules;
    }
    
    public boolean isValid()
    {
        validate();
        return getBrokenRules().size() == 0;
    }
    
    protected boolean isValid(Goliath.Data.DataObjects.DataObject toObject)
    {
        if (toObject != null && !toObject.isValid())
        {
            getBrokenRules().addAll(toObject.getBrokenRules());
            return false;
        }
        return true;
    }
    
    protected boolean isValid(DataObject toObject)
    {
        if (toObject != null && !toObject.isValid())
        {
            getBrokenRules().addAll(toObject.getBrokenRules());
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isNew()
    {
        return m_oPrimaryObject.isNew();
    }
    
    public final boolean isDirty()
    {
        return onIsDirty();        
    }
    
    public DataObject copy()
    {
        if (isValid())
        {
            return onCopy();
        }
        else
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Original data object does not validate");
        }
    }
    
    @Override
    public boolean save()
    {
        if (isValid())
        {
            try
            {
                onSave();
                
                // Remove the item from the cache
            }
            catch (Exception ex)
            {
                // TODO : Need to do something here with the error
            }
        }
        else
        {
            for (BrokenRule loRule : this.getBrokenRules())
            {
                Application.getInstance().log("Could not save " + this.getClass().getName() + " - " + loRule.getMessage(), LogType.WARNING());
            }
        }
        return isValid();
    }
    
    @Override
    public boolean isDeleted()
    {
        return m_lDeleted;
    }
    
    /**
     * Marks this item as deleted
     */
    public void delete()
    {
        onDelete();
        m_lDeleted = true;
    }

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
        final DataObject other = (DataObject) obj;
        return this.m_oPrimaryObject.equals(other.m_oPrimaryObject);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 71 * hash + (this.m_oPrimaryObject != null ? this.m_oPrimaryObject.hashCode() : 0);
        return hash;
    }

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return getGUID();                
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return this.copy();
    }
    
    
}
