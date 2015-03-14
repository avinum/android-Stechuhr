package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.dihco.android.stechuhr.MyApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class ListMonthActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_month);

        fillListView();
    }

    private void fillListView() {

        ListView listview = (ListView) findViewById(R.id.lvMonthView);
        final ArrayList<String> list = new ArrayList<String>();

        long oldSec = ComLib.getUnixOfPreviousFirstInMonthMidnight(+1);

        boolean hideNoWorkTimeOnOverView = MyApplication.getPreferences().getBoolean("hideNoWorkTimeOnOverView", true);
        int monthViewCount = Integer.parseInt(MyApplication.getPreferences().getString("monthViewCount", "10"));

        for (int i = 0; i < monthViewCount; i++) {

            long newSec = ComLib.getUnixOfPreviousFirstInMonthMidnight(-i);

            Cursor cursor = MyApplication.getHelper().getRowsSinceWithSpan(newSec, oldSec - newSec);

            oldSec = newSec;

            if (cursor.getCount() == 0 && hideNoWorkTimeOnOverView)
                continue;

            String line;
            TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);

            line = StrHelp.getMonthNameFromSeconds(newSec) + " " + StrHelp.getYearFromSeconds(newSec) + "\n";
            line += StrHelp.getOverViewText(tOV, MyApplication.context.getString(R.string.space));

            list.add(line);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }
}
