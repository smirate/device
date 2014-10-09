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

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        // 通知が送られてくるキーがdefaultだとして、そこに値が詰まってる時のみ処理
        if(intent != null && extras.getString("default") != null){
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification =
                    new Notification(android.R.drawable.btn_default,"笑顔率の高い番組が放送中です！", System.currentTimeMillis());

            // 通知をクリックされたときのintent設定
            Intent newIntent = new Intent(context, NotificationActivity.class);

            // ★★★　ここでサーバーからの通知を受け取る
            // ★★★　いったんはdefaultキーから値をとることにしておこう
            newIntent.putExtra("notification", extras.getString("default"));
            /*
            for(String key : extras.keySet()){
                String line = String.format("%s=%s", key, extras.getString(key));
                Log.i("msg:", line);
                newIntent.putExtra(key, extras.getString(key));
            }
            */

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(context.getApplicationContext(),"Smirate", "笑顔率の高い番組が放送中です！", contentIntent);
            // 古い通知をクリアし、最新の情報を通知する
            notificationManager.cancelAll();
            notificationManager.notify(R.string.app_name, notification);
        }
    }
}

