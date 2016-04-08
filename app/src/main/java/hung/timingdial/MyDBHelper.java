package hung.timingdial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Hung on 2016/4/4.
 */
public class MyDBHelper extends SQLiteOpenHelper{
    public static String[] Time={"08:00", "21:30", "02:00"};
    public static String[] Name={"Omar", "戴立婷", "Peter"};
    public static String[] PhoneNumber={"0929009230", "0930961318", "0952009230"};
    public static String[] TorF={"F", "F", "F"};
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "TimingDial.db";
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + " (" + KEY_ID + " INTEGER PRIMARY KEY,"
            + TIME_COLUMN + " TEXT,"
            + NAME_COLUMN + " TEXT,"
            + PHONE_COLUMN + " TEXT,"
            + SWITCH_COLUMN + " TEXT)";
    //public static SQLiteDatabase db;
    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
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
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
