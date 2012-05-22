/* ========================================================
 * DynamicDataObjectCollection.java
 *
 * Author:      kenmchugh
 * Created:     Dec 30, 2010, 10:55:11 AM
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
 * ===================================================== */

package Goliath.Collections;

import Goliath.Data.BusinessObjects.DynamicDataObject;
import Goliath.Data.DataObjects.ValueList;
import Goliath.Data.Query.DataQuery;
import Goliath.Data.Query.InList;
import Goliath.Data.Query.JoinQuery;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Dec 30, 2010
 * @author      kenmchugh
**/
public class DynamicDataObjectCollection<T extends DynamicDataObject> extends BusinessObjectCollection<T>
{
    /**
     * Creates a new instance of DynamicDataObjectCollection, this will not load
     * any objects from the database
     */
    public DynamicDataObjectCollection(Class<T> toClass)
    {
        super(toClass);
    }

    @Override
    protected void onLoadList(DataQuery toQuery)
    {
        // Ensure we are loading only the correct type definition
        super.onLoadList(new InList<Long, Long>("TypeDefinitionID", new Long[]{getClassInstance().getPrimaryObject().getTypeDefinitionID()}));
        
        /*
        super.onLoadList(
                new DataQuery(
                    new InList<Long, Long>("TypeDefinitionID", new Long[]{getClassInstance().getPrimaryObject().getTypeDefinitionID()}), 
                    new DataQuery(
                        new JoinQuery(ValueList.class, "ObjectRegistryID", "ID"),
                        
                        toQuery.setContext(ValueList.class))));
=======
//        super.onLoadList(
//                new DataQuery(
//                    new InList<Long, Long>("TypeDefinitionID", new Long[]{getClassInstance().getPrimaryObject().getTypeDefinitionID()}),
//                    new DataQuery(
//                        new JoinQuery(ValueList.class, "ObjectRegistryID", "ID"),
//
//                        toQuery.setContext(ValueList.class))));
>>>>>>> .r1040
        */
        
        /*
         * SELECT DISTINCT 
         *  gdo_ObjectRegistry.guid, gdo_ObjectRegistry.createddate, gdo_ObjectRegistry.createdby, gdo_ObjectRegistry.typedefinitionid, gdo_ObjectRegistry.rowversion, gdo_ObjectRegistry.modifieddate, gdo_ObjectRegistry.modifiedby, gdo_ObjectRegistry.ObjectRegistryID 
         * FROM gdo_ObjectRegistry 
         *  JOIN gdo_ValueList 
         *      ON (gdo_ObjectRegistry.ObjectRegistryID = gdo_ValueList.ObjectRegistryID) 
         * WHERE (gdo_ObjectRegistry.typedefinitionid = 1  AND  
         * (  gdo_ValueList.ownerguid = '95b56a07-07ce-402d-ac3d-a936d8dc111b'))

         * */
    }
}
