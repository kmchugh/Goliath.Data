/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Collections.ISimpleDataObjectCollection;

/**
 *
 * @param <T> 
 * @param <S> 
 * @author kenmchugh
 */
public interface IDataMapper<T extends ISimpleDataObject, S extends Object>
{
    /**
     * Gets a list of all the classes that are supported by this mapper
     * @return the list of classes
     */
    List<Class<T>> getSupportedClasses();
    
    /**
     * Gets a data object by key from the data source
     * @param toParameters the data object to use as the template for the search
     * @param tcKey the key to use (field)
     * @param tlThrowError if true and an object is not found, an error will be thrown
     * @return the object found or null
     * @throws DataException 
     */
    T getObjectByKey(T toParameters, String tcKey, boolean tlThrowError) throws DataException;
    
    /**
     * Gets a data object by id from the data source
     * @param toParameters the data object to use as the template for the search
     * @param tlThrowError if true and an object is not found, an error will be thrown
     * @return the object found or null
     * @throws DataException 
     */
    T getObjectByID(T toParameters, boolean tlThrowError) throws DataException;
    
    
    /**
     * Gets a data object by id from the data source
     * @param toClass the type of the object to get 
     * @param tnKey the id of the object
     * @param tlThrowError if true and an object is not found, an error will be thrown
     * @return the object found or null
     * @throws DataException 
     */
    T getObjectByID(Class<T> toClass, long tnKey, boolean tlThrowError) throws DataException;
    
    /**
     * Gets a List of items using the specified where fields
     * @param toParameters 
     * @param taWhereFields 
     * @param taOrderFields the fields to use to order the results
     * @param tlThrowError if true and an object is not found, an error will be thrown
     * @return An object containing the results of the query
     * @throws DataException 
     */
    java.lang.Object[] getList(T toParameters, String[] taWhereFields, String[] taOrderFields, boolean tlThrowError) throws DataException;
    
    /**
     * Gets a data object by id from the data source
     * @param toSearchCollection the collection of items to use to retrieve the data for the in
     * @param taInFields the list of fields to compare
     * @param taInDataFields the list of fields that contain the data to compare 
     * @param taOrderFields the fields to use to order the results
     * @param tlThrowError if true and an object is not found, an error will be thrown
     * @return An object containing the results of the query
     * @throws DataException 
     */
    java.lang.Object[] getListIn(T toParameters, ISimpleDataObjectCollection<?> toSearchCollection, String[] taInFields, String[] taInDataFields, String[] taOrderFields, boolean tlThrowError) throws DataException;
    
    /**
     * Saves the object to the datasource and modifies the object with any updates
     * @param toObject the object to save
     * @throws Goliath.Exceptions.DataException 
     */
    void save(T toObject) throws Goliath.Exceptions.DataException;
    
    void createFromDataSource(T toObject, S toSource) throws Goliath.Exceptions.DataException;
    
    T convertObject(T toParameter, java.lang.Object toObject);
    
    

}
