package Goliath.Data.Query;

import Goliath.Collections.PropertySet;

/**
 * A Property Query is a query that allow matching of very specific properties.  Only
 * objects that match the properties will be returned
 * @author kmchugh
 */
public class PropertyQuery<T> extends Goliath.Data.Query.DataQuery
{
    private PropertySet m_oProperties;

    /**
     * Helper constructor to allow quick creation of a single value property set
     * @param tcProperty the name of the property
     * @param toValue the value that it should match
     */
    public PropertyQuery(String tcProperty, Object toValue)
    {
        Goliath.Utilities.checkParameterNotNull("tcProperty", tcProperty);
        // Note: the value of toValue can be null, it means we are matching to null values.
        
        m_oProperties = new PropertySet();
        m_oProperties.setProperty(tcProperty, toValue);
    }

    public PropertyQuery(PropertySet toProperties)
    {
        m_oProperties = toProperties;
    }


}
