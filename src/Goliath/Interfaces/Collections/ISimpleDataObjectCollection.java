/* =========================================================
 * ISession.java
 *
 * Author:      Peter Stanbridge
 * Created:     Nov 37 May, 2008 11:21 AM
 * 
 * Description
 * --------------------------------------------------------
 * This interface represents a collection of simple data objects.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Interfaces.Collections;

import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.JoinQuery;
import Goliath.Exceptions.DataException;
import Goliath.Interfaces.IContainedClassIdentifiable;
import Goliath.Collections.List;

/**
 *
 * @param <T> 
 * @author Peter
 */
public interface ISimpleDataObjectCollection<T extends Goliath.Data.DataObjects.SimpleDataObject<T>>
        extends Goliath.Interfaces.Collections.IList<T>, IContainedClassIdentifiable
{

    /**
     * Gets the entire list of items of type class from the data source
     * The list is limited by the default number of items that are allowed to
     * be returned in a query for this type 
     * @throws DataException if there is a problem with retrieval
     */
    void loadList() 
            throws DataException;

    /**
     * Gets the entire list of items of type class from the data source
     * The list is limited by tnMaxItems
     * @param tnMaxItems the maximum number of items to get
     * @throws DataException if there is a problem with retrieval
     */
    void loadList(int tnMaxItems) 
            throws DataException;

    /**
     * Populates the list of data objects based on the data query that is supplied.
     * This list will be limited to the default maximum items
     * @param toClass the class that we are loading
     * @param toQuery the query that we are running
     * @throws DataException if there are any issues with the query or data retrieval
     */
    void loadList(DataQuery toQuery)
            throws DataException;

    /**
     * Populates the list of data objects based on the data query that is supplied.
     * @param toClass the class that we are loading
     * @param toQuery the query that we are running
     * @param tnMaxItems the maximum number of items that should be queried
     * @throws DataException if there are any issues with the query or data retrieval
     */
    void loadList(DataQuery toQuery, int tnMaxItems) throws DataException;

    /**
     * Populates the list of data objects based on the data query and joins that are supplied
     * @param toJoins the list of join query objects,
     * generally this should also include the data query objects that are related to the joined classes
     * @param toQuery the query that we are running
     * @throws DataException if there is problem with retrieval
     */
    void loadList(List<JoinQuery> toJoins, DataQuery toQuery) throws DataException;

    /**
     * Populates the list of data objects based on the data query and joins that are supplied
     * @param toJoins the list of join query objects,
     * generally this should also include the data query objects that are related to the joined classes
     * @param toQuery the query that we are running
     * @param tnMaxItems tnMaxItems the maximum number of items to get
     * @throws DataException if there is problem with retrieval
     */
    void loadList(List<JoinQuery> toJoins, DataQuery toQuery, int tnMaxItems) throws DataException;

    /**
     * Gets a sub set of the current list , that subset will only include
     * items that match the data query specified
     * @param toQuery the data query to use for the filter
     * @return the new list of data objects , 
     * this will return an empty list if there are not matches
     */
    ISimpleDataObjectCollection<T> subList(DataQuery toQuery);

    /**
     * delete - Mark each object in collection as deleted
     */
    void delete();

    /**
     * save - Save each object in collection to database
     * @return true if the operation is successful
     * @throws DataException 
     */
    boolean save() throws Goliath.Exceptions.Exception;

    /**
     * Checks if the collection is dirty
     * @return true if the collection is dirty
     */
    boolean isModified();
    
    /**
     * Checks if this list has been loaded from the data source, if not, then 
     * it means a call to loadList has not been made, or clear has been called
     */
    boolean isLoaded();
    
    // TODO: Implement this
    /**
     * Gets a sub set of the current list, that subset will only include
     * items that match the data query specified
     * @param toQuery the data query to use for the filter
     * @return the new list of data objects, this will return an empty list if there are not matches
     */
    //ISimpleDataObjectCollection<T> subList(DataQuery toQuery);
}
