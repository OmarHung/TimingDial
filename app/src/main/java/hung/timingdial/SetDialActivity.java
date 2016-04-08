package hung.timingdial;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class SetDialActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private Toolbar toolbar;
    public String strTime, strName, strPhoneNum, strSwitch;
    public long code_id;
    public String Hour="",Minute="",Time="",Name="",PhoneNum="";
    Preference TimePreference,PhonePreference;
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    private MyDBHelper myDBHelper=null;
    private String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dial);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getFragmentManager().beginTransaction().replace(R.id.content_wrapper, new MyNewPreferenceFragment()).commit();
        myDBHelper = new MyDBHelper(this);
        Intent intent = getIntent();
        code_id=intent.getLongExtra("id",-1);
        strTime=intent.getStringExtra("time");
        strName=intent.getStringExtra("name");
        strPhoneNum=intent.getStringExtra("phone");
        strSwitch=intent.getStringExtra("switch");
        Time=strTime;
        PhoneNum=strPhoneNum;
        Name=strName;
        Log.e(TAG, "OnCreate_SetDialActivity " + code_id + " " + strTime + " " + strName + " " + strPhoneNum + " " + strSwitch);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_dial, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if(PhoneNum.equals("")) {
                Toast.makeText(this, "請設定電話號碼", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "新增預約撥號", Toast.LENGTH_SHORT).show();
                if (Time.equals(""))
                    Time = TimePreference.getTitle().toString();
                if (Name.equals(""))
                    Name = "未設定聯絡人";
                long nextday=0;
                GregorianCalendar g = new GregorianCalendar();
                int SystemHour=g.get(GregorianCalendar.HOUR_OF_DAY), SystemMinute=g.get(GregorianCalendar.MINUTE);
                Log.e(TAG,"System_Time "+SystemHour+" "+SystemMinute);
                if(Integer.parseInt(Hour)<SystemHour) {
                    nextday=1000*60*60*24;
                }
                else if(Integer.parseInt(Hour)==SystemHour && Integer.parseInt(Minute)<=SystemMinute) {
                    nextday=1000*60*60*24;
                }
                else nextday=0;
                if(strSwitch.equals("") || strSwitch.equals("T")) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
                    cal.set(Calendar.MINUTE, Integer.parseInt(Minute));
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    if(code_id==-1) {
                        Log.e("ID=-1","ID=-1");
                        SQLiteDatabase db = myDBHelper.getReadableDatabase();
                        Cursor mCursor=db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
                        mCursor.moveToLast();
                        if(mCursor.getCount()>0)
                            code_id=mCursor.getLong(0)+1;
                        else code_id=1;
                    }
                    long saveTime=cal.getTimeInMillis()+nextday;
                    Intent intent = new Intent(SetDialActivity.this, SetAlarmManager.class);
                    intent.putExtra("time",saveTime);
                    intent.putExtra("id", (int)code_id);
                    intent.putExtra("mode", "set");
                    startService(intent);
                    Log.e(TAG, "Intent_SetDialActivity " + code_id + " " + Time + " " + Name + " " + PhoneNum + " " + strSwitch+" "+saveTime);
                    if (strSwitch.equals("")) {
                        append(Time, Name, PhoneNum, "T");
                    }
                    else if (strSwitch.equals("T")) {
                        update(code_id, Time, Name, PhoneNum, "T");
                    }
                }else if(strSwitch.equals("F")) {
                    update(code_id, Time, Name, PhoneNum, "F");
                }
                Log.e(TAG, "Done_SetDialActivity " + code_id + " " + Time + " " + Name + " " + PhoneNum + " " + strSwitch);
                finish();
                return true;
            }
        }else finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String Myhour = String.valueOf(hourOfDay), Myminute = String.valueOf(minute);
        if (hourOfDay < 10) Myhour = "0" + hourOfDay;
        if (minute < 10) Myminute = "0" + minute;
        TimePreference.setTitle(Myhour + ":" + Myminute);
        Time = Myhour + ":" + Myminute;
        Hour = Myhour;
        Minute = Myminute;

        Log.e("SetTime", Time);
    }
    class MyNewPreferenceFragment extends PreferenceFragment {
        public String mName="",mPhoneNum="";
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog;
        GregorianCalendar calendar = new GregorianCalendar();
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_dial);
            TimePreference=(Preference)findPreference("TIME");
            PhonePreference=(Preference)findPreference("PHONE");
            String Myhour=String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            String Myminute=String.valueOf(c.get(Calendar.MINUTE));
            Hour=Myhour;
            Minute=Myminute;
            //初始化
            if(strPhoneNum.equals("")) {
                if (c.get(Calendar.HOUR_OF_DAY) < 10) Myhour = "0" + c.get(Calendar.HOUR_OF_DAY);
                if (c.get(Calendar.MINUTE) < 10) Myminute = "0" + c.get(Calendar.MINUTE);
                TimePreference.setTitle(Myhour + ":" + Myminute);
            }else {
                TimePreference.setTitle(strTime);
                PhonePreference.setTitle(strName);
                PhonePreference.setSummary(strPhoneNum);
            }

            TimePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (strPhoneNum.equals(""))
                        timePickerDialog = new TimePickerDialog(getActivity(), SetDialActivity.this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    else {
                        int hour, minute;
                        hour = Integer.valueOf(strTime.substring(0, 2));
                        minute = Integer.valueOf(strTime.substring(3));
                        timePickerDialog = new TimePickerDialog(getActivity(), SetDialActivity.this, hour, minute, false);
                    }
                    timePickerDialog.show();
                    return false;
                }
            });
            PhonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final String[] Items = {"從通訊錄", "輸入電話"};
                    new AlertDialog.Builder(getActivity())
                            .setTitle("電話輸入方式")
                            .setItems(Items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:

                                            break;
                                        case 1:
                                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                                            final View v = inflater.inflate(R.layout.dialog_layout, null);
                                            EditText editName = (EditText) (v.findViewById(R.id.edt_name));
                                            EditText editPhone = (EditText) (v.findViewById(R.id.edt_phone));
                                            if (!strName.equals("")) {
                                                editName.setText(strName, TextView.BufferType.EDITABLE);
                                                Name = strName;
                                            }
                                            if (!strName.equals("")) {
                                                editPhone.setText(strPhoneNum, TextView.BufferType.EDITABLE);
                                                PhoneNum = strPhoneNum;
                                            }
                                            //語法一：new AlertDialog.Builder(主程式類別).XXX.XXX.XXX;
                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("請輸入電話")
                                                    .setView(v)
                                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            EditText editName = (EditText) (v.findViewById(R.id.edt_name));
                                                            EditText editPhone = (EditText) (v.findViewById(R.id.edt_phone));
                                                            if (editPhone.getText().toString().equals("")) {
                                                                try {
                                                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                                                    field.setAccessible(true);
                                                                    field.set(dialog, false);
                                                                    Toast.makeText(SetDialActivity.this, "請輸入電話號碼", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else {
                                                                try {
                                                                    if (editName.getText().toString().equals(""))
                                                                        PhonePreference.setTitle("未設定聯絡人");
                                                                    else
                                                                        PhonePreference.setTitle(editName.getText().toString());
                                                                    PhonePreference.setSummary(editPhone.getText().toString());
                                                                    mName = editName.getText().toString();
                                                                    mPhoneNum = editPhone.getText().toString();
                                                                    Name = mName;
                                                                    PhoneNum = mPhoneNum;
                                                                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                                                    field.setAccessible(true);
                                                                    field.set(dialog, true);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                                        field.setAccessible(true);
                                                        field.set(dialog, true);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    //dialog.dismiss();
                                                }
                                            }).show();
                                            break;
                                    }
                                }
                            })
                            .show();
                    return false;
                }
            });
        }
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
