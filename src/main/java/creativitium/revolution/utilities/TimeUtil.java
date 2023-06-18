package creativitium.revolution.utilities;

import lombok.RequiredArgsConstructor;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil
{
    private static final Calendar calendar = Calendar.getInstance();

    public static long getOffsetFromCurrentTime(final int amount, final TimeUnit unit)
    {
        calendar.setTime(new Date());
        calendar.add(unit.type, amount);
        return calendar.getTimeInMillis();
    }

    @RequiredArgsConstructor
    public enum TimeUnit
    {
        DAYS(Calendar.DAY_OF_MONTH),
        HOURS(Calendar.HOUR),
        MILLISECONDS(Calendar.SECOND),
        MINUTES(Calendar.MINUTE),
        MONTHS(Calendar.MONTH),
        SECONDS(Calendar.SECOND),
        YEARS(Calendar.YEAR);

        final int type;
    }
}
