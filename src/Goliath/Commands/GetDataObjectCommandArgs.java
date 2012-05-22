/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Commands;

import Goliath.Arguments.Arguments;
import Goliath.Collections.PropertySet;

/**
 *
 * @author home_stanbridge
 */
public class GetDataObjectCommandArgs<T extends Goliath.Data.DataObjects.SimpleDataObject> extends Arguments
{
    private Class<T> m_oClass;
    private PropertySet m_oProperties;

    protected GetDataObjectCommandArgs(T tcLookupObject, String[] taProperties)
    {
        m_oClass = (Class<T>)tcLookupObject.getClass();
        m_oProperties = new PropertySet();
        for (String lcItem : taProperties)
        {
            m_oProperties.setProperty(lcItem, Goliath.DynamicCode.Java.getPropertyValue(tcLookupObject, lcItem));
        }
    }


    public GetDataObjectCommandArgs(Class<T> tcLookupObject, PropertySet toProperties)
    {
        m_oClass = tcLookupObject;
        m_oProperties = toProperties;
    }

    public Class<T> getTemplateClass()
    {
        return m_oClass;
    }

    public PropertySet getProperties()
    {
        return m_oProperties;
    }

}
