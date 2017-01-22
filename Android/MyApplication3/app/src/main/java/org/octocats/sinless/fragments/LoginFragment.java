package org.octocats.sinless.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.octocats.sinless.R;
import org.octocats.sinless.Timeline;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nisarg on 21/1/17.
 */

public class LoginFragment extends SlideFragment {

    private CoordinatorLayout coordinatorLayout;
    private EditText email;
    private EditText password;
    private EditText fname;
    private EditText lname;
    private EditText cpassword;
    private Button fakeLogin;
    private Button signupBtn;
    private TextView switchView;

    private boolean loggedIn = false;
    private boolean signup = false;

    private Handler loginHandler = new Handler();

    private Runnable loginRunnable = new Runnable() {
        @Override
        public void run() {
            fakeLogin.setText("Success");

            loggedIn = true;
            updateNavigation();
            nextSlide();
        }
    };

    public LoginFragment() {
        // Required empty public constructor
    }

    private final String URL = "http://52.27.130.78:3000/api";

    private final String TAG = "LoginFragment";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        final AsyncHttpClient client = new AsyncHttpClient();

        coordinatorLayout = (CoordinatorLayout) root.findViewById(R.id.coordinatorLayout);
        email = (EditText) root.findViewById(R.id.email);
        password = (EditText) root.findViewById(R.id.password);
        fname = (EditText) root.findViewById(R.id.fname);
        lname = (EditText) root.findViewById(R.id.lname);
        cpassword = (EditText) root.findViewById(R.id.cpassword);
        fakeLogin = (Button) root.findViewById(R.id.login);
        switchView = (TextView) root.findViewById(R.id.switchViews);
        signupBtn = (Button) root.findViewById(R.id.signup);

        fname.setVisibility(View.GONE);
        lname.setVisibility(View.GONE);
        cpassword.setVisibility(View.GONE);
        signupBtn.setVisibility(View.GONE);

        switchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!signup){
                    fname.setVisibility(View.VISIBLE);
                    lname.setVisibility(View.VISIBLE);
                    cpassword.setVisibility(View.VISIBLE);
                    fakeLogin.setVisibility(View.GONE);
                    signupBtn.setVisibility(View.VISIBLE);
                    switchView.setText("Login?");
                    signup = true;
                } else {
                    fname.setVisibility(View.GONE);
                    lname.setVisibility(View.GONE);
                    cpassword.setVisibility(View.GONE);
                    fakeLogin.setVisibility(View.VISIBLE);
                    signupBtn.setVisibility(View.GONE);
                    switchView.setText("Signup?");
                    signup = false;
                }
            }
        });

        email.setEnabled(!loggedIn);
        password.setEnabled(!loggedIn);
        fakeLogin.setEnabled(!loggedIn);

        fakeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setEnabled(false);
                password.setEnabled(false);
                fakeLogin.setEnabled(false);
                fakeLogin.setText("Loading");

                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("email", email.getText().toString());
                paramMap.put("password", password.getText().toString());

                RequestParams params = new RequestParams(paramMap);

                client.post(getContext(), URL + "/login", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.e(TAG, response.toString());
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SinLess", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            JSONObject user = response.getJSONObject("user");
                            editor.putString("userId", user.getString("_id"));
                            JSONObject account = user.getJSONObject("account");
                            editor.putLong("timeOfLastText", account.getLong("timeOfLastText"));
                            editor.commit();
                            Log.e(TAG, "saved "+account.get("timeOfLastText"));
                            if(account.getInt("balance") > 0){
                                editor.putInt("balance", account.getInt("balance"));
                                editor.commit();
                                Intent i = new Intent(getActivity(), Timeline.class);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loginRunnable.run();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable t, JSONArray resArr) {
                        JSONObject response = null;
                        try {
                            response = resArr.getJSONObject(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, response.toString());
                        fakeLogin.setText("Login");
                        email.setEnabled(true);
                        password.setEnabled(true);
                        fakeLogin.setEnabled(true);
                        try {
                            Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(email.getText().toString().equals("") || fname.getText().toString().equals("") || lname.getText().toString().equals("") || password.getText().toString().equals("") || cpassword.getText().toString().equals("") || !cpassword.getText().toString().equals(password.getText().toString()))) {
                    email.setEnabled(false);
                    password.setEnabled(false);
                    fname.setEnabled(false);
                    lname.setEnabled(false);
                    cpassword.setEnabled(false);
                    signupBtn.setEnabled(false);
                    fakeLogin.setText("Loading");

                    HashMap<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("firstName", fname.getText().toString());
                    paramMap.put("lastName", lname.getText().toString());
                    paramMap.put("email", email.getText().toString());
                    paramMap.put("password", password.getText().toString());
                    paramMap.put("confirmPassword", cpassword.getText().toString());


                    RequestParams params = new RequestParams(paramMap);

                    client.post(getContext(), URL + "/signup", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.e(TAG, response.toString());
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("SinLess", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            try {
                                JSONObject user = response.getJSONObject("user");
                                editor.putString("userId", user.getString("_id"));
                                JSONObject account = user.getJSONObject("account");
                                editor.putLong("timeOfLastText", account.getLong("timeOfLastText"));
                                editor.commit();
                                Log.e(TAG, "saved "+account.get("timeOfLastText"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loginRunnable.run();

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                            Log.e(TAG, response.toString());
                            fakeLogin.setText("Login");
                            email.setEnabled(true);
                            password.setEnabled(true);
                            fakeLogin.setEnabled(true);
                            try {
                                Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        loginHandler.removeCallbacks(loginRunnable);
        super.onDestroy();
    }

    @Override
    public boolean canGoForward() {
        return loggedIn;
    }
}
