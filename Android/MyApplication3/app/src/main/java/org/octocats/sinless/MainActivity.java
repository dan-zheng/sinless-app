package org.octocats.sinless;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        Intent myIntent = new Intent(MainActivity.
                this, MainIntroActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
}
