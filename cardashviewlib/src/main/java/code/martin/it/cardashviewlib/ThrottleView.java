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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class ThrottleView  extends View {

    private final String TAG=this.getClass().getSimpleName();

    private static final int[] SHADER_COLORS=
            {Color.argb(0, 255, 255, 255),
            Color.argb(120,255, 137, 0),
            Color.rgb(255, 137, 0),
            Color.rgb(255, 40, 0)};
    private static final float[] SHADER_GRADIENT=
            {0f,
            0.3f,
            0.7f,
            0.99f
    };

    private int[] mShaderColors;
    private float[] mShaderGradient;

    private RectF mThrottleRectF;

    private Bitmap mBackGroundBitmap;
    private Paint mThrottlePaint,mBackgroundPaint;

    private Float mThrottle=0f;
    private Float mPeak=1f;


    public ThrottleView(Context context) {
        super(context);
        initGraphics();
    }

    public ThrottleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initGraphics();
    }

    public ThrottleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        initAttrs(attrs);
        initGraphics();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CardashViewLib);
        try {
            mPeak=a.getFloat(R.styleable.CardashViewLib_throttlePeak, mPeak);
            int mShaderColorsId = a.getResourceId(R.styleable.CardashViewLib_throttleShaderColors, 0);
            int mShaderGradientId = a.getResourceId(R.styleable.CardashViewLib_throttleShaderGradient, 0);
            //mShaderColors=(mShaderColorsId==0)?SHADER_COLORS:getResources().getIntArray(mShaderColorsId);
            if (mShaderColorsId !=0){
                String[] as=getResources().getStringArray(mShaderColorsId);
                mShaderColors=new int[as.length];
                for(int i=0;i<as.length;i++){
                    mShaderColors[i]=Color.parseColor(as[i]);
                }
            }else mShaderColors=SHADER_COLORS;
            if (mShaderGradientId !=0) {
                String[] as=getResources().getStringArray(mShaderGradientId);
                mShaderGradient=new float[as.length];
                for(int i=0;i<as.length;i++) {
                    mShaderGradient[i]=Float.parseFloat(as[i]);
                }
            } else mShaderGradient=SHADER_GRADIENT;
        }
        finally {
            a.recycle();
        }
    }



    private void initGraphics(){

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setFilterBitmap(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (null != mBackGroundBitmap) {
            // Let go of the old background
            mBackGroundBitmap.recycle();
        }
        // Create a new background according to the new width and height
        mBackGroundBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(mBackGroundBitmap);
        final float scale = Math.min(getWidth(), getHeight());
        canvas.scale(scale, scale);
        canvas.translate((scale == getHeight()) ? ((getWidth() - scale) / 2) / scale : 0
                , (scale == getWidth()) ? ((getHeight() - scale) / 2) / scale : 0);


        mThrottleRectF=new RectF(0f,0f,getWidth(),getHeight());

        mThrottlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThrottlePaint.setFilterBitmap(true);

        mThrottlePaint.setStyle(Paint.Style.FILL);
        mThrottlePaint.setShader(
                new LinearGradient(0.0f,0f,getWidth(),0,mShaderColors,mShaderGradient,Shader.TileMode.CLAMP));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackGroundBitmap!=null) {
            canvas.drawBitmap(mBackGroundBitmap,0,0,mBackgroundPaint);
        }

        //int w = getWidth();
        //float tx = w*mThrottle/mPeak;
        float tx = mThrottle/mPeak;
        canvas.save();
        canvas.scale(tx, 1);

        canvas.drawRoundRect(mThrottleRectF,10f,10f, mThrottlePaint);
        canvas.restore();


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


    public Float getThrottle() {
        return mThrottle;
    }


    public void setThrottle(Float throttle) {
        this.mThrottle = throttle;
        invalidate();
    }


    public Float getPeak() {
        return mPeak;
    }


    public void setPeak(Float peak) {
        this.mPeak = peak;
        invalidate();
    }


}
