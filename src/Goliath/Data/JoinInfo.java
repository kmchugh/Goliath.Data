/* ========================================================
 * JoinInfo.java
 *
 * Author:      kenmchugh
 * Created:     May 9, 2011, 1:30:38 PM
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
 * ===================================================== */

package Goliath.Data;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Interfaces.Collections.IList;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 May 9, 2011
 * @author      kenmchugh
**/
public class JoinInfo<P extends SimpleDataObject<P>, S extends SimpleDataObject<S>> extends Goliath.Object
{
    public class PredicateInfo<T extends SimpleDataObject<T>>
    {
        private Class<T> m_oPredicateClass;
        private String m_cPredicateField;
        private java.lang.Object m_oPredicateValue;

        public PredicateInfo(Class<T> toPredicateClass, String tcField, java.lang.Object toValue)
        {
            m_oPredicateClass = toPredicateClass;
            m_cPredicateField = tcField;
            m_oPredicateValue = toValue;
        }

        public Class<T> getPredicateClass()
        {
            return m_oPredicateClass;
        }

        public String getPredicateField()
        {
            return m_cPredicateField;
        }

        public java.lang.Object getPredicateValue()
        {
            return m_oPredicateValue;
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
            final PredicateInfo<T> other = (PredicateInfo<T>) obj;
            if (this.m_oPredicateClass != other.m_oPredicateClass && (this.m_oPredicateClass == null || !this.m_oPredicateClass.equals(other.m_oPredicateClass)))
            {
                return false;
            }
            if ((this.m_cPredicateField == null) ? (other.m_cPredicateField != null) : !this.m_cPredicateField.equals(other.m_cPredicateField))
            {
                return false;
            }
            if (this.m_oPredicateValue != other.m_oPredicateValue && (this.m_oPredicateValue == null || !this.m_oPredicateValue.equals(other.m_oPredicateValue)))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 67 * hash + (this.m_oPredicateClass != null ? this.m_oPredicateClass.hashCode() : 0);
            hash = 67 * hash + (this.m_cPredicateField != null ? this.m_cPredicateField.hashCode() : 0);
            hash = 67 * hash + (this.m_oPredicateValue != null ? this.m_oPredicateValue.hashCode() : 0);
            return hash;
        }



    }
    private Class<P> m_oPrimary;
    private Class<S> m_oSecondary;


    // TODO: Extend the predicates to allow for greater than, less than, not equals, may need to create a predicate object for that

    private IList<PredicateInfo> m_oPredicates;

    public JoinInfo(Class<P> toPrimary, Class<S> toSecondary)
    {
        m_oPrimary = toPrimary;
        m_oSecondary = toSecondary;
    }

    public JoinInfo(Class<P> toPrimary, Class<S> toSecondary, String tcPredicateField, java.lang.Object toPredicateValue)
    {
        this(toPrimary, toSecondary);
        addPredicate(toPrimary, tcPredicateField, toPredicateValue);
    }

    public final <K extends SimpleDataObject<K>> java.lang.Object addPredicate(Class<K> toPredicateClass, String tcField, java.lang.Object toPredicateValue)
    {
        if (m_oPredicates == null)
        {
            m_oPredicates = new List<PredicateInfo>();
        }
        PredicateInfo loInfo = new PredicateInfo(toPredicateClass, tcField, toPredicateValue);
        return (!m_oPredicates.contains(loInfo)) ? m_oPredicates.add(loInfo) : false;
    }
    
    public Class<P> getPrimaryObject()
    {
        return m_oPrimary;
    }

    public Class<S> getSecondaryObject()
    {
        return m_oSecondary;
    }

    public IList<PredicateInfo> getPredicates()
    {
        return m_oPredicates == null ? new List<PredicateInfo>(0) : m_oPredicates;
    }

}
