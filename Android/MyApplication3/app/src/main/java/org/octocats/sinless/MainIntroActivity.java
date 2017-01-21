package org.octocats.sinless;

import android.os.Bundle;

import com.aigestudio.wheelpicker.WheelPicker;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainIntroActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .fragment(R.layout.check_layout, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorAccent)
                .backgroundDark(R.color.colorAccent)
                .fragment(R.layout.pledge_layout, R.style.AppTheme)
                .build());

        WheelPicker wheelPicker = (WheelPicker) findViewById(R.id.amount);
        List<Integer> data = new ArrayList<>();
        for (int i = 25; i < 200; i+=5)
            data.add(i);
        //wheelPicker.setData(data);
    }
}