package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.MyApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class DataEditActivity extends Activity {

    private long dateOfListSeconds;
    int mYear;
    int mMonth;
    int mDay;

    private long deleteSeconds;

    private List<Long> secondList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_edit);

        ListView lv = (ListView) findViewById(R.id.lvEdit);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                MyApplication.getHelper().deleteRow(deleteSeconds);
                                FillList();
                                //Yes button clicked
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                deleteSeconds = secondList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(DataEditActivity.this);
                builder.setMessage("Zeile wirklich löschen?").setPositiveButton("Ja", dialogClickListener).setNegativeButton("Nein", dialogClickListener).show();

                //MyApplication.getHelper().getRowsSinceWithSpan(dateOfListSeconds, TimeUnit.DAYS.toSeconds(1));

                //Toast.makeText(getApplicationContext(),
                        //"Click ListItem Number " + position, Toast.LENGTH_LONG)
                        //.show();

            }
        });



        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        dateOfListSeconds = ComLib.getSecondsFromDate(year, monthOfYear, dayOfMonth);
                        setTitle(MyApplication.context.getString(R.string.day) + " " + StrHelp.getDateFromSeconds(dateOfListSeconds));
                        FillList();
                    }
                }, mYear, mMonth, mDay){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    finish();
                }else {
                    super.onClick(dialog, which);
                }
            }
        };

        dpd.show();
    }

    private void FillList() {
        ListView listview = (ListView) findViewById(R.id.lvEdit);
        final List<String> list = new ArrayList<String>();
        secondList = new ArrayList<>();
        Cursor cursor = MyApplication.getHelper().getRowsSinceWithSpan(dateOfListSeconds, TimeUnit.DAYS.toSeconds(1));

//        cursor.moveToPosition(cursor.getCount());
//        while (cursor.moveToPrevious()) {
//            if (cursor.getInt(1) == MyApplication.ENDDAY) {
//                cursor.moveToNext();
//                break;
//            }
//        }


        while (cursor.moveToNext()) {
            secondList.add(cursor.getLong(0));
            list.add(StrHelp.getActivityString(cursor.getLong(0), cursor.getInt(1)));
        }


        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, list);


        // Assign adapter to ListView
        listview.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_edit, menu);
        return true;
    }

    int newAction;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_NewDayStart) {
            newAction = MyApplication.STARTDAY;
            showTimePicker();
            return true;
        }

        if (id == R.id.action_NewDayEnd) {
            newAction = MyApplication.ENDDAY;
            showTimePicker();
            return true;
        }

        if (id == R.id.action_NewPauseStart) {
            newAction = MyApplication.STARTPAUSE;
            showTimePicker();
            return true;
        }

        if (id == R.id.action_NewPauseEnd) {
            newAction = MyApplication.ENDPAUSE;
            showTimePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showTimePicker(){
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        long t = ComLib.getSecondsFromDateAndTime(mYear,mMonth,mDay,hourOfDay,minute);
                        MyApplication.getHelper().insertActionWithTime(t, newAction, false);
                        FillList();
                    }
                }, 12, 0, true);
        tpd.show();
    }
}
