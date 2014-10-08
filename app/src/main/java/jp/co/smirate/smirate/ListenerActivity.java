package jp.co.smirate.smirate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import jp.co.smirate.timer.TestTimerThred;

/**
 * 10秒感覚でテキストボックスの中身をアラート表示するのを作ってみた
 */
public class ListenerActivity extends Activity {

    // テスト的なタイマー
    private TestTimerThred testTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        // タイマーを作成
        // 分かりやすくアラートを出すために、画面上のコントロールを渡す
        // 実際にbluetoothの通信結果を渡す時はこのインスタンスのフィールド変数経由でいいと思う
        testTimer = new TestTimerThred(10000, (EditText) findViewById(R.id.omronMsg), this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // タイマー実行開始
        if(testTimer != null) {
            testTimer.execute();
        }
    }

    @Override
    protected void onPause() {
        // タイマー中断
        if(testTimer != null) {
            testTimer.cancel();
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.listener, menu);
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

    public void toNotification(View view) {
        switch (view.getId()){
            case R.id.toNotification:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
        }
    }

}
