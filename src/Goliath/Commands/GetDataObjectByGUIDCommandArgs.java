/* =========================================================
 * GetDataObjectByGUIDCommandArgs.java
 *
 * Author:      kenmchugh
 * Created:     Jun 16, 2010, 9:41:26 PM
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

import Goliath.Data.DataObjects.SimpleDataObject;

/**
 *
 * @author kenmchugh
 */
public class GetDataObjectByGUIDCommandArgs<T extends SimpleDataObject<T>> extends GetDataObjectCommandArgs<T>
{
    public GetDataObjectByGUIDCommandArgs(T toLookup)
    {
        super(toLookup, new String[]{"GUID"});
    }

}
