package de.dihco.android.stechuhr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.dihco.android.stechuhr.common.ComLib;


/**
 * Created by Martin on 29.01.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqliteDatabase;

    public DatabaseHelper(Context context) {
        super(context, "storage.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists actionlog (DateTime INTEGER PRIMARY KEY, ActionCode INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        sqliteDatabase = super.getWritableDatabase();
        return sqliteDatabase;
    }

    public long insertAction(int actionCode) {
        return insertActionWithTime(ComLib.getUnixTimeNow() , actionCode, false);
    }

    public long insertActionWithTime(long seconds, int actionCode, boolean silent) {
        ContentValues values = new ContentValues();
        values.put("DateTime", seconds);
        values.put("ActionCode", actionCode);
        long row = sqliteDatabase.insert("actionlog", null, values);
        if (row == -1 && ! silent)
            ComLib.ShowMessage("Fehler!\n Zeitstempel schon vorhanden.");
        return row;
    }

    public Cursor getAppState() {
        Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, "DateTime > ?", new String[]{Long.toString(ComLib.getUnixPrevMidnight())}, null, null, "DateTime DESC" , "1");
        return cursor;
    }

    public Cursor getFirstEvent() {
        Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, null, null , null, null, "DateTime ASC" , "1");
        return cursor;
    }

    public Cursor getRowsSince(long startPoint) {
        Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, "DateTime > ?", new String[]{Long.toString(startPoint)}, null, null, "DateTime");
        return cursor;
    }

    public Cursor getRowsSinceWithSpan(long startPoint, long TimeSpan) {
        Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, "DateTime > ? AND DateTime < ?", new String[]{Long.toString(startPoint),Long.toString(startPoint + TimeSpan)}, null, null, "DateTime");
        return cursor;
    }

    public void deleteRow(long seconds) {
        //Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, "DateTime > ? AND DateTime < ?", new String[]{Long.toString(startPoint),Long.toString(startPoint + TimeSpan)}, null, null, "DateTime");
        sqliteDatabase.delete("actionlog","DateTime = ?",new String[]{Long.toString(seconds)} );
        //return cursor;
    }

    public Cursor getAll() {
        Cursor cursor = sqliteDatabase.query("actionlog", new String[]{"DateTime", "ActionCode"}, null, null , null, null, "DateTime");
        return cursor;
    }

    public void deleteAll() {
        sqliteDatabase.delete("actionlog",null,null );
    }
}
