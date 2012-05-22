package Goliath.Data.Constants;

import Goliath.DynamicEnum;

/**
 * Static enumeration types for language alignment
 * @author admin
 */
public class LanguageDirection extends DynamicEnum
{
    private static LanguageDirection g_oLTR;
    /**
     * Left to right language alignment
     * @return Static object for LTR
     */
    public static LanguageDirection LTR()
    {
        if (g_oLTR == null)
        {
            g_oLTR = createEnumeration(LanguageDirection.class, "LTR");
        }
        return g_oLTR;
    }
    
    private static LanguageDirection g_oRTL;
    /**
     * Right to left language alignment
     * @return Static object for RTL
     */
    public static LanguageDirection RTL()
    {
        if (g_oRTL == null)
        {
            g_oRTL = createEnumeration(LanguageDirection.class, "RTL");
        }
        return g_oRTL;
    }
    
    private static LanguageDirection g_oTTB;
    /**
     * Top to bottom alignment
     * @return Static object for TTB
     */
    public static LanguageDirection TTB()
    {
        if (g_oTTB == null)
        {
            g_oTTB = createEnumeration(LanguageDirection.class, "TTB");
        }
        return g_oTTB;
    }
    
    
    
    /**
     * Creates a new instance of a LanguageDirection Object 
     *
     * @param tcValue The value for the Language Direction
     * @throws Goliath.Exceptions.InvalidParameterException
     */
    protected LanguageDirection(String tcValue)
    {
        super(tcValue);
    }
    
    
    
    
}
