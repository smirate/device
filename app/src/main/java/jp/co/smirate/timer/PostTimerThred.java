package jp.co.smirate.timer;

import android.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;

import jp.co.smirate.smirate.ListenerActivity;

/**
 * 定期POST実行用タイマー
 */
public class PostTimerThred extends AbstractTimerThred {
    ListenerActivity context;

    /**
     * コンストラクタ
     * @param period
     */
    public PostTimerThred(long period,ListenerActivity context) {
        super(period);
        this.context = context;
    }

    /**
     * 実行用メソッド
     */
    @Override
    protected void invokersMethod() {
        // TODO: post処理の実装
        String streamId = context.streamId4Post;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("定期POSTタイマー" + context.registrationId);
        if(streamId != null) {
            try {
                String buf = "放送局：" + streamId;
                buf += "\nタイトル：" + context.streamJson4Post.getString("title");
                buf += "\n開始時刻" + context.streamJson4Post.getString("start");
                buf += "\n終了時刻" + context.streamJson4Post.getString("end");
                buf += "\n番組概要" + context.streamJson4Post.getString("description");
                buf += "\n番組内容" +context.streamJson4Post.getString("detail");
                buf += "\n出演者" + context.streamJson4Post.getString("actors");
                buf += "\nサービスID" + context.streamJson4Post.getString("service_id");
                buf += "\nイベントID" + context.streamJson4Post.getString("event_id");

                builder.setMessage(buf);
                builder.show();
            } catch (JSONException e) {

            }

        } else {
            builder.setMessage("放送局IDが取得できなかった");
            builder.show();
        }
    }
/*

    public String doPost(String url) {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);

        // リクエストパラメータの設定
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("params_id", getId()));
        params.add(new BasicNameValuePair("params_data", getData()));
        try {
            method.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse response = client.execute(method);
            int status = response.getStatusLine().getStatusCode();
            return "Status:" + status;
        } catch (Exception e) {
            return "Error:" + e.getMessage();
        }
    }
    */
}
