package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;

public class ExportActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_export);

        Preference button_action_ExportStart = findPreference("action_ExportStart");
        button_action_ExportStart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                startExport();
                return true;
            }
        });
    }

    private void startExport() {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Export");
            if (!folder.exists()) {
                folder.mkdir();
            }

            Integer exportFormat = Integer.parseInt(StechuhrApplication.getPreferences().getString("exportformat", "1"));

            String pre = "";

            switch (exportFormat) {
                case 1:
                    pre = getString(R.string.days);
                    break;
                case 7:
                    pre = getString(R.string.weeks);
                    break;
                case 30:
                    pre = getString(R.string.months);
                    break;
            }

            String fileName = StrHelp.getExportFileName(pre);
            File traceFile = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Export", fileName);
            if (!traceFile.exists()) {
                traceFile.createNewFile();
                // Adds a line to the trace file
                BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));


                switch (exportFormat) {
                    case 1:
                        createDayExport(writer, ";");
                        break;
                    case 7:
                        createWeekExport(writer, ";");
                        break;
                    case 30:
                        createMonthExport(writer, ";");
                        break;
                }

                // Refresh the data so it can seen when the device is plugged in a
                // computer. You may have to unplug and replug the device to see the
                // latest changes. This is not necessary if the user should not modify
                // the files.
                MediaScannerConnection.scanFile(StechuhrApplication.context,
                        new String[]{traceFile.toString()},
                        null,
                        null);

                String filePath = traceFile.getPath();

