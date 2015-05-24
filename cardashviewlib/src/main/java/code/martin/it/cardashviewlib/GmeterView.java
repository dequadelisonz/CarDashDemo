/**
 * **************************************************************
 * Copyright 2015 Paolo Martinello; created on 02/05/15
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************
 */
package code.martin.it.cardashviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;

public class GmeterView extends View implements SensorEventListener {

    private final String TAG=this.getClass().getSimpleName();

    private final Context context;

    private Bitmap rimBitmap,reflBitmap,curBitmap;
    private RectF rimRectF,
            curRectf,
            xAxisRectf,
            yAxisRectf,
            circle1RectF,
            circle2RectF,
            circle3RectF;

    private Paint curPaint,
            textPaint,
            reflPaint,
            axisPaint,
            circlesPaint,
            mBackgroundPaint;

    private Float mGx=0f;
    private Float mGy=0f;
    private Float mMaxG=1f;
    private Float y;
    private Float z;
    private DecimalFormat mFrm;
    private int mCentralIconRes=0;

    private SensorManager mSensorManager;
    private Sensor mLinAccelerator;
    private Boolean isSensorPresent=false;


    public GmeterView(Context context) {
        super(context);
        this.context=context;
        initGraphics();
        initSensor();
    }

    public GmeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardashViewLib);
        try {
            mGx=a.getFloat(R.styleable.CardashViewLib_gMeterGx, mGx);
            mGy=a.getFloat(R.styleable.CardashViewLib_gMeterGy, mGy);
            mCentralIconRes=a.getResourceId(R.styleable.CardashViewLib_gMeterCentralIconRes, R.drawable.blank_bkrg);
        } finally {
            a.recycle();
        }
        initGraphics();
        initSensor();
    }

    public GmeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.context=context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardashViewLib);
        try {
            mGx=a.getFloat(R.styleable.CardashViewLib_gMeterGx, mGx);
            mGy=a.getFloat(R.styleable.CardashViewLib_gMeterGy, mGy);
            mCentralIconRes=a.getResourceId(R.styleable.CardashViewLib_gMeterCentralIconRes, R.drawable.blank_bkrg);
        } finally {
            a.recycle();
        }
        initGraphics();
        initSensor();
    }

    private void initSensor() {
        if (!this.isInEditMode()) {
            mFrm =new DecimalFormat("#.#");
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mLinAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if (mLinAccelerator!=null) {
                setMaxG(mLinAccelerator.getMaximumRange());
                isSensorPresent=true;
            };
        }
    }

    private void initGraphics(){


        reflBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.reflection);

        reflPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        reflPaint.setAlpha(65);

        curBitmap=BitmapFactory.decodeResource(getResources(), mCentralIconRes);
        curPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        curPaint.setFilterBitmap(true);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setFilterBitmap(true);

        axisPaint=new Paint();
        axisPaint.setFilterBitmap(true);
        axisPaint.setStyle(Paint.Style.FILL);
        axisPaint.setShader(
                new LinearGradient(0.0f,0.0f, 0.0f,1.0f,
                        new int[]{
                                Color.rgb(255, 137, 0),
                                Color.rgb(255, 255, 255)
                        }, new float[]{
                        0.0f,
                        0.7f
                }, Shader.TileMode.CLAMP));
        axisPaint.setAlpha(120);

        circlesPaint=new Paint();
        circlesPaint.setFilterBitmap(true);
        circlesPaint.setStyle(Paint.Style.FILL);

        circlesPaint.setColor(0xFF8400);
        circlesPaint.setAlpha(60);

        textPaint=new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(1f);
        textPaint.setTextSize(15f);
        textPaint.setTypeface(Typeface.SANS_SERIF);
        textPaint.setTextAlign(Paint.Align.CENTER);
        //textPaint.setShadowLayer(0.005f, 0.002f, 0.002f, Color.GRAY);
        textPaint.setAlpha(120);

        setBackgroundResource(R.drawable.rim);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (null != rimBitmap) {
            // Let go of the old background
            rimBitmap.recycle();
        }
        // Create a new background according to the new width and height
        rimBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(rimBitmap);
        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);


        drawScale(canvas);
    }


    private void drawScale(Canvas canvas) {

        rimRectF=new RectF(0f,0f,getWidth(),getHeight());
        curRectf=new RectF(getWidth()/2-getWidth()*.05f,
                getHeight()/2-getHeight()*.05f,
                getWidth()/2+getWidth()*.05f,
                getHeight()/2+getHeight()*.05f);

        xAxisRectf=new RectF(0.49f,0.075f,0.51f,0.93f);
        yAxisRectf=new RectF(0.077f,0.49f,0.935f,0.51f);

        circle1RectF=new RectF(0.125f,0.125f,0.875f,0.875f);
        circle2RectF=new RectF(0.25f,0.25f,0.75f,0.75f);
        circle3RectF=new RectF(0.375f,0.375f,0.625f,0.625f);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.drawOval(circle1RectF, circlesPaint);
        canvas.drawOval(circle2RectF, circlesPaint);
        canvas.drawOval(circle3RectF, circlesPaint);
        canvas.drawRect(xAxisRectf,axisPaint);
        canvas.drawRect(yAxisRectf,axisPaint);

        canvas.restore();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rimBitmap!=null) {
            canvas.drawBitmap(rimBitmap,0,0,mBackgroundPaint);
        }

        int w = getWidth();
        int h = getHeight();
        float tx = w*mGx/mMaxG;
        float ty = h*mGy/mMaxG;
        canvas.save();
        canvas.translate(tx,ty );
        canvas.drawBitmap(curBitmap, null, curRectf, curPaint);
        canvas.restore();

        if (!this.isInEditMode()) {
            canvas.drawText(mFrm.format(mGx),0.85f*w,0.57f*h, textPaint);
            canvas.drawText(mFrm.format(mGy),0.57f*w, 0.15f*h, textPaint);
        } else {
            canvas.drawText("mGx",0.85f*w,0.57f*h, textPaint);
            canvas.drawText("mGy",0.57f*w, 0.15f*h, textPaint);
        }
        canvas.drawBitmap(reflBitmap, null, rimRectF, reflPaint);

    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int chosenWidth = chooseDimension(widthMode, widthSize);
        final int chosenHeight = chooseDimension(heightMode, heightSize);

        final int side=Math.min(chosenWidth,chosenHeight);

        setMeasuredDimension(side,side);

    }

    private int chooseDimension(final int mode, final int size) {
        switch (mode) {
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.EXACTLY:
                return size;
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return getDefaultDimension();
        }
    }

    private int getDefaultDimension() {
        return 50;
    }


    private void setGx(Float gx) {
        this.mGx = gx;
        invalidate();
    }


    private void setGy(Float gy) {
        this.mGy = gy;
        invalidate();
    }


    public Float getMaxG() {
        return mMaxG;
    }

    private void setMaxG(Float mMaxG) {
        this.mMaxG = mMaxG;
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        y=event.values[1];
        z=event.values[2];
        setGx(y);
        setGy(z);

    }

    public void pause() {
        mSensorManager.unregisterListener(this);
    }

    public void resume() {
        mSensorManager.registerListener(this,mLinAccelerator, SensorManager.SENSOR_DELAY_UI);
    }


}