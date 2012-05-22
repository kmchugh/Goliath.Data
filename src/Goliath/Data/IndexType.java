/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.DynamicEnum;
import Goliath.Interfaces.Data.IIndexType;

/**
 *
 * @author kenmchugh
 */
public class IndexType extends DynamicEnum
        implements IIndexType
{
    protected IndexType(String tcType)
    {
        super(tcType);
    }
    
    private static IIndexType g_oUnique = null;
    
    public static IIndexType UNIQUE()
    {
        if (g_oUnique == null)
        {
            g_oUnique = new IndexType("unqiue");
        }
        return g_oUnique;
    }
}
