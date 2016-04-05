package hung.timingdial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Hung on 2016/4/4.
 */
public class MyDB {
    //String[] Time={"08:00"};
    //String[] Name={"Omar"};
    //String[] PhoneNumber={"0929009230"};
    //String[] TorF={"T"};

    String[] Time={"08:00", "21:30", "02:00"};
    String[] Name={"Omar", "戴立婷", "Peter"};
    String[] PhoneNumber={"0929009230", "0930961318", "0952009230"};
    String[] TorF={"F", "F", "F"};
    public SQLiteDatabase db = null;
    public static final String DATABASE_NAME = "TimingDial.db";
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";

    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ID + " INTEGER PRIMARY KEY,"
            + TIME_COLUMN + " TEXT,"
            + NAME_COLUMN + " TEXT,"
            + PHONE_COLUMN + " TEXT,"
            + SWITCH_COLUMN + " TEXT)";

    private Context mCtx = null;
    public MyDB() {

    }
    public MyDB(Context ctx) {
        this.mCtx = ctx;
    }

    public void getID() {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++) {
            Log.e("_ID",c.getString(0));
            c.moveToNext();
        }
    }
    public int getCount() {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c.getCount();
    }
    public String getItem(int position, int key) {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToPosition(position);
        return c.getString(key);
    }
    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try {
            //Log.e("TAG", "No DB"+Name.length);
            db.execSQL(CREATE_TABLE);
            for (int i=0;i<Name.length;i++) {
                ContentValues args = new ContentValues();
                args.put(NAME_COLUMN, Name[i]);
                args.put(PHONE_COLUMN, PhoneNumber[i]);
                args.put(TIME_COLUMN, Time[i]);
                args.put(SWITCH_COLUMN, TorF[i]);
                db.insert(TABLE_NAME, null, args);
            }
        }catch (Exception e) {
            //Log.e("TAG", "Has DB"+e);
        }
    }
    public void close() {
        db.close();
    }
    public long append(String time, String name, String phone, String torf) { // 新增資料
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(TIME_COLUMN, time);
        args.put(SWITCH_COLUMN, torf);
        return db.insert(TABLE_NAME, null, args);
    }
    public boolean delete(long rowId) {
        return db.delete(TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
    }

    public void update(long rowId, String time, String name, String phone, String torf) {
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(TIME_COLUMN, time);
        args.put(SWITCH_COLUMN, torf);
        db.update(TABLE_NAME, args, KEY_ID + "=" + rowId, null);
    }
}
