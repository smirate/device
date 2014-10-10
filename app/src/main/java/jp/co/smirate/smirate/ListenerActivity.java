package jp.co.smirate.smirate;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import com.evixar.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import net.enswer.ear.*;

import java.io.IOException;

import jp.co.smirate.cst.EvixarCst;
import jp.co.smirate.cst.GcmCst;
import jp.co.smirate.cst.PostCst;
import jp.co.smirate.dto.OmronInfoDto;
import jp.co.smirate.dto.StreamInfoDto;
import jp.co.smirate.timer.PostTimerThred;
import jp.co.smirate.utils.PostUtil;

public class ListenerActivity extends Activity implements EvixarCst, GcmCst, PostCst {
    /** POST用番組情報. */
    public StreamInfoDto streamInfoDto4Post;
    /** POST用OMRON情報. */
    public OmronInfoDto omronInfoDto4Post;
    /** POST用デバイストークンID. */
    public String deviceTokenId;

    // 定期POST実行用タイマー
    private PostTimerThred postTimerThred;

    // evixar 処理用
    private EARSDK ear;
    private boolean finishEarInit;
    private boolean earIsRunning;
    private EarResultHandler earResultHandler = new EarResultHandler();
    private EarErrorHandler earErrorHandler = new EarErrorHandler();

    // 通知処理用
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        // 定期POST実行用タイマーを作成
        postTimerThred = new PostTimerThred(PERIOD, this);

        // TODO:omron関連のなにか。笑顔かどうかを表す何かを送ることになる想定
        omronInfoDto4Post = new OmronInfoDto();
        omronInfoDto4Post.smirate = "30";

        // evixar初期化
        finishEarInit = false;
        init();

        // 通知用処理
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        register();

        //TODO:サーバーサイドできてから有効化
        //PostUtil.post4DeviceTokenId(deviceTokenId);
    }

    // デバイストークン登録
    private void register() {
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(PJ_NUMBER);
                    Log.i("registrationId", token);
                    deviceTokenId = token;
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

    // evixar初期化
    private void init() {
        metaResolver.init();
        earIsRunning = false;
        Context context = getBaseContext().getApplicationContext();
        ear = new EARSDK(Certification.APP.val, Certification.ACCESS.val, context, earResultHandler, earErrorHandler);
        finishEarInit = true;
    }

    // evixar用ハンドラー
    @SuppressLint("HandlerLeak")
    private class EarResultHandler extends Handler implements EvixarCst {
        @Override
        public void handleMessage(Message msg) {

            streamInfoDto4Post = null;
            EARMatchResult matchResult = (EARMatchResult)msg.obj;
            EARAppkeyMatchResult appkeyMatchResult = matchResult.appkeyMatchResults.get(matchResult.activeAppkey);
            switch (appkeyMatchResult.matchState) {
                case EAR_MATCHSTATE_MATCHED:
                    for(EARMatchItem item : appkeyMatchResult.matchItems){
                        if(item instanceof EARStreamMatchItem){
                            EARStreamMatchItem streamMatchItem = (EARStreamMatchItem)item;
                            Log.i("RESULT", "streamId = " + streamMatchItem.streamId);

                            streamInfoDto4Post = new StreamInfoDto();
                            streamInfoDto4Post.streamId = streamMatchItem.streamId;

                            metaResolver.resolve(streamMatchItem.streamId, new AsyncCallback() {
                                public void onPreExecute() {
                                    // 特に何もしない
                                }
                                public void onProgressUpdate(int progress) {
                                    // 特に何もしない
                                }
                                public void onPostExecute(String response) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        streamInfoDto4Post.serviceId = json.getString(ResponseKey.SERVICEID.val);
                                        streamInfoDto4Post.eventId = json.getString(ResponseKey.EVENTID.val);
                                        streamInfoDto4Post.title = json.getString(ResponseKey.TITLE.val);
                                        streamInfoDto4Post.start = json.getString(ResponseKey.START.val);
                                        streamInfoDto4Post.end = json.getString(ResponseKey.END.val);
                                        streamInfoDto4Post.detail = json.getString(ResponseKey.DETAIL.val);
                                        streamInfoDto4Post.actors = json.getString(ResponseKey.ACTORS.val);
                                    } catch (JSONException e) {
                                        // 特に何もしない
                                        Log.i("EvixarJsonError",e.getMessage());
                                    }
                                }
                                public void onCancelled() {
                                    // 特に何もしない
                                }
                            });
                        }
                    }
                    break;
                case EAR_MATCHSTATE_NOT_MATCHED:
                    Log.i("EvixarNoMatch","EAR_MATCHSTATE_NOT_MATCHED");
                    break;
                case EAR_MATCHSTATE_UNDEFINED:
                    Log.i("EvixarNoMatch","EAR_MATCHSTATE_UNDEFINED");
                    break;
                case EAR_MATCHSTATE_ERROR:
                    Log.i("EvixarNoMatch","EAR_MATCHSTATE_ERROR");
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
            // エラーとなったからといって特に何もしない
            Log.e("EvixarErrorHandler","errcode: " + code);
        }
    }
}
