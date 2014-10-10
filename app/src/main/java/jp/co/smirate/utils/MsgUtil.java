package jp.co.smirate.utils;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.smirate.cst.PostCst;
import jp.co.smirate.dto.OmronInfoDto;
import jp.co.smirate.dto.StreamInfoDto;

public class MsgUtil  {

    public static String notificationMsg(String title, String smirate) {
        StringBuilder notificationMsg = new StringBuilder();
        notificationMsg.append(title);
        notificationMsg.append("の笑顔率");
        notificationMsg.append(smirate);
        notificationMsg.append("%!");

        return notificationMsg.toString();
    }
}
