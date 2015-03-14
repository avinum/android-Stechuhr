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
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class ListWeekActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_week);

        fillListView();
    }

    private void fillListView() {

        ListView listview = (ListView) findViewById(R.id.lvWeekView);
        final ArrayList<String> list = new ArrayList<String>();

        boolean hideNoWorkTimeOnOverView = MyApplication.getPreferences().getBoolean("hideNoWorkTimeOnOverView", true);
        int weekViewCount = Integer.parseInt(MyApplication.getPreferences().getString("weekViewCount", "10"));

        for (int i = 0; i < weekViewCount ; i++){

            long sTime =ComLib.getUnixOfPreviousMondayMidnight(-i);
                    Cursor cursor = MyApplication.getHelper().getRowsSinceWithSpan(sTime, TimeUnit.DAYS.toSeconds(7));

            if (cursor.getCount() == 0 && hideNoWorkTimeOnOverView)
                continue;

            String line;
            TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);


            line = MyApplication.context.getString(R.string.week) + " " + StrHelp.getWeekNumberFromSeconds(sTime) + "\n";
            line += StrHelp.getOverViewText(tOV,MyApplication.context.getString(R.string.space));

            list.add(line);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }

}
