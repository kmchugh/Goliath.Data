/* =========================================================
 * Permission.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 4:57:07 PM
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
public class Permission extends LookupType<Permission>
{
    private Date m_dExpires;
    private String m_cHash;

    @Override
    public boolean hasGUID()
    {
        return true;
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

    public String getHash()
    {
        canReadProperty();
        return m_cHash;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setHash(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cHash, tcValue))
        {
            m_cHash = tcValue;
            propertyHasChanged();
        }
    }
}
