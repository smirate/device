package jp.co.smirate.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.smirate.cst.PostCst;
import jp.co.smirate.dto.OmronInfoDto;
import jp.co.smirate.dto.StreamInfoDto;

public class PostUtil implements PostCst {
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

     enum Encode {
        UTF8("utf-8");

        public final String val;

        private Encode(String val) {
            this.val = val;
        }
    }

    public static HttpResponse post4DeviceTokenId(String deviceTokenId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("deviceTokenId", deviceTokenId));
        return post(Url.DEVICETOKENID.val, params);
    }

    public static HttpResponse post4StreamInfo(OmronInfoDto omronInfoDto, StreamInfoDto streamInfoDto, String deviceTokenId) {
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

        return post(Url.STREAMINFO.val, params);
    }

    public static HttpResponse post(String url, List<NameValuePair> params) {
        return post(url, params, Encode.UTF8);
    }

    public static HttpResponse post(String url, List<NameValuePair> params, Encode encode) {
        // 現在日時追加
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        params.add(new BasicNameValuePair("postDate", sdf.format(new Date())));

        HttpResponse response;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(url);
        try {
            method.setEntity(new UrlEncodedFormEntity(params, encode.val));
            response = client.execute(method);
        } catch (Exception e) {
            response = null;
        }
        return response;
    }
}
