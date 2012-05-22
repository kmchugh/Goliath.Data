package Goliath.Applications;

import Goliath.Collections.List;
import Goliath.Data.DataManager;
import Goliath.Data.DataMap;
import Goliath.Data.DataMapItem;
import Goliath.Exceptions.CriticalException;
import Goliath.Interfaces.Data.ISimpleDataObject;

/**
 * The DataSourceStartup class is a startup module that when run ensures the
 * data objects and data bases required by the application exist and are up to date
 *
 * @version     1.0 17-Jun-2008
 * @author      kmchugh
**/
public class DataSourceStartup extends Startup
{
    /** Creates a new instance of DataSourceStartup */
    public DataSourceStartup()
    {

    }
    
    /**
     * Makes sure that the data source for this item and the data entity (table)
     * exists for this item
     * @param toItem the item to check
     * @return true if this already exists
     * @throws CriticalException if the data map items DataObject did not exist and could not be created
     */
    private boolean checkDataItem(DataMapItem toItem)
    {
        // Check the object and make sure it exists in the database
        // This will also make sure that the data source exits
        if (!DataManager.getInstance().ensureDataObjectExists(toItem))
        {
            // The data object did not exist in the data source and we could not create it
            throw new CriticalException(toItem.getActualSourceName() + " does not already exist in the data source, and could not be created.");
        }
        return true;
    }

    /**
     * Ensure all of the Data Items exist for each of the DataObjects
     */
    @Override
    protected boolean onRun()
    {
        DataMap loMap = DataManager.getInstance().getDataMap();
        boolean llModified = false;

        // Loop through all of the data objects and make sure they are in the data map
        List<Class<ISimpleDataObject>> loSDOClasses = Application.getInstance().getObjectCache().getClasses(ISimpleDataObject.class);
        for (Class<ISimpleDataObject> loClass : loSDOClasses)
        {
            // Check if the map already has the object
            if (!loMap.hasMapItem(loClass))
            {
                llModified = true;
                loMap.addMapItem(loClass);
            }
        }

        // TODO: This should be done automatically when cleaning up the datamanager
        // Save the data map if needed
        if (llModified)
        {
            DataManager.getInstance().saveDataMap();
        }

        // Loop through all of the DataItemMap objects and make sure they exist in the correct data source
        for (DataMapItem loItem : loMap.getDataItems())
        {
            checkDataItem(loItem);
        }
        return true;
    }

    
    /**
     * We don't need to do anything specific for the first run of the DataSourceStartup as we will be
     * Checking that the db and tables exist every run.  This will allow us to do simple updates
     * with each release.  A simple update is an addition of table or data source. 
     * @return true
     */
    @Override
    protected boolean onFirstRun()
    {
        return true;
    }

    @Override
    protected int onGetSequence()
    {
        // TODO: These integers should be changed to constants so it is easier to reorganise.  e.g. SYSTEMSTARTUP = 5, DATASTARTUP = 10, APPLICATIONSTARTUP = 9999
        return 5;
    }
}
