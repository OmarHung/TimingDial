package hung.timingdial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Hung on 2016/4/7.
 */
public class TimeCursorAdapter extends CursorAdapter {
    private MyDBHelper myDBHelper=null;
    public Context context;
    public LayoutInflater mInflater;
    public boolean mySwitchValue=false;
    public static final String TABLE_NAME = "timingdial";
    public static final String KEY_ID = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String TIME_COLUMN = "time";
    public static final String SWITCH_COLUMN = "switch";
    private String TAG="TAG";
    public TimeCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context=context;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.listview_layout, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.txtName = (TextView)view.findViewById(R.id.txt_name);
        viewHolder.txtTime = (TextView)view.findViewById(R.id.txt_time);
        viewHolder.txtPhoneNumber = (TextView)view.findViewById(R.id.txt_phone_number);
        viewHolder.mSwitch = (Switch)view.findViewById(R.id.switch1);
        view.setTag(viewHolder);
        return view;
    }
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        myDBHelper = new MyDBHelper(context);
        if (view == null || cursor == null)
            return;
        final String strName, strTime, strPhoneNum, strSwitch;
        final long id;
        id=cursor.getLong(0);
        strTime = cursor.getString(1);
        strName = cursor.getString(2);
        strPhoneNum = cursor.getString(3);
        strSwitch = cursor.getString(4);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.txtName.setText(strName);
        viewHolder.txtTime.setText(strTime);
        viewHolder.txtPhoneNumber.setText(strPhoneNum);
        final boolean TorF = viewHolder.mSwitch.isChecked();
        mySwitchValue=false;
        if(strSwitch.equals("T")) mySwitchValue=true;
        viewHolder.mSwitch.setChecked(mySwitchValue);
        viewHolder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch mSwitch = (Switch) v.findViewById(R.id.switch1);
                boolean TorF = mSwitch.isChecked();
                String nowSwitch = "F";
                if (TorF) {
                    nowSwitch = "T";
                    long nextday=0;
                    String Hour=strTime.substring(0, 2), Minute = strTime.substring(3);
                    GregorianCalendar g = new GregorianCalendar();
                    int SystemHour=g.get(GregorianCalendar.HOUR_OF_DAY), SystemMinute=g.get(GregorianCalendar.MINUTE);
                    if(Integer.parseInt(Hour)<SystemHour) {
                        nextday=1000*60*60*24;
                    }
                    else if(Integer.parseInt(Hour)==SystemHour && Integer.parseInt(Minute)<=SystemMinute) {
                        nextday=1000*60*60*24;
                    }
                    else nextday=0;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
                    cal.set(Calendar.MINUTE, Integer.parseInt(Minute));
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Intent intent = new Intent(context, SetAlarmManager.class);
                    intent.putExtra("time", cal.getTimeInMillis() + nextday);
                    intent.putExtra("id", (int)id);
                    intent.putExtra("mode", "set");
                    context.startService(intent);
                } else {
                    Intent intent = new Intent(context, SetAlarmManager.class);
                    intent.putExtra("id", (int)id);
                    intent.putExtra("mode", "cancel");
                    context.startService(intent);
                }
                update(id, strTime, strName, strPhoneNum, nowSwitch);
            }
        });
    }
    public class ViewHolder {
        TextView txtName;
        TextView txtTime;
        TextView txtPhoneNumber;
        Switch mSwitch;
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
