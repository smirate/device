package jp.co.smirate.smirate;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.WindowManager;

import java.util.Random;

public class WaitView extends View{
    Paint paint = new Paint();
    int init_y = 1450;
    int playerX = 250;
    int playerVX = 5;
    int playerY = 1200;
    int playerVY = 0;
    private int dp_w;
    private int dp_h;
    private int drow_h;
    private int drow_s;

    Random rnd = new Random();
    int ran = rnd.nextInt(10);

    //画像読み込み
    Resources res = this.getContext().getResources();
    Bitmap main_item = BitmapFactory.decodeResource(res, R.drawable.main_item);
    Bitmap bg = BitmapFactory.decodeResource(res, R.drawable.wait_bg);

    public WaitView(Context context) {
        super(context);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display dp = wm.getDefaultDisplay();
        dp_w = dp.getWidth();
        dp_h = dp.getHeight();
        // リサイズ画像の高さ
        drow_h = (dp_w / 2) * 3;
        // 描画始点の高さ
        drow_s = (dp_h - drow_h)/2;
        bg = Bitmap.createScaledBitmap(bg, dp_w, drow_h , true);  // イメージ画像リサイズ

    }

    @Override
    public void onDraw(Canvas c) {
        ran = rnd.nextInt(100);
        c.drawColor(Color.BLACK);
        c.drawBitmap(bg,0, drow_s, null);
        //数値処理
        playerX += playerVX;
        if(playerX<0 || 830<playerX) playerVX *= -1;
        if(playerY<100) playerY = 100;
        c.drawBitmap(main_item, playerX, playerY, paint);
        playerY += playerVY;
        playerVY += 4;
        if(playerY>=init_y){
            if(ran == 0) playerVY = -80;
        }
        if(playerY>init_y) playerY = init_y;

        //roop
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent me) {
        if(me.getAction() == MotionEvent.ACTION_DOWN) {
            playerVY = -60;
        }

        return true;
    }
}
