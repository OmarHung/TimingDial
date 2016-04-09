package hung.timingdial;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Hung on 2016/4/8.
 */
public class UpdateUIService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Intent updateUIIntent = new Intent();
        updateUIIntent.setAction("UPDATE_UI_ACTION");
        sendBroadcast(updateUIIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
