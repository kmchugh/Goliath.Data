/* =========================================================
 * LanguageLookup.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 3:51:16 PM
 *
 * Description
 * --------------------------------------------------------
 * Defines individual words or phrases that can be translated
 * between languages.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

/**
 *
 * @author kenmchugh
 */
public class LanguageLookup extends UndoableDataObject<LanguageLookup>
{
    private long m_nLanguageID;
    private String m_cLookupValue;
    private String m_cTranslatedValue;

    private Language m_oLanguage;

    public String getLookupValue()
    {
        canReadProperty();
        return m_cLookupValue;
    }

    @Goliath.Annotations.MaximumLength(length=2500)
    @Goliath.Annotations.NoNulls
    public void setLookupValue(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cLookupValue, tcValue))
        {
            m_cLookupValue = tcValue;
            propertyHasChanged();
        }
    }

    public String getTranslatedValue()
    {
        canReadProperty();
        return m_cTranslatedValue;
    }

    @Goliath.Annotations.MaximumLength(length=2500)
    @Goliath.Annotations.NoNulls
    public void setTranslatedValue(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cTranslatedValue, tcValue))
        {
            m_cTranslatedValue = tcValue;
            propertyHasChanged();
        }
    }

    public long getLanguageID()
    {
        canReadProperty();
        if (m_oLanguage != null)
        {
            return m_oLanguage.getID();
        }
        return m_nLanguageID;
    }

    @Goliath.Annotations.ForeignKey(className=Goliath.Data.DataObjects.Language.class, fieldName="ID")
    public void setLanguageID(long tnValue)
    {
        canWriteProperty();
        if (isDifferent(m_nLanguageID, tnValue))
        {
            m_nLanguageID = tnValue;
            if (m_oLanguage != null && m_oLanguage.getID() != tnValue)
            {
                m_oLanguage = null;
            }
            propertyHasChanged();
        }
    }


    @Goliath.Annotations.NotProperty
    public Language getLanguage()
    {
        if (m_oLanguage == null)
        {
            m_oLanguage = lazyLoad(Language.class, m_nLanguageID);
        }
        return m_oLanguage;
    }

    @Goliath.Annotations.NotProperty
    public void setLanguage(Language toType)
    {
        if (isDifferent(m_oLanguage, toType))
        {
            m_oLanguage = toType;
            setLanguageID(m_oLanguage.getID());
        }
    }

}
