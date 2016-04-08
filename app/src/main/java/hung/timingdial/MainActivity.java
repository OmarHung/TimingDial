package hung.timingdial;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ListView TimeList;
    public TimeCursorAdapter timeCursorAdapter;
    private Cursor mCursor;
    private MyDBHelper myDBHelper=null;
    private Toolbar toolbar;
    private UpdateUIBroadcast updateUIBroadcast;
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    private String TAG="TAG";
    String strInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.CALL_PHONE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_CALL_PHONE);
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        TimeList = (ListView)findViewById(R.id.listView);

        myDBHelper = new MyDBHelper(this);
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        timeCursorAdapter = new TimeCursorAdapter(this,mCursor,0);
        //timeListAdapter = new TimeListAdapter(this,items);
        TimeList.setAdapter(timeCursorAdapter);

        TimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strTime, strName, strPhoneNum, strSwitch;
                long code_id;
                SQLiteDatabase db = myDBHelper.getReadableDatabase();
                mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                mCursor.moveToPosition(position);
                code_id = mCursor.getLong(0);
                strTime = mCursor.getString(1);
                strName = mCursor.getString(2);
                strPhoneNum = mCursor.getString(3);
                strSwitch = mCursor.getString(4);
                Intent intent = new Intent(MainActivity.this, SetDialActivity.class);
                intent.putExtra("id",code_id);
                intent.putExtra("time",strTime);
                intent.putExtra("name",strName);
                intent.putExtra("phone",strPhoneNum);
                intent.putExtra("switch", strSwitch);
                startActivity(intent);
                Log.e(TAG, "OnItemCkick " + code_id + " " + strTime + " " + strName + " " + strPhoneNum + " " + strSwitch);
                //Toast.makeText(getApplicationContext(), strTime+" "+strName+" "+strPhoneNum+" "+strSwitch, Toast.LENGTH_SHORT).show();
            }
        });
        TimeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("確定刪除？")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete(id);
                                SQLiteDatabase db = myDBHelper.getReadableDatabase();
                                mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                                timeCursorAdapter.changeCursor(mCursor);
                                timeCursorAdapter.notifyDataSetChanged();
                                TimeList.setAdapter(timeCursorAdapter);
                                Intent intent = new Intent(MainActivity.this, SetAlarmManager.class);
                                intent.putExtra("id", id);
                                intent.putExtra("mode", "cancel");
                                startService(intent);
                            }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                //Toast.makeText(getApplicationContext(), "Long Click "+items.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        //getID();
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        timeCursorAdapter.changeCursor(mCursor);
        timeCursorAdapter.notifyDataSetChanged();
        TimeList.setAdapter(timeCursorAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(MainActivity.this, SetDialActivity.class);
            intent.putExtra("id",-1);
            intent.putExtra("time","");
            intent.putExtra("name","");
            intent.putExtra("phone","");
            intent.putExtra("switch","");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        myDBHelper.close();
    }

    public long append(String time, String name, String phone, String torf) { // 新增資料
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(NAME_COLUMN, name);
        args.put(PHONE_COLUMN, phone);
        args.put(TIME_COLUMN, time);
        args.put(SWITCH_COLUMN, torf);
        return db.insert(TABLE_NAME, null, args);
    }
    public boolean delete(long rowId) {
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID + "=" + rowId, null) > 0;
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
    public void getID() {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++) {
            Log.e("_ID", c.getString(0));
            c.moveToNext();
        }
    }
    public int getCount() {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return c.getCount();
    }
    public String getItem(int position, int key) {
        SQLiteDatabase db = myDBHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        c.moveToPosition(position);
        return c.getString(key);
    }
    protected void onStart() {
        updateUIBroadcast = new UpdateUIBroadcast();
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("UPDATE_UI_ACTION");
        registerReceiver(updateUIBroadcast, filter1);
        super.onStart();
        Log.e(TAG, "onStart");
    }
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, UpdateUIService.class));
        unregisterReceiver(updateUIBroadcast);
        super.onDestroy();
    }

    public class UpdateUIBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SQLiteDatabase db = myDBHelper.getReadableDatabase();
            mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            timeCursorAdapter.changeCursor(mCursor);
            timeCursorAdapter.notifyDataSetChanged();
            TimeList.setAdapter(timeCursorAdapter);
            stopService(new Intent(MainActivity.this, UpdateUIService.class));
            //unregisterReceiver(updateUIBroadcast);
            Log.e(TAG, "UpdateUIBroadcast");
        }
    }
}
