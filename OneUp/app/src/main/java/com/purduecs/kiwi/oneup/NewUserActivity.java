package com.purduecs.kiwi.oneup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends AppCompatActivity {

    static String username;
    private String TAG = "OneUP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
    }

    public void goNext(View v) {
        EditText username = (EditText) findViewById(R.id.editText);
        if(username.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show();
        }
        else {
            this.username = username.getText().toString();
            Log.d(TAG, "Got username as " + this.username);
            Intent intent = new Intent(this, NewUserProfileActivity.class);
            startActivity(intent);
        }
    }
}
