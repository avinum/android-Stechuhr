package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class ListMonthActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        fillListView();
    }

    private void fillListView() {

        ListView listview = (ListView) findViewById(R.id.lvList);
        final ArrayList<String> list = new ArrayList<>();

        long oldSec = ComLib.getUnixOfPreviousFirstInMonthMidnight(+1);

        boolean hideNoWorkTimeOnOverView = StechuhrApplication.getPreferences().getBoolean("hideNoWorkTimeOnOverView", true);
        int monthViewCount = Integer.parseInt(StechuhrApplication.getPreferences().getString("monthViewCount", "10"));

        for (int i = 0; i < monthViewCount; i++) {

            long newSec = ComLib.getUnixOfPreviousFirstInMonthMidnight(-i);

            Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(newSec, oldSec - newSec);

            oldSec = newSec;

            if (cursor.getCount() == 0 && hideNoWorkTimeOnOverView)
                continue;

            String line;
            TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);

            line = StrHelp.getMonthNameFromSeconds(newSec) + " " + StrHelp.getYearFromSeconds(newSec) + "\n";
            line += StrHelp.getOverViewText(tOV, StechuhrApplication.context.getString(R.string.space));

            list.add(line);
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }
}
