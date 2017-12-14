package com.aziz.anees.appfit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity  extends AppCompatActivity implements SensorEventListener
{
    public static final String MyPREFERENCES = "MyData" ;
    public static final float DistanceRan = 0 ;
    public static final float calories = 0;
    public static final int goal = 0;
    public static final int Weight = 0;


    public int pStatus = -1,percent=0,pStatusprev=-999;

    String toast1="";
    Button rstbtn,startbtn,setgoalbtn,savebtn,resetAll;
    private Handler handler = new Handler();
    TextView tv;
    TextView no_of_steps,prev_run,cal,curdis;
    EditText dailygoaltext,weight;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;

    ProgressBar mProgress;
    int stepsAdded=0,startflag=0,maxBar=100,y;
    float ps,x=0,mb,distanceRan=0,currentDistance=0;
    float temp=0;

   // private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount=0.2f;
        getWindow().setAttributes(lp);
        startflag=0;
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rstbtn=(Button)findViewById(R.id.reset);
        startbtn=(Button)findViewById(R.id.start);
        savebtn=(Button)findViewById(R.id.save);
        setgoalbtn=(Button)findViewById(R.id.setgoal);
        resetAll=(Button)findViewById(R.id.resetAll);


    /*    mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5523787502380809/5983618575");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });

      */
        no_of_steps = (TextView) findViewById(R.id.steps);
        cal=(TextView) findViewById(R.id.calories);
        tv = (TextView) findViewById(R.id.tv);
        curdis = (TextView) findViewById(R.id.curdistance);

        dailygoaltext = (EditText) findViewById(R.id.dailygoaltext);
        weight = (EditText) findViewById(R.id.weight);

        prev_run = (TextView) findViewById(R.id.prev);
         mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);

        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(maxBar); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

       SharedPreferences sharedPreferences=getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);

        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        distanceRan=sharedPreferences1.getFloat("DistanceRan", 0);
        y = (sharedPreferences1.getInt("Weight", 0));
        cal.setText("Total Calories burnt:"+String.format("%.2f",sharedPreferences1.getFloat("calories", 0))+"cal");
        weight.setText(""+y);

       // weight2.setText(String.valueOf(y));

        // prev_run.setText(String.valueOf(stepsAdded));
        //RESET
        rstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pStatus=0;
                currentDistance=0;
                distanceRan=0;
                no_of_steps.setText("Steps: " + 0);
                curdis.setText("Distance(m): " + 0);
                mProgress.setProgress(pStatus);
                tv.setText(0 + "%");
            //    prev_run.setText("0");
            }
        });
        //StartButton
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startflag++;
                if(startflag%2!=0) {
                    startbtn.setText("Stop");
                }
                else
                {
                    startbtn.setText("Start");
                }
              }
        });
        //Set Goal
        setgoalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setMax(Integer.parseInt(dailygoaltext.getText().toString())); // Maximum Progress
               maxBar= Integer.parseInt(dailygoaltext.getText().toString());
            }
        });
        //Calculate calories
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(weight.getText().toString()!="0")
                {
                   temp=(float)(sharedPreferences.getFloat("DistanceRan",0)* 0.00202*Integer.parseInt(weight.getText().toString()));
                    cal.setText("Total calories burned:"+String.format("%.2f",temp)+"cal");
                }
                else
                {
                    cal.setText("Put Weight first");
                }
                y=Integer.parseInt(weight.getText().toString());
                editor.putInt("Weight",y);
                editor.putFloat("calories",temp);
                editor.apply();

            }
        });
        //resetAll
        resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat("DistanceRan",0);
                editor.putFloat("calories",0);
                editor.putInt("goal",0);
                editor.putInt("Weight",0);
                editor.commit();
                prev_run.setText("Total Distance Ran:0.0 metres");
                weight.setText("0");
                cal.setText("Total Calories burnt:0 cal");
                no_of_steps.setText("Steps:0");
                tv.setText("0%");
                curdis.setText("Distance(m):"+0);
                mProgress.setProgress(0);
                pStatus=0;
                distanceRan=0;
                currentDistance=0;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepCounterSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mStepDetectorSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(dailygoaltext.getText().toString()!=null)
        {
            maxBar=Integer.parseInt(dailygoaltext.getText().toString());
            mProgress.setMax(maxBar);
        }
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;
        if (values.length > 0)
        {
            value = (int) values[0];
            if (startflag % 2 != 0)
            {
                pStatus++;
                stepsAdded++;
                currentDistance=(float)((pStatus)*0.762);
                mb=maxBar;
                distanceRan+=0.762;
                percent=(int)(currentDistance/mb*100);
            }
        }
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER)
        {
            mProgress.setProgress((int)currentDistance);
            tv.setText(percent + "%");
            no_of_steps.setText("Steps: " + pStatus);
            curdis.setText("Distance(m):"+currentDistance);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            no_of_steps.setText("Steps: " + pStatus);
            mProgress.setProgress((int)currentDistance);
            tv.setText(percent + "%");
            curdis.setText("Distance(m):"+currentDistance);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (pStatus != 0 && pStatus != pStatusprev) {
            x = (float)(0.762 + sharedPreferences.getFloat("DistanceRan", 0));
            pStatusprev = pStatus;
            stepsAdded = 0;
            distanceRan=0;
        } else
            x = (sharedPreferences.getFloat("DistanceRan", 0));

        editor.putFloat("DistanceRan", x);
        editor.commit();
        prev_run.setText(" Total Distance Ran:"+String.format("%.2f",x)+"metres");
        if (currentDistance >= maxBar) {
            mProgress.setProgress(0);   // Main Progress
            pStatus = 0;
            toast1="Congrats you have completed your goal of "+maxBar+" metres";
            Toast.makeText(getApplicationContext(),toast1,Toast.LENGTH_LONG).show();
        }
    }

}
