package shree.e.animationgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;


public class Stars extends GameObject {

    private int index = 0 ;
    private float counter = 0.0f;

    public int timer = 1000;

    private GameSurface gameSurface;

    private Bitmap[] stars = new Bitmap[6];


    public Stars(GameSurface GameSurface, Bitmap image, Bitmap[] stars, int x, int y) {
        super(image, 0, 0, x, y);
        this.gameSurface = GameSurface;

        this.stars = stars;

        Random rand = new Random();
        this.counter = (float) rand.nextFloat() * (5.0f - 0.0f) + 0.0f;
    }


    public void update()  {

        this.counter = counter + 0.1f;
        this.index = (int) Math.floor(counter);

        if(this.index >= 5) {
            this.index = 0;
            counter = 0.0f;
        }

        this.timer--;
    }


    public void draw(Canvas canvas)  {

            Bitmap bitmap = this.stars[this.index];
            canvas.drawBitmap(bitmap, this.x, this.y,null);
    }
}
