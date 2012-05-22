/* ========================================================
 * DataAgent.java
 *
 * Author:      P Stanbridge
 * Created:
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 * The abstract class for Data Agents, Agents that can be saved to storage
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */
package Goliath.Data.DataObjects;

import Goliath.Date;
// import Goliath.Scheduling.Agent;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;


/**
 * Abstract class for Data Agents, These are agents that can be saved to storage
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0
 * @author      P Stanbridge
 **/
public abstract class DataAgent<T> extends UndoableDataObject<DataAgent> implements Goliath.Interfaces.Scheduling.IAgent<T>
{
    // Reference to the underlying agent object fot his data agent
    Agent m_oAgent;
/**
 * Constructor creates a reference to an agent defined as an inner class; Goliath.Scheduling.Agent is abstract. Pass this data agent into constructor
 */
    public DataAgent()
    {
      m_oAgent = new Agent(this);
    }

/**
 * Inner class to create a reference to an agent
 */
    private  class Agent extends Goliath.Scheduling.Agent<T>
    {
        // refernce to the data agent instance associated with this inner class
        DataAgent m_oDataAgent;

        private Agent(DataAgent loDataAgent)
        {
            m_oDataAgent = loDataAgent;
        }
        /**
        * Overridden onRun() to execute the data agent instance onRun()
        */
       @Override
       protected void onRun()
        {
           m_oDataAgent.onRun();
        }

       /**
        * Overridden onRun(T) to execute the data agent instance onRun(T)
        */
        @Override
       protected void onRun(T toItem)
        {
           m_oDataAgent.onRun(toItem);
        }

        /**
        * Overridden onGetItems() to execute the data agent onGetItems()
        */
        @Override
       protected List<T> onGetItems()
        {
           return m_oDataAgent.onGetItems();
        }
    }

    /**
     * abstract onRun() - extending classes will code onRun(), which will be executed as part of the scheduled agent run. This method runs
     * executable code (if applicable) without processing items.
     */
    abstract void onRun();
    /**
     * Abstract onRun(T) - extending classes will code onRun(T), which will be executed as part of the scheduled agent run. This method runs
     * executable code over each item. But items are optional.
     * @param toItem - the item to be run
     */
    abstract void onRun(T toItem);
    /**
     * Abstract onGetItems - extending classes will code onGetItems(), which will be executed as part of the scheduled agent run.
     * @return a list of items associated with the agent. The onRun(T) will run on each of these items.
     */
    abstract List<T> onGetItems();

    /**
     * Whether or not the data object has a GUID
     * @return true - the DataAgent does have a guid
     */
    @Override
    public boolean hasGUID()
    {
        return true;
    }

    /**
     * Returns the errors associated with the passed item
     * @param toItem
     * @return
     */
    @Override
    @Goliath.Annotations.NotProperty
    public List<Throwable> getItemErrors(T toItem)
    {
        canReadProperty();
        return m_oAgent.getItemErrors(toItem);
    }

    /**
     * Returns a table of errors associated with all the items processed by the agent
     * @return - table of errors associated with the items processed by the agent
     */
    @Override
    @Goliath.Annotations.NotProperty
    public HashTable<T, List<Throwable>> getItemErrors()
    {
        canReadProperty();
        return m_oAgent.getItemErrors();
    }

    /**
     * Run time errors associated with the general non-item agent run()
     * @return - list of errors encountered with the general non-item agent run()
     */
   @Override
   @Goliath.Annotations.NotProperty
    public List<Throwable> getErrors()
    {
        canReadProperty();
        return m_oAgent.getErrors();
    }
    
    /**
     * Run the agent
     */
    @Override
    public void run()
    {
        m_oAgent.run();
        
    }
    
    /**
     * dynamically determines if the agent can run - e.g. within start and end dates, not canceled or deactivated.
     * @return true if agent can run otherwise false
     */
    @Override
    public boolean toRun()
    {
        return m_oAgent.toRun();
    }

    /**
    * Load the agent so that it can run - called by scheduler
    */
    @Override
    public void load()
    {
        m_oAgent.load();
    }

    /**
     * whether or not the agent is executing
     * @return true if the agent is executing (run method executing) otherwise false
     */
    @Override
    public boolean isExecuting()
    {
       return getExecuting();
    }

    /**
     * private method that determines if the agent is executing - complies with the data object save
     * so will save the data object.
     * @return true if the agent is executing
     */
    private boolean getExecuting()
    {
        canReadProperty();
        return m_oAgent.isExecuting();
    }

    /**
     * set the isExecuting parameter
     * @param tlExecuting - boolean true if the agent is executing otherwise false
     */
    private void setExecuting(boolean tlExecuting)
    {
        canWriteProperty();
        if (isDifferent(getExecuting(), tlExecuting))
        {
            m_oAgent.setExecuting(tlExecuting);
            propertyHasChanged();
        }

    }

    /**
     * determines if the agent is active - and agent is active if it has not been paused
     * @return true if the agent is active otherwise if it has been deactivated, will return false
     */
    @Override
    public boolean isActive()
    {
        return getActivate();
    }

    /**
     * set the active flag to false
     */
    @Override
    public void deactivate()
    {
        setActivate(false);
    }

    /**
     * set the active flag to true
     */
    @Override
    public void activate()
    {
        setActivate(true);
    }

    /**
     * determines if the agent is active for data object save purposes
     * @return true if the agent is active otherwise false if deactivated
     */
    private boolean getActivate()
    {
        canReadProperty();
        return m_oAgent.isActive();
    }

