package org.octocats.sinless;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

/**
 * Created by nisarg on 21/1/17.
 */

public class Focus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        HoloCircleSeekBar picker = (HoloCircleSeekBar) findViewById(R.id.picker);
        picker.getValue();
    }
}
