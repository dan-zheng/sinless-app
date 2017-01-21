package org.octocats.sinless.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.heinrichreimersoftware.materialintro.app.SlideFragment;

import org.octocats.sinless.R;

/**
 * Created by nisarg on 21/1/17.
 */

public class LoginFragment extends SlideFragment {

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

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

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

                loginHandler.postDelayed(loginRunnable, 2000);
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
