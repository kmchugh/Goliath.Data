/* =========================================================
 * MultiLingualValue.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 3:51:33 PM
 *
 * Description
 * --------------------------------------------------------
 * MultiLingual value is used to specify the translation of
 * a specific string value in a db field
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 *
 * @author kenmchugh
 */
public class MultiLingualValue extends UndoableDataObject<MultiLingualValue>
{
    private long m_nLanguageID;
    private String m_cTableName;
    private String m_cColumnName;
    private long m_nRowID;
    private String m_cValue;

    private Language m_oLanguage;

    public String getTableName()
    {
        canReadProperty();
        return m_cTableName;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    @Goliath.Annotations.NoNulls
    public void setTableName(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cTableName, tcValue))
        {
            m_cTableName = tcValue;
            propertyHasChanged();
        }
    }

    public String getColumnName()
    {
        canReadProperty();
        return m_cColumnName;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    @Goliath.Annotations.NoNulls
    public void setColumnName(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cColumnName, tcValue))
        {
            m_cColumnName = tcValue;
            propertyHasChanged();
        }
    }

    public String getValue()
    {
        canReadProperty();
        return m_cValue;
    }

    @Goliath.Annotations.MaximumLength(length=2500)
    @Goliath.Annotations.NoNulls
    public void setValue(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cValue, tcValue))
        {
            m_cValue = tcValue;
            propertyHasChanged();
        }
    }


    public long getRowID()
    {
        canReadProperty();
        return m_nRowID;
    }

    public void setRowID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nRowID, tnValue))
        {
            m_nRowID = tnValue;
            propertyHasChanged();
        }
    }
    
    public long getLanguageID()
    {
        canReadProperty();
        if (m_oLanguage != null)
        {
            return m_oLanguage.getID();
        }
        return m_nLanguageID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Language.class, fieldName="ID")
    public void setLanguageID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nLanguageID, tnValue))
        {
            m_nLanguageID = tnValue;
            if (m_oLanguage != null && m_oLanguage.getID() != tnValue)
            {
                m_oLanguage = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public Language getLanguage()
    {
        if (m_oLanguage == null)
        {
            m_oLanguage = lazyLoad(Language.class, m_nLanguageID);
        }
        return m_oLanguage;
    }

    @Goliath.Annotations.NotProperty
    public void setLanguage(Language toType)
    {
        if (isDifferent(m_oLanguage, toType))
        {
            m_oLanguage = toType;
            setLanguageID(m_oLanguage.getID());
        }
    }

    

}
