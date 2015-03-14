package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;

public class DataCheckActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_check);

        fillListView();
    }

    private void fillListView() {
        ListView listview = (ListView) findViewById(R.id.lvCheck);
        final ArrayList<String> list = new ArrayList<String>();


        // ==== Ersten Tag finden ====
        Cursor cursor = StechuhrApplication.getHelper().getFirstEvent();

        if (cursor.getCount() == 0)
            return;

        cursor.moveToFirst();

        long startDay = ComLib.getMidnightFormSeconds(cursor.getLong(0));

        long now = ComLib.getUnixTimeNow();


        for (long i = startDay; i < now; i = i + TimeUnit.DAYS.toSeconds(1)) {

            cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(i, TimeUnit.DAYS.toSeconds(1));
            if (cursor.getCount() == 0)
                continue;

            String res = checkOneDay(cursor);
            if (res != "")
                list.add(res);

        }

        if (list.size() == 0)
            list.add(getString(R.string.noError));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }


    private String checkOneDay(Cursor cursor) {
        cursor.moveToFirst();

        if (cursor.getInt(1) != StechuhrApplication.STARTDAY) {
            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_beginDay));
        }

        int LastEvent = StechuhrApplication.STARTDAY;


        while (cursor.moveToNext()) {

            switch (cursor.getInt(1)) {
                case StechuhrApplication.STARTDAY:
                    return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MultipleBeginDay));
                case StechuhrApplication.ENDPAUSE:
                    switch (LastEvent) {
                        case StechuhrApplication.STARTDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingPauseBegin));
                        case StechuhrApplication.ENDPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingPauseBegin));
                        case StechuhrApplication.STARTPAUSE:
                            LastEvent = StechuhrApplication.ENDPAUSE;
                            continue ;
                        case StechuhrApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;
                case StechuhrApplication.ENDDAY:
                    switch (LastEvent) {
                        case StechuhrApplication.STARTDAY:
                            LastEvent = StechuhrApplication.ENDDAY;
                            continue;
                        case StechuhrApplication.ENDPAUSE:
                            LastEvent = StechuhrApplication.ENDDAY;
                            continue;
                        case StechuhrApplication.STARTPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MissingPauseEnd));
                        case StechuhrApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;
                case StechuhrApplication.STARTPAUSE:
                    switch (LastEvent) {
                        case StechuhrApplication.STARTDAY:
                            LastEvent = StechuhrApplication.STARTPAUSE;
                            continue;
                        case StechuhrApplication.ENDPAUSE:
                            LastEvent = StechuhrApplication.STARTPAUSE;
                            continue;
                        case StechuhrApplication.STARTPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MissingPauseEnd));
                        case StechuhrApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;

            }
        }

        cursor.moveToLast();

        if (cursor.getInt(1) != StechuhrApplication.ENDDAY) {
            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingEnd));
        }

        return ("");
    }
}
