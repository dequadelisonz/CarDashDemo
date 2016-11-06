package code.martin.it.cardashdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import code.martin.it.cardashviewlib.GaugeView;
import code.martin.it.cardashviewlib.GearWheelView;
import code.martin.it.cardashviewlib.GmeterView;
import code.martin.it.cardashviewlib.HalView;
import code.martin.it.cardashviewlib.ThrottleView;


public class MainActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private Button mStartHalBtn, mStopHalBtn, mStartGmeter, mStopGmeter, mUpBtn, mDownBtn;
    private SeekBar mTorqueBar, mThrottleBar;
    private TextView mTorqueTextView, mThrottleTextView;
    private GaugeView mTorqueGauge,mRpmGauge;
    private ThrottleView mThrottleView;
    private HalView mHalView;
    private GmeterView mGmeterView;
    private GearWheelView mGearWheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartHalBtn = (Button) findViewById(R.id.startHalBtn);
        mStartHalBtn.setOnClickListener(this);
        mStopHalBtn = (Button) findViewById(R.id.stopHalBtn);
        mStopHalBtn.setOnClickListener(this);
        mStartGmeter = (Button) findViewById(R.id.startGmeterBtn);
        mStartGmeter.setOnClickListener(this);
        mStopGmeter = (Button) findViewById(R.id.stopGmeterBtn);
        mUpBtn = (Button) findViewById(R.id.upBtn);
        mUpBtn.setOnClickListener(this);
        mDownBtn = (Button) findViewById(R.id.downBtn);
        mDownBtn.setOnClickListener(this);
        mStopGmeter.setOnClickListener(this);
        mTorqueGauge = (GaugeView) findViewById(R.id.torqueGauge);
        mTorqueGauge.setTargetValue(0f);
        mRpmGauge=(GaugeView)findViewById(R.id.rpmGauge);
        mRpmGauge.setTargetValue(0f);
        mTorqueTextView = (TextView) findViewById(R.id.torqueTxv);
        mThrottleTextView = (TextView) findViewById(R.id.throttleTxv);
        mTorqueBar = (SeekBar) findViewById(R.id.torqueSeekBar);
        mTorqueBar.setOnSeekBarChangeListener(this);
        mThrottleBar = (SeekBar) findViewById(R.id.throttleSeekBar);
        mThrottleBar.setOnSeekBarChangeListener(this);
        mThrottleView = (ThrottleView) findViewById(R.id.throttleView);
        mHalView = (HalView) findViewById(R.id.halView);
        mGmeterView = (GmeterView) findViewById(R.id.gMeterView);
        mGearWheelView = (GearWheelView) findViewById(R.id.gearWheelView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.equals(mTorqueBar)) {
            //mTorqueGauge.setTargetValue((float) i);
            mTorqueGauge.setAnimTarget(i);
            mTorqueTextView.setText(Integer.toString(i));
        } else if (seekBar.equals(mThrottleBar)) {
            mThrottleView.setThrottle(i * 1f);
            mThrottleTextView.setText((Integer.toString(i)));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        if (view.equals(mStartHalBtn)) {
            mHalView.startTalking();
        } else if (view.equals(mStopHalBtn)) {
            mHalView.stopTalking();
        } else if (view.equals(mStartGmeter)) {
            mGmeterView.resume();
        } else if (view.equals(mStopGmeter)) {
            mGmeterView.pause();
        } else if (view.equals(mUpBtn)) {
            mGearWheelView.setCurrentItem(mGearWheelView.getCurrentItem() + 1,true);
        } else if (view.equals(mDownBtn)) {
            mGearWheelView.setCurrentItem(mGearWheelView.getCurrentItem() - 1,true);
        }

    }
}