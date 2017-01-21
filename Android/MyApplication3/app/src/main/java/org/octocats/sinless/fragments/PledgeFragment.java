package org.octocats.sinless.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aigestudio.wheelpicker.WheelPicker;
import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.octocats.sinless.R;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.ContentValues.TAG;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

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

    WheelPicker wheelPicker;
    AsyncHttpClient client;
    SharedPreferences sharedPreferences;

    int balance = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.pledge_layout, container, false);
        client = new AsyncHttpClient();

        sharedPreferences = getContext().getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        wheelPicker = (WheelPicker) root.findViewById(R.id.amount);
        List<Integer> data = new ArrayList<>();
        for (int i = 25; i < 200; i+=5)
            data.add(i);
        wheelPicker.setData(data);
        wheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                editor.putInt("initBalance", (Integer)data);
                editor.commit();
                balance = (Integer)data;
            }
        });
        return root;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        String userId = sharedPreferences.getString("userId", null);

        RequestParams params = new RequestParams();
        params.put("id", userId);
        params.put("key", "balance");
        params.put("value", balance);
        client.post(getContext(), URL + "/account/", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                Log.e(TAG, response.toString());
            }
        });
    }
}
