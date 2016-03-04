package com.purduecs.kiwi.oneup;

/* Challenge Detail Activity : Empty Activity

 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.purduecs.kiwi.oneup.models.Challenge;

public class ChallengeDetailActivity extends AppCompatActivity {

    private static String EXTRA_CHALLENGE_ID = "com.purduecs.kiwiw.challenge_id";

    public static Intent intentFor(Context context, String challengeId) {
        Intent intent = new Intent(context, ChallengeDetailActivity.class);
        intent.putExtra(EXTRA_CHALLENGE_ID, challengeId);
        return intent;
    }

    Challenge mChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        String challengeId = getIntent().getStringExtra(EXTRA_CHALLENGE_ID);
    }
}
