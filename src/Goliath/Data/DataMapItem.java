/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Constants.CacheType;
import Goliath.Constants.StringFormatType;
import Goliath.Interfaces.Data.ISimpleDataObject;

/**
 *
 * @author kmchugh
 */
public class DataMapItem<T extends ISimpleDataObject> extends Goliath.Object
{
    private String m_cSupportedClassName;
    private String m_cConnectionStringName;
    private Boolean m_lUsesPrefix;
    private Integer m_nMaxQueryItems;
    private String m_cSourceName;
    private DataMap m_oDataMap;
    private CacheType m_oCacheType;


    /**
     * Creates a new instance of the DataMapItem, this will set the connection
     * string to null.  This will also set the
     * name of the data object to the simple name of the class
     * @param toMap the data map that this item belongs to
     * @param toClass the class that this data map is for
     */
    public DataMapItem(DataMap toMap, Class<T> toClass)
    {
        m_oDataMap = toMap;
        m_cSupportedClassName = toClass.getName();
        m_cConnectionStringName = null;
        m_lUsesPrefix = null;
        m_nMaxQueryItems = null;
        m_cSourceName = toClass.getSimpleName();
        updateCacheType(toClass);
    }
    
    
    /**
     * Creates a new instance of the DataMapItem
     * @param toMap The data map to attach the data map item to
     * @param toClass the call that is being mapped to the database
     * @param tcName the name of the data entity, or null to use the default (simple name of the class)
     * @param tcDefaultConnection the name of the connection to use, 
     *  the connection should exist in the list of sources.  Use null for the default connection
     * @param tcPrefix the prefix for this item, use null for the default prefix
     * @param tlUsePrefix true to use a prefix, false to ignore the prefix, null to use the default setting
     * @param tnMaxQuery the maximum number of items that can be queried from this object.  Null for default
     */
    public DataMapItem(DataMap toMap, Class<T> toClass, String tcName, String tcDefaultConnection, String tcPrefix, Boolean tlUsePrefix, Integer tnMaxQuery)
    {
        m_oDataMap = toMap;
        m_cSupportedClassName = toClass.getName();
        m_cConnectionStringName = tcDefaultConnection;
        m_lUsesPrefix = tlUsePrefix;
        m_nMaxQueryItems = null;
        m_cSourceName = tcName == null ? toClass.getSimpleName() : tcName;
        updateCacheType(toClass);
    }
    
    /**
     * Sets the cache type based on the class passed in
     * @param toClass the class to get the cache type from
     */
    private void updateCacheType(Class<T> toClass)
    {
        try
        {
            m_oCacheType = toClass.newInstance().getCacheType();
        }
        catch (Throwable ex)
        {
            m_oCacheType = CacheType.NONE();
        }
    }

    // TODO: Refactor this to use class instead of String
    public String getSupportedClassName()
    {
        return m_cSupportedClassName;
    }

    public String getSourceName()
    {
        return m_cSourceName;
    }

    @Goliath.Annotations.NotProperty
    public String getActualSourceName()
    {
        return ((usesPrefix()) ? m_oDataMap.getPrefix() : "" ) + m_cSourceName;
    }

    public Boolean getUsePrefix()
    {
        return m_lUsesPrefix;
    }

    @Goliath.Annotations.NotProperty
    public boolean usesPrefix()
    {
        return m_lUsesPrefix == null ? m_oDataMap.getUsePrefix() : m_lUsesPrefix;
    }


    public String getConnectionName()
    {
        return m_cConnectionStringName;
    }

    @Goliath.Annotations.NotProperty
    public String getActualConnectionName()
    {
        return (Goliath.Utilities.isNullOrEmpty(m_cConnectionStringName)) ? m_oDataMap.getDefaultConnection() : m_cConnectionStringName;
    }

    public CacheType getCacheType()
    {
        return m_oCacheType;
    }

    public void setCacheType(CacheType toType)
    {
        m_oCacheType = toType;
    }

    @Goliath.Annotations.NotProperty
    public int getDefaultQuerySize()
    {
        return m_nMaxQueryItems == null ? m_oDataMap.getDefaultMaxQuerySize() : m_nMaxQueryItems;
    }

    public Integer getMaxQuerySize()
    {
        return m_nMaxQueryItems;
    }

    public void setMaxQuerySize(Integer toValue)
    {
        m_nMaxQueryItems = toValue;
    }

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        if (toFormat == StringFormatType.DEFAULT())
        {
            return m_cSourceName +
                    (usesPrefix() ? "(" + getActualSourceName() + ")" : "") + " - " +
                    m_cSupportedClassName;
        }
        return super.formatString(toFormat);
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
        final DataMapItem other = (DataMapItem) obj;
        if ((this.m_cSupportedClassName == null) ? (other.m_cSupportedClassName != null) : !this.m_cSupportedClassName.equals(other.m_cSupportedClassName))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        return hash + ((m_cSupportedClassName != null) ? m_cSupportedClassName.hashCode() : 0);
    }





}
