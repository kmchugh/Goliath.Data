/* =========================================================
 * DataObjectCollection.java
 *
 * Author:      Ken McHugh
 * Created:     Jan 9, 2008, 12:54:34 AM
 * 
 * Description
 * --------------------------------------------------------
 * This class represents a list of data objects
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Collections;

import Goliath.Validation.*;

/**
 * This class represents a list of data objects
 *
 * @param <T> 
 * @see         Goliath.Data.BusinessObjects.DataObject
 * @version     1.0 Jan 9, 2008
 * @author      Ken McHugh
 **/
public class DataObjectCollection<T extends Goliath.Data.DataObjects.DataObject<T>>
        extends Goliath.Collections.SimpleDataObjectCollection<T>
        implements Goliath.Interfaces.Collections.IDataObjectCollection<T>
{

    private int m_nValidationLimit = 100;
    private BrokenRulesCollection m_oBrokenRules;

    /** Creates a new instance of DataObjectCollection */
    public DataObjectCollection(Class<T> toClass)
    {
        super(toClass);
    }

    /**
     * Saves all items in the collection
     * @throws Goliath.Exceptions.DataException
     */
    @Override
    public boolean save() throws Goliath.Exceptions.Exception
    {
        // Validate each object in the collection
        return isValid() ? super.save() : false;
    }

    public void setValidationLimit(int tnValidationLimit)
    {
        m_nValidationLimit = tnValidationLimit;
    }

    public int getValidationLimit()
    {
        return m_nValidationLimit;
    }

    /**
     * Returns whether the collection is completely valid or not - i.e. each object
     * in the collection is to be vald before the collection is valid
     * @return whether there are broken rules or not
     */
    public boolean isValid()
    {
        for (T loObject : this)
        {
            if (!loObject.isValid())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Return broken rules (validation rules) for this collection
     * @return Collection containing all the broken rules 
     */
    public BrokenRulesCollection getBrokenRules()
    {
        m_oBrokenRules = new BrokenRulesCollection();
        for (T loObject : this)
        {
            if (!loObject.isValid())
            {
                for(BrokenRule loRule : loObject.getBrokenRules())
                {
                    if (m_oBrokenRules.size() >= m_nValidationLimit)
                    {
                        return m_oBrokenRules;
                    }
                    m_oBrokenRules.add(loRule);
                }
            }
        }
        return m_oBrokenRules;
    }


}
