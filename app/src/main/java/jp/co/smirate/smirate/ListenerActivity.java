package jp.co.smirate.smirate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.evixar.EARSDK;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.enswer.ear.*;

import java.io.IOException;

import jp.co.smirate.timer.PostTimerThred;

/**
 * 10秒感覚でテキストボックスの中身をアラート表示するのを作ってみた
 */
public class ListenerActivity extends Activity {
    /** POST用放送局ID保管フィールド. */
    public String streamId4Post;

    // 定期POST実行用タイマー
    private PostTimerThred postTimerThred;

    // evixar 処理用
    private String liveAppKey;
    private EARSDK ear;
    private boolean finishEarInit;
    private boolean earIsRunning;
    private EarResultHandler earResultHandler = new EarResultHandler();
    private EarErrorHandler earErrorHandler = new EarErrorHandler();


    private GoogleCloudMessaging gcm;
    public String registrationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        // 定期POST実行用タイマーを作成
        postTimerThred = new PostTimerThred(10000, this);

        // evixar初期化
        finishEarInit = false;
        init();


        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        register();

        // ★★★★
        // サーバーへregistrationIdを送る
    }

    private void register() {
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register("792401251374");
                    Log.i("registrationId", token);
                    registrationId = token;
                }
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // タイマー実行開始
        if(postTimerThred != null) {
            postTimerThred.execute();
        }

        // evixar開始
        if(!finishEarInit) {
            init();
        }
        if(!earIsRunning){
            earIsRunning = true;
            ear.startRecognizing();
        }
    }

    @Override
    protected void onPause() {
        // タイマー中断
        if(postTimerThred != null) {
            postTimerThred.cancel();
        }
        super.onPause();

        // evixar停止
        if(earIsRunning) {
            earIsRunning = false;
            ear.stopRecognizing();
        }
        if(finishEarInit){
            ear.release();
            finishEarInit = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // evixar停止
        if(earIsRunning) {
            earIsRunning = false;
            ear.stopRecognizing();
        }
        if(finishEarInit){
            ear.release();
            finishEarInit = false;
        }
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

    /**
     * 通知画面へ遷移
     * @param view
     */
    public void toNotification(View view) {
        switch (view.getId()){
            case R.id.toNotification:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
        }
    }

    // evixar初期化
    private void init() {
        earIsRunning = false;
        liveAppKey = "v4aEbiBDtFI5g3mXL7Us6RRtGkLQbAzU";
        String liveAccessKey = "dnZ2dnZ2dnbWJ8ptzKB+nKWv+ECxeU9rASmuzct3i7kwPCl2xWbPQFpk6fjbyX8g+AKDCco7B0OKwW9X3IOdJQfdz+drqZOA6DLMoDf32y0PcnzMeKV448QvZSmOcHOhSFZpvcLuNZphTOESLknvFtYrGW10Y25ooco0LJeSI3mQG2fT9pWSMA==";
        Context context = getBaseContext().getApplicationContext();
        ear = new EARSDK(liveAppKey, liveAccessKey, context, earResultHandler, earErrorHandler);
        finishEarInit = true;
    }

    // evixar用ハンドラー
    @SuppressLint("HandlerLeak")
    private class EarResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            streamId4Post = null;
            EARMatchResult matchResult = (EARMatchResult)msg.obj;
            EARAppkeyMatchResult appkeyMatchResult = matchResult.appkeyMatchResults.get(matchResult.activeAppkey);
            switch (appkeyMatchResult.matchState) {
                case EAR_MATCHSTATE_MATCHED:
                    for(EARMatchItem item : appkeyMatchResult.matchItems){
                        if(item instanceof EARStreamMatchItem){
                            EARStreamMatchItem streamMatchItem = (EARStreamMatchItem)item;
                            Log.i("RESULT", "streamId = " + streamMatchItem.streamId);
                            streamId4Post = streamMatchItem.streamId;
                        }
                    }
                    break;
                case EAR_MATCHSTATE_NOT_MATCHED:
                    Log.i("RESULT","EAR_MATCHSTATE_NOT_MATCHED");
                    break;
                case EAR_MATCHSTATE_UNDEFINED:
                    Log.i("RESULT","EAR_MATCHSTATE_UNDEFINED");
                    break;
                case EAR_MATCHSTATE_ERROR:
                    Log.i("RESULT","EAR_MATCHSTATE_ERROR");
                    break;
                default:
                    break;
            }
        }
    }

    // evixar用エラーハンドラー
    @SuppressLint("HandlerLeak")
    private class EarErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            EARErrorCode code = (EARErrorCode)msg.obj;
            Log.e("RESULT","errcode: " + code);
        }
    }
}
