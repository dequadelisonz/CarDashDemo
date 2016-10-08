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

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HalView  extends View {

    private final String TAG=this.getClass().getSimpleName();
    private final Property<HalView, Float> propAmp = Property.of(HalView.class, Float.class, "amp");
    private Bitmap rimBitmap,reflBitmap;
    private RectF rimRectF,coreRectf;
    private Paint corePaint,reflPaint,mBackgroundPaint;
    private Float mAmp=5.0f;
    private ObjectAnimator oAnimator;
    private boolean mTalking=false;
    private Timer talkTimer ;
    private Handler timerHandler=new Handler();

    public HalView(Context context) {
        super(context);
        initGraphics();
    }

    public HalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardashViewLib);
        initAttrs(a);
        initGraphics();
    }




    public HalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardashViewLib);
        initAttrs(a);
        initGraphics();
    }

    private void initAttrs(TypedArray a) {
        try {
            mAmp=a.getFloat(R.styleable.CardashViewLib_halViewAmp, mAmp);
        } finally {
            a.recycle();
        }
    }
    private void initGraphics(){
        coreRectf=new RectF(0.1f,0.1f,0.9f,0.9f);

        corePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        corePaint.setStyle(Paint.Style.FILL);



        if (!this.isInEditMode()) {
            corePaint.setShader(new RadialGradient(0.5f, 0.5f, coreRectf
                    .width() / 2, new int[] { Color.rgb(50, 132, 206),
                    Color.rgb(36, 89, 162), Color.argb(120, 27, 59, 131) },
                    new float[] { 0.2f, 0.5f, 0.99f }, Shader.TileMode.CLAMP));
        }


        reflBitmap= BitmapFactory.decodeResource(getResources(), R.drawable.reflection);

        reflPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        reflPaint.setAlpha(65);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setFilterBitmap(true);

        setBackgroundResource(R.drawable.rim);

        oAnimator=ObjectAnimator.ofFloat(this, propAmp,10f);

        oAnimator.setInterpolator(new DecelerateInterpolator());

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (null != rimBitmap) {
            // Let go of the old background
            rimBitmap.recycle();
        }
        // Create a new background according to the new width and height
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable=true;
        Bitmap src=BitmapFactory.decodeResource(getResources(), R.drawable.blank_bkrg, options);
        rimBitmap=Bitmap.createScaledBitmap(src, getWidth(), getHeight(), false);
        //rimBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(rimBitmap);
        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);
        rimRectF=new RectF(0f,0f,getWidth(),getHeight());
        if (rimBitmap != null) {
            canvas.drawBitmap(rimBitmap, 0, 0, mBackgroundPaint);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        int w = getWidth();
        int h = getHeight();
        float scale = Math.min(w, h)*mAmp/100;
        float tx = w/2-scale/2;
        float ty = h/2-scale/2;
        canvas.save();
        canvas.translate(tx,ty );
        canvas.scale(scale, scale);
        canvas.drawOval(
                coreRectf,
                corePaint);
        canvas.restore();
        canvas.drawBitmap(reflBitmap, null, rimRectF, reflPaint);
        /*test
        Paint pp=new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        pp.setColor(Color.WHITE);
        pp.setStyle(Paint.Style.FILL_AND_STROKE);
        pp.setStrokeWidth(0.003f);
        pp.setTextSize(30f);
        pp.setTypeface(Typeface.SANS_SERIF);
        pp.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("provaaaaaaaaa",80f,50f,pp);
        */
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


    public Float getAmp() {
        return mAmp;
    }


    public void setAmp(Float mAmp) {
        this.mAmp = mAmp;
        invalidate();
    }

    public boolean isTalking() {
        return mTalking;
    }


    public void setFlashAmp(Float start,Float end,int duration){
        final PropertyValuesHolder pVh=PropertyValuesHolder.ofFloat(propAmp, start,end);
        oAnimator.setValues(pVh);
        oAnimator.setDuration(duration);
        oAnimator.start();
    }

    public void startTalking() {
        mTalking=true;
        talkTimer = new Timer();
        talkTimer.schedule(new TimerTask() {
            Random r=new Random();
            @Override
            public void run() {
                Thread.currentThread().setName("talkTimer");
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float f=r.nextFloat()*100f;
                        setFlashAmp(f, 30f, 200);
                    }
                });
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        },0,20);

    }

    public void stopTalking(){
        Log.d(TAG,"stop talking");
        if (talkTimer!=null) {
            talkTimer.cancel();
        }
        new Thread(new Stopper(this)).start();
    }

    private class Stopper implements Runnable {

        private HalView mHalView;

        public Stopper(HalView hl){
            this.mHalView=hl;
        }

        @Override
        public void run() {
            mHalView.post(new Runnable (){

                @Override
                public void run() {
                    mHalView.setFlashAmp(mAmp, 10f, 300);
                    mHalView.mTalking=false;
                }

            });

        }

    }
}
