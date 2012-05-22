/* =========================================================
 * SimpleDataObjectCollectionIterator.java
 *
 * Author:      P Stanbridge
 * Created:     29th May2008, 11:21
 *
 * Description
 * --------------------------------------------------------
 * Used for iterating a SimpleDataObjectCollection returning non-deleted records.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Collections;

import java.util.Iterator;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @param <T> 
 * @see         Related Class
 * @version     1.0 29-May-2008
 * @author      Peter
 **/
public class DataObjectCollectionIterator<T extends Goliath.Data.DataObjects.SimpleDataObject<T>> implements java.util.Iterator<T> 
{
    private SimpleDataObjectCollection<T> m_oCollection;
    private int m_nPointer;
    private int m_nLastPointer;
    private T m_oNext;

    /** Creates a new instance of SimpleDataObjectCollectionIterator
     * @param toDataObjectCollection - Simple data object collection to iterate over
     **/
    public DataObjectCollectionIterator(SimpleDataObjectCollection<T> toDataObjectCollection)
    {
        m_nPointer = -1;
        m_oCollection = toDataObjectCollection;
    }

    /**
     * Control loop for getting the next non deleted item in the list
     *
     * @return  the next Simple data object in the list or null if no items left
     * 
     */
    @Override
    public T next()
    {
        if (hasNext())
        {
            m_nPointer++;
            return m_oNext;
        }
        return null;
    }

    /**
     * Returns whether there is a next element in the collection
     * @return  boolean indicating whether there is a next member of the collection
     */
    @Override
    public boolean hasNext()
    {
        boolean llReturn = false;
        if (m_nLastPointer == m_nPointer)
        {
            return true;
        }
        while (!llReturn)
        {
            if (m_nPointer + 1 >= m_oCollection.size())
            {
                break;
            }
            m_oNext = m_oCollection.get(m_nPointer + 1);
            if (m_oNext == null)
            {
                // TODO: Find out why sometimes this picks up a null.
                try {Thread.currentThread().sleep(10);}catch(Throwable ex){}
                m_oNext = m_oCollection.get(m_nPointer + 1);
            }
            // TODO: Actually get() will only return sorted and/or filtered records, so is it still necessary to check isFilteredOff() here?
            if (m_oNext.isDeleted() || m_oCollection.isFilteredOut(m_nPointer + 1))
            {
                m_nPointer++;
                continue;
            }
            llReturn = true;
            m_nLastPointer = m_nPointer;
        }
        return llReturn;
    }

    /**
     * Unsupported for the moment.
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
