package shree.e.animationgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;


public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private GameCharacter char1;
    private final List<Stars> starList = new ArrayList<Stars>();
    private final List<Explosion> explosionList = new ArrayList<Explosion>();

    public static int score = 0;
    private Bitmap[] starArray = new Bitmap[6];

    private static final int MAX_STREAMS=100;
    private int soundExplosion;
    private int soundCoins;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    public int start = 1000;
    public int health = 100;
    public boolean end = false;

    public GameSurface(Context context)  {

        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        this.initSoundPool();
    }


    public void update()  {

        if(start <= 0 && !end) {

            this.char1.update();

            for (Explosion explosion : this.explosionList) {
                explosion.update();
            }

            for (Stars star1 : starList) {

                star1.update();

                if (star1.timer == 0) {

                    Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);
                    Explosion explosion = new Explosion(this, bitmap, star1.getX() - 30, star1.getY() - 30);

                    this.explosionList.add(explosion);

                    Iterator<Explosion> iterator = this.explosionList.iterator();
                    while (iterator.hasNext()) {
                        Explosion explosion1 = iterator.next();

                        if (explosion1.isFinish()) {
                            iterator.remove();
                            continue;
                        }
                    }

                    RectF char_rect = new RectF(char1.x, char1.y, char1.x + char1.width, char1.y + char1.height);
                    RectF star_rect = new RectF(explosion.x, explosion.y, explosion.x + explosion.width, explosion.y + explosion.height);

                    double dist = (((char_rect.centerX() - star_rect.centerX()) * (char_rect.centerX() - star_rect.centerX())) + ((char_rect.centerY() - star_rect.centerY()) * (char_rect.centerY() - star_rect.centerY())));

                    if (dist < 30000) {
                        health = health - (int) (dist / 1000) - 5;
                    }

                    Random rand = new Random();
                    star1.x = rand.nextInt((1800 - 10) + 1) + 10;
                    star1.y = rand.nextInt((900 - 100) + 1) + 100;

                    score -= 10;
                    star1.timer = 1000;
                }

                RectF char_rect = new RectF(char1.x, char1.y, char1.x + char1.width, char1.y + char1.height);
                RectF star_rect = new RectF(star1.x, star1.y, star1.x + star1.width, star1.y + star1.height);

                if (char_rect.intersect(star_rect)) {

                    this.playSoundCoins();

                    Random rand = new Random();
                    star1.x = rand.nextInt((1800 - 10) + 1) + 10;
                    star1.y = rand.nextInt((800 - 100) + 1) + 100;

                    score += (int) (star1.timer / 100) * 10;
                    star1.timer = 1000;
                }
            }
        }
    }


    @Override
    public void draw(Canvas canvas)  {

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        super.draw(canvas);

        if( start <= 0 && !end) {

            this.char1.draw(canvas);

            for (Explosion explosion : this.explosionList) {
                explosion.draw(canvas);
            }

            for (Stars star1 : starList) {

                star1.draw(canvas);
                paint.setTextSize(20);
                canvas.drawText(String.valueOf((star1.timer / 100) * 10), star1.x + 20, star1.y + 90, paint);
            }

            paint.setTextSize(40);
            canvas.drawText("Score : " + score, 1650, 50, paint);

            paint.setTextSize(40);
            canvas.drawText("Health : " + health + "%", 1200, 50, paint);
        }
        else  if (start > 0){
            paint.setTextSize(60);
            canvas.drawText("Collect the Coins!", 800, 200, paint);

            paint.setTextSize(35);
            canvas.drawText("Collect the coins and earn the points!!", 200, 400, paint);
            canvas.drawText("The game character will go in the direction of your touch on the screen, direct it correctly.", 200, 450, paint);
            canvas.drawText("Each coin has value associated with it, which decreases by 10 every second.", 200, 500, paint);
            canvas.drawText("You will get the equal number of points as the coin has value when you collect it.", 200, 550, paint);
            canvas.drawText("Once the coin value become 0, coin will explode and you will lose 10 points.", 200, 600, paint);
            canvas.drawText("Your health will be reduced based on how close you were to the explosion.", 200, 650, paint);
            canvas.drawText("Game will finish once your health becomes 0.", 200, 700, paint);
            canvas.drawText("Good Luck!!! Game will start in " + String.valueOf(start/100) +" seconds..", 200, 900, paint);

            start--;
        }

        if (health <= 0) {
            paint.setTextSize(100);
            canvas.drawText("Game Over!", 800, 500, paint);
            paint.setTextSize(40);
            canvas.drawText("Your Score : " + score, 900, 800, paint);
            end = true;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

     //   this.setBackground(this.getResources().getDrawable(R.drawable.char2));

            Bitmap char1Bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.char1);
            this.char1 = new GameCharacter(this, char1Bitmap1, 0, 0);

            starArray[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star1);
            starArray[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star2);
            starArray[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star3);
            starArray[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star4);
            starArray[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star5);
            starArray[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.star6);

            for (int i = 0; i < 5; i++) {

                Random rand = new Random();
                int x = rand.nextInt((1800 - 10) + 1) + 10;
                int y = rand.nextInt((800 - 100) + 1) + 100;
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

            this.char1.setTargetPosition(x, y);
            return true;
        }
        return false;
    }


    private void initSoundPool()  {

        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);
            this.soundPool = builder.build();
        }
        else
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);

        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { @Override public void onLoadComplete(SoundPool soundPool, int sampleId, int status) { soundPoolLoaded = true; } });

        this.soundCoins = this.soundPool.load(this.getContext(), R.raw.coins,1);
        this.soundExplosion = this.soundPool.load(this.getContext(), R.raw.explosion,1);
    }


    public void playSoundExplosion()  {
        if(this.soundPoolLoaded) {
            this.soundPool.play(this.soundExplosion,0.8f, 0.8f, 1, 0, 1f);
        }
    }


    public void playSoundCoins()  {
        if(this.soundPoolLoaded) {
            this.soundPool.play(this.soundCoins,0.8f, 0.8f, 1, 0, 1f);
        }
    }
}