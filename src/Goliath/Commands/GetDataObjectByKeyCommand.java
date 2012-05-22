/* =========================================================
 * GetDataObjectByKey.java
 *
 * Author:      kenmchugh
 * Created:     Jun 16, 2010, 9:20:42 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Commands;

import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.Interfaces.ISession;
import Goliath.Collections.PropertySet;
import Goliath.Data.DataObjects.DataObject;
import Goliath.Data.Query.InList;
import Goliath.Exceptions.DataObjectNotFoundException;

/**
 *
 * @author kenmchugh
 */
public class GetDataObjectByKeyCommand <T extends Goliath.Data.DataObjects.SimpleDataObject<T>> extends Command<GetDataObjectCommandArgs<T>, T>
{

    public GetDataObjectByKeyCommand(GetDataObjectCommandArgs<T> toArguments, ISession toSession)
    {
        super(toArguments, toSession);
    }

    public GetDataObjectByKeyCommand(GetDataObjectCommandArgs<T> toArguments)
    {
        super(toArguments);
    }

    public GetDataObjectByKeyCommand()
    {
        super(false);
    }

    @Override
    public T doExecute() throws Throwable
    {
        // Get the object to query with and make sure it is valid
        Class<T> loLookupClass = getArguments().getTemplateClass();
        PropertySet loProperties = getArguments().getProperties();

        Goliath.Utilities.checkParameterNotNull("LookupClass", loLookupClass);
        Goliath.Utilities.checkParameterNotNull("Properties", loProperties);
        
        // If we are looking up by property or by guid, just use the data object helper functions as they are quicker
        if (loProperties.size() == 1)
        {
            String lcProperty = loProperties.getPropertyKeys().get(0);
            if (lcProperty.equalsIgnoreCase("guid") || lcProperty.equalsIgnoreCase("id"))
            {
                try
                {
                    if (lcProperty.equalsIgnoreCase("guid"))
                    {
                        return DataObject.getObjectByGUID(loLookupClass, (String)loProperties.getProperty(lcProperty));
                    }
                    else
                    {
                        return DataObject.getObjectByID(loLookupClass, (Long)loProperties.getProperty(lcProperty));
                    }
                }
                catch (DataObjectNotFoundException ex)
                {
                    return null;
                }
            }
            else
            {
                SimpleDataObjectCollection<T> loCollection = new SimpleDataObjectCollection<T>(loLookupClass);
                loCollection.loadList(new InList(lcProperty, loProperties.getProperty(lcProperty)), 1);
                if (loCollection.size() > 0)
                {
                    return loCollection.get(0);
                }
            }
        }
        else if (loProperties.size() > 0)
        {
            SimpleDataObjectCollection<T> loCollection = new SimpleDataObjectCollection<T>(loLookupClass);
            //loCollection.loadList(loLookupClass, loProperties, 1);
            if (loCollection.size() > 0)
            {
                // We are only looking to get one item, so we will get the first item in the list
                return loCollection.get(0);
            }
        }
        return null;
    }
}
