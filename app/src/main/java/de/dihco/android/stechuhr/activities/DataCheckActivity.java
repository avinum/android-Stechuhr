package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.MyApplication;
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
        Cursor cursor = MyApplication.getHelper().getFirstEvent();

        if (cursor.getCount() == 0)
            return;

        cursor.moveToFirst();

        long startDay = ComLib.getMidnightFormSeconds(cursor.getLong(0));

        long now = ComLib.getUnixTimeNow();


        for (long i = startDay; i < now; i = i + TimeUnit.DAYS.toSeconds(1)) {

            cursor = MyApplication.getHelper().getRowsSinceWithSpan(i, TimeUnit.DAYS.toSeconds(1));
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

        if (cursor.getInt(1) != MyApplication.STARTDAY) {
            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_beginDay));
        }

        int LastEvent = MyApplication.STARTDAY;


        while (cursor.moveToNext()) {

            switch (cursor.getInt(1)) {
                case MyApplication.STARTDAY:
                    return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MultipleBeginDay));
                case MyApplication.ENDPAUSE:
                    switch (LastEvent) {
                        case MyApplication.STARTDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingPauseBegin));
                        case MyApplication.ENDPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingPauseBegin));
                        case MyApplication.STARTPAUSE:
                            LastEvent = MyApplication.ENDPAUSE;
                            continue ;
                        case MyApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;
                case MyApplication.ENDDAY:
                    switch (LastEvent) {
                        case MyApplication.STARTDAY:
                            LastEvent = MyApplication.ENDDAY;
                            continue;
                        case MyApplication.ENDPAUSE:
                            LastEvent = MyApplication.ENDDAY;
                            continue;
                        case MyApplication.STARTPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MissingPauseEnd));
                        case MyApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;
                case MyApplication.STARTPAUSE:
                    switch (LastEvent) {
                        case MyApplication.STARTDAY:
                            LastEvent = MyApplication.STARTPAUSE;
                            continue;
                        case MyApplication.ENDPAUSE:
                            LastEvent = MyApplication.STARTPAUSE;
                            continue;
                        case MyApplication.STARTPAUSE:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MissingPauseEnd));
                        case MyApplication.ENDDAY:
                            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_EndDay));
                    }
                    break;

            }
        }

        cursor.moveToLast();

        if (cursor.getInt(1) != MyApplication.ENDDAY) {
            return (StrHelp.getDateFromSeconds(cursor.getLong(0)) + " " + getString(R.string.err_MessingEnd));
        }

        return ("");
    }
}
