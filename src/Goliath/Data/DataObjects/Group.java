/* =========================================================
 * Group.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:40:32 PM
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
public class Group extends UndoableDataObject<Group>
{
    // TODO: Add validation expires not null

    private String m_cName;
    private String m_cDescription;
    private Date m_dExpires;

    @Override
    public boolean hasGUID()
    {
        return true;
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
