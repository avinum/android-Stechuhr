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


public class ListDayActivity extends Activity {

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
        int dayViewCount = Integer.parseInt(StechuhrApplication.getPreferences().getString("dayViewCount", "10"));

        for (int i = 0; i < dayViewCount ; i++){

            long sTime = ComLib.getUnixPrevMidnight(-i);
            Cursor cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan( sTime, TimeUnit.DAYS.toSeconds(1));


            if (cursor.getCount() == 0 && hideNoWorkTimeOnOverView)
                continue;

            String line;

            TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);

            line = StrHelp.getWeekDayFromSeconds(sTime + TimeUnit.HOURS.toSeconds(12)) + " " + StrHelp.getDateFromSeconds(sTime + TimeUnit.HOURS.toSeconds(12));

            if(tOV.startZeit != 0)
                line += " " + StrHelp.getClockTimeFromSeconds(tOV.startZeit);

            if(tOV.endZeit != 0)
                line +=  " - " + StrHelp.getClockTimeFromSeconds(tOV.endZeit) ;

            line +=  "\n" + StrHelp.getOverViewText(tOV, StechuhrApplication.context.getString(R.string.space));

            list.add(line);
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listview.setAdapter(adapter);
    }

}
