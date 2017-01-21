package org.octocats.sinless;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        userId = mSharedPreferences.getString("userId", null);

        if(userId != null){
            Intent i = new Intent(MainActivity.this, Timeline.class);
            startActivity(i);
        } else {
            Intent i = new Intent(MainActivity.this, MainIntroActivity.class);
            startActivity(i);
        }
    }
}
