package de.dihco.android.stechuhr.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.MyApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.TimeOverView;

/**
 * Created by Martin on 01.02.2015.
 */
public final class StrHelp {

    private StrHelp() {

    }

    public static String getBackupFileName(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return format1.format(c.getTime()) + ".txt";
    }


    public static String getYearFromSeconds(long secs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(secs * 1000L);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
        return format1.format(c.getTime());
    }

    public static String getWeekDayFromSeconds(long secs) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(secs * 1000L);
            SimpleDateFormat format1 = new SimpleDateFormat("E");
            return format1.format(c.getTime());
    }

    public static String getWeekNumberFromSeconds(long secs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(secs * 1000L);
        SimpleDateFormat format1 = new SimpleDateFormat("ww");
        return format1.format(c.getTime());
    }

    public static String getMonthNameFromSeconds(long secs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(secs * 1000L);
        SimpleDateFormat format1 = new SimpleDateFormat("MMMM");
        return format1.format(c.getTime());
    }

    public static String getClockTimeFromSeconds(long secs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(secs * 1000L);
        return String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public static String getTimeSpanFromSeconds(long secs) {
        if (TimeUnit.SECONDS.toHours(secs) == 0)
            return String.format("%dmin", TimeUnit.SECONDS.toMinutes(secs));
        else
            return String.format("%dh %02dmin", TimeUnit.SECONDS.toHours(secs), TimeUnit.SECONDS.toMinutes(secs) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(secs)));
    }

    public static String getDateFromSeconds(long secs) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(secs * 1000L);
        SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
        return format1.format(c.getTime());
    }

    public static String getActivityString(long secs, int code) {
        String res = StrHelp.getClockTimeFromSeconds(secs) + " - ";
        switch (code) {
            case MyApplication.STARTDAY:
                res += MyApplication.context.getString(R.string.btnStartDay);
                break;
            case MyApplication.ENDDAY:
                res += MyApplication.context.getString(R.string.btnEndDay);
                break;
            case MyApplication.STARTPAUSE:
                res += MyApplication.context.getString(R.string.btnStartPause);
                break;
            case MyApplication.ENDPAUSE:
                res += MyApplication.context.getString(R.string.btnEndPause);
                break;

        }
        return res;
    }

    public static String getOverViewText(TimeOverView timeOverView, String lineSpace) {
        if (timeOverView.arbeitsZeit == 0)
            return lineSpace + MyApplication.context.getString(R.string.notWorked);

        String res = lineSpace + MyApplication.context.getString(R.string.worktime) + ": " + StrHelp.getTimeSpanFromSeconds(timeOverView.arbeitsZeit);

        if (timeOverView.pausenZeit != 0) {
            res += "\n" + lineSpace + MyApplication.context.getString(R.string.pause) + ": " + StrHelp.getTimeSpanFromSeconds(timeOverView.pausenZeit);

            if (MyApplication.getPreferences().getBoolean("useMinPauseTime", false)) {
                if (timeOverView.forcedPauseTime != 0) {
                    res += " ( -" + StrHelp.getTimeSpanFromSeconds(timeOverView.forcedPauseTime) + ")";
                }
            }
        }

        if (MyApplication.getPreferences().getBoolean("useWorkTime", false)) {
            res += "\n" + lineSpace + MyApplication.context.getString(R.string.overtime);

            if (timeOverView.überStunden < 0) {
                res += ": -" + StrHelp.getTimeSpanFromSeconds(Math.abs(timeOverView.überStunden));
            } else {
                res += ": +" + StrHelp.getTimeSpanFromSeconds(Math.abs(timeOverView.überStunden));
            }
        }



        return res;
    }


}
