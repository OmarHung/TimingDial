package hung.timingdial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Hung on 2016/4/8.
 */
public class SetAlarmManager extends Service {
    long Time;
    int ID;
    String Mode;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("服務", "建立");
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("服務", "銷毀");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.e("服務", "執行 "+intent);
        //intent = new Intent();
        Time = intent.getLongExtra("time", 0);
        ID = intent.getIntExtra("id", -1);
        Mode = intent.getStringExtra("mode");
        Intent mIntent = new Intent(SetAlarmManager.this,AlarmReceiver.class);
        mIntent.putExtra("Long_id"+ID,(long)ID);
        Log.e("onStartCommand", "onStartCommand" + " " + ID);
        PendingIntent pi = PendingIntent.getBroadcast(SetAlarmManager.this, ID, mIntent, 0);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if(Mode.equals("set")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, Time, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, Time, pi);
            }
        }else if(Mode.equals("cancle")) am.cancel(pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
