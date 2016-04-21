package com.purduecs.kiwi.oneup;

/* Login Activity : Login Activity. LAUNCHER.

 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.purduecs.kiwi.oneup.web.LoginPostUsernameWebRequest;
import com.purduecs.kiwi.oneup.web.LoginPostWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;
import com.purduecs.kiwi.oneup.web.RequestQueueSingleton;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    protected CallbackManager callbackManager;
    protected LoginButton loginButton;
    protected SharedPreferences preferences;

    protected String TAG = "OneUP";
    protected String USERID = "fb_user_id";
    protected String EMAIL = "fb_email_id";
    protected String AUTH = "fb_auth_token";

    String email = "NOT_FOUND";
    String id = "NOT_FOUND";
    String auth_tok = "NOT_FOUND";

    LoginPostWebRequest webRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        ////////////////////////SHARED PREFS/////////////////////////////
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString(EMAIL, "NOT_FOUND");
        if(email != null && !email.equals("NOT_FOUND")) {
            id = preferences.getString(USERID, "NOT_FOUND");
            auth_tok = preferences.getString(AUTH, "NOT_FOUND");
            checkUser();
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
            loginButton.setReadPermissions(Arrays.asList("email"));

            //--------------VERY BAD PRACTICE. DO NOT TRY AT HOME------------------
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //---------------------------------------------------------------------

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v(TAG, response.toString());

                                    try {
                                        email = object.getString("email");
                                        id = object.getString("id");
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }

                                }});
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    request.setParameters(parameters);
                    request.executeAndWait();

                    auth_tok = loginResult.getAccessToken().getToken();

                    Log.v(TAG, String.format(
                                    "User ID: " + id
 //                                           + " Token: " + auth_tok
                                            + "\nEmail: " + email)
                    );

                    //TODO: What if no email (user registered through phone number and not email?)

                    //Add to Shared Prefs
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(USERID, id);
                    editor.putString(AUTH, auth_tok);
                    editor.putString(EMAIL, email);
                    editor.apply();

                    checkUser();
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

    protected void newUser() {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
    }

    protected void oldUser() {
        Intent intent = new Intent(this, NewsfeedActivity.class);
        startActivity(intent);
    }

    protected void checkUser() {

        webRequest = new LoginPostWebRequest(email, auth_tok, new RequestHandler<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                Log.d(TAG, "response is " + response);
                if(response) {
                    newUser();
                }
                else {
                    oldUser();
                }
            }
            @Override
            public void onFailure() {
                Log.e(TAG, "Something happened in sending login");
            }
        });
        //newUser();
        //oldUser();
    }
}

