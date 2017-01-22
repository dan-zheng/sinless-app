package org.octocats.sinless;

//import com.starboardland.pedometer.*;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by utkyb on 22/1/17.
 */

public class Steps extends AppCompatActivity implements SensorEventListener{
    String TAG = "Steps";
    public final int[] progressValue = new int[1];



    private SensorManager sensorManager;
    public static int count = 0;
    boolean activityRunning;
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_steps);
        //count = (TextView) findViewById(R.id.count);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    */

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            count = (int) event.values[0];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        setContentView(R.layout.activity_steps);

        final EditText a = (EditText) findViewById(R.id.txtTimer);

        TextView tv = (TextView) findViewById(R.id.lblTimer);
        final int[] COUNTER = {CounterActivity.count};
        //Intent i = new Intent(Steps.this, CounterActivity.class);
        //startActivity(i);
        final int[] flag = {0};
        final int diff = count;
        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
                TextView tv = (TextView) findViewById(R.id.lblTimer);
                EditText timer = (EditText) findViewById(R.id.txtTimer);
                int usersGivenSteps =  Integer.parseInt(timer.getText().toString());
                //count = CounterActivity.count;
                Log.e(TAG, String.valueOf(count-118430));
                flag[0] = 1;
                if(usersGivenSteps>(count-118400)){
                    tv.setText("You haven't completed your goal for today. Walk more or your money will be deducted");
                }
                else{
                    tv.setText("Congrats! you have walked enough for today :)");
                }

            }

        });

   /*     new Thread() {
            public void run() {
                while (true) {

                        COUNTER[0] = CounterActivity.count;
                        Intent i = new Intent(Steps.this, Timeline.class);
                        i.putExtra("focus", 1);
                        startActivity(i);


                        //  isStopped[0]=true;
                        break;

                }
            }
        }.start();
*/
    }
}