/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Interfaces.Data.ConnectionTypes.IDataLayerAdapter;

/**
 *
 * @author kenmchugh
 */
public class DataBase extends Goliath.Object
        implements Goliath.Interfaces.Data.IDataBase
{
    private String m_cName;
    private Boolean m_lExists = null;
    private IDataLayerAdapter m_oDataLayerAdapter;


    public DataBase(String tcName, IDataLayerAdapter toAdapter)
    {
        Goliath.Utilities.checkParameterNotNull("tcName", tcName);
        m_oDataLayerAdapter = toAdapter;
        m_cName = tcName;
    }

    @Override
    public boolean create()
    {
        if (!exists())
        {
            m_lExists = this.getDataLayerAdapter().create(this);
            return m_lExists;
        }
        return false;
    }

    @Override
    public boolean exists()
    {
        if (m_lExists == null)
        {
            m_lExists = getDataLayerAdapter().exists(this);
        }
        return m_lExists;
    }

    @Override
    public String getName()
    {
        return m_cName;
    }

    private IDataLayerAdapter getDataLayerAdapter()
    {
        return m_oDataLayerAdapter;
    }
}
