package hung.timingdial;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Hung on 2016/4/5.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private String CALL=Intent.ACTION_CALL;
    private Cursor mCursor;
    private MyDBHelper myDBHelper=null;
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    private String TAG="TAG";
    @Override
    public void onReceive(Context context, Intent intent) {
        myDBHelper = new MyDBHelper(context);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        long ID=intent.getLongExtra("Long_id",-1);
        String strID=String.valueOf(ID);
        Log.e(TAG, "onReceive " + strID);
        mCursor.moveToPosition((int) ID - 1);
        Log.e(TAG, "onReceive " + strID);
        String strTime=mCursor.getString(1);
        Log.e(TAG, "onReceive " + strID + " " + strTime);
        String strName=mCursor.getString(2);
        Log.e(TAG, "onReceive " + strID + " " + strTime + " " + strName);
        String strPhone=mCursor.getString(3);
        Log.e(TAG, "onReceive " + strID + " " + strTime + " " + strName + " " + strPhone);
        update(Long.parseLong(strID), strTime, strName, strPhone, "F");
        context.stopService(new Intent(context, SetAlarmManager.class));
        try {
            Intent updateUIIntent = new Intent(context, UpdateUIService.class);
            context.startService(updateUIIntent);
        }catch (Exception e) {
            Log.e(TAG, "onReceive " + e);
        }

        //callDirectly(context, strInput);
    }
    private void callDirectly(Context context, String phoneNum) {
        Intent intent =new Intent(CALL, Uri.parse("tel:" + phoneNum));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public void update(long rowId, String time, String name, String phone, String torf) {
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(TIME_COLUMN, time);
        args.put(SWITCH_COLUMN, torf);
        db.update(TABLE_NAME, args, KEY_ID + "=" + rowId, null);
    }
}
