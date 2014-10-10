package jp.co.smirate.smirate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import jp.co.smirate.cst.NotificationCst;

public class ExternalReceiver extends BroadcastReceiver implements NotificationCst {

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if(intent != null && extras.getString(NOTIFICATION_KEY) != null){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(android.R.drawable.btn_default, Msg.TITLE.val, System.currentTimeMillis());
            Intent newIntent = new Intent(context, NotificationActivity.class);
            newIntent.putExtra(NOTIFICATION_KEY, extras.getString(NOTIFICATION_KEY));
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(context.getApplicationContext(), Msg.APPNAME.val, Msg.TITLE.val, contentIntent);
            notificationManager.cancelAll();
            notificationManager.notify(R.string.app_name, notification);
        }
    }
}

