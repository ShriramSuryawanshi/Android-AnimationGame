package shree.e.animationgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Stars extends GameObject {

    private int index = 0 ;
    private float counter = 0.0f;

    private boolean finish = false;
    private GameSurface gameSurface;

    private Bitmap[] stars = new Bitmap[6];


    public Stars(GameSurface GameSurface, Bitmap image, Bitmap[] stars, int x, int y) {
        super(image, 0, 0, x, y);
        this.gameSurface = GameSurface;

        this.stars = stars;
    }


    public void update()  {

        counter = counter + 0.1f;
        this.index = (int) Math.floor(counter);

        if(this.index >= 5) {
            this.index = 0;
            counter = 0.0f;
        }
    }


    public void draw(Canvas canvas)  {

            Bitmap bitmap = this.stars[this.index];
            canvas.drawBitmap(bitmap, this.x, this.y,null);
    }
}
