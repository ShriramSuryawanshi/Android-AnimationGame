package shree.e.animationgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;


public class GameCharacter extends GameObject {

    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    private int rowUsing = ROW_LEFT_TO_RIGHT;
    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    private int movingVectorX = 0;
    private int movingVectorY = 0;

    private long lastDrawNanoTime = -1;

    private GameSurface gameSurface;


    public GameCharacter(GameSurface gameSurface, Bitmap image, int x, int y) {

        super(image, 4, 3, x, y);

        this.gameSurface= gameSurface;

        this.topToBottoms = new Bitmap[colCount];
        this.rightToLefts = new Bitmap[colCount];
        this.leftToRights = new Bitmap[colCount];
        this.bottomToTops = new Bitmap[colCount];

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }


    public Bitmap[] getMoveBitmaps()  {

        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }


    public Bitmap getCurrentMoveBitmap()  {

        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }


    public void update()  {

        this.colUsing++;
        if (colUsing >= this.colCount) {
            this.colUsing = 0;
        }

        if (this.x == movingVectorX) {
            if(this.y == movingVectorY)
                this.colUsing = 1;
        }


        long now = System.nanoTime();


        if(lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }

        if (this.x < movingVectorX) this.x++;
        if (this.x > movingVectorX) this.x--;
        if (this.y < movingVectorY) this.y++;
        if (this.y > movingVectorY) this.y--;

        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;

        } else if(this.x > this.gameSurface.getWidth() - width)  {
            this.x = this.gameSurface.getWidth()- width;
            this.movingVectorX = - this.movingVectorX;
        }

        if(this.y < 0 )  {
            this.y = 0;
            this.movingVectorY = - this.movingVectorY;

        } else if(this.y > this.gameSurface.getHeight() - height)  {
            this.y= this.gameSurface.getHeight() - height;
            this.movingVectorY = - this.movingVectorY ;
        }

        if (movingVectorX > this.x)      this.rowUsing = ROW_LEFT_TO_RIGHT;
        if (movingVectorX < this.x)      this.rowUsing = ROW_RIGHT_TO_LEFT;

        if (movingVectorX == this.x) {

            if(movingVectorY > this.y)  this.rowUsing = ROW_TOP_TO_BOTTOM;
            if(movingVectorY < this.y)  this.rowUsing = ROW_BOTTOM_TO_TOP;
            if(movingVectorY == this.y)  this.rowUsing = ROW_TOP_TO_BOTTOM;
        }
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap,x, y, null);
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }
}