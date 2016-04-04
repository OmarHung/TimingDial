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
    String[] Time={"08:00", "21:30", "02:00", "03:00", "04:00"};
    String[] Name={"Omar", "Tobby", "Peter", "Linda", "Yasu"};
    String[] PhoneNumber={"0929009230", "0960318960", "0952009230", "0939009230", "09827308"};
    String[] TorF={"T", "T", "F", "F", "T"};
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

    public MyDB(Context ctx) {
            this.mCtx = ctx;
        }

    public int getCount() {
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null).getCount();
    }
    public String getItem(int position, int key) {
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToPosition(position);
        return c.getString(key);
    }
    public void open() throws SQLException {
        db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
        try {
            Log.e("TAG", "No DB");
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
            Log.e("TAG", "Has DB"+e);
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
    public boolean update(long rowId, String time, String name, String phone, String torf) {
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(TIME_COLUMN, time);
        args.put(SWITCH_COLUMN, torf);
        return db.update(TABLE_NAME, args, KEY_ID + "=" + rowId, null) > 0;
    }
}
