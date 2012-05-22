/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data.DataObjects;

import Goliath.Date;

/**
 *
 * @author kmchugh
 */
public class Version extends LookupType<Version>
{
    private int m_nMajor;
    private int m_nMinor;
    private int m_nBuild;
    private int m_nRevision;
    private Date m_dRelease;
    
    
    public int getMajor()
    {
        canReadProperty();
        return m_nMajor;
    }
    
    public void setMajor(int tnValue)
    {
        canWriteProperty();
        if (m_nMajor != tnValue)
        {
            m_nMajor = tnValue;
            propertyHasChanged();
        }
    }
    
    public int getMinor()
    {
        canReadProperty();
        return m_nMinor;
    }
    
    public void setMinor(int tnValue)
    {
        canWriteProperty();
        if (m_nMinor != tnValue)
        {
            m_nMinor = tnValue;
            propertyHasChanged();
        }
    }
    
    public int getBuild()
    {
        canReadProperty();
        return m_nBuild;
    }
    
    public void setBuild(int tnValue)
    {
        canWriteProperty();
        if (m_nBuild != tnValue)
        {
            m_nBuild = tnValue;
            propertyHasChanged();
        }
    }
    
    public int getRevision()
    {
        canReadProperty();
        return m_nRevision;
    }
    
    public void setRevision(int tnValue)
    {
        canWriteProperty();
        if (m_nRevision != tnValue)
        {
            m_nRevision = tnValue;
            propertyHasChanged();
        }
    }

    public Goliath.Date getInstallDate()
    {
        canReadProperty();
        return m_dRelease;
    }

    public void setInstallDate(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dRelease, toDate))
        {
            m_dRelease = toDate;
            propertyHasChanged();
        }
    }



}
