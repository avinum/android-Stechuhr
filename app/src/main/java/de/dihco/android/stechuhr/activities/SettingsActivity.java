package de.dihco.android.stechuhr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.StechuhrApplication;


public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        Preference button_action_LegalNotice = findPreference("action_LegalNotice");
        button_action_LegalNotice.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(StechuhrApplication.context, LegalNoticeActivity.class);
                startActivity(intent);
                return true;
            }
        });

        Preference button_action_privacyPolicy = findPreference("action_privacyPolicy");
        button_action_privacyPolicy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                Intent intent = new Intent(StechuhrApplication.context, LegalNoticeActivity.class);
                startActivity(intent);
                return true;
            }
        });


    }



}
