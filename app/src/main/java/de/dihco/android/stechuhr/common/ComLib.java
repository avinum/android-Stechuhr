package de.dihco.android.stechuhr.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.Backup.BackupLocalActivity;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.activities.MainActivity;
import de.dihco.android.stechuhr.activities.SettingsActivity;

/**
 * Created by Martin on 31.01.2015.
 */
public final class ComLib {

    private ComLib() {
    }


    public static void ShowMessage(String msg) {
        Toast.makeText(StechuhrApplication.context, msg, Toast.LENGTH_LONG).show();

    }

    public static TimeOverView getTimeOverViewFromCursor(Cursor cursor) {
        cursor.moveToPosition(-1);

        long sollZeit = 0;
        if (StechuhrApplication.getPreferences().getBoolean("useWorkTime", false)) {
            String t1 = StechuhrApplication.getPreferences().getString("standardWorkTime", "480");
            sollZeit = TimeUnit.MINUTES.toSeconds(Long.parseLong(t1));
        }

        long minPauseTime = 0;
        boolean useMinPauseTime = StechuhrApplication.getPreferences().getBoolean("useMinPauseTime", false);
        if (useMinPauseTime) {
            String t2 = StechuhrApplication.getPreferences().getString("minimalPauseTime", "30");
            minPauseTime = TimeUnit.MINUTES.toSeconds(Long.parseLong(t2));
        }

        TimeOverView res = new TimeOverView();

        if (cursor.getCount() == 0)
            return res;

        boolean noEnd = true;
        boolean noEndPause = false;
        long countDay = 0;
        long pauseTimePerDay = 0;


        while (cursor.moveToNext()) {
            switch (cursor.getInt(1)) {
                case StechuhrApplication.STARTDAY:
                    res.arbeitsZeit -= cursor.getLong(0);
                    res.startZeit = cursor.getLong(0);
                    pauseTimePerDay = 0;
                    noEnd = true;
                    countDay++;
                    break;
                case StechuhrApplication.STARTPAUSE:
                    res.arbeitsZeit += cursor.getLong(0);
                    pauseTimePerDay -= cursor.getLong(0);
                    noEndPause = true;
                    break;
                case StechuhrApplication.ENDPAUSE:
                    res.arbeitsZeit -= cursor.getLong(0);
                    pauseTimePerDay += cursor.getLong(0);
                    noEndPause = false;
                    break;
                case StechuhrApplication.ENDDAY:
                    res.arbeitsZeit += cursor.getLong(0);
                    res.endZeit = cursor.getLong(0);
                    noEnd = false;
                    if (useMinPauseTime) {
                        if (minPauseTime > pauseTimePerDay) {//zu wenig Pause
                            res.forcedPauseTime += (minPauseTime - pauseTimePerDay);
                            res.arbeitsZeit -= (minPauseTime - pauseTimePerDay);
                            res.pausenZeit += minPauseTime;
                            pauseTimePerDay = 0;
                        } else {//genug Pause
                            res.pausenZeit += pauseTimePerDay;
                            pauseTimePerDay = 0;
                        }

                    } else {
                        res.pausenZeit += pauseTimePerDay;
                        pauseTimePerDay = 0;
                    }
                    break;
            }
        }

        if (noEndPause) {
            pauseTimePerDay += (System.currentTimeMillis() / 1000L);

            res.arbeitsZeit -= (System.currentTimeMillis() / 1000L);
        }

        if (noEnd) {
            res.arbeitsZeit += (System.currentTimeMillis() / 1000L);

            if (useMinPauseTime) {
                if (minPauseTime > pauseTimePerDay) {//zu wenig Pause
                    res.forcedPauseTime += (minPauseTime - pauseTimePerDay);
                    res.arbeitsZeit -= (minPauseTime - pauseTimePerDay);
                    res.pausenZeit += minPauseTime;
                } else {//genug Pause
                    res.pausenZeit += pauseTimePerDay;
                }
            } else {
                res.pausenZeit += pauseTimePerDay;
            }

        }

        if (StechuhrApplication.getPreferences().getBoolean("useWorkTime", false)) {
            res.überStunden = res.arbeitsZeit - (sollZeit * countDay);
        }

        return res;
    }

    public static long getMidnightFormSeconds(long seconds) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(seconds * 1000L);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getSecondsFromDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getSecondsFromDateAndTime(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getUnixPrevMidnight() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getUnixOfPreviousMondayMidnight(int offset) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, offset * 7);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getUnixOfPreviousFirstInMonthMidnight(int offset) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, offset);
        return (c.getTimeInMillis() / 1000L);
    }

    public static long getUnixTimeNow() {
        return (System.currentTimeMillis() / 1000L);
    }

    public static void deleteData() {
        StechuhrApplication.getHelper().deleteAll();
    }


    public static void createBackup(Writer writer) throws IOException {
        writer.write("Stechuhr Backup\n" + StrHelp.getDateFromSeconds(ComLib.getUnixTimeNow()) + "\n" + StrHelp.getClockTimeFromSeconds(ComLib.getUnixTimeNow()) + "\n===============\n");

        Cursor cursor = StechuhrApplication.getHelper().getAll();

        while (cursor.moveToNext()) {
            writer.write(cursor.getString(0) + "," + cursor.getString(1) + "\n");
        }

        writer.close();
    }

    public static void importBackup(final BufferedReader bufferedReader , Context context) {

        final ProgressDialog progDailog = ProgressDialog.show(context, StechuhrApplication.context.getString(R.string.backupImport),
                StechuhrApplication.context.getString(R.string.pleaseWait), true);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                int errorCounter = 0;
                int successCounter = 0;

                String line = null;
                try {
                    line = bufferedReader.readLine();


                    if (!line.equals("Stechuhr Backup"))
                        ComLib.ShowMessage("Kein Stechuhr Backup Format");

                    line = bufferedReader.readLine();//Datum
                    line = bufferedReader.readLine();//Zeit
                    line = bufferedReader.readLine();//Trenner

                    line = bufferedReader.readLine(); // Erste Zeile
                    while (line != null) {
                        String[] lineSplit = line.split(",");
                        long secs = Long.parseLong(lineSplit[0]);
                        int code = Integer.parseInt(lineSplit[1]);

                        if (StechuhrApplication.getHelper().insertActionWithTime(secs, code, true) == -1)
                            errorCounter++;
                        else
                            successCounter++;

                        line = bufferedReader.readLine();
                    }

                } catch (IOException e) {
                    ComLib.ShowMessage("Import fehlgeschlagen.\n\n" + e.getMessage());
                }

                ComLib.ShowMessage("Import abgeschlossen.\n\n" + successCounter + " eingefügt\n" + errorCounter + " übersprungen");

                progDailog.dismiss();
                Looper.loop();
            }
        }.start();


    }

    public static ArrayList<String> getLocalBackupFileList() {
        final ArrayList<String> list = new ArrayList<String>();

        File folder = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Backup");
        File fileArray[] = folder.listFiles();
        if (fileArray != null) {
            Arrays.sort(fileArray);

            for (int i = fileArray.length - 1; i >= 0; i--) {
                list.add(fileArray[i].getName());
            }
        } else {
            ComLib.ShowMessage("Keine Backups vorhanden!");
        }
        return list;
    }
}
