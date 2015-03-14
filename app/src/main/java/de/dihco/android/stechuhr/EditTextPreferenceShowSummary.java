package de.dihco.android.stechuhr;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by Martin on 28.02.2015.
 */
public class EditTextPreferenceShowSummary extends EditTextPreference {

    String preText;
    String postText;

    public EditTextPreferenceShowSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        preText = attrs.getAttributeValue(null, "preText");
        if (preText == null)
            preText = "";

        postText = attrs.getAttributeValue(null, "postText");
        if (postText == null)
            postText = "";

        init();
    }

    public EditTextPreferenceShowSummary(Context context){
        super(context);
        init();
    }

    private void init() {

        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference arg0, Object arg1) {
                arg0.setSummary(preText + getText() + postText);
                return true;
            }
        });
    }

    @Override
    public CharSequence getSummary() {
        return preText + super.getText() + postText;
    }

}
