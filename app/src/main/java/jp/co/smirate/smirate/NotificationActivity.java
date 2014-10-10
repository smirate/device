package jp.co.smirate.smirate;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import jp.co.smirate.cst.NotificationCst;


public class NotificationActivity extends Activity implements NotificationCst {
    public String testMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();

        String title = intent.getStringExtra(NotificationKey.TITLE.val);
        String smirate = intent.getStringExtra(NotificationKey.SMIRATE.val);

        if(intent != null && title != null && smirate != null) {
            TextView titleTextView = (TextView) this.findViewById(R.id.title);
            titleTextView.setText(title);

            TextView smirateTextView = (TextView) this.findViewById(R.id.smirate);
            smirateTextView.setText(smirate + "%");

            // 通知バー除去
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toListener(View view) {
        switch (view.getId()){
            case R.id.toListener:
                Intent intent = new Intent(this, ListenerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
