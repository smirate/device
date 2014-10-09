package jp.co.smirate.timer;

import android.app.AlertDialog;
import android.widget.EditText;

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
        builder.setTitle("定期POSTタイマー");
        if(streamId != null) {
            builder.setMessage("放送局：" + streamId);
            builder.show();
        } else {
            builder.setMessage("放送局IDが取得できなかった");
            builder.show();
        }
    }
}
