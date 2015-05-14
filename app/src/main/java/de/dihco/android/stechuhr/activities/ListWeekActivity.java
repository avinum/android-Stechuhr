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
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class ListWeekActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fillListView();
    }

    private void fillListView() {

        ListView listview = (ListView) findViewById(R.id.lvList);
        final ArrayList<String> list = new ArrayList<>();

        boolean hideNoWorkTimeOnOverView = StechuhrApplication.getPreferences().getBoolean("hideNoWorkTimeOnOverView", true);
        int weekViewCount = Integer.parseInt(StechuhrApplication.getPreferences().getString("weekViewCount", "10"));

        for (int i = 0; i < weekViewCount ; i++){

            long sTime =ComLib.getUnixOfPreviousMondayMidnight(-i);
                    Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(sTime, TimeUnit.DAYS.toSeconds(7));

            if (cursor.getCount() == 0 && hideNoWorkTimeOnOverView)
                continue;

            String line;
            TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);


            line = StechuhrApplication.context.getString(R.string.week) + " " + StrHelp.getWeekNumberFromSeconds(sTime + TimeUnit.HOURS.toSeconds(12)) + "\n";
            line += StrHelp.getOverViewText(tOV, StechuhrApplication.context.getString(R.string.space));

            list.add(line);
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }

}
