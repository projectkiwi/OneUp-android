package com.purduecs.kiwi.oneup;

/* Login Activity : Login Activity. LAUNCHER.

 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    protected SharedPreferences preferences;

    protected String TAG = "OneUP";
    protected String USERID = "fb_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        final Intent intent = new Intent(this, NewsfeedActivity.class);

        ////////////////////////SHARED PREFS/////////////////////////////
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString(USERID, "not_found");
        Log.d(TAG, String.format("USERID is " + name));
        if(name != null && !name.equals("not_found")) {
            startActivity(intent);
        }
        else {

            ////////////////////////HIDE ACTION BAR/////////////////////////////
            try {
                getSupportActionBar().hide();
            } catch (Exception e) {
                Log.e(TAG, String.format("No Action Bar. Error : " + e.getMessage()));
            }

            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.fb_login_button);

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    //TODO: Send to Backend.
                    Log.d(TAG, String.format(
                                    "User ID: " + loginResult.getAccessToken().getUserId()
                                            + " Token: " + loginResult.getAccessToken().getToken())
                    );
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(USERID, String.format("" + loginResult.getAccessToken().getUserId()));
                    editor.commit();
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

