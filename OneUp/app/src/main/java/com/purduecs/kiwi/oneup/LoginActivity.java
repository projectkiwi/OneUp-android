package com.purduecs.kiwi.oneup;

/* Login Activity : Login Activity. LAUNCHER.

 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    protected CallbackManager callbackManager;
    protected LoginButton loginButton;

    protected String TAG = "OneUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);


        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e(TAG, String.format("No Action Bar. Error : " + e.getMessage()));
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);

        final Intent intent = new Intent(this, NewsfeedActivity.class);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                //TODO: Send to Backend. Store in SharedPrefs
                Log.d(TAG, String.format(
                                "User ID: " + loginResult.getAccessToken().getUserId()
                                        + " Token: " + loginResult.getAccessToken().getToken())
                );
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FB Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, String.format("FB Error" + exception.getMessage()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

