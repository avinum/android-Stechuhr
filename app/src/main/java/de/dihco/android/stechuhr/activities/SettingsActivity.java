package de.dihco.android.stechuhr.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.common.ComLib;


public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        Preference button_action_DataEdit = (Preference) findPreference("action_DataEdit");
        button_action_DataEdit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(StechuhrApplication.context, DataEditActivity.class);
                startActivity(intent);
                return true;
            }
        });

        Preference button_action_BackupCreate = (Preference) findPreference("action_BackupCreate");
        button_action_BackupCreate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                ComLib.createBackup();
                return true;
            }
        });

        Preference button_action_DataCheck = (Preference) findPreference("action_DataCheck");
        button_action_DataCheck.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(StechuhrApplication.context, DataCheckActivity.class);
                startActivity(intent);
                return true;
            }
        });

        Preference button_action_DataDelete = (Preference) findPreference("action_DataDelete");
        button_action_DataDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                deleteStep1();
                return true;
            }
        });

        Preference button_action_BackupImport = (Preference) findPreference("action_BackupImport");
        button_action_BackupImport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(StechuhrApplication.context, BackupImportActivity.class);
                startActivity(intent);
                return true;
            }
        });


    }

    private void deleteStep1(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteStep2();
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("Alle Daten löschen?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Abbrechen", dialogClickListener).show();
    }

    private void deleteStep2(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        ComLib.deleteData();
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("Wirklich alle Daten löschen? Vorgang kann nicht rückgängig gemacht werden.").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Abbrechen", dialogClickListener).show();
    }

}
