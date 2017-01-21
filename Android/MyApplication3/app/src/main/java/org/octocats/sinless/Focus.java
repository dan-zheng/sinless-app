package org.octocats.sinless;


import android.os.Bundle;

/**
 * Created by utkyb on 1/21/2017.
 */

public class Focus
{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        HoloSeekBar picker = (HoloSeekBar) findViewById(R.id.picker);
        picker.getValue();
    }
}