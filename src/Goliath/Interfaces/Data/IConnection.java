/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Data;

import Goliath.Interfaces.DynamicCode.ISqlGenerator;

/**
 *
 * @author kenmchugh
 */
public interface IConnection
        extends java.sql.Connection
{
    /**
     * Leases out the connection
     * @return true if the connection has been leased correctly
     */
    boolean lease();
    
    /**
     * Validates the connection
     * @return false if the connection is no longer valid
     */
    boolean validate();
    
    /**
     * Checks if the connection is currently in use
     * @return true if the connection is in use
     */
    boolean inUse();
    
    /**
     * Gets the last time the connection was used
     * @return the last time the connection was used
     */
    long getLastUse();

    /**
     * Gets the query generator to use for creating queries
     * @return
     */
    ISqlGenerator getQueryGenerator();

    /**
     * Gets the connection string that was used to create this connection
     * @return the connection string
     */
    IConnectionString getConnectionString();
}
