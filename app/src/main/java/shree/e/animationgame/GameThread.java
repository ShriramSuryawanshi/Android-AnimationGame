package shree.e.animationgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;


    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {

        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run()  {

        long startTime = System.nanoTime();

        while(running)  {

            Canvas canvas= null;

            try {

                canvas = this.surfaceHolder.lockCanvas();

                synchronized (canvas)  {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing
            } finally {
                if(canvas!= null)  {
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            long now = System.nanoTime() ;

            long waitTime = (now - startTime)/1000000;
            if(waitTime < 10)  {
                waitTime = 10; // Millisecond.
            }

            try {
                // Sleep.
                this.sleep(waitTime);
                //this.sleep(100);
            } catch(InterruptedException e)  {

            }
            startTime = System.nanoTime();
        }
    }


    public void setRunning(boolean running)  {
        this.running= running;
    }
}