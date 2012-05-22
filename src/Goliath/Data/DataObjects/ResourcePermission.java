/* =========================================================
 * ResourcePermission.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:57:20 PM
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

package Goliath.Data.DataObjects;

import Goliath.Date;

/**
 *
 * @author kenmchugh
 */
public class ResourcePermission extends UndoableDataObject<ResourcePermission>
{
    private String m_cTableName;
    private String m_cColumnName;
    private long m_nRowID;
    private String m_cHash;
    private Date m_dExpires;


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

    public String getHash()
    {
        canReadProperty();
        return m_cHash;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    @Goliath.Annotations.NoNulls
    public void setHash(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cHash, tcValue))
        {
            m_cHash = tcValue;
            propertyHasChanged();
        }
    }

    public Goliath.Date getExpires()
    {
        canReadProperty();
        return m_dExpires;
    }

    public void setExpires(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dExpires, toDate))
        {
            m_dExpires = toDate;
            propertyHasChanged();
        }
    }
}
