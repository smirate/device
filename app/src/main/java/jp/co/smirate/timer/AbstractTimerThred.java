package jp.co.smirate.timer;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定期実行用の抽象クラス
 */
public abstract class AbstractTimerThred {
    private long period;    // 実行周期（ミリ秒）
    private boolean isDaemon;   // デーモンモードで動かすかどうか
    private boolean isCancelled = true;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;

    /**
     * デーモンモードコンストラクタ
     * @param period
     * @param isDaemon
     */
    public AbstractTimerThred(long period, boolean isDaemon) {
        handler = new Handler();
        this.period = period;
        this.isDaemon = isDaemon;
    }

    /**
     * ユーザモードコンストラクタ
     * @param period
     */
    public AbstractTimerThred(long period) {
        this(period, false);
    }

    /**
     * 実行用メソッド
     */
    public void execute() {
        if (!isCancelled) {
            return;
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                preInvokersMethod();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        invokersMethod();
                    }
                });
                postInvokersMethod();
            }
        };

        timer = new Timer(isDaemon);
        timer.scheduleAtFixedRate(timerTask, period, period);
    }

    /**
     * 中断用メソッド
     */
    public void cancel() {
        if (timer == null || timerTask == null) {
            return;
        }
        timer.cancel();
        timer = null;
        isCancelled = true;
    }

    /**
     * 指定周期毎に実行されるメソッド
     */
    abstract protected void invokersMethod();

    /**
     * 指定周期毎に実行されるメソッド前処理
     */
    protected void preInvokersMethod() {
    }

    /**
     * 指定周期毎に実行されるメソッド後処理
     */
    protected void postInvokersMethod() {
    }
}
