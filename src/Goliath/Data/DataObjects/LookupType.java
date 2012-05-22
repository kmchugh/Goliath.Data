/* =========================================================
 * LookupType.java
 *
 * Author:      kmchugh
 * Created:     06-Feb-2008, 18:43:06
 * 
 * Description
 * --------------------------------------------------------
 * This class is used as a base class for any lookup object
 * Lookups always have the following:
 * - ID
 * - Name (required)
 * - Description (optional)
 * - Sequence
 * - IsDefault
 * - System
 *
 * It is possible for a lookup to also use a GUID by overriding
 * hasGUID so that it returns true
 *
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Constants.CacheType;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @param <T>
 * @see         Related Class
 * @version     1.0 06-Feb-2008
 * @author      kmchugh
**/
public abstract class LookupType<T extends LookupType> extends Goliath.Data.DataObjects.UndoableDataObject<T>
{
    private String m_cName;
    private String m_cDescription;
    private boolean m_lIsDefault;
    private boolean m_lSystem;
    private long m_nSequence;
    
    public LookupType()
    {
    }

    /**
     * Generally a lookup does not need a unique identity
     * @return false
     */
    @Override
    public boolean hasGUID()
    {
        return false;
    }

    /**
     * Usually a lookup type is a small list of objects, therefore by default
     * we will application cache the lookups to speed access
     * @return CacheType.APPLCATION();
     */
    @Override
    public CacheType getCacheType()
    {
        return CacheType.APPLICATION();
    }

    /**
     * Determines if this is a default value or not.
     * An example of default value use would be when the lookup types are
     * displayed to the user in a dropdown list.  The expectation would be that
     * the default value would be the selected value when the list was first
     * displayed
     * @return true if this is a default value, false otherwise
     */
    public boolean getIsDefault()
    {
        canReadProperty();
        return m_lIsDefault;
    }

    /**
     * Sets this value as being (or not being) a default value for this lookup type
     * @param tlValue true if this value should be a default, false otherwise
     */
    public void setIsDefault(boolean tlValue)
    {
        canWriteProperty();
        if (isDifferent(m_lIsDefault, tlValue))
        {
            m_lIsDefault = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Determines if this lookup is a system lookup.  System lookups are protected
     * and are not allowed to be changed unless it is the system user doing the changes.
     * System lookups are generally used and coded in to the system.
     * @return true if this is a system lookup, false otherwise
     */
    public boolean getSystem()
    {
        canReadProperty();
        return m_lSystem;
    }

    /**
     * Sets if this is a system value or not.  Anyone can make a value a system value,
     * but once made a system value, only the system user is then able to modify the value
     * @param tlValue true if this should be a system value, false otherwise.
     */
    public void setSystem(boolean tlValue)
    {
        // TODO: implement system variable protection
        canWriteProperty();
        if (isDifferent(m_lSystem, tlValue))
        {
            m_lSystem = tlValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the sequence of this lookup type.  An example of sequence use, would be
     * when a list of lookup types are displayed.  They would be displayed in the order
     * presented here.
     * @return the sequence of this lookup type
     */
    public long getSequence()
    {
        canReadProperty();
        return m_nSequence;
    }

    /**
     * Sets the sequence of this lookup type
     * @param tnValue
     */
    public void setSequence(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nSequence, tnValue))
        {
            m_nSequence = tnValue;
            propertyHasChanged();
        }
    }
    
    public String getName()
    {
        canReadProperty();
        return m_cName;
    }

    @Goliath.Annotations.UniqueIndex
    @Goliath.Annotations.MaximumLength(length=150)
    @Goliath.Annotations.NoNulls
    public void setName(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cName, tcValue))
        {
            m_cName = tcValue;
            propertyHasChanged();
        }
    }
    
    public String getDescription()
    {
        canReadProperty();
        return m_cDescription;
    }

    @Goliath.Annotations.MaximumLength(length=500)
    public void setDescription(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cDescription, tcValue))
        {
            m_cDescription = tcValue;
            propertyHasChanged();
        }
    }
}
