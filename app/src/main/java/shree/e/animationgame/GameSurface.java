package shree.e.animationgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private GameCharacter char1;


    public GameSurface(Context context)  {

        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
    }



    public void update()  {
        this.char1.update();
    }


    @Override
    public void draw(Canvas canvas)  {

       super.draw(canvas);
       this.char1.draw(canvas);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

     //   this.setBackground(this.getResources().getDrawable(R.drawable.char2));

        Bitmap char1Bitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.char1);
        this.char1 = new GameCharacter(this,char1Bitmap1,100,50);

        Log.i("1", "surface created");

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            int movingVectorX = x -  this.char1.getX() ;
            int movingVectorY = y -  this.char1.getY() ;

            Log.i("onTouch-", "x : " + x + ", y : "+ y + ", movingVectorX : " + movingVectorX + ", movingVectorY : " + movingVectorY);

            this.char1.setMovingVector(x, y);
            return true;
        }
        return false;
    }
}