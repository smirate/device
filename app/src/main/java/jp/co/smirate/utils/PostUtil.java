package jp.co.smirate.utils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.text.SpannableStringBuilder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.smirate.cst.PostCst;
import jp.co.smirate.dto.OmronInfoDto;
import jp.co.smirate.dto.StreamInfoDto;

/**
 * POST用ユーティリティクラス
 */
public class PostUtil implements PostCst {
    // POST日時のフォーマット
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * エンコード種類
     */
    public enum Encode {
        UTF8("utf-8");

        public final String val;

        private Encode(String val) {
            this.val = val;
        }
    }

    /**
     * デバイストークン送信用POST
     * @param deviceTokenId
     * @return
     */
    public static void post4DeviceTokenId(String deviceTokenId) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("deviceTokenId", deviceTokenId));
        params.add(new BasicNameValuePair("postDate", sdf.format(new Date())));
        post(Url.DEVICETOKENID.val, params);
    }

    /**
     * 番組情報など送信用POST
     * @param omronInfoDto
     * @param streamInfoDto
     * @param deviceTokenId
     * @return
     */
    public static void post4StreamInfo(OmronInfoDto omronInfoDto, StreamInfoDto streamInfoDto, String deviceTokenId) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("streamId", streamInfoDto.streamId));
        params.add(new BasicNameValuePair("serviceId", streamInfoDto.serviceId));
        params.add(new BasicNameValuePair("eventId", streamInfoDto.eventId));
        params.add(new BasicNameValuePair("title", streamInfoDto.title));
        params.add(new BasicNameValuePair("start", streamInfoDto.start));
        params.add(new BasicNameValuePair("end", streamInfoDto.end));
        params.add(new BasicNameValuePair("description", streamInfoDto.description));
        params.add(new BasicNameValuePair("detail", streamInfoDto.detail));
        params.add(new BasicNameValuePair("actors", streamInfoDto.actors));
        params.add(new BasicNameValuePair("deviceTokenId", deviceTokenId));
        params.add(new BasicNameValuePair("smirate", omronInfoDto.smirate));
        params.add(new BasicNameValuePair("postDate", sdf.format(new Date())));

        post(Url.STREAMINFO.val, params);
    }

    /**
     * POST処理（エンコードはUTF-8）
     * @param url
     * @param params
     * @return
     */
    public static void post(String url, List<NameValuePair> params) {
        post(url, params, Encode.UTF8);
    }

    /**
     * POST処理コア
     * @param url
     * @param params
     * @param encode
     */
    public static void post(final String url,final List<NameValuePair> params, final Encode encode) {
        final List<NameValuePair> sendParams = params;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                // HTTPリクエストの構築
                HttpPost request = new HttpPost(url);
                try {
                    request.setEntity(new UrlEncodedFormEntity(sendParams, encode.val));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // HTTPリクエスト発行
                AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android HTTP Client Test");
                HttpResponse response = null;
                String response_str = "NG";
                try {
                    response = httpClient.execute(request);
                    // HttpResponseのEntityデータをStringへ変換
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    response_str = builder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    response_str = e.toString();
                }

                if(httpClient != null){
                    httpClient.close();
                }

                return response_str; //返値はonPostExecuteに渡される
            }


            @Override
            protected void onPostExecute(String result) { //引数はdoInBackgroundの結果
                // 画面に文字列を表示
                Log.i("response", result);
            }
        }.execute();
    }
}
