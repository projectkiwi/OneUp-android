package com.purduecs.kiwi.oneup.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

import com.purduecs.kiwi.oneup.helpers.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Adam on 4/23/16.
 */
public class CameraSurfaceView extends SurfaceView {

    // rbg frame
    int[] rgbints;

    // video stuff
    boolean takingVideo;
    AnimatedGifEncoder videoMaker;
    int skip;
    long videoStart;

    // Timer stuff
    long startTime = 0;
    String timerText = "";
    boolean timerRunning = false;
    Timer mTimer;
    Paint timerPaint;

    public CameraSurfaceView(Context c) {
        super(c);
        setWillNotDraw(false);

        takingVideo = false;
        skip = 0;

        mTimer = new Timer();
        timerPaint = new Paint();
        timerPaint.setColor(Color.WHITE);
        timerPaint.setTextSize(75);
    }

    public void toggleTimer() {
        if (timerRunning) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = new Timer();
            timerRunning = false;
        } else {
            startTime = System.currentTimeMillis();
            mTimer.schedule(new Stopwatch(), 0, 33);
            timerRunning = true;
        }
    }

    public void render(byte[] image, Camera.Size size) {
        Canvas canvas = null;

        rgbints = new int[size.width * size.height];

        if (getHolder() == null) {
            return;
        }

        try {
            synchronized (getHolder()) {
                canvas = getHolder().lockCanvas(null);

                decodeYUV(rgbints, image, size.width, size.height);

                draw(canvas, size);

                if (takingVideo && skip > 1) {
                    Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),Bitmap.Config.ARGB_8888);
                    Canvas canvas2 = new Canvas(bitmap);
                    draw(canvas2, size);
                    videoMaker.add(Bitmap.createScaledBitmap(bitmap, getWidth()/3, getHeight()/3, false));

                    skip = 0;
                } else { skip++; }


            }
        }  catch (Exception e){
            e.printStackTrace();
        } finally {
            // do this in a finally so that if an exception is thrown
            // during the above, we don't leave the Surface in an
            // inconsistent state
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public byte[] getPicture(Camera.Size size) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas, size);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);

        return os.toByteArray();
    }

    public void startVideo(File file) {
        takingVideo = true;
        skip = 0;
        videoStart = System.currentTimeMillis();
        try {
            videoMaker = new AnimatedGifEncoder();
            videoMaker.setFrameRate(10);
            videoMaker.start(new FileOutputStream(file));
        } catch (Exception e) {
            Log.e("HEY", "problem starting video");
            videoMaker = null;
            takingVideo = false;
            return;
        }
    }

    public void cancelVideo() {
        videoMaker.cancel();
        takingVideo = false;
    }

    public long elapsedVideo() {
        return System.currentTimeMillis() - videoStart;
    }

    public void endVideo(AnimatedGifEncoder.OnFinishListener listener) {
        videoMaker.finish(listener);
        takingVideo = false;
    }

    private void draw(Canvas c, Camera.Size size) {
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();
        // draw the decoded image, centered on canvas
        int x = canvasWidth-((size.width+canvasWidth)>>1);
        int y = canvasHeight-((size.height+canvasHeight)>>1);
        c.drawBitmap(rgbints, 0, size.width, x, y, size.width, size.height, false, null);

        // use some color filter
        //canvas.drawColor(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        if (timerRunning) {
            c.drawText(timerText, x + 50, y + 100, timerPaint);
        }
    }

    private void decodeYUV(int[] out, byte[] fg, int width, int height) throws NullPointerException, IllegalArgumentException {
        int sz = width * height;
        if (out == null)
            throw new NullPointerException("buffer out is null");
        if (out.length < sz)
            throw new IllegalArgumentException("buffer out size " + out.length + " < minimum " + sz);
        if (fg == null)
            throw new NullPointerException("buffer 'fg' is null");
        if (fg.length < sz)
            throw new IllegalArgumentException("buffer fg size " + fg.length + " < minimum " + sz * 3 / 2);
        int i, j;
        int Y, Cr = 0, Cb = 0;
        for (j = 0; j < height; j++) {
            int pixPtr = j * width;
            final int jDiv2 = j >> 1;
            for (i = 0; i < width; i++) {
                Y = fg[pixPtr];
                if (Y < 0)
                    Y += 255;
                if ((i & 0x1) != 1) {
                    final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
                    Cb = fg[cOff];
                    if (Cb < 0)
                        Cb += 127;
                    else
                        Cb -= 128;
                    Cr = fg[cOff + 1];
                    if (Cr < 0)
                        Cr += 127;
                    else
                        Cr -= 128;
                }
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0)
                    R = 0;
                else if (R > 255)
                    R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1) + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if (G < 0)
                    G = 0;
                else if (G > 255)
                    G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0)
                    B = 0;
                else if (B > 255)
                    B = 255;
                out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }
    }

    private class Stopwatch extends TimerTask {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds     = seconds % 60;
            millis      = (millis / 10) % 100;

            timerText = String.format("%d:%02d.%02d", minutes, seconds, millis);
        }
    }
}
