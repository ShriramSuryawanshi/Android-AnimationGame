package shree.e.animationgame;

import android.graphics.Bitmap;

public class GameObject {

    public Bitmap image;

    public int rowCount;
    public int colCount;

    public int WIDTH;
    public int HEIGHT;

    public int width;
    public int height;

    public int x;
    public int y;


    public GameObject(Bitmap image, int rowCount, int colCount, int x, int y) {

        this.image = image;
        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();

        this.width = this.WIDTH/colCount;
        this.height = this.HEIGHT/rowCount;
    }


    protected Bitmap createSubImageAt(int row, int col) {
        Bitmap subImage = Bitmap.createBitmap(image, col* width, row* height, width, height);
        return subImage;
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
