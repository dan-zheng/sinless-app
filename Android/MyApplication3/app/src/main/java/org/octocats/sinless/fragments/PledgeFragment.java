package org.octocats.sinless.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aigestudio.wheelpicker.WheelPicker;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import org.octocats.sinless.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nisarg on 21/1/17.
 */

public class PledgeFragment extends SlideFragment{

    public PledgeFragment() {
        // Required empty public constructor
    }

    public static PledgeFragment newInstance() {
        return new PledgeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.pledge_layout, container, false);

        WheelPicker wheelPicker = (WheelPicker) root.findViewById(R.id.amount);
        List<Integer> data = new ArrayList<>();
        for (int i = 25; i < 200; i+=5)
            data.add(i);
        wheelPicker.setData(data);

        return root;
    }
}
