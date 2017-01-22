package org.octocats.sinless;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

/**
 * Created by utkyb on 22/1/17.
 */

public class Steps extends AppCompatActivity {
    String TAG = "Steps";
    public final int[] progressValue = new int[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        EditText timer = (EditText) findViewById(R.id.txtTimer);
        TextView tv = (TextView) findViewById(R.id.lblTimer);


        final int[] count = {0};
        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) findViewById(R.id.lblTimer);
                EditText timer = (EditText) findViewById(R.id.txtTimer);


            }

        });

       
    }
}
