package com.purduecs.kiwi.oneup;

/* Challenge Creation Activity : Empty Activity

 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengePostWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import org.w3c.dom.Text;

public class ChallengeCreationActivity extends AppCompatActivity {

    public static final int REQUEST_POST = 12;
    public static final String EXTRA_ID = "com.purduecs.kiwi.oneup.extra_id";

    public static Intent intentFor(Context context) {
        return new Intent(context, ChallengeCreationActivity.class);
    }

    TextView nameField;
    TextView descField;
    TextView catField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_creation);
        setUpActionBar();

        nameField = (TextView)findViewById(R.id.challenge_name);
        descField = (TextView)findViewById(R.id.challenge_desc);
        catField = (TextView)findViewById(R.id.challenge_categories);
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge_creation, menu);
        return true;
    }

    public void uploadChallenge(MenuItem menuItem) {
        OneUpWebRequest r = new ChallengePostWebRequest(getChallenge(), new RequestHandler<String>() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_ID, response);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                }
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Failed to post challenge");
            }
        });
    }

    private Challenge getChallenge() {
        Challenge c = new Challenge();
        c.name = nameField.getText().toString();
        c.owner = "loeb";
        c.desc = descField.getText().toString();
        c.categories = catField.getText().toString().split(",");
        return c;
    }
}
