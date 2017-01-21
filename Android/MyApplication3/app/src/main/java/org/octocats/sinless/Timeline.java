package org.octocats.sinless;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by utkarsh on 21/1/17.
 */

public class Timeline extends AppCompatActivity{

    private String TAG = "Timeline";

    private final String URL = "http://pal-nat186-94-246.itap.purdue.edu:3000/api";

    private final String[] array = {"Jan 18, 2017", "Jan 19, 2017", "Jan 20, 2017", "Jan 21, 2017"};

    SharedPreferences mSharedPreferences;
    AsyncHttpClient client;

    private String userId;
    private int balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        client = new AsyncHttpClient();

        userId = mSharedPreferences.getString("userId", null);
        balance = mSharedPreferences.getInt("balance", 0);

        if(userId==null) {
            Intent i = new Intent(Timeline.this, MainIntroActivity.class);
            startActivity(i);
        } else {
            if(balance == 0){
                int initBalance = mSharedPreferences.getInt("initBalance", 25);
                RequestParams params = new RequestParams();
                params.put("id", userId);
                params.put("key", "balance");
                params.put("value", initBalance);
                client.post(this, URL + "/account/", params, new JsonHttpResponseHandler(){
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
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.view_row, R.id.header_text, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview);

        expandableLayoutListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout) {
            SharedPreferences sharedPreferences = getApplication().getSharedPreferences("SinLess", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(Timeline.this, MainIntroActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
