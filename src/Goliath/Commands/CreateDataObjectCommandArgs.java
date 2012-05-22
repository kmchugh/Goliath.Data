/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Commands;

/**
 *
 * @author kenmchugh
 */
public class CreateDataObjectCommandArgs<T extends Goliath.Data.DataObjects.SimpleDataObject<T>> extends Goliath.Arguments.Arguments
{
    private T m_oObject;

    public T getObject()
    {
        return m_oObject;
    }

    public CreateDataObjectCommandArgs(T toObject)
    {
        Goliath.Utilities.checkParameterNotNull("toObject", toObject);
        m_oObject = toObject;
    }

}
