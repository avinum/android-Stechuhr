package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.common.ComLib;

public class BackupImportActivity extends Activity {

    String backupFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_import);

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
                                ComLib.importBackup(backupFileName);
                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                backupFileName = String.valueOf(lv.getItemAtPosition(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(BackupImportActivity.this);
                builder.setMessage("Backup " + backupFileName + " importieren?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();

                //StechuhrApplication.getHelper().getRowsSinceWithSpan(dateOfListSeconds, TimeUnit.DAYS.toSeconds(1));

                //Toast.makeText(getApplicationContext(),
                //"Click ListItem Number " + position, Toast.LENGTH_LONG)
                //.show();

            }
        });

        fillList();
    }

    private void fillList() {
        ListView listview = (ListView) findViewById(R.id.lvBackupImport);
        final ArrayList<String> list = ComLib.getLocalBackupFileList();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }


}
