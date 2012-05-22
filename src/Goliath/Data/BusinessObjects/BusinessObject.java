/* ========================================================
 * BusinessObject.java
 *
 * Author:      kmchugh
 * Created:     Jul 31, 2010, 4:41:07 PM
 *
 * Description
 * --------------------------------------------------------
 * The business object class represents a concept within
 * the system.  That concept can contain single or multiple
 * data objects.  All of the rules for validation and interaction
 * should take place in BusinessObjects.
 *
 * A business object must be uniquely identifiable, therefore
 * a business object must have a primary class that uses
 * a guid
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Data.BusinessObjects;

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
import Goliath.Collections.PropertySet;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Constants.CacheType;
import Goliath.Constants.EventType;
import Goliath.Constants.StringFormatType;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.DynamicCode.Java;
import Goliath.Event;
import Goliath.EventDispatcher;
import Goliath.Exceptions.DataObjectNotFoundException;
import Goliath.Exceptions.ObjectNotCreatedException;
import Goliath.Exceptions.PermissionDeniedException;
import Goliath.Interfaces.Data.BusinessObjects.IBusinessObject;
import Goliath.Interfaces.IDelegate;
import Goliath.Validation.BrokenRulesCollection;
import Goliath.Validation.RuleHandler;
import Goliath.Validation.ValidationManager;


        
/**
 *
 * @author kmchugh
 */
