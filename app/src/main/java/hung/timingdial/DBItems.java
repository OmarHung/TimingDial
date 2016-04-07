package hung.timingdial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Hung on 2016/4/7.
 */
public class DBItems {
    // 資料庫物件
    private SQLiteDatabase db;
    public DBItems() {

    }
    // 建構子，一般的應用都不需要修改
    public DBItems(Context context) {
    }
}
