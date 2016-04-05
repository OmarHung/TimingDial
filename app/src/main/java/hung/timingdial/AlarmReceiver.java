package hung.timingdial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Hung on 2016/4/5.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private String CALL=Intent.ACTION_CALL;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bData = intent.getExtras();
        Log.e("onReceive", "onReceive");
        String strInput=bData.getString("phone");
        //callDirectly(context, strInput);
    }
    private void callDirectly(Context context, String phoneNum) {
        Intent intent =new Intent(CALL, Uri.parse("tel:" + phoneNum));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
