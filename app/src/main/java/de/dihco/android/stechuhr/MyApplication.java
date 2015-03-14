package de.dihco.android.stechuhr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

/**
 * Created by Martin on 29.01.2015.
 */
public class MyApplication extends Application {

    public static final int STARTDAY = 1;
    public static final int STARTPAUSE = 2;
    public static final int ENDPAUSE = 3;
    public static final int ENDDAY = 4;
    public static Context context;
    private static SQLiteDatabase sqliteDatabase;
    private static DatabaseHelper sqliteOpenHelper;
    private static SharedPreferences preferences;


    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static SQLiteDatabase getDatabase() {
        return sqliteDatabase;
    }

    public static DatabaseHelper getHelper() {
        return sqliteOpenHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        sqliteOpenHelper = new DatabaseHelper(context);
        if (sqliteOpenHelper != null) {
            sqliteDatabase = sqliteOpenHelper.getWritableDatabase();
            if (sqliteDatabase != null) {
            }
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public void onTerminate() {
        if (sqliteDatabase != null) {
            sqliteDatabase.close();
        }

        if (sqliteOpenHelper != null) {
            sqliteOpenHelper.close();
        }

        super.onTerminate();
    }
}
