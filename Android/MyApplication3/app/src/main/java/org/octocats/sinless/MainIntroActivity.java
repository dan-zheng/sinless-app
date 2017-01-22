package org.octocats.sinless;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import org.octocats.sinless.fragments.LoginFragment;
import org.octocats.sinless.fragments.PledgeFragment;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainIntroActivity extends IntroActivity {

    SharedPreferences mSharedPreferences;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        userId = mSharedPreferences.getString("userId", null);

        if(userId != null){
            Intent i = new Intent(MainIntroActivity.this, Timeline.class);
            startActivity(i);
        }


        addSlide(new FragmentSlide.Builder()
                .background(R.color.primary)
                .backgroundDark(R.color.primary_dark)
                .fragment(LoginFragment.newInstance())
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.primary)
                .backgroundDark(R.color.primary_dark)
                .fragment(R.layout.check_layout, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.primary)
                .backgroundDark(R.color.primary_dark)
                .fragment(PledgeFragment.newInstance())
                .build());



    }
}