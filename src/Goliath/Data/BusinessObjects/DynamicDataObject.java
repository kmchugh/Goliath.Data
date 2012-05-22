/* =========================================================
 * DynamicDataObject.java
 *
 * Author:      kenmchugh
 * Created:     Sep 6, 2010, 1:34:30 PM
 *
 * Description
 * --------------------------------------------------------
 * A DynamicDataObject is an object that is defined and created
 * in the data source using the TypeDefinition and related tables.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.BusinessObjects;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Data.DataManager;
import Goliath.Data.DataObjects.ObjectRegistry;
import Goliath.Data.DataObjects.TypeComposition;
import Goliath.Data.DataObjects.ValueList;
import Goliath.Data.Query.InList;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.Exception;
import Goliath.Exceptions.ObjectNotCreatedException;
import java.lang.reflect.Method;

/**
 *
 * @author kenmchugh
 */
public abstract class DynamicDataObject extends BusinessObject<ObjectRegistry>
{
    private static HashTable<Class, HashTable<String, TypeComposition>> g_oTypeCompositions;

    
    /**
     * Helper function to get the type composition of the property on the class specified.
     * If a type composition does not already exist, then one will be created.
     * @param toClass the class that contains the property
     * @param tcProperty the property
     * @return the TypeComposition for this class and property combination
     */
    @Goliath.Annotations.NotProperty
    private static TypeComposition getTypeComposition(Class toClass, String tcProperty)
    {
        tcProperty = tcProperty.toLowerCase();

        if (g_oTypeCompositions == null)
        {
            g_oTypeCompositions = new HashTable<Class, HashTable<String, TypeComposition>>();
        }

        if (!g_oTypeCompositions.containsKey(toClass))
        {
            g_oTypeCompositions.put(toClass, new HashTable<String, TypeComposition>());
        }

        if (!g_oTypeCompositions.get(toClass).containsKey(tcProperty))
        {
            g_oTypeCompositions.get(toClass).put(tcProperty, TypeComposition.getTypeCompositionFromClass(toClass, tcProperty));
        }
        
        return g_oTypeCompositions.get(toClass).get(tcProperty);
    }



    // TODO: Implement object versioning and conversion
    
    

    private SimpleDataObjectCollection<ValueList> m_oValues;

    /**
     * Creates a new instance of a data object, this will not have any effect on 
     * the back end persistant storage
     */
    protected DynamicDataObject()
            throws InstantiationException, IllegalAccessException
    {
        super(ObjectRegistry.class);

        // Load or create the type definition
        setTypeDefinition(TypeDefinition.getTypeDefinitionFromClass(getClass()));
    }

    /**
     * Loads the object from the specified object registry, this will make any calls to
     * persistant storage until properties of the class are accessed
     * @param toObject the object to load from
     */
    protected DynamicDataObject(ObjectRegistry toObject)
    {
        super(toObject);

        // Make sure the types are correct
        if (!getPrimaryObject().getTypeDefinition().equals(TypeDefinition.getTypeDefinitionFromClass(getClass()).getPrimaryObject()))
        {
            throw new ObjectNotCreatedException("The types do not match, trying to create a " + getClass().getName() + " from a " + toObject.getTypeDefinition().getName());
        }

        if (!isNew())
        {
            loadValueList();
        }
    }

    /**
     * Gets the DynamicDataObject from the datasource with the specified GUID,
     * if the object does not exist, then this will create the object
     * @param tcGUID the guid for the business object to get or create
     */
    protected DynamicDataObject(String tcGUID)
    {
        super(ObjectRegistry.class, tcGUID);

        // Load or create the type definition
        setTypeDefinition(TypeDefinition.getTypeDefinitionFromClass(getClass()));

        if (!isNew())
        {
            loadValueList();
        }
    }

    /**
     * Sets the type of this object
     * @param toDefinition the type definition for this object
     */
    protected final void setTypeDefinition(TypeDefinition toDefinition)
    {
        getPrimaryObject().setTypeDefinition(toDefinition.getPrimaryObject());
    }

    /**
     * Gets the type definition of this object
     * @return the objects type definition
     */
    @Goliath.Annotations.NotProperty
    public final Goliath.Data.DataObjects.TypeDefinition getTypeDefinition()
    {
        return getPrimaryObject().getTypeDefinition();
    }

