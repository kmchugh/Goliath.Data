/* =========================================================
 * QueryArguments.java
 *
 * Author:      kenmchugh
 * Created:     Sep 13, 2010, 8:30:26 AM
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

package Goliath.Data;

import Goliath.Arguments.Arguments;
import Goliath.Collections.PropertySet;
import Goliath.Interfaces.Collections.IList;

/**
 *
 * @author kenmchugh
 */
public class QueryArguments extends Arguments
{
    private PropertySet m_oFilter;
    private String[] m_aOrder;
    private IList<JoinInfo> m_oJoinInfo;
    
    public QueryArguments(PropertySet toFilter)
    {
        m_oFilter = toFilter;
    }

    public QueryArguments(IList<JoinInfo> toJoinConstraint)
    {
        m_oJoinInfo = toJoinConstraint;
    }

    public QueryArguments(PropertySet toFilter, String[] taOrder)
    {
        this(toFilter);
        m_aOrder = taOrder;
    }

    public PropertySet getFilter()
    {
        return m_oFilter;
    }

    public String[] getOrder()
    {
        return m_aOrder;
    }

    public IList<JoinInfo> getJoinInfo()
    {
        return m_oJoinInfo;
    }
}
