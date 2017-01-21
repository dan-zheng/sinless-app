package org.octocats.sinless;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    private long timeOfLastText = 0;

    private TextView txtCb;
    private FloatingActionButton focusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        client = new AsyncHttpClient();

        userId = mSharedPreferences.getString("userId", null);
        balance = mSharedPreferences.getInt("balance", 0);
        timeOfLastText = mSharedPreferences.getLong("timeOfLastText", 0);

        txtCb = (TextView) findViewById(R.id.txtCb);
        txtCb.setText("$"+balance);
        focusBtn = (FloatingActionButton) findViewById(R.id.fab_focus);

        focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Focus.class);
                startActivity(i);
            }
        });

        if(userId==null) {
            Intent i = new Intent(Timeline.this, MainIntroActivity.class);
            startActivity(i);
        } else {
            if(balance == 0){
                final int initBalance = mSharedPreferences.getInt("initBalance", 25);
                RequestParams params = new RequestParams();
                params.put("id", userId);
                params.put("key", "balance");
                params.put("value", initBalance);
                client.post(this, URL + "/account/", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.e(TAG, response.toString());
                        balance = initBalance;
                        txtCb.setText("$"+balance);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                        Log.e(TAG, response.toString());
                    }
                });
            }
        }

        try {
            getAllSms();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.view_row, R.id.header_text, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview);

        expandableLayoutListView.setAdapter(arrayAdapter);
    }

    public void getAllSms() throws IOException {
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                String sms = c.getString(c.getColumnIndexOrThrow("body"));
                long date = Long.parseLong(c.getString(c.getColumnIndexOrThrow("date")));
                if (!c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    if (smsContainsCurse(sms.toLowerCase())) {
                        Log.e(TAG, "Date " + c.getString(c.getColumnIndexOrThrow("date")));
                        Log.e(TAG, "Last Text " + timeOfLastText);
                        Log.e(TAG, "Found " + sms);
                        if(date > timeOfLastText){
                            RequestParams params = new RequestParams();
                            params.put("id", userId);
                            params.put("type", "swear");
                            params.put("time", c.getString(c.getColumnIndexOrThrow("date")));
                            client.post(this, URL + "/account/action", params, new JsonHttpResponseHandler(){
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
                }
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }

    }

    public boolean smsContainsCurse(String inputStr) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("curses.txt")));
        String line;
        ArrayList<String> curses = new ArrayList<>();
        while((line=br.readLine()) != null){
            curses.add(line);
        }
        for(int i =0; i < curses.size(); i++) {
            if(inputStr.contains(curses.get(i))) {
                return true;
            }
        }
        return false;
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
