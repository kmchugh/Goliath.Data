/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

import Goliath.Collections.List;
import Goliath.Exceptions.DataException;

/**
 *
 * @author kenmchugh
 */
public interface ITable 
{
    String getName();
    boolean exists();
    boolean create();

    List<IColumn> getColumns();
    boolean hasColumn(IColumn toColumn);
    boolean addColumn(IColumn toColumn);
    boolean removeColumn(IColumn toColumn);
    
    List<IColumn> getKeyColumns();
    boolean hasKey(IColumn toKeyColumn);
    boolean addKey(IColumn toKeyColumn) throws DataException;
    boolean removeKey(IColumn toKeyColumn);
    IColumn getColumn(String tcColumnName);
    
    List<IIndex> getIndexes();
    boolean hasIndex(IIndex toIndex);
    boolean addIndex(IIndex toIndex) throws DataException;
    boolean removeIndex(IIndex toIndex);
    
    List<IRelation> getRelations();
    boolean hasRelation(IRelation toRelation);
    boolean addRelation(IRelation toRelation) throws DataException;
    boolean removeRelation(IRelation toRelation);

    /**
     * Helper functions for the tables
     */
    boolean addGUIDColumn();
    boolean addCreatedByColumn();
    boolean addCreatedDateColumn();
    boolean addModifiedByColumn();
    boolean addModifiedDateColumn();
    boolean addRowVersionColumn();
}
