package gcm.play.android.samples.com.gcmquickstart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ashish Agarwal on 03-08-2015.
 */

public class MyDB {
    private static final String TAG = "MyDb";
    private DataStorage dbHelper;
    private SQLiteDatabase database;
    public final static String EMP_ID = "_id"; // id value for employee
    public final static String EMP_TABLE = "Messages"; // name of table
    public final static String EMP_NAME = "value";  // name of employee
    public final static String EMP_TIME = "created";//time and date
    public final static String SENDER = "sender";  // sender

    public MyDB(Context context) {
        dbHelper = new DataStorage(context);
        database = dbHelper.getWritableDatabase();

    }


    public long createRecords(String name, String sender) {
        ContentValues values = new ContentValues();

        String time =  getDateTime();
        Log.v("MyDB","entered rec is +:"+time);
        values.put(EMP_NAME, name);
        values.put(EMP_TIME, time);
        values.put(SENDER, sender);
        return database.insert(EMP_TABLE, null, values);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MMMM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String[][] getRecords(String sender, int viewTab) {

        try {
            String selectQuery = null;
            if (viewTab == 1) {
                selectQuery = "SELECT  * FROM " + EMP_TABLE + " WHERE " + SENDER + "= '" + sender + "'";
            }if(viewTab == 2){
                Log.v("MyDB","inside 2");
                selectQuery = "SELECT  * FROM " + EMP_TABLE + " WHERE " + SENDER + "!= '" + sender + "'";
            }
            int i = 0;
            Cursor cursor = database.rawQuery(selectQuery, null);
            String[][] dateData = new String[2][cursor.getCount()];

            // String[] data = new String[cursor.getCount()];
            int colIndex = cursor.getColumnIndex(EMP_NAME);
            int dateIndex = cursor.getColumnIndex(EMP_TIME);

            if (cursor.moveToFirst()) {
                do {
                    dateData[1][i] = cursor.getString(dateIndex).substring(0, 16);
                    Log.v(TAG,dateData[1][i]);
                    // get  the  data into array,or class variable
                    dateData[0][i] = cursor.getString(colIndex);
                    Log.v("MyGcm : ", dateData[0][i]);
                    i++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            return dateData;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }


    }


    public void removeRecord(String msg) {

        String sql = "DELETE FROM " + EMP_TABLE + " WHERE " + EMP_NAME + "= '" + msg + "'";
        database.execSQL(sql);
    }

    public void deleteAllRecords() {
        String sql = "DELETE FROM " + EMP_TABLE;
        database.execSQL(sql);

    }


    public int getCount() {
        String selectQuery = "SELECT  * FROM " + EMP_TABLE;

        Cursor cursor = database.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public String getTime() {
        String sql = "SELECT * FROM " + EMP_TABLE;
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToLast();
        int dateIndex = cursor.getColumnIndex(EMP_TIME);
        return cursor.getString(dateIndex);

    }

    public void editName(int id, String extension) {

        String val = "abcdef" + id + extension;
        String sql = "UPDATE " + EMP_TABLE + " SET " + EMP_NAME + " ='" + val + "' WHERE " + EMP_ID + "= " + id;
        database.execSQL(sql);
    }


    public String getToastTime(int l){
        String date = null;
        String sql = "SELECT * FROM " + EMP_TABLE+" WHERE "+EMP_ID+" = '"+l+"'";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.moveToFirst()){
            Log.v("SQL","at first");
            String rawDate =  cursor.getString(2);

            date = rawDate.substring(5,10);
        }
       // int dateIndex = cursor.getColumnIndex(EMP_TIME);

        return date;
    }
}
