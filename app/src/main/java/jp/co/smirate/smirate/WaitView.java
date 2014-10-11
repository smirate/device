package jp.co.smirate.smirate;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;

public class WaitView extends View{
    Paint paint = new Paint();
    int playerX = 250;
    int playerVX = 10;
    int playerY = 1200;
    int playerVY = 0;

    //画像読み込み
    Resources res = this.getContext().getResources();
    Bitmap main_item = BitmapFactory.decodeResource(res, R.drawable.main_item);
    Bitmap bg = BitmapFactory.decodeResource(res, R.drawable.wait_bg);

    public WaitView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas c) {
        //数値処理
        playerX += playerVX;
        if(playerX<0 || 830<playerX) playerVX *= -1;
        c.drawBitmap(bg, 0, 0, paint);
        if(playerY<0) playerY = 0;
        c.drawBitmap(main_item, playerX, playerY, paint);
        playerY += playerVY;
        playerVY += 4;
        if(playerY>1200) playerY = 1200;

        //roop
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent me) {
        if(me.getAction() == MotionEvent.ACTION_DOWN) {
            playerVY = -40;
        }

        return true;
    }
}
