package org.octocats.sinless.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.octocats.sinless.R;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nisarg on 21/1/17.
 */

public class LoginFragment extends SlideFragment {

    private CoordinatorLayout coordinatorLayout;
    private EditText fakeUsername;
    private EditText fakePassword;
    private Button fakeLogin;

    private boolean loggedIn = false;

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

    private final String URL = "http://pal-nat186-94-246.itap.purdue.edu:3000/api";

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
        fakeUsername = (EditText) root.findViewById(R.id.email);
        fakePassword = (EditText) root.findViewById(R.id.password);
        fakeLogin = (Button) root.findViewById(R.id.fakeLogin);

        fakeUsername.setEnabled(!loggedIn);
        fakePassword.setEnabled(!loggedIn);
        fakeLogin.setEnabled(!loggedIn);

        fakeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fakeUsername.setEnabled(false);
                fakePassword.setEnabled(false);
                fakeLogin.setEnabled(false);
                fakeLogin.setText("Loading");

                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("email", fakeUsername.getText().toString());
                paramMap.put("password", fakePassword.getText().toString());

                RequestParams params = new RequestParams(paramMap);

                client.post(getContext(), URL + "/login", params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.e(TAG, response.toString());
                        loginRunnable.run();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject response) {
                        Log.e(TAG, response.toString());
                        fakeLogin.setText("Login");
                        fakeUsername.setEnabled(true);
                        fakePassword.setEnabled(true);
                        fakeLogin.setEnabled(true);
                        try {
                            Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                //loginHandler.postDelayed(loginRunnable, 2000);
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
