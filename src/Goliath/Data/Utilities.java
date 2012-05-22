/* =========================================================
 * Utilities.java
 *
 * Author:      kmchugh
 * Created:     24-Apr-2008, 18:54:19
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

package Goliath.Data;

import Goliath.Exceptions.DataException;
import Goliath.Interfaces.Data.IColumn;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 24-Apr-2008
 * @author      kmchugh
**/
public class Utilities 
{
    /** Creates a new instance of Utilities */
    public Utilities()
    {
    }
    
    public static boolean checkColumnExists(IColumn toColumn)
    {
        /*
        java.sql.Connection loConnection = null;
        String lcSelect = null;
        try
        {
            // TODO: implement SQLCodeGenerator as App setting or singleton
            Goliath.Interfaces.DynamicCode.ISqlGenerator loGenerator = toColumn.getTable().getConnection().getQueryGenerator();
            lcSelect = loGenerator.generateColumnExists(toColumn);
            
            loConnection = toColumn.getTable().getConnection();
            
            java.sql.PreparedStatement loStatement = loConnection.prepareStatement(lcSelect);
            loStatement.setMaxRows(1);
            
            java.sql.ResultSet loResults = loStatement.executeQuery();
            
            return true;
        }
        catch (SQLSyntaxErrorException ex)
        {
            Application.getInstance().log(lcSelect, LogType.EVENT());
            Application.getInstance().log(ex);
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            if (loConnection != null)
            {
                try
                {
                    loConnection.close();
                }
                catch (SQLException ex)
                {}
            }
        }
         *
         *
         */
        return false;
    }

}