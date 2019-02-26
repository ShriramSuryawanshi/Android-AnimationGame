package shree.e.animationgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private GameCharacter char1;
    private final List<Stars> starList = new ArrayList<Stars>();

    public static int score = 0;

    private Bitmap[] starArray = new Bitmap[6];

    public GameSurface(Context context)  {

        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
    }


    public void update()  {

        this.char1.update();

        for (Stars star1: starList) {

            star1.update();

            if(star1.timer == 0) {

                Random rand = new Random();
                star1.x = rand.nextInt((1800 - 10) + 1) + 10;
                star1.y = rand.nextInt((900 - 10) + 1) + 10;

                score -= 10;
                star1.timer = 1000;
            }

            int star_x1 = star1.x;
            int star_x2 = star1.x + star1.getWidth();
            int star_y1 = star1.y;
            int star_y2 = star1.y + star1.getHeight();

            int char_x1 = char1.x;
            int char_x2 = char1.x + char1.getWidth();
            int char_y1 = char1.y;
            int char_y2 = char1.y + char1.getHeight();

            if ((char_x1 <= star_x2 && char_x1 >= star_x1 && char_y1 <= star_y2 && char_y1 >= star_y1) || (char_x2 <= star_x2 && char_x2 >= star_x1 && char_y2 <= star_y2 && char_y2 >= star_y1 )) {

                Random rand = new Random();
                star1.x = rand.nextInt((1800 - 10) + 1) + 10;
                star1.y = rand.nextInt((900 - 10) + 1) + 10;

                score += (int)(star1.timer/100) * 10;
                star1.timer = 1000;
            }
        }
    }


    @Override
    public void draw(Canvas canvas)  {

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        super.draw(canvas);
        this.char1.draw(canvas);

        for (Stars star1: starList) {

            star1.draw(canvas);
            paint.setTextSize(15);
            canvas.drawText(String.valueOf((star1.timer/100)*10), star1.x + 20, star1.y + 80, paint);
        }


        paint.setTextSize(40);
        canvas.drawText("Score : " + score, 1650, 50, paint);
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

        for (int i = 0; i < 5; i++) {

            Random rand = new Random();
            int x = rand.nextInt((1800-10)+1) + 10;
            int y = rand.nextInt((900-10)+1) + 10;
            starList.add(new Stars(this, starArray[0], starArray, x, y));
        }

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

           // Log.i("onTouch-", "x : " + x + ", y : "+ y + ", movingVectorX : " + movingVectorX + ", movingVectorY : " + movingVectorY);

            this.char1.setTargetPosition(x, y);
            return true;
        }
        return false;
    }
}