package de.dihco.android.stechuhr.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import de.dihco.android.stechuhr.StechuhrApplication;
import de.dihco.android.stechuhr.R;
import de.dihco.android.stechuhr.TimeOverView;
import de.dihco.android.stechuhr.common.ComLib;
import de.dihco.android.stechuhr.common.StrHelp;


public class MainActivity extends Activity {


    AppState appstate = AppState.BEFOREWORK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
        fullRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_Refresh) {
            fullRefresh();
            return true;
        }

        if (id == R.id.action_ListDay) {
            Intent intent = new Intent(this, ListDayActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_ListWeek) {
            Intent intent = new Intent(this, ListWeekActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_ListMonth) {
            Intent intent = new Intent(this, ListMonthActivity.class);
            startActivity(intent);
            return true;
        }



//        if (id == R.id.action_EditView) {
//            Intent intent = new Intent(this, DataEditActivity.class);
//            startActivity(intent);
//            return true;
//        }

//        if (id == R.id.action_CreateBackup){
//            ComLib.createBackup();
//        }


        return super.onOptionsItemSelected(item);
    }

    private void fullRefresh() {
        appStateRefresh();

        boolean orientationPortrait = (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT);

        boolean hasBeenSomethingShownEven = false;
        boolean hasBeenSomethingShownUneven = false;

        hasBeenSomethingShownUneven = SetGroupState(R.id.tvGroup1_Header, R.id.tvGroup1_Text, 0, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group1", "1")), !hasBeenSomethingShownUneven) || hasBeenSomethingShownUneven;
        if (orientationPortrait) {
            hasBeenSomethingShownEven = hasBeenSomethingShownUneven;
        }
        hasBeenSomethingShownEven = SetGroupState(R.id.tvGroup2_Header, R.id.tvGroup2_Text, R.id.Space2, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group2", "7")), !hasBeenSomethingShownEven) || hasBeenSomethingShownEven;
        if (orientationPortrait) {
            hasBeenSomethingShownUneven = hasBeenSomethingShownEven;
        }
        hasBeenSomethingShownUneven = SetGroupState(R.id.tvGroup3_Header, R.id.tvGroup3_Text, R.id.Space3, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group3", "30")), !hasBeenSomethingShownUneven) || hasBeenSomethingShownUneven;
        if (orientationPortrait) {
            hasBeenSomethingShownEven = hasBeenSomethingShownUneven;
        }
        hasBeenSomethingShownEven = SetGroupState(R.id.tvGroup4_Header, R.id.tvGroup4_Text, R.id.Space4, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group4", "0")), !hasBeenSomethingShownEven) || hasBeenSomethingShownEven;
        if (orientationPortrait) {
            hasBeenSomethingShownUneven = hasBeenSomethingShownEven;
        }
        hasBeenSomethingShownUneven = SetGroupState(R.id.tvGroup5_Header, R.id.tvGroup5_Text, R.id.Space5, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group5", "0")), !hasBeenSomethingShownUneven) || hasBeenSomethingShownUneven;
        if (orientationPortrait) {
            hasBeenSomethingShownEven = hasBeenSomethingShownUneven;
        }
        hasBeenSomethingShownEven = SetGroupState(R.id.tvGroup6_Header, R.id.tvGroup6_Text, R.id.Space6, Integer.parseInt(StechuhrApplication.getPreferences().getString("Design_Group6", "0")), !hasBeenSomethingShownEven) || hasBeenSomethingShownEven;

        TextView tvWelcome = (TextView) findViewById(R.id.tvWelcome);
        if (!(hasBeenSomethingShownUneven || hasBeenSomethingShownEven) && StechuhrApplication.getHelper().isDatabaseEmpty()) {
            tvWelcome.setVisibility(View.VISIBLE);
            tvWelcome.setText(getString(R.string.Welcome));
            if (!orientationPortrait) {
                LinearLayout LinLayRight = (LinearLayout) findViewById(R.id.LinLayRight);
                LinLayRight.setVisibility(View.GONE);
            }
        } else {
            tvWelcome.setVisibility(View.GONE);
            if (!orientationPortrait) {
                LinearLayout LinLayLeft = (LinearLayout) findViewById(R.id.LinLayLeft);
                if (hasBeenSomethingShownUneven) {
                    LinLayLeft.setVisibility(View.VISIBLE);
                } else {
                    LinLayLeft.setVisibility(View.GONE);
                }
                LinearLayout LinLayRight = (LinearLayout) findViewById(R.id.LinLayRight);
                if (hasBeenSomethingShownEven) {
                    LinLayRight.setVisibility(View.VISIBLE);
                } else {
                    LinLayRight.setVisibility(View.GONE);
                }
            }
        }

        if (appstate == AppState.AFTERWORK && !false) { //TODO Außer es gibt Fortsetzung
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(lp);
        } else {
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            lp.setMargins(0, 20, 0, 0);
            scrollView.setLayoutParams(lp);
        }
    }

    private boolean SetGroupState(int headerId, int textId, int spaceId, int prefContent, boolean firstWithContent) {
        TextView tvHeader = (TextView) findViewById(headerId);
        TextView tvText = (TextView) findViewById(textId);

        Space space;
        if (spaceId != 0)
            space = (Space) findViewById(spaceId);
        else
            space = null;


        //==== Visibility of Groups ====
        if (prefContent == 0) {
            tvHeader.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            if (space != null) {
                space.setVisibility(View.GONE);
            }
            return false;
        } else {
            tvHeader.setVisibility(View.VISIBLE);
            tvText.setVisibility(View.VISIBLE);
            if (space != null) {
                if (firstWithContent)
                    space.setVisibility(View.GONE);
                else
                    space.setVisibility(View.VISIBLE);
            }
        }

        //==== get the Cursor ====
        Cursor cursor;

        if (prefContent == 1 || prefContent == 7 || prefContent == 30) {
            long timeSince;
            switch (prefContent) {
                case 1:
                    timeSince = ComLib.getUnixPrevMidnight();
                    break;
                case 7:
                    timeSince = ComLib.getUnixOfPreviousMondayMidnight(0);
                    break;
                case 30:
                    timeSince = ComLib.getUnixOfPreviousFirstInMonthMidnight(0);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown group content!");
            }
            cursor = StechuhrApplication.getHelper().getRowsSince(timeSince);
        } else {
            if (prefContent == 9999) {
                cursor = StechuhrApplication.getHelper().getAll();
            } else {
                if (prefContent == 2 || prefContent == 14 || prefContent == 60) {
                    long timeSince;
                    long timeSpan;
                    switch (prefContent) {
                        case 2:
                            timeSince = ComLib.getUnixPrevMidnight() - TimeUnit.DAYS.toSeconds(1);
                            timeSpan = TimeUnit.DAYS.toSeconds(1);
                            break;
                        case 14:
                            timeSince = ComLib.getUnixOfPreviousMondayMidnight(-1);
                            timeSpan = TimeUnit.DAYS.toSeconds(7);
                            break;
                        case 60:
                            timeSince = ComLib.getUnixOfPreviousFirstInMonthMidnight(-1);
                            timeSpan = ComLib.getUnixOfPreviousFirstInMonthMidnight(0) - timeSince;
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown group content!");
                    }
                    cursor = StechuhrApplication.getHelper().getRowsSinceWithSpan(timeSince, timeSpan);
                } else {
                    throw new IllegalArgumentException("Unknown group content!");
                }
            }
        }

        boolean hideNoWorkTimeOnStartDisplay = StechuhrApplication.getPreferences().getBoolean("hideNoWorkTimeOnStartDisplay", true);
        if (cursor.getCount() == 0 && hideNoWorkTimeOnStartDisplay) {
            tvHeader.setVisibility(View.GONE);
            tvText.setVisibility(View.GONE);
            if (space != null) {
                space.setVisibility(View.GONE);
            }
            return false;
        }

        TimeOverView tOV = ComLib.getTimeOverViewFromCursor(cursor);
        tvText.setText(StrHelp.getOverViewText(tOV, ""));

        //==== Fill the Views ====
        //Header
        String headerText = "";
        switch (prefContent) {
            case 1:
                headerText = getString(R.string.today);
                if (tOV.startZeit != 0) {
                    headerText += " " + StrHelp.getClockTimeFromSeconds(tOV.startZeit);
                    if (tOV.endZeit != 0)
                        headerText += " - " + StrHelp.getClockTimeFromSeconds(tOV.endZeit);
                }
                break;
            case 2:
                headerText = getString(R.string.yesterday);
                if (tOV.startZeit != 0) {
                    headerText += " " + StrHelp.getClockTimeFromSeconds(tOV.startZeit);
                    if (tOV.endZeit != 0)
                        headerText += " - " + StrHelp.getClockTimeFromSeconds(tOV.endZeit);
                }
                break;
            case 7:
            case 14:
                headerText = getString(R.string.week) + " " + StrHelp.getWeekNumberFromSeconds(tOV.startZeit);
                break;
            case 30:
            case 60:
                headerText = getString(R.string.month) + " " + StrHelp.getMonthNameFromSeconds(tOV.startZeit);
                break;
            case 9999:
                Cursor tCursor = StechuhrApplication.getHelper().getFirstEvent();
                tCursor.moveToFirst();
                headerText = getString(R.string.since) + " " + StrHelp.getDateFromSeconds(tCursor.getLong(0));
                break;
            default:
                throw new IllegalArgumentException("Unknown group content!");
        }
        tvHeader.setText(headerText);

        return true;
    }

    private void appStateRefresh() {
        Cursor cursor = StechuhrApplication.getHelper().getAppState();

        if (cursor.getCount() == 0) {
            appstate = AppState.BEFOREWORK;
        } else {

            cursor.moveToLast();
            switch (cursor.getInt(1)) {
                case StechuhrApplication.STARTDAY:
                    appstate = AppState.WORKING;
                    break;
                case StechuhrApplication.ENDPAUSE:
                    appstate = AppState.WORKING;
                    break;
                case StechuhrApplication.ENDDAY:
                    appstate = AppState.AFTERWORK;
                    break;
                case StechuhrApplication.STARTPAUSE:
                    appstate = AppState.PAUSING;
                    break;
            }
        }


        setButtonState();
    }

    private void setButtonState() {
        Button btnStartDay = (Button) findViewById(R.id.btnStartDay);
        Button btnDayEnd = (Button) findViewById(R.id.btnDayEnd);
        Button btnStartPause = (Button) findViewById(R.id.btnStartPause);
        Button btnEndPause = (Button) findViewById(R.id.btnEndPause);

        switch (appstate) {

            case WORKING:
                btnStartDay.setVisibility(View.INVISIBLE);
                btnDayEnd.setVisibility(View.VISIBLE);
                btnStartPause.setVisibility(View.VISIBLE);
                btnEndPause.setVisibility(View.INVISIBLE);
                break;
            case BEFOREWORK:
                btnStartDay.setVisibility(View.VISIBLE);
                btnDayEnd.setVisibility(View.INVISIBLE);
                btnStartPause.setVisibility(View.INVISIBLE);
                btnEndPause.setVisibility(View.INVISIBLE);
                break;
            case PAUSING:
                btnStartDay.setVisibility(View.INVISIBLE);
                btnDayEnd.setVisibility(View.INVISIBLE);
                btnStartPause.setVisibility(View.INVISIBLE);
                btnEndPause.setVisibility(View.VISIBLE);
                break;
            case AFTERWORK:
                btnStartDay.setVisibility(View.GONE);
                btnDayEnd.setVisibility(View.GONE);
                btnStartPause.setVisibility(View.GONE);
                btnEndPause.setVisibility(View.GONE);
                break;
        }
    }

    public void btnStartDayClick(View view) {
        StechuhrApplication.getHelper().insertAction(StechuhrApplication.STARTDAY);
        fullRefresh();
    }

    public void btnEndDayClick(View view) {
        StechuhrApplication.getHelper().insertAction(StechuhrApplication.ENDDAY);
        fullRefresh();
    }

    public void btnStartPauseClick(View view) {
        StechuhrApplication.getHelper().insertAction(StechuhrApplication.STARTPAUSE);
        fullRefresh();
    }

    public void btnEndPauseClick(View view) {
        StechuhrApplication.getHelper().insertAction(StechuhrApplication.ENDPAUSE);
        fullRefresh();
    }

    public enum AppState {
        BEFOREWORK, WORKING, AFTERWORK, PAUSING
    }
}