    /**
     * Saves the dynamic data object to the data storage.  This gets split down
     * to an object registry and a ValueList for each property value
     * @throws Exception 
     */
    @Override
    protected void onSave() throws Exception
    {
        if (isDeleted())
        {
            loadValueList();
            m_oValues.delete();
            m_oValues.save();
            
            getPrimaryObject().delete();
            getPrimaryObject().save();
        }
        else
        {
            // Need to loop through all of the properties and save them as part of the object
            if (getPrimaryObject().save())
            {
                // Update all the properties before saving
                for (String lcProperty : Java.getPropertyMethods(getClass()))
                {
                    setValueInList(lcProperty, Java.getPropertyValue(this, lcProperty));
                }

                if (m_oValues != null && m_oValues.isModified())
                {
                    m_oValues.save();
                }
            }
        }
    }

    /**
     * Loads the full list of values for this object, if the list is already loaded
     * this does nothing
     */
    @Goliath.Annotations.NotProperty
    protected final void loadValueList()
    {
        if (m_oValues == null)
        {
            m_oValues = new SimpleDataObjectCollection<ValueList>(ValueList.class);
            m_oValues.loadList(new InList<Long, Long>("ObjectRegistryID", new Long[]{getPrimaryObject().getID()}));

            onFilterValueList(m_oValues);

            if (m_oValues.size() > 0)
            {
                // Get a reference to the class we are attempting to load
                Class loClass = getClass();
            
                // Get the list of type composition ID's that we need to load, then load them to ensure they are cached for the next calls
                SimpleDataObjectCollection<TypeComposition> loCompositionList = new SimpleDataObjectCollection<TypeComposition> (TypeComposition.class); 
                loCompositionList.loadList(new InList<ValueList, Long>("ID", m_oValues, "TypeCompositionID"));
                // Force the caching of all the TypeComposition objects that are loaded here
                for (TypeComposition loComposition : loCompositionList)
                {
                    DataManager.getInstance().putCachedDataObject(loComposition);
                }
            
                // Sort all of the value lists by type composition to make the loop only need one access.
                HashTable<String, List<ValueList>> loDataCache = new HashTable<String, List<ValueList>>();
                for (ValueList loValue : m_oValues)
                {
                    if (loValue.getTypeComposition() != null)
                    {
                        String lcGUID = loValue.getTypeComposition().getGUID();
                        if (!loDataCache.containsKey(lcGUID))
                        {
                            loDataCache.put(lcGUID, new List<ValueList>(1));
                        }
                        loDataCache.get(lcGUID).add(loValue);
                    }
                }
                
                
                // Now that we have a keyed set of value lists, we can process quicker for each property
                for (String lcProperty : Java.getPropertyMethods(loClass))
                {
                    TypeComposition loTypeComp = getTypeComposition(loClass, lcProperty);
                    List<ValueList> loValues = loDataCache.get(loTypeComp.getGUID());
                    if (loValues != null && loValues.size() > 0)
                    {
                        if (!loTypeComp.getTypeDefinition().getArrayType())
                        {
                            java.lang.Object loObjectValue = loValues.get(0).getValue();
                            if (loObjectValue != null && Java.isEqualOrAssignable(Long.class, loObjectValue.getClass()))
                            {
                                // Integers are also be stored as long
                                Method loMethod = Java.getClassDefinition(loClass).getMethod(lcProperty).getMutator();
                                if (loMethod.getParameterTypes().length == 1 && int.class.equals(loMethod.getParameterTypes()[0]))
                                {
                                    loObjectValue = ((Long)loObjectValue).intValue();
                                }
                            }
                            Java.setPropertyValue(this, lcProperty, loObjectValue);
                            // TODO: Also implement for lists of objects with a single item
                        }
                        else
                        {
                            Method loMethod = Java.getClassDefinition(loClass).getMethod(lcProperty).getAccessor();
                            try
                            {
                                java.util.List loList = (java.util.List)loMethod.invoke(this);
                                if (loList != null)
                                {
                                    loList.clear();
                                    for (ValueList loValue : loValues)
                                    {
                                        loList.add(loValue.getValue());
                                    }
                                }
                                else
                                {
                                    // TODO: Implement when the list is initialised to a null list
                                }
                            }
                            catch (Throwable ex)
                            {
                                Application.getInstance().log(ex);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Provides a hook to allow subclasses to filter value lists after they have
     * been loaded from the data source
     * @param toValueList the list of valuelist items
     */
    protected void onFilterValueList(SimpleDataObjectCollection<ValueList> toValueList)
    {
    }

    /**
     * Gets the value for a specified property
     * @param tcProperty the property to get the value for
     * @return the list of value list objects for the specified property
     */
    @Goliath.Annotations.NotProperty
    protected List<ValueList> getValueListForProperty(String tcProperty)
    {
        TypeComposition loComp = getTypeComposition(getClass(), tcProperty);
        List<ValueList> loReturn = new List<ValueList>(1);
        if (loComp != null)
        {
            loadValueList();
            
            // TODO: This could be optimised so we cache a lookup rather than looking up each time
            for (ValueList loValue : m_oValues)
            {
                if (loValue.getTypeComposition().getGUID().equals(loComp.getGUID()))
                {
                    loReturn.add(loValue);
                }
            }
            
            if (loReturn.size() == 0)
            {
                // Need to create a new value list for this property, if this is a list, then a new ValueList can be created for each item
                ValueList loValue = new ValueList();
                loValue.setObjectRegistry(getPrimaryObject());
                loValue.setTypeDefinition(loComp.getParentType());
                loValue.setTypeComposition(loComp);
                loValue.setPropertyName(loComp.getName());
                m_oValues.add(loValue);
                loReturn.add(loValue);
            }
        }
        return loReturn;
    }

    @Goliath.Annotations.NotProperty
    protected Object getValueFromList(String tcProperty)
    {
        TypeComposition loComp = getTypeComposition(getClass(), tcProperty);
        List<ValueList> loValues = getValueListForProperty(tcProperty);
        if (loValues != null)
        {
            if (getTypeComposition(this.getClass(), tcProperty).getTypeDefinition().getArrayType())
            {
                // List item value
                
            }
            else
            {
                // Single item value
                return loValues.get(0).getValue();
            }
        }
        return null;
    }

    @Goliath.Annotations.NotProperty
    protected void setValueInList(String tcProperty, Object toValue)
    {
        TypeComposition loComp = getTypeComposition(getClass(), tcProperty);
        List<ValueList> loValues = getValueListForProperty(tcProperty);
        if (loValues != null)
        {
            if (getTypeComposition(this.getClass(), tcProperty).getTypeDefinition().getArrayType())
            {
                java.util.List loValueList = (java.util.List)toValue;
                int lnMinValues = loValues.size();
                for (int i=0, lnLength = Math.max(loValueList.size(), loValues.size()); i<lnLength; i++)
                {
                    // Check if we need to create a new value list
                    if (i >= lnMinValues)
                    {
                        // Create a new ValueList
                        ValueList loValue = new ValueList();
                        loValue.setObjectRegistry(getPrimaryObject());
                        loValue.setTypeDefinition(loComp.getParentType());
                        loValue.setTypeComposition(loComp);
                        loValue.setPropertyName(loComp.getName());
                        m_oValues.add(loValue);
                        loValues.add(loValue);
                        lnMinValues++;
                    }
                    
                    // Get the value list at the position we are currently in
                    ValueList loListItem = loValues.get(i);
                    
                    // Check if we are inserting or deleting
                    if (i < loValueList.size())
                    {
                        // inserting
                        loListItem.setValue(loValueList.get(i));
                        loListItem.undelete();
                    }
                    else
                    {
                        // deleting
                        loListItem.setValue(null);
                        loListItem.delete();
                    }
                }
            }
            else
            {
                // Update as an individual item
                loValues.get(0).setValue(toValue);
            }
        }
    }

    

    @Override
    protected boolean onIsModified()
    {
        // When checking if modified we need to check the values from the class
        for (String lcProperty : Java.getPropertyMethods(getClass()))
        {
            setValueInList(lcProperty, Java.getPropertyValue(this, lcProperty));
        }
        return m_oValues != null && m_oValues.isModified();
    }
    

    @Override
    protected BusinessObject<ObjectRegistry> onCopy()
    {
        // TODO: Implement this
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Marks the object as deleted, this does not remove the object from the data store 
     * until save is called
     */
    @Override
    protected void onDelete()
    {
        getPrimaryObject().delete();
    }





}
