/* =========================================================
 * UndoableDataObject.java
 *
 * Author:      Ken McHugh
 * Created:     Jan 9, 2008, 1:43:49 AM
 * 
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 * Data object class that allows changes to be rolled back (in memory only)
 * Changes will be commited to the database when saved and cannot be rolled back
 * at that point
 *
 * @see         Goliath.Data.BusinessObjects.DataObject
 * @version     1.0 Jan 9, 2008
 * @author      Ken McHugh
**/
public abstract class UndoableDataObject<T extends UndoableDataObject> extends Goliath.Data.DataObjects.DataObject<T> 
        implements Goliath.Interfaces.IUndoableObject
{
    private int m_nEditLevel;

    
    /**
     * Saves the item to the database and returns the new version of the item 
     *
     * @throws Goliath.Exceptions.UnsupportedOperationException if attempting to save a child object
     * @throws Goliath.Exceptions.ValidationException if the object has unapplied changes or is not valid
     * @return  the new version of the object
     */
    /*
    public T save()
    {
        try
        {
            return super.save();
        }
        catch (Throwable ex)
        {
            // TODO: Decide which is best, to throw the exception and have it unchecked, or
            // to handle the exception in the SimpleDataObject

        }
        return null;
        
        // Check if this item was loaded as a child item
        if (this.isChild())
        {
            throw new Goliath.Exceptions.UnsupportedOperationException("Can not save a child object, must save the parent");
        }
        
        // Check if this item has unapplied changes
        if (getEditLevel() > 0)
        {
            throw new Goliath.Exceptions.ValidationException("Object is being edited");
        }
        
        // Check if the item is valid
        if (!isValid())
        {
            throw new Goliath.Exceptions.ValidationException(getValidationRules().getBrokenRules().toString());
        }
        
        // Check if the item is even dirty (has been modified)
        if (isDirty)
        {
            return (T)Goliath.Data.DataPortal.Client.DataPortal.save(this);                        
        }
        else
        {
            return (T)this;
        }
         
    }
     * 
     */
    
    /**
     * Copies the internal state of the object 
     */
    @Override
    public void copyState()
    {}
    
     /**
     * Reverts the internal state to the original state
     */
    @Override
    public void undoChanges()
    {}
    
     /**
     * mark a new original state point and remove all history
     */
    @Override
    public void acceptChanges()
    {}
    
     /**
     * undo a single state level
     */
    @Override
    public void undo()
    {}
    
     /**
     * redo a single state level
     */
    @Override
    public void redo()
    {}
    
    /**
     * Checks if it is possible to undo an action 
     *
     * @return  true if it is possible to undo a level
     */
    @Override
    public boolean canUndo()
    { 
        return false;
    }

    /**
     * Checks if it is possible to redo an action 
     *
     * @return  true if it is possible to redo a level
    */
    @Override
    public boolean canRedo()
    {
        return false;
    }
}