/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Data;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Constants.LogType;
import Goliath.DynamicCode.Java;
import Goliath.DynamicEnum;
import Goliath.Interfaces.Data.IDataType;
import java.util.Date;

/**
 *
 * @author kenmchugh
 */
public class DataType extends DynamicEnum
        implements IDataType
{
    private int m_nLength = 0;
    private Class m_oMap = null;

    /**
     * Gets the appropriate data type for the class specified
     * @param toClass the class to get the datatype for
     * @return the datatype for this class
     */
    public static IDataType createFromClass(Class toClass)
    {
        return createFromClass(toClass, 0);
    }

    /**
     * Gets the appropriate data type for the class specified
     * @param toClass the class to get the datatype for
     * @param tnLength The length of the datatype, if the datatype does not use a length, this is ignored.
     * @return the datatype for this class
     */
    public static IDataType createFromClass(Class toClass, long tnLength)
    {
        Goliath.Utilities.checkParameterNotNull("toClass", toClass);
        DataType loReturn = null;

        // If it is a date, get it out of the way now
        if (Goliath.Date.class.isAssignableFrom(toClass) || java.util.Date.class.isAssignableFrom(toClass))
        {
            return DataType.DATETIME();
        }
        
        // If the class is a dynamic enumeration, then the data type is ID or LONG
        if (Java.isEqualOrAssignable(DynamicEnum.class, toClass))
        {
            return DataType.BIGINT();
        }

        Class loByteArrayClass = null;
        try
        {
            loByteArrayClass = Class.forName("[B");
            if (loByteArrayClass.isAssignableFrom(toClass))
            {
                return DataType.BINARY();
            }
        }
        catch (Throwable ignore)
        {}

        // A class must be a primitive type in order to be converted to a DataType
        if (Goliath.DynamicCode.Java.isPrimitive(toClass))
        {
            if (long.class.isAssignableFrom(toClass) || Long.class.isAssignableFrom(toClass))
            {
                return DataType.BIGINT();
            }
            else if (int.class.isAssignableFrom(toClass) || Integer.class.isAssignableFrom(toClass))
            {
                return DataType.INTEGER();
            }
            else if (boolean.class.isAssignableFrom(toClass) || Boolean.class.isAssignableFrom(toClass))
            {
                return DataType.BOOLEAN();
            }
            else if (float.class.isAssignableFrom(toClass) || Float.class.isAssignableFrom(toClass))
            {
                return DataType.FLOAT();
            }
            else if (double.class.isAssignableFrom(toClass) || Double.class.isAssignableFrom(toClass))
            {
                return DataType.DOUBLE();
            }
            else if (String.class.isAssignableFrom(toClass))
            {
                if (tnLength <= 0)
                {
                    Application.getInstance().log("Invalid string length for string with length " + Long.toString(tnLength) + " on " + toClass.getName() + " using length of 1000", LogType.WARNING());
                    tnLength = 1000;
                }
                return DataType.VARCHAR((int)tnLength);
            }
        }
        
        if (loReturn == null)
        {
            throw new UnsupportedOperationException("The type " + toClass.getName() + " can not be converted to a DataType.");
        }
        return loReturn;
    }

    
    protected DataType(String tcType, Class toMap)
    {
        super(tcType);
        
    }

    @Override
    public int getLength()
    {
        return m_nLength;
    }

    @Override
    public Class getMap()
    {
        return m_oMap;
    }

    @Override
    public String getName()
    {
        return super.getValue();
    }
    
    protected void setLength(int tnLength)
    {
        m_nLength = tnLength;
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
        final DataType other = (DataType) obj;
        if (this.m_nLength != other.m_nLength)
        {
            return false;
        }
        if (this.getName() != other.getName() && (this.getName() == null || !this.getName().equals(other.getName())))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 29 * hash + Float.floatToIntBits(this.m_nLength);
        hash = 29 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
        return hash;
    }

    
    
    
    private static IDataType g_oDateTime = null;
    private static IDataType g_oTimestamp = null;
    private static IDataType g_oBigInt = null;
    private static IDataType g_oInt = null;
    private static IDataType g_oFloat = null;
    private static IDataType g_oBoolean = null;
    private static IDataType g_oBinary = null;
    private static IDataType g_oDouble = null;
    private static HashTable<String, IDataType> g_oVarchars = null;
    


    public static IDataType INTEGER()
    {
        if (g_oInt == null)
        {
            g_oInt = new DataType("int", Integer.class);
        }
        return g_oInt;
    }


    public static IDataType BIGINT()
    {
        if (g_oBigInt == null)
        {
            g_oBigInt = new DataType("bigint", Long.class);
        }
        return g_oBigInt;
    }

    public static IDataType BINARY()
    {
        if (g_oBinary == null)
        {
            try
            {
                g_oBinary = new DataType("blob", Class.forName("[B"));
            }
            catch (Throwable ignore)
            {}
        }
        return g_oBinary;
    }

    public static IDataType FLOAT()
    {
        if (g_oFloat == null)
        {
            g_oFloat = new DataType("real", Float.class);
        }
        return g_oFloat;
    }

    public static IDataType DOUBLE()
    {
        if (g_oDouble == null)
        {
            g_oDouble = new DataType("double", Double.class);
        }
        return g_oDouble;
    }

    public static IDataType BOOLEAN()
    {
        if (g_oBoolean == null)
        {
            g_oBoolean = new DataType("bit", Boolean.class);
            ((DataType)g_oBoolean).setLength(1);
        }
        return g_oBoolean;
    }

    public static IDataType VARCHAR(int tnLength)
    {
        if (g_oVarchars == null)
        {
            g_oVarchars = new HashTable<String, IDataType>();
        }
        if (!g_oVarchars.containsKey(Integer.toString(tnLength)))
        {
            DataType loType = new DataType("varchar", String.class);
            loType.setLength(tnLength);
            g_oVarchars.put(Integer.toString(tnLength), loType);
        }
        return g_oVarchars.get(Integer.toString(tnLength));
    }
    
    public static IDataType DATETIME()
    {
        if (g_oDateTime == null)
        {
            g_oDateTime = new DataType("bigint", Long.class);
        }
        return g_oDateTime;
    }
    
    public static IDataType TIMESTAMP()
    {
        if (g_oTimestamp == null)
        {
            g_oTimestamp = new DataType("timestamp", Date.class);
        }
        return g_oTimestamp;
    }
    
}