    /**
     * Set the active flag - setter for the data object save
     * @param tlActivate - whether the agent is to be active or to be deactivated
     */
    private void setActivate(boolean tlActivate)
    {
        canWriteProperty();
        if (isDifferent(getActivate(), tlActivate))
        {
            if (tlActivate)
            {
                m_oAgent.activate();
            }
            else
            {
                m_oAgent.deactivate();
            }
            propertyHasChanged();
        }
    }

    /**
     * set the agent's between run interval
     * @param tnInterval - the time between each running of the agent in milliseconds
     */
    @Override
    public void setInterval(long tnInterval)
    {
        canWriteProperty();
        if (isDifferent(getInterval(), tnInterval))
        {
            m_oAgent.setInterval(tnInterval);
            propertyHasChanged();
        }
    }

    /**
     * return the between run interval of the agent
     * @return - the time between each running of the agent in milliseconds
     */
    @Override
    public long getInterval()
    {
        canReadProperty();
        return m_oAgent.getInterval();
    }

    /**
     * return the next run date of the agent
     * @return - the next run date/time of the agent
     */
    @Override
    public Date getNextRun()
    {
        canReadProperty();
        return m_oAgent.getNextRun();
    }

    /**
     * return the date in which the agent as last run
     * @return - the date/time the agent was last run
     */
    @Override
    public Date getLastRun()
    {
        canReadProperty();
        return m_oAgent.getLastRun();
    }

    /**
     * set the last run date for the agent
     * @param tdLastRunDate - date/time agent last run
     */
    public void setLastRun(Date tdLastRunDate)
    {
        canWriteProperty();
        if (isDifferent(getLastRun(), tdLastRunDate))
        {
            m_oAgent.setLastRun(tdLastRunDate);
            propertyHasChanged();
        }
    }

    /**
     * set the next run date/time for the agent to run
     * @param tdNextRunDate - the date/time the agent is next to run
     */
    public void setNextRun(Date tdNextRunDate)
    {
        canWriteProperty();
        if (isDifferent(getNextRun(), tdNextRunDate))
        {
            m_oAgent.setNextRun(tdNextRunDate);
            propertyHasChanged();
        }
    }

    /**
     * set the end date for the agent - the agent cannot be run after this date
     * @param tdEndDate - the date in which the agent will never be subsequently run
     */
    @Override
    public void setEndDate(Date tdEndDate)
    {
        canWriteProperty();
        if (isDifferent(getEndDate(), tdEndDate))
        {
            m_oAgent.setEndDate(tdEndDate);
            propertyHasChanged();
        }
    }

   /**
    * set the start date for the agent - the agent will not run until this date is reached
    * @param tdStartDate - the date prior to which the agent will not run
    */
    @Override
    @Goliath.Annotations.NoNulls
    public void setStartDate(Date tdStartDate)
    {
        canWriteProperty();
        if (isDifferent(getStartDate(), tdStartDate))
        {
            m_oAgent.setStartDate(tdStartDate);
            propertyHasChanged();
        }

    }

    /**
     * return the end date for this agent - the date after which the agent will no longer run
     * @return - the end date of the agent
     */
    @Override
    public Date getEndDate()
    {
        canReadProperty();
        return m_oAgent.getEndDate();
    }

    /**
     * return the start date for this agent - the date before which the agent will not run
     * @return - the start date of the agent
     */
    @Override
    public Date getStartDate()
    {
        canReadProperty();
        return m_oAgent.getStartDate();
    }

    /**
     * Return the number of items to be processed by the agent
     * @return - the number of items to be processed by the agent
     */
    @Goliath.Annotations.NotProperty
    @Override
    public int getItemCount()
    {
        canReadProperty();
        return m_oAgent.getItemCount();
    }

    /**
     * Return the items to be processed by this agent. Note - and agent may have executable code (onRun()) with no items
     * @return - the items to be process by this agent
     */
    @Override
    @Goliath.Annotations.NotProperty
    public List<T> getItems()
    {
        canReadProperty();
        return m_oAgent.getItems();
    }

    /**
     * set whether to continue processing once encountering an error
     * @param tlContinue - boolean - true to continue to process after encountering an error otherwise false
     */
    @Override
    public void setContinueAfterError(boolean tlContinue)
    {
        canWriteProperty();
        if (isDifferent(getContinueAfterError(), tlContinue))
        {
            m_oAgent.setContinueAfterError(tlContinue);
            propertyHasChanged();
        }
    }

    /**
     * retrieve whether the agent should continue after encountering an error
     * @return - boolean - true if the agent processing should continue after encountering and error otherwise false
     */
    @Override
    public boolean getContinueAfterError()
    {
        canReadProperty();
        return m_oAgent.getContinueAfterError();
    }


    /**
     * returns whether the agent has items (to process) defined for it. An agent does not have to have items as the run() (implemented in the
     * overridden onRun() method) will process agent level logic.
     * @return - true if the agent has items defined for it
     */
    @Override
    public boolean hasItems()
    {
        return m_oAgent.hasItems();
    }

    /**
     * Cancel the agent - will stop the agent from running including processing items. But will not unregister it.
     */
    @Override
    public void cancel()
    {
        setCancelled(true);
    }

    /**
     * set the cancel flag
     * @param tlCancelled - true if the agent is to be canceled otherwise false
     */
    private void setCancelled(boolean tlCancelled)
    {
        canWriteProperty();
        if (isDifferent(getCancelled(), tlCancelled))
        {
            m_oAgent.cancel();
            propertyHasChanged();
        }
    }

    /**
     * returns whether or not the agent has been canceled
     * @return - true if the agent has been canceled otherwise false
     */
    @Override
    public boolean isCancelled()
    {
        return getCancelled();
    }

    /**
     * return whether the agent has been canceled
     * @return - true if the agent has been canceled otherwise false
     */
    private boolean getCancelled()
    {
        canReadProperty();
        return m_oAgent.isCancelled();
    }


}