//                ComLib.ShowMessage(getString(R.string.exportSuccess) + "\n\n" + filePath);
                shareQuestion(filePath, fileName);


            } else {
                ComLib.ShowMessage(getString(R.string.exportError) + "\n\nDatei schon vorhanden.");
            }
        } catch (IOException e) {
            ComLib.ShowMessage(getString(R.string.exportError) + "\n\nFehler bei der Ausgabe.");
        }
    }

    private void shareQuestion(final String filePath, final String fileName) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        share(filePath, fileName);
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        ComLib.ShowMessage("Exportpfad:" + "\n\n" + filePath);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ExportActivity.this);
        builder.setMessage(getString(R.string.exportSuccess) + "\n\n" + "Jetzt teilen?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();
    }

    private void share(String filePath, String fileName) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(filePath);

        if (fileWithinMyDir.exists()) {
            intentShareFile.setType("text/csv");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filePath));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, fileName);
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Export teilen...");

            startActivity(Intent.createChooser(intentShareFile, "Export teilen"));
        }
    }

    public static void createDayExport(Writer writer, String splitter) throws IOException {

        boolean useWorkTime = StechuhrApplication.getPreferences().getBoolean("useWorkTime", false);
        boolean useMinPauseTime = StechuhrApplication.getPreferences().getBoolean("useMinPauseTime", false);

        writer.write("Datum" + splitter + "Wochentag" + splitter + "Startzeit" + splitter + "Endzeit" + splitter + "Arbeitszeit" + splitter + "Pausenzeit" + splitter);
        if (useWorkTime)
            writer.write(StechuhrApplication.context.getString(R.string.overtime) + splitter);
        if (useMinPauseTime)
            writer.write("Mindestpausenzeit" + splitter);
        writer.write("Pausen\n");

        Cursor cursor2 = StechuhrApplication.getHelper().getFirstEvent();

        if (cursor2.getCount() == 0) {
            writer.close();
            return;
        }

        cursor2.moveToFirst();

        long firstStartSec = cursor2.getLong(0);

        cursor2.close();

        long todayEndSec = ComLib.getUnixPrevMidnight(+1);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(firstStartSec * 1000L);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);


        long currentMidnight = c.getTimeInMillis() / 1000L;

        while (currentMidnight < todayEndSec) {

            Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(currentMidnight, TimeUnit.DAYS.toSeconds(1));

            String line = StrHelp.getDateFromSeconds(currentMidnight + TimeUnit.HOURS.toSeconds(12)) + splitter + StrHelp.getWeekDayFromSeconds(currentMidnight + TimeUnit.HOURS.toSeconds(12));

            if (cursor.getCount() != 0) {

                TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);


                line += splitter + StrHelp.getClockTimeFromSeconds(tOV.startZeit) + splitter + StrHelp.getClockTimeFromSeconds(tOV.endZeit)
                        + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.arbeitsZeit) + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.pausenZeit) + splitter;

                if (useWorkTime)
                    line += StrHelp.getExportTimeSpanFromSeconds(tOV.überStunden) + splitter;
                if (useMinPauseTime)
                    line += StrHelp.getExportTimeSpanFromSeconds(tOV.forcedPauseTime) + splitter;

                line += tOV.pauseTimesString;
            }
            line += "\n";

            writer.write(line);

            c.add(Calendar.DATE, 1);
            currentMidnight = c.getTimeInMillis() / 1000L;

            cursor.close();

        }

        writer.close();
    }

    public static void createWeekExport(Writer writer, String splitter) throws IOException {
        boolean useWorkTime = StechuhrApplication.getPreferences().getBoolean("useWorkTime", false);
        boolean useMinPauseTime = StechuhrApplication.getPreferences().getBoolean("useMinPauseTime", false);

        writer.write("Woche" + splitter + "gearbeitete Tage" + splitter + "durch. Arbeitszeit" + splitter + "ges. Arbeitszeit" + splitter + "Pausenzeit");
        if (useWorkTime)
            writer.write(splitter + StechuhrApplication.context.getString(R.string.overtime));
        if (useMinPauseTime)
            writer.write(splitter + "Mindestpausenzeit");
        writer.write("\n");
        Cursor cursor2 = StechuhrApplication.getHelper().getFirstEvent();

        if (cursor2.getCount() == 0) {
            writer.close();
            return;
        }

        cursor2.moveToFirst();

        long firstStartSec = cursor2.getLong(0);

        cursor2.close();

        long todayEndSec = ComLib.getUnixPrevMidnight(+1);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(firstStartSec * 1000L);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        long currentMidnight = c.getTimeInMillis() / 1000L;

        while (currentMidnight < todayEndSec) {

            Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(currentMidnight, TimeUnit.DAYS.toSeconds(7));

            String line = StrHelp.getWeekNumberFromSeconds(currentMidnight + TimeUnit.HOURS.toSeconds(12));

            if (cursor.getCount() != 0) {

                TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);

                line += splitter + tOV.daysWorked + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.arbeitsZeit / tOV.daysWorked);

                line += splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.arbeitsZeit) + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.pausenZeit);

                if (useWorkTime)
                    line +=  splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.überStunden) ;
                if (useMinPauseTime)
                    line +=  splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.forcedPauseTime) ;

            }
            line += "\n";

            writer.write(line);

            c.add(Calendar.DATE, 7);
            currentMidnight = c.getTimeInMillis() / 1000L;

            cursor.close();

        }

        writer.close();
    }

    public static void createMonthExport(Writer writer, String splitter) throws IOException {
        boolean useWorkTime = StechuhrApplication.getPreferences().getBoolean("useWorkTime", false);
        boolean useMinPauseTime = StechuhrApplication.getPreferences().getBoolean("useMinPauseTime", false);

        writer.write("Monat" + splitter + "gearbeitete Tage" + splitter + "durch. Arbeitszeit" + splitter + "ges. Arbeitszeit" + splitter + "Pausenzeit");
        if (useWorkTime)
            writer.write( splitter + StechuhrApplication.context.getString(R.string.overtime));
        if (useMinPauseTime)
            writer.write( splitter + "Mindestpausenzeit");
        writer.write("\n");
        Cursor cursor2 = StechuhrApplication.getHelper().getFirstEvent();

        if (cursor2.getCount() == 0) {
            writer.close();
            return;
        }

        cursor2.moveToFirst();

        long firstStartSec = cursor2.getLong(0);

        cursor2.close();

        long todayEndSec = ComLib.getUnixPrevMidnight(+1);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(firstStartSec * 1000L);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);

        long currentMidnight = c.getTimeInMillis() / 1000L;

        while (currentMidnight < todayEndSec) {
            c.add(Calendar.MONTH, 1);
            long nextMidnight = c.getTimeInMillis() / 1000L;

            Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(currentMidnight, nextMidnight - currentMidnight);

            String line = StrHelp.getMonthNameFromSeconds(currentMidnight + TimeUnit.HOURS.toSeconds(12));

            if (cursor.getCount() != 0) {

                TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);

                line += splitter + tOV.daysWorked + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.arbeitsZeit / tOV.daysWorked);

                line += splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.arbeitsZeit) + splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.pausenZeit);

                if (useWorkTime)
                    line += splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.überStunden) ;
                if (useMinPauseTime)
                    line += splitter + StrHelp.getExportTimeSpanFromSeconds(tOV.forcedPauseTime) ;

            }
            line += "\n";

            writer.write(line);


            currentMidnight = nextMidnight;

            cursor.close();

        }

        writer.close();
    }
}
