package org.octocats.sinless;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

import org.octocats.sinless.fragments.LoginFragment;
import org.octocats.sinless.fragments.PledgeFragment;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainIntroActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.primary)
                .backgroundDark(R.color.primary_dark)
                .fragment(LoginFragment.newInstance())
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.accent)
                .backgroundDark(R.color.accent)
                .fragment(R.layout.check_layout, R.style.AppTheme)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(R.color.primary)
                .backgroundDark(R.color.primary_dark)
                .fragment(PledgeFragment.newInstance())
                .build());



    }
}