/* ========================================================
 * DataQuery.java
 *
 * Author:      admin
 * Created:     Jul 19, 2011, 9:48:25 AM
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
 * This class can be used for creating complex queries
 *
 * @see         Related Class
 * @version     1.0 Jul 19, 2011
 * @author      admin
 **/
public class DataQuery<T> extends Goliath.Object
{

    private DataQuery m_oParent;
    private DataQuery m_oLeftArg;
    private DataQuery m_oRightArg;
    private DataQueryOperation m_oOperator;
    private String m_cProperty;
    private T m_oQueryData;
    private Class<? extends SimpleDataObject> m_oContextClass;

    /**
     * Creates an instance of DataQuery
     */
    protected DataQuery()
    {
    }
    
    /**
     * Creates a new instance of DataQuery, there is no reason to be able 
     * to create an empty query publicly
     * @param tcProperty the property in the class specified that this query is to be run against
     */
    protected DataQuery(String tcProperty)
    {
        m_cProperty = tcProperty;
    }

    /**
     * Creates a new instance of DataQuery, there is no reason to be able 
     * to create an empty query publically
     * @param tcProperty the property in the class specified that this query is to be run against
     * @param toQueryData the query data that is checked against the class and property
     */
    protected DataQuery(String tcProperty, T toQueryData)
    {
        m_cProperty = tcProperty;
        m_oQueryData = toQueryData;
    }

    /**
     * Creates a new instance of data query, allowing insertion of a sub query
     * and specification of a "NOT" clause
     * @param toInnerQuery the query to evaluate
     * @param tlInvert true if this should be returning the inverted results (not clause)
     */
    public DataQuery(DataQuery toInnerQuery, boolean tlInvert)
    {
        m_oLeftArg = toInnerQuery;
        m_oLeftArg.m_oParent = this;
        m_oOperator = DataQueryOperation.NOT();
    }

    /**
     * Creates a new instance of data query, allowing left and right side arguments.
     * This will AND the arguments by default
     * @param toArg1 the left argument
     * @param toArg2 the right argument
     */
    public DataQuery(DataQuery toArg1, DataQuery toArg2)
    {
        this(toArg1, toArg2, DataQueryOperation.AND());

    }

    /**
     * Creates a new instance of data query, allowing left and right side arguments, and
     * specifying how the arguments are related to each other
     * @param toArg1 the left argument
     * @param toArg2 the right argument
     * @param toOperation the operation to apply to the arguments
     */
    public DataQuery(DataQuery toArg1, DataQuery toArg2, DataQueryOperation toOperation)
    {
        m_oLeftArg = toArg1;
        m_oLeftArg.m_oParent = this;
        m_oRightArg = toArg2;
        m_oRightArg.m_oParent = this;
        m_oOperator = toOperation;
    }

    /**
     * Sets the left arguments of the data query
     * @param toArg1
     */
    public void setLeftArgument(JoinQuery toArg1)
    {
        m_oLeftArg = toArg1;
        m_oLeftArg.m_oParent = this;
    }

    /**
     * Sets the right arguments of the data query
     * @param toArg1
     */
    public void setRightArgument(JoinQuery toArg1)
    {
        m_oRightArg = toArg1;
        m_oRightArg.m_oParent = this;
    }

    /**
     * Gets the left arguments of this data query if there is one
     * @return the left argument or null if there is none
     */
    public DataQuery getLeftArgument()
    {
        return m_oLeftArg;
    }

    /**
     * Gets the right arguments of this data query if there is one
     * @return the right argument or null if there is none
     */
    public DataQuery getRightArgument()
    {
        return m_oRightArg;
    }

    /**
     * Gets the operator of this data query if there is one
     * @return the operator or null if there is none
     */
    public DataQueryOperation getOperator()
    {
        return m_oOperator;
    }

    /**
     * Gets the data used to validate this data query if there is any
     * @return the data or null if there is none
     */
    public T getQueryData()
    {
        return m_oQueryData;
    }

    /**
     * Sets the query data for this query
     * @param toQueryData the query data for this query
     */
    protected void setQueryData(T toQueryData)
    {
        m_oQueryData = toQueryData;
    }

    /**
     * Checks if the result of this data query should be inverted
     * @return true if this a a NOT clause
     */
    public boolean isInverted()
    {
        return m_oOperator == DataQueryOperation.NOT();
    }

    /**
     * Gets the property that this query is meant to run against
     * @return the property for the query
     */
    public String getQueryProperty()
    {
        return m_cProperty;
    }

    // TODO: Implement this
    public <T extends SimpleDataObject> boolean match(T toObject)
    {
        return true;
    }
    
    /**
     * Sets the class that this data query should be acting over, if this
     * is null, then the primary class will be defined up the by query generator
     * @return this, this makes the call chainable
     */
    public DataQuery setContext(Class<? extends SimpleDataObject> toPrimary)
    {
        m_oContextClass = toPrimary;
        return this;
    }

    public Class<? extends SimpleDataObject> getContext()
    {
        return m_oContextClass == null ? 
                (m_oParent != null) ? m_oParent.getContext() : null : m_oContextClass;
    }
    // TODO: Also need to parse this object from a string to recreate queries
}
