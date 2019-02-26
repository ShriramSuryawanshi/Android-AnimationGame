package shree.e.animationgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;

    private GameCharacter char1;
    private Stars star1;
    private final List<Stars> starList = new ArrayList<Stars>();

    private Bitmap[] starArray = new Bitmap[6];

    public GameSurface(Context context)  {

        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
    }


    public void update()  {

        this.char1.update();
        this.star1.update();

    }


    @Override
    public void draw(Canvas canvas)  {

       super.draw(canvas);
       this.char1.draw(canvas);
       this.star1.draw(canvas);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

     //   this.setBackground(this.getResources().getDrawable(R.drawable.char2));

        Bitmap char1Bitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.char1);
        this.char1 = new GameCharacter(this, char1Bitmap1,0,0);


        starArray[0] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star1);
        starArray[1] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star2);
        starArray[2] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star3);
        starArray[3] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star4);
        starArray[4] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star5);
        starArray[5] = BitmapFactory.decodeResource(this.getResources(),R.drawable.star6);



        this.star1 = new Stars(this, starArray[0], starArray, 500,500);

        this.gameThread = new GameThread(this, holder);
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

            this.char1.setTargetPosition(x, y);
            return true;
        }
        return false;
    }
}