/* =========================================================
 * Language.java
 *
 * Author:      kenmchugh
 * Created:     Jul 26, 2010, 3:51:22 PM
 *
 * Description
 * --------------------------------------------------------
 * Defines each language that is usable in the system
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Data.DataObjects;

import Goliath.Data.Constants.LanguageDirection;

/**
 *
 * @author kenmchugh
 */
public class Language extends LookupType<Language>
{
    private String m_cISOCode;
    private LanguageDirection m_oDirection;

    /**
     * Creates a new instance of language
     */
    public Language()
    {
        // By default direction is LTR
        this.m_oDirection = LanguageDirection.LTR();
    }
    
    /**
     * Yes a language must be uniquely identifiable across systems
     * for migration purposes
     * @return true
     */
    @Override
    public boolean hasGUID()
    {
        return true;
    }

    /**
     * Sets the ISO Code for this language
     * @return The ISO Code
     */
    public String getISOCode()
    {
        canReadProperty();
        return m_cISOCode;
    }

    /**
     * Gets the ISO Code, or null if there is no code specified
     * @param tcValue the language code
     */
    @Goliath.Annotations.MaximumLength(length=10)
    public void setISOCode(String tcValue)
    {
        canWriteProperty();
        if (isDifferent(m_cISOCode, tcValue))
        {
            m_cISOCode = tcValue;
            propertyHasChanged();
        }
    }
    
    /**
     * Gets the direction of this language
     * @return the direction of this language
     */
    public LanguageDirection getDirection()
    {
        canReadProperty();
        return m_oDirection;
    }
    
    /**
     * Sets the direction of this language
     * @param toDirection the language direction
     */
    @Goliath.Annotations.NoNulls
    public void setDirection(LanguageDirection toDirection)
    {
        canWriteProperty();
        if (isDifferent(m_oDirection, toDirection))
        {
            m_oDirection = toDirection;
            propertyHasChanged();
        }
    }
}
