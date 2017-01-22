package org.octocats.sinless;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by nisarg on 21/1/17.
 */

    public class Focus extends AppCompatActivity {
        String TAG = "Focus";
        public final int[] progressValue = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        //      final HoloCircleSeekBar picker = (HoloCircleSeekBar) findViewById(R.id.picker);
        EditText timer = (EditText) findViewById(R.id.txtTimer);
        TextView tv = (TextView) findViewById(R.id.lblTimer);
        //    picker.getValue();
        final boolean[] isStopped = {true};
        //progressValue[0] = Integer.parseInt(timer.getText().toString());

        final int[] count = {0};
        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) findViewById(R.id.lblTimer);
                FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
                EditText timer = (EditText) findViewById(R.id.txtTimer);
                progressValue[0] = Integer.parseInt(timer.getText().toString());

                Log.e(TAG, "clicked");
                isStopped[0] = !isStopped[0];
                start.setEnabled(false);

                  /*  while (isStopped[0] == false && progressValue[0] != 0) {
                        tv.setText(String.valueOf(progressValue[0]));
                        Log.e(TAG, "inside while");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                        Timer myTimer = new Timer();
                        myTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                            }
                        }, 1000);
                        progressValue[0]--;

               /*     Log.e(TAG, "after timer: " + count[0] + " " + picker.getValue());
                    if (picker.getValue() != 0) {
                        if (count[0] >= 2) {
                            int temp = picker.getValue();
                            picker.setValue(temp - 1);
                            float cur = picker.getRotation();
                            picker.setRotationX(cur - (float)(1/60)*360);
                            count[0] = 0;
                        }
                    }


                    }*/
                TimerTextHelper timerTextHelper = new TimerTextHelper(tv);
                if (isStopped[0] == false) {
                    timerTextHelper.start();

                }
            }

        });

       /*     while(true){
                String s = tv.toString();
                String a="";
                for(int i=0;s.charAt(i)!=':';i++){
                    a+=s.charAt(i);
                }
                int t = Integer.parseInt(a);
                if(t==Integer.parseInt(tv.toString())){
                    timerTextHelper.stop();
                }
            }
        */

        new Thread() {
            public void run() {
                while (true) {
                    if (isStopped[0] == false) {
                        try {
                            Thread.sleep(progressValue[0] * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Focus Session Succcesful!")
                                .setMessage("Good job focusing!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Focus.this, Timeline.class);
                                        i.putExtra("focus", true);
                                        startActivity(i);
                                    }
                                })
                                .show();


                        //  isStopped[0]=true;
                        break;
                    }
                }
            }
        }.start();
        }
    }


