/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

import Goliath.Collections.List;

/**
 *
 * @author kenmchugh
 */
public interface IIndex 
{
    IIndexType getType();
    String getName();
    List<IColumn> getColumns();
    boolean hasColumn(IColumn toColumn);
    boolean addColumn(IColumn toColumn);
    boolean removeColumn(IColumn toColumn);

}
