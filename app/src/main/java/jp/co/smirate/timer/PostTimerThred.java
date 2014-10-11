package jp.co.smirate.timer;

import android.app.AlertDialog;

import jp.co.smirate.dto.OmronInfoDto;
import jp.co.smirate.dto.StreamInfoDto;
import jp.co.smirate.smirate.ListenerActivity;
import jp.co.smirate.utils.PostUtil;

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
        StreamInfoDto streamInfoDto = context.streamInfoDto4Post;
        OmronInfoDto omronInfoDto = context.omronInfoDto4Post;

        //AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //builder.setTitle("定期POSTタイマー");
        if (streamInfoDto != null) {
           /* String buf = "放送局：" + streamInfoDto.streamId;
            buf += "\nタイトル：" + streamInfoDto.title;
            buf += "\n開始時刻" + streamInfoDto.start;
            buf += "\n終了時刻" + streamInfoDto.end;
            buf += "\n番組概要" + streamInfoDto.description;
            buf += "\n番組内容" + streamInfoDto.detail;
            buf += "\n出演者" + streamInfoDto.actors;
            buf += "\nサービスID" + streamInfoDto.serviceId;
            buf += "\nイベントID" + streamInfoDto.eventId;
            builder.setMessage(buf);
            builder.show();*/

            //TODO: bluetoothへコマンド送信
            //context.write(new byte[] { 0x50, 0x4d, 0x03 });


            PostUtil.post4StreamInfo(omronInfoDto, streamInfoDto, context.deviceTokenId);
        } else {
            //builder.setMessage("放送局IDが取得できなかった");
            //builder.show();
        }
    }
}
