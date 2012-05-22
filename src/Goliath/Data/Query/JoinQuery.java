/* ========================================================
 * JoinQuery.java
 *
 * Author:      admin
 * Created:     Aug 10, 2011, 6:36:01 PM
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
package Goliath.Data.Query;

import Goliath.Data.DataObjects.SimpleDataObject;

/**
 * This Data Query Joins in the specified table, based on the dataquery specified
 *
 * @see         Related Class
 * @version     1.0 Aug 10, 2011
 * @author      admin
 **/
public class JoinQuery extends DataQuery
{
    private Class<? extends SimpleDataObject> m_oSecondary;
    private String m_cProperty;
    private String m_cForeignKey;
    
    /**
     * Creates a new JoinQuery that will join toJoin in to the query based on toQuery
     * specified
     * @param toJoin the object to join in to the query
     * @param toQuery the query to join using
     */
    public JoinQuery(Class<? extends SimpleDataObject> toJoinClass, String tcProperty, String tcKey)
    {
        super(null);
        m_oSecondary = toJoinClass;
        m_cProperty = tcProperty;
        m_cForeignKey = tcKey;
    }
    
    public Class<? extends SimpleDataObject> getSecondary()
    {
        return m_oSecondary;
    }
    
    public String getProperty()
    {
        return m_cProperty;
    }
    
    public String getForeignKey()
    {
        return m_cForeignKey;
    }
}
