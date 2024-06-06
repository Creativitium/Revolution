package creativitium.revolution.foundation.utilities;

import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Util
{
    public static DateFormat DATE_FORMAT = new SimpleDateFormat("MMMMM d, yyyy 'at' h:mm:ss aaa");

    public static DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM'/'dd'/'yyyy '@' hh:mm");

    private static final Calendar calendar = Calendar.getInstance();

    public static long getOffsetFromCurrentTimeMilis(final int amount, final TimeUnit unit)
    {
        calendar.setTime(new Date());
        calendar.add(unit.type, amount);
        return calendar.getTimeInMillis();
    }

    public static long getOffsetFromCurrentTime(final int amount, final TimeUnit unit)
    {
        calendar.setTime(new Date());
        calendar.add(unit.type, amount);
        return calendar.getTime().getTime() / 1000;
    }

    public static boolean isUUIDValid(String input)
    {
        try
        {
            UUID.fromString(input);
            return true;
        }
        catch (Throwable ex)
        {
            return false;
        }
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
