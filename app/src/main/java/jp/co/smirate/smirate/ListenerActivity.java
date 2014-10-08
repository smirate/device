package jp.co.smirate.smirate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import net.enswer.ear.*;

import jp.co.smirate.timer.TestTimerThred;

/**
 * 10秒感覚でテキストボックスの中身をアラート表示するのを作ってみた
 */
public class ListenerActivity extends Activity {

    // テスト的なタイマー
    private TestTimerThred testTimer;

    // evixar用サンプルから start
    private String liveAppKey;

    private EARSDK ear;
    private boolean finishEarInit;
    private boolean earIsRunning;

    private EarResultHandler earResultHandler = new EarResultHandler();
    private EarErrorHandler earErrorHandler = new EarErrorHandler();
    private Button mEarButton;

    public String myStreamId;
    // evixar用サンプルから end



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);


        // タイマーを作成
        // 分かりやすくアラートを出すために、画面上のコントロールを渡す
        // 実際にbluetoothの通信結果を渡す時はこのインスタンスのフィールド変数経由でいいと思う
        testTimer = new TestTimerThred(10000, (EditText) findViewById(R.id.omronMsg), this);

        // evixar用サンプルから start

        mEarButton = (Button)findViewById(R.id.onTest);
        mEarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchEarButton();
            }
        });
        finishEarInit = false;
        init();
        // evixar用サンプルから end
    }

    @Override
    protected void onResume() {
        super.onResume();

        // タイマー実行開始
        if(testTimer != null) {
            testTimer.execute();
        }

        // evixar用サンプルから start
        if(!finishEarInit) init();
        // evixar用サンプルから end
    }

    @Override
    protected void onPause() {
        // タイマー中断
        if(testTimer != null) {
            testTimer.cancel();
        }
        super.onPause();

        // evixar用サンプルから start
        if(earIsRunning) touchEarButton();
        if(finishEarInit){
            ear.release();
            finishEarInit = false;
        }
        // evixar用サンプルから end
    }

    @Override
    protected void onStop() {
        super.onStop();

        // evixar用サンプルから start
        if(earIsRunning) touchEarButton();
        if(finishEarInit){
            ear.release();
            finishEarInit = false;
        }
        // evixar用サンプルから end
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


    // evixar用サンプルから start
    private void init() {

        earIsRunning = false;

        // live app key
        liveAppKey = "v4aEbiBDtFI5g3mXL7Us6RRtGkLQbAzU";
        // live access key
        String liveAccessKey = "dnZ2dnZ2dnbWJ8ptzKB+nKWv+ECxeU9rASmuzct3i7kwPCl2xWbPQFpk6fjbyX8g+AKDCco7B0OKwW9X3IOdJQfdz+drqZOA6DLMoDf32y0PcnzMeKV448QvZSmOcHOhSFZpvcLuNZphTOESLknvFtYrGW10Y25ooco0LJeSI3mQG2fT9pWSMA==";

        Context context = getBaseContext().getApplicationContext();
        ear = new EARSDK(liveAppKey, liveAccessKey, context, earResultHandler, earErrorHandler);

        finishEarInit = true;
    }

    private void touchEarButton() {
        if(earIsRunning){
            earIsRunning = false;

            // stop
            ear.stopRecognizing();
            mEarButton.setText("Start");
        }
        else{
            earIsRunning = true;

            // start
            ear.startRecognizing();
            mEarButton.setText("Stop");
        }
    }

    @SuppressLint("HandlerLeak")
    private class EarResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            EARMatchResult matchResult = (EARMatchResult)msg.obj;

            EARAppkeyMatchResult appkeyMatchResult = matchResult.appkeyMatchResults.get(matchResult.activeAppkey);
            switch (appkeyMatchResult.matchState) {
                case EAR_MATCHSTATE_MATCHED:

                    for(EARMatchItem item : appkeyMatchResult.matchItems){
                        if(item instanceof EARStreamMatchItem){

                            EARStreamMatchItem streamMatchItem = (EARStreamMatchItem)item;
                            Log.i("RESULT", "streamId = " + streamMatchItem.streamId);
                            Log.i("MSG", msg.toString());

                            myStreamId = streamMatchItem.streamId;
                            // 所望の結果を得て、処理を終了する例
						/*
						if(streamMatchItem.streamId.equals("TBS") || streamMatchItem.streamId.equals("BSTBS")){
							if(earIsRunning){
								touchEarButton();
							}
						}
						*/

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

    @SuppressLint("HandlerLeak")
    private class EarErrorHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            EARErrorCode code = (EARErrorCode)msg.obj;
            Log.e("RESULT","errcode: " + code);
        }
    }
    // evixar用サンプルから end

}
