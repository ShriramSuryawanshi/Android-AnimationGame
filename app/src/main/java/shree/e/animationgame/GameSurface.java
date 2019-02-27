package shree.e.animationgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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
    private final List<Explosion> explosionList = new ArrayList<Explosion>();

    public static int score = 0;
    private Bitmap[] starArray = new Bitmap[6];

    private static final int MAX_STREAMS=100;
    private int soundExplosion;
    private int soundCoins;

    private boolean soundPoolLoaded;
    private SoundPool soundPool;

    public GameSurface(Context context)  {

        super(context);
        this.setFocusable(true);
        this.getHolder().addCallback(this);
        this.initSoundPool();
    }


    public void update()  {

        this.char1.update();

        for(Explosion explosion: this.explosionList)  {
            explosion.update();
        }

        for (Stars star1: starList) {

            star1.update();

            if(star1.timer == 0) {

                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.explosion);
                Explosion explosion = new Explosion(this, bitmap, star1.getX()-20, star1.getY()-20);

                this.explosionList.add(explosion);

                Iterator<Explosion> iterator= this.explosionList.iterator();
                while(iterator.hasNext())  {
                    Explosion explosion1 = iterator.next();

                    if(explosion1.isFinish()) {
                        iterator.remove();
                        continue;
                    }
                }

                Random rand = new Random();
                star1.x = rand.nextInt((1800 - 10) + 1) + 10;
                star1.y = rand.nextInt((900 - 10) + 1) + 10;

                score -= 10;
                star1.timer = 1000;
            }

            int x1 = char1.x;
            int x2 = char1.x + char1.getWidth();
            int y1 = char1.y;
            int y2 = char1.y + char1.getHeight();

            if((star1.getX() < x1 && x1 < star1.getX() + star1.getWidth() && star1.getY() < y1 && y1 < star1.getY()+ star1.getHeight())
                    || star1.getX() < x2 && x2 < star1.getX() + star1.getWidth() && star1.getY() < y2 && y2 < star1.getY()+ star1.getHeight()){

                this.playSoundCoins();

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

        for(Explosion explosion: this.explosionList)  {
            explosion.draw(canvas);
        }

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
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundExplosion,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }


    public void playSoundCoins()  {
        if(this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn =  0.8f;
            int streamId = this.soundPool.play(this.soundCoins,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
}