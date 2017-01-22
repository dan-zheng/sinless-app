package org.octocats.sinless;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nisarg on 21/1/17.
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;
    String userId;

    public static String URL= "http://pal-nat186-94-246.itap.purdue.edu:3000/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        userId = mSharedPreferences.getString("userId", null);

        AsyncHttpClient client = new AsyncHttpClient();

        if(userId != null){
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("id",userId);
            RequestParams params = new RequestParams(paramMap);

            client.post(this, MainActivity.URL + "/user/id", params, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.e("Main", response.toString());
                    SharedPreferences sharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    try {
                        JSONObject user = response.getJSONObject("user");
                        editor.putString("userId", user.getString("_id"));
                        JSONObject account = user.getJSONObject("account");
                        editor.putLong("timeOfLastText", account.getLong("timeOfLastText"));
                        editor.commit();
                        Log.e("MAIN", "saved "+account.get("timeOfLastText"));
                        if(account.getInt("balance") > 0){
                            editor.putInt("balance", account.getInt("balance"));
                            editor.commit();
                            Intent i = new Intent(MainActivity.this, Timeline.class);
                            startActivity(i);
                        } else {
                            Intent i = new Intent(MainActivity.this, MainIntroActivity.class);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable t, JSONArray resArr) {
                    JSONObject response = null;
                    try {
                        response = resArr.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Intent i = new Intent(MainActivity.this, Timeline.class);
            startActivity(i);
        } else {
            Intent i = new Intent(MainActivity.this, MainIntroActivity.class);
            startActivity(i);
        }
    }
}
