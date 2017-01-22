package org.octocats.sinless;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.octocats.sinless.models.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by utkarsh on 21/1/17.
 */

public class Timeline extends AppCompatActivity{

    private String TAG = "Timeline";

    public final String URL = "http://52.27.130.78:3000/api";

    private HashMap<String, ArrayList<Action>> dataMap = new HashMap<>();
    private ArrayList<String> dates = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;
    DatesAdapter datesAdapter;

    SharedPreferences mSharedPreferences;
    AsyncHttpClient client;

    private String userId;
    private int balance = 0;
    private long timeOfLastText = 0;

    private TextView txtCb;
    private FloatingActionButton focusBtn;
    private FloatingActionButton walkBtn;

    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.getBoolean("focus") == true){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Focus Sesion Succcesful!")
                    .setMessage("Good job focusing!")
                    .show();
        }

        mSharedPreferences = getSharedPreferences("SinLess", Context.MODE_PRIVATE);
        client = new AsyncHttpClient();

        userId = mSharedPreferences.getString("userId", null);
        balance = mSharedPreferences.getInt("balance", 0);
        timeOfLastText = mSharedPreferences.getLong("timeOfLastText", 0);

        txtCb = (TextView) findViewById(R.id.txtCb);
        txtCb.setText("$"+balance);
        focusBtn = (FloatingActionButton) findViewById(R.id.fab_focus);
        walkBtn = (FloatingActionButton) findViewById(R.id.fab_walk);

        focusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Focus.class);
                startActivity(i);
            }
        });
        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Steps.class);
                startActivity(i);
            }
        });

        try {
            getAllSms();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getAllAction();

        arrayAdapter = new ArrayAdapter<>(this, R.layout.view_row, R.id.header_text, dates);
        final ExpandableListView expandableLayoutListView = (ExpandableListView) findViewById(R.id.listview);

        datesAdapter = new DatesAdapter(getApplicationContext(), dataMap, dates);

        expandableLayoutListView.setAdapter(datesAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllAction();
            }
        });

    }

    public void getAllAction() {
        RequestParams params = new RequestParams();
        params.put("id", userId);
        client.post(this, URL + "/user/data", params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, response.toString());
                try {
                    JSONArray data = response.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject dataObj = data.getJSONObject(i);
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
                        // Create a calendar object that will convert the date and time value in milliseconds to date.
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(dataObj.getLong("date"));
                        String dateStr = formatter.format(calendar.getTime());
                        dates.add(dateStr);
                        Log.e(TAG, "date " + dateStr);

                        JSONArray actionsArr = dataObj.getJSONArray("actions");
                        ArrayList<Action> actions = new ArrayList<>();
                        for (int j = 0; j < actionsArr.length(); j++) {
                            JSONObject actionJSON = actionsArr.getJSONObject(j);
                            String actionType = "swear";
                            int amtDed;
                            if(actionJSON.getString("actionType").equals("swear")){
                                actionType = "You swore via SMS";
                                amtDed = 1;
                            } else if (actionJSON.getString("actionType").equals("timer")){
                                actionType = "You were productive";
                                amtDed = -1;
                            }
                            Action action = new Action(actionJSON.getString("_id"), actionJSON.getLong("time"), actionType, actionJSON.getInt("amountDeducted"));
                            actions.add(action);
                            Log.e(TAG, "" + actionJSON.toString());
                        }
                        dataMap.put(dateStr, actions);
                        datesAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                Log.e(TAG, "onFailure");
            }
        });
    }

    public void getAllSms() throws IOException {
        final ProgressDialog progress = new ProgressDialog(Timeline.this);
        progress.setMessage("You get max 3 swears per day");
        progress.setTitle("Checking your SMS");
        progress.show();

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
                                    progress.dismiss();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                                    Log.e(TAG, response.toString());
                                    progress.dismiss();
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
