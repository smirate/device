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
import jp.co.smirate.utils.MsgUtil;

public class ExternalReceiver extends BroadcastReceiver implements NotificationCst {

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String msgs = extras.getString(NotificationKey.AWS.val);
        if(intent == null || msgs == null) return;

        String[] msgsArray = msgs.split("\t");
        if(msgsArray.length != 2) return;

        String title = msgsArray[0];
        String smirate = msgsArray[1];

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(android.R.drawable.btn_default, Msg.TITLE.val, System.currentTimeMillis());
        Intent newIntent = new Intent(context, NotificationActivity.class);
        newIntent.putExtra(NotificationKey.TITLE.val, title);
        newIntent.putExtra(NotificationKey.SMIRATE.val, smirate);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context.getApplicationContext(), Msg.APPNAME.val, MsgUtil.notificationMsg(title, smirate), contentIntent);
        notificationManager.cancelAll();
        notificationManager.notify(R.string.app_name, notification);
    }
}

