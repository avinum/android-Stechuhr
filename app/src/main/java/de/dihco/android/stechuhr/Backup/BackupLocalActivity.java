package de.dihco.android.stechuhr.Backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;

public class BackupLocalActivity extends Activity {

    String backupFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_local);

        final ListView lv = (ListView) findViewById(R.id.lvBackupImport);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                importBackup( backupFileName );
                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                backupFileName = String.valueOf(lv.getItemAtPosition(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(BackupLocalActivity.this);
                builder.setMessage("Backup " + backupFileName + " importieren?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();

                //StechuhrApplication.getHelper().getRowsSinceWithSpan(dateOfListSeconds, TimeUnit.DAYS.toSeconds(1));

                //Toast.makeText(getApplicationContext(),
                //"Click ListItem Number " + position, Toast.LENGTH_LONG)
                //.show();

            }
        });

        fillList();
    }

    private void importBackup(final String fileName) {
        try {

            File traceFile = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Backup", fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(traceFile));
            ComLib.importBackup(bufferedReader, BackupLocalActivity.this);

        } catch (FileNotFoundException e) {
            ComLib.ShowMessage("Import fehlgeschlagen.\n\n" + e.getMessage());
        }
    }

    private void fillList() {
        ListView listview = (ListView) findViewById(R.id.lvBackupImport);
        final ArrayList<String> list = ComLib.getLocalBackupFileList();

        if (list.size() == 0){
            listview.setAdapter(null);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }

    public void btnCreateBackupClick(View view) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Backup");
            if (!folder.exists()) {
                folder.mkdir();
            }

            File traceFile = new File(Environment.getExternalStorageDirectory() + "/Stechuhr_Backup", StrHelp.getBackupFileName());
            if (!traceFile.exists()) {
                traceFile.createNewFile();
                // Adds a line to the trace file
                BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, true /*append*/));

                ComLib.createBackup(writer);
                // Refresh the data so it can seen when the device is plugged in a
                // computer. You may have to unplug and replug the device to see the
                // latest changes. This is not necessary if the user should not modify
                // the files.
                MediaScannerConnection.scanFile(StechuhrApplication.context,
                        new String[]{traceFile.toString()},
                        null,
                        null);

                ComLib.ShowMessage(getString(R.string.backupSuccess) + "\n\n" + traceFile.getPath());
            } else {
                ComLib.ShowMessage(getString(R.string.backupError) + "\n\nDatei schon vorhanden.");
            }
        } catch (IOException e) {

        }
        fillList();
    }


}
