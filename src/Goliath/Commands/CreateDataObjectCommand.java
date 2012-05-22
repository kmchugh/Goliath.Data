/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Commands;

import Goliath.Interfaces.ISession;

/**
 *
 * @author kenmchugh
 */
public class CreateDataObjectCommand<T extends Goliath.Data.DataObjects.SimpleDataObject<T>> extends Command<CreateDataObjectCommandArgs<T>, Goliath.Data.DataObjects.SimpleDataObject>
{

    public CreateDataObjectCommand(CreateDataObjectCommandArgs<T> toArguments, ISession toSession)
    {
        super(toArguments, toSession);
    }

    public CreateDataObjectCommand(CreateDataObjectCommandArgs<T> toArguments)
    {
        super(toArguments);
    }

    public CreateDataObjectCommand()
    {
        super(false);
    }

    @Override
    public T doExecute() throws Throwable
    {
        T loObject = getArguments().getObject();
        loObject.save();
        return loObject;
    }

}
