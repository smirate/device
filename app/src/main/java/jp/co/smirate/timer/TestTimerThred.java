package jp.co.smirate.timer;

import android.app.AlertDialog;
import android.widget.EditText;

import jp.co.smirate.smirate.ListenerActivity;

/**
 * タイマーサンプルクラス
 */
public class TestTimerThred extends AbstractTimerThred {
    EditText control;   // 値の取得元（実際に作るときはcontextのフィールド変数からとればよい）
    ListenerActivity context;

    /**
     * コンストラクタ
     * @param period
     * @param control
     */
    public TestTimerThred(long period, EditText control, ListenerActivity context) {
        super(period);
        this.control = control;
        this.context = context;
    }

    /**
     * 実行用メソッド
     */
    @Override
    protected void invokersMethod() {
        // コントロールの値を取得
        String text = control.getText().toString();

        // アラート表示（ここでＰＯＳＴ送信すればいいはず）
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ID=" + context.myStreamId);
        builder.setMessage(text);
        builder.show();
    }
}
