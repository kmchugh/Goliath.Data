/* =========================================================
 * DataObject.java
 *
 * Author:      Ken McHugh
 * Created:     Jan 9, 2008, 12:54:04 AM
 * 
 * Description
 * --------------------------------------------------------
 * Base data object.  A data object represents a single
 * data entity, for example, a single table, a single file,
 * or a single worksheet.  All the rules pertaining to
 * the dataobject with relation to itself should be encapsulated here
 * Relationships with other entities are also contained here,
 * however any rules regarding interaction between entities should
 * be placed in the business objects.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Annotations.MaximumLength;
import Goliath.Annotations.MaximumValue;
import Goliath.Annotations.MinimumLength;
import Goliath.Annotations.MinimumValue;
import Goliath.Annotations.RegEx;
import Goliath.Annotations.NoNulls;
import Goliath.Applications.Application;
import Goliath.Arguments.Arguments;
import Goliath.Arguments.SingleParameterArguments;
import Goliath.Collections.List;
import Goliath.Constants.EventType;
import Goliath.Data.DataManager;
import Goliath.Data.PropertyChangedEvent;
import Goliath.Date;
import Goliath.DynamicCode.Java;
import Goliath.Event;
import Goliath.EventDispatcher;
import Goliath.Interfaces.Data.ISimpleDataObject;
import Goliath.Interfaces.IDelegate;
import Goliath.Interfaces.IValidatable;
import Goliath.Session;
import Goliath.Validation.BrokenRulesCollection;
import Goliath.Validation.RuleHandler;
import Goliath.Validation.ValidationManager;
import java.lang.reflect.Type;

/**
 * Base data object.  A data object represents a single
 * data entity, for example, a single table, a single file,
 * or a single worksheet.  All the rules pertaining to
 * the dataobject with relation to itself should be encapsulated here
 * Relationships with other entities are also contained here,
 * however any rules regarding interaction between entities should
 * be placed in the business objects.
 * 
 * @param <T> 
 * @version     1.0 Jan 9, 2008
 * @author      Ken McHugh
**/
public abstract class DataObject<T extends DataObject> extends Goliath.Data.DataObjects.SimpleDataObject<T>
        implements Goliath.Interfaces.Data.IDataObject, IValidatable
{
    private static List<Class> g_oClassRulesAdded;
    
    /**
     * Checks if the class validation rules have already been added to the class validation manager
     * @param <K>
     * @param toClass the class to check for
     * @return true if the rules have already been added
     */
    private static <K extends DataObject> boolean isClassValidationAdded(Class<K> toClass)
    {
        return g_oClassRulesAdded != null && g_oClassRulesAdded.contains(toClass);
    }



    
    /**
     * Creation of event handlers for data objects
     */
    private static EventType g_oModifiedStateChanged;
    public static EventType ONMODIFIEDSTATECHANGED()
    {
        if (g_oModifiedStateChanged == null)
        {
            try
            {
                g_oModifiedStateChanged = new EventType("ONMODIFIEDSTATECHANGED");
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oModifiedStateChanged;
    }


    // Local storage of any broken validation rules
    private EventDispatcher<EventType, Event<ISimpleDataObject>> m_oEventDispatcher;
    private ValidationManager m_oValidationManager;
    private String m_cCreatedBy;
    private Goliath.Date m_dModified;
    private Goliath.Date m_dCreated;
    private String m_cModifiedBy;
    private Goliath.Date m_dRowversion;

    
    
    /**
     * Creates an instance of a DataObject 
     */
    protected DataObject()
    {
        addClassValidationRules();
        addValidationRules();
    }
    
    /**
     * This is used for loading the object from a data source, we don't want the
     * change events to fire each time we change a property so we need to suppress
     * the events first.
     * @param tlValue true if we want to suppress the events, false if not
     */
    public final void changeSuppressEventsFlag(boolean tlValue)
    {
        if (m_oEventDispatcher != null)
        {
            m_oEventDispatcher.suppressEvents(tlValue);
        }
    }

    /**
     * Hook method to allow subclasses to add custom validation rules to the object
     */
    private void addValidationRules()
    {
        onAddValidationRules();
    }

    /**
     * Hook method to allow subclasses to add custom validation rules to the class
     */
    private void addClassValidationRules()
    {
        // This creates the validation manager
        getValidationManager();


        Class loClass = getClass();
        // Check if this has already been done, if so, don't do it again
        if (isClassValidationAdded(loClass) != true)
        {
            if (g_oClassRulesAdded == null)
            {
                g_oClassRulesAdded = new List<Class>();
            }
            synchronized(g_oClassRulesAdded)
            {
                g_oClassRulesAdded.add(loClass);
                // If there is a guid, then we need to make sure it has a value
                if (hasGUID())
                {
                    addClassValidationRule(Goliath.Validation.Rules.StringRequiredRule.class, "GUID", null);
                }

                // Add all of the validation rules based on the annotations
                Java.ClassDefinition loClassDef = Java.getClassDefinition(loClass);
                for (String lcProperty : loClassDef.getProperties())
                {
                    // GUID is already done
                    if (lcProperty.equalsIgnoreCase("guid") ||
                            lcProperty.equalsIgnoreCase("createdby") ||
                            lcProperty.equalsIgnoreCase("modifiedby") ||
                            lcProperty.equalsIgnoreCase("createddate") ||
                            lcProperty.equalsIgnoreCase("modifieddate") ||
                            lcProperty.equalsIgnoreCase("id") ||
                            lcProperty.equalsIgnoreCase("rowversion"))
                    {
                        continue;
                    }

                    Java.MethodDefinition loMethodDef = loClassDef.getMethod(lcProperty);
                    Type loReturnType = loMethodDef.getReturnType();

                    // Required
                    if ((loReturnType != null && ((Class)loReturnType).isPrimitive()) || loMethodDef.getAnnotation(NoNulls.class) != null)
                    {
                        if (loReturnType != null)
                        {
                            // If the type is a string then use the StringRequiredRule
                            if (loReturnType != null && loReturnType.equals(String.class))
                            {
                                addClassValidationRule(Goliath.Validation.Rules.StringRequiredRule.class, lcProperty, null);
                            }
                            else
                            {
                                addClassValidationRule(Goliath.Validation.Rules.ValueRequiredRule.class, lcProperty, null);
                            }
                        }
                    }

                    // Min Length
                    MinimumLength loMinLength = loMethodDef.getAnnotation(MinimumLength.class);
                    if (loMinLength != null)
                    {
                        // TODO: Also need to use this property for lists
                        addClassValidationRule(Goliath.Validation.Rules.MinimumLengthRule.class, lcProperty, new SingleParameterArguments<Number>(loMinLength.length()));
                    }

                    // Max Length
                    MaximumLength loMaxLength = loMethodDef.getAnnotation(MaximumLength.class);
                    if (loMaxLength != null)
                    {
                        // TODO: Also need to use this property for lists
                        addClassValidationRule(Goliath.Validation.Rules.MaximumLengthRule.class, lcProperty, new SingleParameterArguments<Number>(loMaxLength.length()));
                    }

                    // Minimum Value
                    MinimumValue loMinValue = loMethodDef.getAnnotation(MinimumValue.class);
                    if (loMinValue != null)
                    {
                        // TODO: Also need to use this property for lists
                        addClassValidationRule(Goliath.Validation.Rules.MinimumValueRule.class, lcProperty, new SingleParameterArguments<Number>(loMinValue.value()));
                    }

                    // Maximum Value
                    MaximumValue loMaxValue = loMethodDef.getAnnotation(MaximumValue.class);
                    if (loMaxValue != null)
                    {
                        // TODO: Also need to use this property for lists
                        addClassValidationRule(Goliath.Validation.Rules.MaximumValueRule.class, lcProperty, new SingleParameterArguments<Number>(loMaxValue.value()));
                    }
                    // RegEX
                    RegEx loRegEX = loMethodDef.getAnnotation(RegEx.class);
                    if (loRegEX != null)
                    {
                        addClassValidationRule(Goliath.Validation.Rules.RegExRule.class, lcProperty, new SingleParameterArguments<String>(loRegEX.matchString()));
                    }
                }

                onAddClassValidationRules();
            }
        }
    }

    /**
     * The subclass can override this to add custom class level validation
     * This is called during construction of the data object so not all data may be available
     */
    protected void onAddClassValidationRules()
    {

    }

    /**
     * The subclass can override this to add custom validation
     * This is called during construction of the data object so not all data may be available
     */
    protected void onAddValidationRules()
    {
    }
    
    


    /**
     * Allows the turning off of use of the created by field by a subclass
     * @return
     */
    public boolean hasCreatedBy()
    {
        return true;
    }
    
    /**
     * Gets the GUID of the user that created this object
     * @return the GUID of the user that created this object
     */
    public String getCreatedBy()
    {
        canReadProperty();
        return m_cCreatedBy;
    }
    
    /**
     * Sets the guid of the user that created this object
     * @param tcValue the guid of the user that created the object
     */
    @Goliath.Annotations.MaximumLength(length=40)
    @Goliath.Annotations.NoNulls
    public void setCreatedBy(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cCreatedBy, tcValue))
        {
            m_cCreatedBy = tcValue;
            propertyHasChanged();
        }
    }

    /**
     * Allows the turning off of use of the modified by field by a subclass
     * @return
     */
    public boolean hasModifiedBy()
    {
        return true;
    }

    /**
     * The modified by field is the name of the user that last modified this object
     * @return
     */
    public String getModifiedBy()
    {
        canReadProperty();
        return m_cModifiedBy;
    }

    /**
     * Sets the user who last modified this object
     * @param tcValue the GUID of the user
     */
    @Goliath.Annotations.MaximumLength(length=40)
    @Goliath.Annotations.NoNulls
    public void setModifiedBy(String tcValue)
    {
        canWriteProperty();
        if (tcValue == null)
        {
            tcValue = "";
        }
        if (isDifferent(m_cModifiedBy, tcValue))
        {
            m_cModifiedBy = tcValue;
            propertyHasChanged();
        }
    }


    /**
     * Allows the turning off of use of the modified date field by a subclass
     * @return
     */
    public boolean hasModifiedDate()
    {
        return true;
    }

    /*
     * Gets the data this object was last modified
     */
    public Goliath.Date getModifiedDate()
    {
        canReadProperty();
        return m_dModified;        
    }

    /**
     * sets the date this object was last modified
     * @param toDate
     */
    @Goliath.Annotations.NoNulls
    public void setModifiedDate(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dModified, toDate))
        {
            m_dModified = toDate;
            propertyHasChanged();
        }
    }

    /**
     * Allows the turning off of use of the created date field by a subclass
     * @return
     */
    public boolean hasCreatedDate()
    {
        return true;
    }

    /**
     * Gets the date this object was created
     * @return
     */
    public Goliath.Date getCreatedDate()
    {
        canReadProperty();
        return m_dCreated;
    }

    /**
     * Sets the date this object was created
     * @param toDate
     */
    @Goliath.Annotations.NoNulls
    public void setCreatedDate(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dCreated, toDate))
        {
            m_dCreated = toDate;
            propertyHasChanged();
        }
    }


    /**
     * Allows the turning off of use of the rowVersion field by a subclass
     * @return
     */
    public boolean hasRowVersion()
    {
        return true;
    }

    /**
     * Sets the row version of this object, used for concurrent modification detection
     * @return
     */
    public Goliath.Date getRowVersion()
    {
        canReadProperty();
        return m_dRowversion;
    }

    /**
     * Sets the row version of this object
     * @param toDate
     */
    @Goliath.Annotations.NoNulls
    public void setRowVersion(Goliath.Date toDate)
    {
        canWriteProperty();
        if (isDifferent(m_dRowversion, toDate))
        {
            m_dRowversion = toDate;
            propertyHasChanged();
        }
    }
    
    /**
     * Tests if the property that called this method is allowed to be read
     * @throws SecurityException if not allowed
     */
    protected final void canReadProperty()
    {
        String lcName = Goliath.DynamicCode.Java.getCallingMethodName(true, true);
        boolean llResult = canReadProperty(lcName);
        if (!llResult)
        {
            // We were asked to throw an error on failure
            throw new Goliath.Exceptions.SecurityException("Read denied for " + lcName);
        }
    }

    /**
     * The actual check for reading the property
     * @param tcProperty the name of the property
     * @return
     */
    private boolean canReadProperty(String tcProperty)
    {
        // TODO : Implement this
        //return Application.getInstance().getSecurityManager().checkPermission(this, tcProperty);

        return true;        
    }

    /**
     * Checks if the user can actually write the property that called this method
     * @throws SecurityException if not allowed
     */
    protected final void canWriteProperty()
    {
        String lcName = Goliath.DynamicCode.Java.getCallingMethodName(true, true);
        boolean llResult = canWriteProperty(lcName);
        if (!llResult)
        {
            // We were asked to throw an error on failure
            throw new Goliath.Exceptions.SecurityException("Write denied for " + lcName);
        }
    }

    // TODO: It may not be a good idea to have this overridable
    protected boolean canWriteProperty(String tcProperty)
    {
        boolean llResult = true;                
        // TODO : Implement this
        return llResult;        
    }
    
    /**
     * Helper function for notifying the event dispatcher that an event needs to be fired as
     * data for a specific property has been changed
     */
    protected final void propertyHasChanged()
    {
        // If we are not suppressing events, then we want to make sure we have marked the object dirty
        // and notify all the event handlers
        if (!areEventsSuppressed())
        {
            // Because something has changed, set the flag
            setDirtyFlag(true);  // TODO: See if this can be moved out of the if statement (check when loading from the db)
            String lcName = Goliath.DynamicCode.Java.getCallingMethodName(true, true);
            fireEvent(EventType.ONCHANGED(), new PropertyChangedEvent(this, lcName));
        }
    }

    @Override
    protected void onDirtyFlagChanged()
    {
        if (!areEventsSuppressed())
        {
            fireEvent(ONMODIFIEDSTATECHANGED(), new Event<ISimpleDataObject>(this));
        }
    }

    // TODO: Check if this is needed, both the specific GUID case as well as the method itself
    @Override
    protected void onGUIDChanged()
    {
        if (!areEventsSuppressed())
        {
            fireEvent(EventType.ONCHANGED(), new PropertyChangedEvent(this, "GUID"));
        }
    }

    // TODO: Check if this is needed, both the specific GUID case as well as the method itself
    @Override
    protected void onIDChanged()
    {
        if (!areEventsSuppressed())
        {
            fireEvent(EventType.ONCHANGED(), new PropertyChangedEvent(this, "ID"));
        }
    }



    

    @Override
    protected boolean onBeforeSave()
    {
        // Check if this object is actually valid
        if (!isDeleted() && !isValid())
        {
            throw new Goliath.Exceptions.ValidationException(getBrokenRules());
        }

        // Before we attempt a save we need to update some of the fields, we also dont want
        // events firing, so we modify them directly

        // Get the user
        Goliath.Security.User loUser = Session.getCurrentSession().getUser();

        // If this is a new field, then we want to update the created fields
        if (isNew())
        {
            if (hasCreatedDate())
            {
                m_dCreated = new Date();
            }
            if (hasCreatedBy())
            {
                m_cCreatedBy = loUser.getGUID();
            }
        }

        // We want to update the modified fields if they are being used
        if (hasModifiedBy())
        {
            m_cModifiedBy = loUser.getGUID();
        }

        if (hasModifiedDate())
        {
            m_dModified = new Date();
        }

        // We do not update the rowversion, as that is what is used to check if the record
        // has been changed in the data source, but if the row version does not exist, we need to add it
        if (hasRowVersion())
        {
            if (m_dRowversion == null)
            {
                m_dRowversion = new Date();
            }
        }

        // If we actually got this far, then the object is valid, so it is okay to continue
        return true;
    }

    @Override
    protected void onSaveCompleted()
    {
        // Because the save completed successfully, we need to update the rowversion in order
        // to make sure we can still detect concurrent modifications
        if (this.hasRowVersion())
        {
            m_dRowversion = DataManager.getInstance().getObjectVersion(this);
        }
    }


    @Override
    public void suppressEvents(boolean tlSuppress)
    {
        if (m_oEventDispatcher == null)
        {
            return;
        }
        m_oEventDispatcher.suppressEvents(tlSuppress);
    }

    @Override
    public boolean removeEventListener(EventType toEvent, IDelegate toCallback)
    {
        return (m_oEventDispatcher != null) ? m_oEventDispatcher.removeEventListener(toEvent, toCallback) : false;
    }

    @Override
    public boolean hasEventsFor(EventType toEvent)
    {
        return (m_oEventDispatcher != null) ? m_oEventDispatcher.hasEventsFor(toEvent) : false;
    }

    @Override
    public final void fireEvent(EventType toEventType, Event<ISimpleDataObject> toEvent)
    {
        if (m_oEventDispatcher == null)
        {
            return;
        }
        m_oEventDispatcher.fireEvent(toEventType, toEvent);
    }

    @Override
    public boolean clearEventListeners(EventType toEvent)
    {
        return (m_oEventDispatcher != null) ? m_oEventDispatcher.clearEventListeners(toEvent) : false;
    }

    @Override
    public boolean clearEventListeners()
    {
        return (m_oEventDispatcher != null) ? m_oEventDispatcher.clearEventListeners() : false;
    }

    @Override
    public boolean areEventsSuppressed()
    {
        return (m_oEventDispatcher != null) ? m_oEventDispatcher.areEventsSuppressed() : false;
    }

    @Override
    public boolean addEventListener(EventType toEvent, IDelegate toCallback)
    {
        if (m_oEventDispatcher == null)
        {
            m_oEventDispatcher = new EventDispatcher<EventType, Event<ISimpleDataObject>>();
        }
        return m_oEventDispatcher.addEventListener(toEvent, toCallback);
    }

    @Override
    public final <T extends RuleHandler> void addClassValidationRule(Class<T> toRuleClass, String tcProperty, Arguments toArgs)
    {
        try
        {
            getValidationManager().addClassValidationRule(toRuleClass, tcProperty, toArgs);
        }
        catch (Goliath.Exceptions.InvalidPropertyException ex)
        {
            Application.getInstance().log(ex);
        }
    }

    @Override
    public final <T extends RuleHandler> void addValidationRule(Class<T> toRuleClass, String tcProperty, Arguments toArgs)
    {
        try
        {
            getValidationManager().addValidationRule(toRuleClass, tcProperty, toArgs);
        }
        catch (Goliath.Exceptions.InvalidPropertyException ex)
        {
            Application.getInstance().log(ex);
        }
    }

    private ValidationManager getValidationManager()
    {
        if (m_oValidationManager == null)
        {
            m_oValidationManager = new ValidationManager(this);
        }
        return m_oValidationManager;
    }


    @Goliath.Annotations.NotProperty
    @Override
    public final BrokenRulesCollection getBrokenRules()
    {
        return (m_oValidationManager != null) ? m_oValidationManager.getBrokenRules() : null;
    }

    @Goliath.Annotations.NotProperty
    @Override
    public final BrokenRulesCollection getBrokenRules(String tcProperty)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.getBrokenRules(tcProperty) : null;
    }

    @Override
    public boolean addValidationException(String tcRuleName, Throwable toException)
    {
        return addValidationException(tcRuleName, "", toException);
    }

    @Override
    public boolean addValidationException(String tcRuleName, String tcPropertyName, Throwable toException)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.addValidationException(tcRuleName, tcPropertyName, toException) : false;
    }



    @Override
    public final boolean isValid()
    {
        return isValid(isModified());
    }

    @Override
    public final boolean isValid(boolean tlClearBrokenRules)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.isValid(tlClearBrokenRules) : true;
    }
}
