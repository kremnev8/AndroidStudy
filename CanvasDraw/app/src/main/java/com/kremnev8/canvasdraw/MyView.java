package com.kremnev8.canvasdraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MyView extends SurfaceView implements Runnable{

    private Paint paint = new Paint();
    private Path path = new Path();
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private volatile boolean playing;
    private Thread gameThread = null;
    private volatile long lastTimeMillis;

    public MyView(Context context) {
        super(context);

        surfaceHolder = getHolder();
    }


    public int houseSize = 700;
    public int grassHeight = 300;

    public int houseX = 200;
    public int houseY = 150;

    public float angle = 0;

    private void drawRectStroked(float left, float top, float right, float bottom, Canvas canvas){
        int color = paint.getColor();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(left, top, right, bottom, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(left, top, right, bottom, paint);
        paint.setColor(color);
    }

    private void drawRectLined(float left, float top, float right, float bottom, Canvas canvas){
        int color = paint.getColor();
        drawRectStroked(left, top, right, bottom, canvas);

        paint.setColor(Color.BLUE);
        float density = 7;

        float dx = (bottom - top) / density;
        float dy = (right - left) / density;
        for (int i = 0; i < density; i++) {
            canvas.drawLine(left, top + dx * i, right, top + dx * i, paint);
        }

        for (int i = 0; i < density; i++) {
            canvas.drawLine(left + dy * i, top, left + dy * i, bottom, paint);
        }

        paint.setColor(color);
    }

    private void drawRectAngleLined(float left, float top, float right, float bottom, Canvas canvas){
        int color = paint.getColor();
        drawRectStroked(left, top, right, bottom, canvas);

        paint.setColor(Color.WHITE);
        int density = 20;

        float step = Math.max((bottom - top) / density, (right - left) / density) * 1.5f;
        for (int i = 0; i < density; i++) {
            float topClamped = Math.max(top, Math.min(bottom, bottom-step*i));
            float leftClamped = topClamped == bottom-step*i ? left : left - (bottom-step*i - topClamped);
            float rightClamped = Math.max(left, Math.min(right, left+step*i));
            float bottomClamped = rightClamped == left+step*i ? bottom : bottom - (left+step*i - rightClamped);

            canvas.drawLine(leftClamped, topClamped, rightClamped, bottomClamped, paint);
        }

        paint.setColor(color);
    }

    private void drawCircleAngleLined(float cx, float cy, float radius, Canvas canvas){
        int color = paint.getColor();
        drawCircleStroked(cx, cy, radius, canvas);

        paint.setColor(Color.WHITE);
        float density = 10;

        float startAngle = 90+45;

        float step = 180 / density;
        for (float i = 0; i < 180; i+=step) {
            float angleStart = startAngle + i;
            float angleEnd = startAngle - i;

            float left = (float) Math.cos(Math.toRadians(angleStart)) * radius + cx;
            float right = (float) Math.cos(Math.toRadians(angleEnd)) * radius + cx;

            float top = (float) Math.sin(Math.toRadians(angleStart)) * radius + cy;
            float bottom = (float) Math.sin(Math.toRadians(angleEnd)) * radius + cy;

            canvas.drawLine(left, top, right, bottom, paint);
        }

        paint.setColor(color);
    }

    private void drawPathStroked(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);
    }

    private void drawCircleStroked(float cx, float cy, float radius, Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, cy, radius, paint);
    }


    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            paint.setColor(Color.parseColor("#00aa00"));
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);
            paint.setAntiAlias(true);

            int height = getHeight();
            int width = getWidth();

            canvas.drawRect(0, height - grassHeight, width, height, paint);
            paint.setColor(Color.parseColor("#4DE1FF"));
            canvas.drawRect(0, 0, width, height - grassHeight, paint);

            DrawHouse(canvas);
            DrawSun(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    private void DrawSun(Canvas canvas) {
        paint.setColor(Color.parseColor("#FFFF33"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0,0, 200, paint);

        for (float i = angle; i < angle +360; i += 10) {
            float top = (float) Math.sin(Math.toRadians(i)) * 250;
            float left = (float) Math.cos(Math.toRadians(i)) * 250;

            float bottom = (float) Math.sin(Math.toRadians(i)) * 450;
            float right = (float) Math.cos(Math.toRadians(i)) * 450;

            canvas.drawLine(left, top, right, bottom, paint);
        }
    }

    private void DrawHouse(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();

        paint.setColor(Color.parseColor("#964B00"));
        paint.setStyle(Paint.Style.FILL);
        drawRectStroked(houseX, height - houseY - houseSize, houseX + houseSize, height - houseY, canvas);
        drawRectAngleLined(houseX + houseSize / 2f + 30, height - houseY - houseSize + 70, houseX + houseSize - 30,height - houseY, canvas);

        paint.setColor(Color.parseColor("#550080"));
        drawRectLined(houseX + 50, height - houseY - houseSize  + 140, houseX + 50 + houseSize / 2.5f, height - houseY - houseSize  + 140 + houseSize / 2.5f, canvas);

        path.reset();
        path.moveTo(houseX, height - houseY- houseSize);
        path.lineTo(houseX + houseSize, height - houseY - houseSize);
        path.lineTo(houseX  + houseSize / 2f, height - houseY - houseSize *  1.5f);
        path.close();

        paint.setColor(Color.parseColor("#964B00"));
        drawPathStroked(canvas);

        paint.setColor(Color.parseColor("#550080"));
        drawCircleAngleLined(houseX  + houseSize / 2f, height - houseY - houseSize - 120, 80, canvas);
    }

    private void update(){
        long currentTime = System.currentTimeMillis();
        float timeElapsed = (currentTime - lastTimeMillis) / 1000f;
        angle += 20 * timeElapsed;

        lastTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        lastTimeMillis = System.currentTimeMillis();
        gameThread = new Thread(this);
        gameThread.start();
    }
}