public abstract class BusinessObject<T extends SimpleDataObject<T>> extends Goliath.Object
        implements IBusinessObject<T>
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
     * Creates the Business object with the specified GUID, loading it from the data source
     * @param <K> The type of the business object to load
     * @param toBusinessClass the class of the business object
     * @param tcGUID the GUID of the business object to load
     * @return The business object populated from the data source
     * @throws DataObjectNotFoundException if the business object with the specified guid could not be found
     */
    @Goliath.Annotations.NotProperty
    public static <K extends BusinessObject> K getObjectByGUID(Class<K> toBusinessClass, String tcGUID)
            throws DataObjectNotFoundException
    {
        return getObjectByGUID(toBusinessClass, tcGUID, true);
    }


    /**
     * Creates the Business object with the specified GUID, loading it from the data source
     * @param <K> The type of the business object to load
     * @param toBusinessClass the class of the business object
     * @param tcGUID the GUID of the business object to load
     * @return The business object populated from the data source
     * @throws DataObjectNotFoundException if the business object with the specified guid could not be found
     */
    @Goliath.Annotations.NotProperty
    public static <K extends BusinessObject> K getObjectByGUID(Class<K> toBusinessClass, String tcGUID, boolean tlThrowError)
            throws DataObjectNotFoundException
    {
        K loReturn = null;
        try
        {
            loReturn = toBusinessClass.newInstance();
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }

        if (loReturn != null)
        {
            SimpleDataObject loObject = SimpleDataObject.getObjectByGUID(loReturn.getPrimaryClass(), tcGUID);
            if (loObject != null)
            {
                return (K)createObjectWithDataObject(toBusinessClass, loObject);
            }
        }
        if (tlThrowError)
        {
            throw new DataObjectNotFoundException("Unable to retrieve object of type " + toBusinessClass.getName() + " with GUID " + tcGUID);
        }
        return null;
    }

    /**
     * Gets an object of the specified type.  The object to find will have the same value in the tcKey property
     * as the template has specified.  If multiple objects match, only the first object will be returned
     * @param <K> must extend from SimpleDataObject
     * @param toTemplate The template object, this object should have the properties that will be searched for set
     * @param tcKey the property from toTemplate to use for the search
     * @return the found object, or null if there was a problem
     * @throws DataObjectNotFoundException if the object was not found
     */
    @Goliath.Annotations.NotProperty
    public static <K extends BusinessObject, S extends SimpleDataObject<S>> K getObjectByProperty(Class<K> toBusinessClass, Class<S> toDataClass, PropertySet toPropertySet)
            throws DataObjectNotFoundException
    {
        // TODO: This needs to be revisited to return multiple objects
        SimpleDataObjectCollection<S> loObjects = SimpleDataObject.getObjectsByProperty(toDataClass, toPropertySet);
        S loObject = loObjects != null && loObjects.size() == 1 ? loObjects.get(0) : null;
        if (loObject != null)
        {
            return (K)createObjectWithDataObject(toBusinessClass, loObject);
        }
        throw new DataObjectNotFoundException("Unable to create object of type " + toBusinessClass.getName() + " with properties " + toPropertySet.toString());
    }


    /**
     * Helper function to get an object by GUID
     * @param <K> must extend simple data object
     * @param toClass The class to get
     * @param tcGUID The GUID of the object to get
     * @return The object requested or null if there was a problem.
     * @throws DataObjectNotFoundException if an object could not be found with the guid specified
     */
    @Goliath.Annotations.NotProperty
    public static <K extends BusinessObject, S extends SimpleDataObject<S>> K getObjectByGUID(Class<K> toBusinessClass, Class<S> toDataClass, String tcGUID)
            throws DataObjectNotFoundException
    {
        SimpleDataObject loObject = SimpleDataObject.getObjectByGUID(toDataClass, tcGUID);
        if (loObject != null)
        {
            return (K)createObjectWithDataObject(toBusinessClass, loObject);
        }
        throw new DataObjectNotFoundException("Unable to create object of type " + toBusinessClass.getName() + " with GUID " + tcGUID);
    }

    /**
     * Helper function to get an object by ID
     * @param <K> must extend simple data object
     * @param toClass The class to get
     * @param tnID The ID of the object to get
     * @return The object requested or null if there was a problem.
     * @throws DataObjectNotFoundException if an object could not be found with the ID specified
     */
    @Goliath.Annotations.NotProperty
    public static <K extends BusinessObject, S extends SimpleDataObject<S>> K getObjectByID(Class<K> toBusinessClass, Class<S> toDataClass, long tnID)
            throws DataObjectNotFoundException
    {
        SimpleDataObject loObject = SimpleDataObject.getObjectByID(toDataClass, tnID);
        if (loObject != null)
        {
            return (K)createObjectWithDataObject(toBusinessClass, loObject);
        }
        throw new DataObjectNotFoundException("Unable to create object of type " + toBusinessClass.getName() + " with ID " + tnID);
    }

    /**
     * Helper function to get an object by ID
     * @param <K> must extend simple data object
     * @param toClass The class to get
     * @param tnID The ID of the object to get
     * @return The object requested or null if there was a problem.
     * @throws DataObjectNotFoundException if an object could not be found with the ID specified
     */
    @Goliath.Annotations.NotProperty
    protected static <K extends BusinessObject, S extends SimpleDataObject<S>> K createObjectWithDataObject(Class<K> toBusinessClass, S toDataObject)
            throws DataObjectNotFoundException
    {
        return Goliath.DynamicCode.Java.createObject(toBusinessClass, new Object[]{toDataObject});
    }



    private ValidationManager m_oValidationManager;
    private EventDispatcher<EventType, Event<IBusinessObject<T>>> m_oEventDispatcher;

    private Class<T> m_oPrimaryClass;
    private T m_oPrimaryObject;

    /**
     * Creates a new empty business object
     * @param toPrimaryClass the class that this business object is dependent on
     */
    protected BusinessObject(Class<T> toPrimaryClass)
            throws InstantiationException, IllegalAccessException
    {
        this(toPrimaryClass.newInstance());
    }
    
    /**
     * Creates and loads the business object with the specified ID, this is a lazy load method
     * @param toPrimaryClass the primary class of the business object
     * @param tnID the ID of the object to load
     */
    private BusinessObject(Class<T> toPrimaryClass, long tnID)
    {
        this(SimpleDataObject.getObjectByID(toPrimaryClass, tnID));
    }

    /**
     * Creates and loads the business object with the specified GUID, this is a lazy load method
     * @param toPrimaryClass the primary class of the business object
     * @param tnID the GUID of the object to load
     */
    protected BusinessObject(Class<T> toPrimaryClass, String tcGUID)
    {
        this(SimpleDataObject.getObjectByGUID(toPrimaryClass, tcGUID));
        // If the primary class is not set then the constructor was not able to find an object with tcGUID, so create it
        if (m_oPrimaryClass == null)
        {
            m_oPrimaryClass = toPrimaryClass;
            try
            {
                m_oPrimaryObject = toPrimaryClass.newInstance();
                m_oPrimaryObject.setGUID(tcGUID);
            }
            catch (Throwable ex)
            {
                throw new ObjectNotCreatedException("A Business object must be identifiable by guid, the primary object does not use guids for business object " + this.getClass().getName());                
            }
        }
    }
    
    /**
     * Creates a new instance of the business object, using toPrimary object as it's base.
     * This is a lazy load method
     * @param toPrimaryObject the primary object to use for this business object
     */
    protected BusinessObject(T toPrimaryObject)
    {
        if (toPrimaryObject != null)
        {
            m_oPrimaryClass = (Class<T>)toPrimaryObject.getClass();
            m_oPrimaryObject = toPrimaryObject;
            if (!m_oPrimaryObject.hasGUID())
            {
                throw new Goliath.Exceptions.ObjectNotCreatedException("A Business object must be identifiable by guid, the primary object does not use guids for business object " + this.getClass().getName());
            }
        }
        addClassValidationRules();
        addValidationRules();
        // TODO: Set up events on the primary class to watch for modifications
    }

    /**
     * Gets the GUID of this business object, the guid is the same as the primary
     * object
     * @return the guid
     */
    public final String getGUID()
    {
        return m_oPrimaryObject != null ? m_oPrimaryObject.getGUID() : null;
    }

    /**
     * Sets the primary objects GUID
     * @param tcGUID the new guid
     */
    public final void setGUID(String tcGUID)
    {
        m_oPrimaryObject.setGUID(tcGUID);
    }

    /**
     * Gets the ID of this business object, the id is the same as the primary
     * object
     * @return the id
     */
    @Goliath.Annotations.NotProperty
    public final long getID()
    {
        return m_oPrimaryObject != null ? m_oPrimaryObject.getID() : 0;
    }

    /**
     * Gets the primary class used by this business object
     * @return the primary class used for the business object
     */
    @Goliath.Annotations.NotProperty
    public final Class<T> getPrimaryClass()
    {
        return m_oPrimaryClass;
    }

    /**
     * Gets the cache type for this business object
     * @return
     */
    protected CacheType getCacheType()
    {
        return CacheType.NONE();
    }

    /**
     * attempts to save the business object and returns true if saved correctly
     * @return true if saved, false if not
     */
    public final boolean save()
            throws PermissionDeniedException
    {
        // First check if we need to bother saving
        if (isModified() || isNew())
        {
            // The object has been changed or it is new, so we will need to do something.
            try
            {
                if (isValid())
                {
                    // TODO: Start a transaction here
                    // as this is made up of multiple data objects, we will leave it up to the subclass to do the saving
                    onSave();
                    // TODO: Complete a transaction here
                    return true;
                }
                return false;
            }
            catch(Throwable ex)
            {
                // TODO: Rollback anything that has saved
                // The save failed
                new Goliath.Exceptions.Exception("Error saving " + this.getClass().getSimpleName(), ex);
                return false;
            }
        }
        return true;
    }

    /**
     * Makes a deep copy of the business object object
     * @return a copy of the business object
     */
    public final BusinessObject<T> copy()
    {
        if (isValid())
        {
            return onCopy();
        }
        else
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Original data object does not validate");
        }
    }

    /**
     * Hook to allow subclass to copy
     * @return the copy
     */
    protected abstract BusinessObject<T> onCopy();

    /**
     * Marks this item as deleted
     */
    public final void delete()
    {
        getPrimaryObject().delete();
        onDelete();
    }

    /**
     * Hook to allow subclass to delete
     */
    protected abstract void onDelete();

    /**
     * Saves the business object to the database, any errors that occur will cause the save to faile
     * @throws Goliath.Exceptions.Exception
     */
    protected abstract void onSave() throws Goliath.Exceptions.Exception;

    /**
     * Gets the primary object
     * @return the primary object of this business object
     */
    @Goliath.Annotations.NotProperty
    public final T getPrimaryObject()
    {
        return m_oPrimaryObject;
    }

    /**
     * Checks if this is modified, this will always check the primary object first
     * @return true if modified false otherwise
     */
    @Override
    public final boolean isModified()
    {
        return getPrimaryObject().isModified() || onIsModified();
    }

    /**
     * Checks all the items in the business object and returns true if anything has been modified
     * @return true if modified false otherwise
     */
    protected abstract boolean onIsModified();

    /**
     * Checks if this object is marked as a new object
     * @return true if this is a new object
     */
    public boolean isNew()
    {
        return getPrimaryObject().isNew();
    }

    /**
     * Checks if this object has been marked deleted
     * @return true if this object is deleted
     */
    public boolean isDeleted()
    {
        return getPrimaryObject().isDeleted();
    }

    /**
     * Checks if this object is valid, if the validation has been run before and this object has
     * not been modified, then this will only recheck the custom validation
     * @return true if the object is valid
     */
    @Override
    public final boolean isValid()
    {
        // If we are deleting, there is no need to check for validation
        return onIsValid() && (isDeleted() || isValid(isModified()));
    }

    /**
     * Checks if this object is valid, this can use the cached value, or can clear the validation rules and revalidate
     * @param tlClearBrokenRules true to clear the validation rules on completely revalidate, false to just use the cached value
     * @return true if the object is valid, false otherwise
     */
    @Override
    public final boolean isValid(boolean tlClearBrokenRules)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.isValid(tlClearBrokenRules) : true;
    }

    /**
     * Hook method to allow for custom validation outside the validation manager
     * @return true if the object is valid, false otherwise
     */
    protected boolean onIsValid()
    {
        return true;
    }



    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final BusinessObject<T> other = (BusinessObject<T>) obj;
        return this.m_oPrimaryObject.equals(other.m_oPrimaryObject);
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 71 * hash + (this.m_oPrimaryObject != null ? this.m_oPrimaryObject.hashCode() : 0);
        return hash;
    }

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return getGUID();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return this.copy();
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
                addClassValidationRule(Goliath.Validation.Rules.StringRequiredRule.class, "GUID", null);

                // Add all of the validation rules based on the annotations
                Java.ClassDefinition loClassDef = Java.getClassDefinition(loClass);
                for (String lcProperty : loClassDef.getProperties())
                {
                    // GUID is already done
                    if (lcProperty.equalsIgnoreCase("guid") ||
                            lcProperty.equalsIgnoreCase("id"))
                    {
                        continue;
                    }

                    Java.MethodDefinition loMethodDef = loClassDef.getMethod(lcProperty);

                    Class loReturnType = Java.getPropertyType(loClass, lcProperty, false);

                    // Required
                    if ((loReturnType != null && (loReturnType).isPrimitive()) || loMethodDef.getAnnotation(NoNulls.class) != null)
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

    @Override
    public boolean addValidationException(String tcRuleName, Throwable toException)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.addValidationException(tcRuleName, "", toException) : false;
    }

    @Override
    public boolean addValidationException(String tcRuleName, String tcPropertyName, Throwable toException)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.addValidationException(tcRuleName, tcPropertyName, toException) : false;
    }

    @Goliath.Annotations.NotProperty
    @Override
    public final BrokenRulesCollection getBrokenRules(String tcProperty)
    {
        return (m_oValidationManager != null) ? m_oValidationManager.getBrokenRules(tcProperty) : null;
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
    public final void fireEvent(EventType toEventType, Event<IBusinessObject<T>> toEvent)
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
            m_oEventDispatcher = new EventDispatcher<EventType, Event<IBusinessObject<T>>>();
        }
        return m_oEventDispatcher.addEventListener(toEvent, toCallback);
    }

    

    

    
}
