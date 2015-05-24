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
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import code.martin.it.wheelview.WheelView;
import code.martin.it.wheelview.adapters.ArrayWheelAdapter;

public class GearWheelView   extends WheelView {


    private String mGears[];
    private Context context;
    private int mGearsRes=0;

    public GearWheelView(Context context) {
        super(context);
        initView();

    }

    public GearWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        initView();

    }

    public GearWheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        initView();
    }


    private void init(Context context,AttributeSet attrs) {
        this.context= context;
        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.CardashViewLib);

        mGearsRes=a.getResourceId(R.styleable.CardashViewLib_gearWheelRangeRes,R.array.gearWheelDefault);
        initView();
    }

    private void initView() {
        mGears=getResources().getStringArray(mGearsRes);
        setViewAdapter(new GearArrayAdapter(getContext(), mGears, 0));
        setCurrentItem(0);
        setVisibleItems(3);
    }

    /* (non-Javadoc)
     * @see com.martin.viewslib.wheel.widget.WheelView#setCurrentItem(int, boolean)
     */
    @Override
    public void setCurrentItem(int index, boolean animated) {
        index=(index<0)?0:index;
        index=(index>mGears.length)?mGears.length:index;
        super.setCurrentItem(index, animated);
    }


    private class GearArrayAdapter extends ArrayWheelAdapter<String> {

        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        public GearArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(35);
            setTextColor(0xFFFF8400);
        }


        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }

    }



}
