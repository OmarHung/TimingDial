package hung.timingdial;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NewDialActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public String Hour="",Minute="",Time="",Name="",PhoneNum="";
    Preference TimePreference,PhonePreference;
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    private MyDBHelper myDBHelper=null;
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
                Log.e("SetTime", Time);
                Log.e("SetName", Name);
                Log.e("SetPhoneNum", PhoneNum);
                append(Time, Name, PhoneNum, "T");
                long nextday=0;
                android.text.format.Time t=new Time();
                if(Integer.parseInt(Hour)<t.hour) {
                    nextday=1000*60*60*24;
                }
                else if(Integer.parseInt(Hour)==t.hour && Integer.parseInt(Minute)<t.minute) {
                    nextday=1000*60*60*24;
                }
                else nextday=0;
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
                cal.set(Calendar.MINUTE, Integer.parseInt(Minute));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("phone",PhoneNum);
                PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+nextday, pi);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis()+nextday, pi);
                }
                finish();
                return true;
            }
        }else finish();
        return super.onOptionsItemSelected(item);
    }
    class MyNewPreferenceFragment extends PreferenceFragment {
        public String mHour="",mMinute="",mTime="",mName="",mPhoneNum="";
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog;
        GregorianCalendar calendar = new GregorianCalendar();
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_dial);
            TimePreference=(Preference)findPreference("TIME");
            String Myhour=String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            String Myminute=String.valueOf(c.get(Calendar.MINUTE));
            Hour=Myhour;
            Minute=Myminute;
            if(c.get(Calendar.HOUR_OF_DAY)<10) Myhour="0"+c.get(Calendar.HOUR_OF_DAY);
            if(c.get(Calendar.MINUTE)<10) Myminute="0"+c.get(Calendar.MINUTE);
            TimePreference.setTitle(Myhour+":"+Myminute);
            PhonePreference=(Preference)findPreference("PHONE");

            TimePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String Myhour=String.valueOf(hourOfDay),Myminute=String.valueOf(minute);
                            if(hourOfDay<10) Myhour="0"+hourOfDay;
                            if(minute<10) Myminute="0"+minute;
                            TimePreference.setTitle(Myhour+":"+Myminute);
                            mHour=Myhour;
                            mMinute=Myminute;
                            mTime=Myhour+":"+Myminute;
                            Time=mTime;
                            Hour=mHour;
                            Minute=mMinute;
                            Log.e("SetTime",mTime);
                        }
                    },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                    timePickerDialog.show();
                    return false;
                }
            });
            PhonePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final String[] Items = {"從通訊錄","輸入電話"};
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
                                                                    Toast.makeText(NewDialActivity.this, "請輸入電話號碼", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else {
                                                                try {
                                                                    if(editName.getText().toString().equals("")) PhonePreference.setTitle("未設定聯絡人");
                                                                    else PhonePreference.setTitle(editName.getText().toString());
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
}
