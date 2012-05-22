/* ========================================================
 * TimePeriod.java
 *
 * Author:      admin
 * Created:     Dec 13, 2011, 8:08:35 AM
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */
package Goliath.Data.DataObjects;

import Goliath.Date;
import Goliath.Exceptions.InvalidParameterException;

/**
 * A Time period is a period with a specific start and an end date
 *
 * @see         Related Class
 * @version     1.0 Dec 13, 2011
 * @author      admin
 **/
public class TimePeriod extends UndoableDataObject<TimePeriod>
{
    private Date m_dStart;
    private Date m_dEnd;

    /**
     * Creates a new instance of TimePeriod
     */
    public TimePeriod()
    {
        // A TimePeriod will not be allowed to have null dates
        this(new Date(), 1);
    }

    /**
     * Creates a new time period
     * @param tdStart the starting time of the time period
     * @param tnMilliseconds the amount of time in milliseconds until the end of this time period
     */
    public TimePeriod(Date tdStart, long tnMilliseconds)
    {
        this(tdStart, new Date(tdStart.getLong() + tnMilliseconds));
    }

    /**
     * Creates a new time period that expires tnMilliseconds from now
     * @param tnMilliseconds the number of milliseconds to expire from now
     */
    public TimePeriod(long tnMilliseconds)
    {
        this(new Date(), tnMilliseconds);
    }

    /**
     * Creates a time period that starts on tdStart and ends on tdEnd
     * @param tdStart the start date
     * @param tdEnd the end date
     */
    public TimePeriod(Date tdStart, Date tdEnd)
    {
        if (tdStart.getLong() > tdEnd.getLong())
        {
            throw new InvalidParameterException("Start date is after end date", "tdStart");
        }
        m_dStart = tdStart;
        m_dEnd = tdEnd;
    }
    
    /**
     * Checks if this time period is current, a current time period is one
     * that the current date is between the start and end date
     * @return true if this date is current
     */
    public final boolean isCurrent()
    {
        return isCurrent(new Date());
    }

    /**
     * Checks if the date is between the start and end date of this time period
     * @param toDate the date
     * @return true if between the start and end date
     */
    public final boolean isCurrent(Date toDate)
    {
        long lnCurrent = toDate.getLong();
        return m_dStart.getLong() <= lnCurrent && lnCurrent <= m_dEnd.getLong();
    }
    
    /**
     * Checks if we have not have not yet entered this time period
     * @return true if we are before this time period
     */
    public final boolean isBefore()
    {
        return isBefore(new Date());
    }

    /**
     * Checks if the specified date is before this time period
     * @param toDate the date to check
     * @return true if the date specified is before the time period
     */
    public final boolean isBefore(Date toDate)
    {
        long lnCurrent = toDate.getLong();
        return lnCurrent < m_dStart.getLong();
    }

    /**
     * Checks if we are post this time period
     * @return true if we are after this time period
     */
    public final boolean isAfter()
    {
        return isAfter(new Date());
    }

    /**
     * Checks if the specified date is after this time period
     * @param toDate the date to check
     * @return true if after the end date of this time period
     */
    public final boolean isAfter(Date toDate)
    {
        long lnCurrent = toDate.getLong();
        return m_dEnd.getLong() < lnCurrent;
    }

    /**
     * Checks if the specified time period overlaps with this time period
     * @param toPeriod the time period to check
     * @return true if it overlaps
     */
    public final boolean isOverlapping(TimePeriod toPeriod)
    {
        return (toPeriod.m_dStart.compareTo(m_dStart) == 1 && toPeriod.m_dStart.compareTo(m_dEnd) == -1) ||
                (toPeriod.m_dEnd.compareTo(m_dStart) == 1 && toPeriod.m_dEnd.compareTo(m_dEnd) == -1);

    }

    /**
     * Gets the start date to this period
     * @return the start date of the period
     */
    public final Goliath.Date getStartDate()
    {
        canReadProperty();
        return m_dStart;
    }

    /**
     * Sets the start date of this period
     * @param tdValue the time period start date
     */
    @Goliath.Annotations.NoNulls
    public final void setStartDate(Goliath.Date tdValue)
    {
        Goliath.Utilities.checkParameterNotNull("tdValue", tdValue);
        canWriteProperty();
        if (isDifferent(m_dStart, tdValue))
        {
            m_dStart = tdValue;
            propertyHasChanged();
        }
    }

    /**
     * Gets the end date of this period
     * @return the end date of this period
     */
    public final Goliath.Date getEndDate()
    {
        canReadProperty();
        return m_dEnd;
    }

    /**
     * Sets the end date of this period
     * @param tdValue the end date
     */
    @Goliath.Annotations.NoNulls
    public void setEndDate(Goliath.Date tdValue)
    {
        Goliath.Utilities.checkParameterNotNull("tdValue", tdValue);
        canWriteProperty();
        if (isDifferent(m_dEnd, tdValue))
        {
            m_dEnd = tdValue;
            propertyHasChanged();
        }
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
        final TimePeriod other = (TimePeriod) obj;
        if (this.m_dStart != other.m_dStart && (this.m_dStart == null || !this.m_dStart.equals(other.m_dStart)))
        {
            return false;
        }
        if (this.m_dEnd != other.m_dEnd && (this.m_dEnd == null || !this.m_dEnd.equals(other.m_dEnd)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 47 * hash + (this.m_dStart != null ? this.m_dStart.hashCode() : 0);
        hash = 47 * hash + (this.m_dEnd != null ? this.m_dEnd.hashCode() : 0);
        return hash;
    }


    
}
