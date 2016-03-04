package com.purduecs.kiwi.oneup;

/* Challenge Detail Activity : Empty Activity

 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengeWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

public class ChallengeDetailActivity extends AppCompatActivity {

    private static String EXTRA_CHALLENGE_ID = "com.purduecs.kiwiw.challenge_id";

    public static Intent intentFor(Context context, String challengeId) {
        Intent intent = new Intent(context, ChallengeDetailActivity.class);
        intent.putExtra(EXTRA_CHALLENGE_ID, challengeId);
        return intent;
    }

    Challenge mChallenge;
    ImageView mMedia;
    TextView mTitle, mWinner, mDesc, mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        mMedia = (ImageView) findViewById(R.id.challenge_media);
        mTitle = (TextView) findViewById(R.id.challenge_name);
        mWinner = (TextView) findViewById(R.id.challenge_winner);
        mDesc = (TextView) findViewById(R.id.challenge_desc);
        mCategories = (TextView) findViewById(R.id.challenge_categories);

        String challengeId = getIntent().getStringExtra(EXTRA_CHALLENGE_ID);

        new ChallengeWebRequest(challengeId, new RequestHandler<Challenge>() {
            @Override
            public void onSuccess(Challenge response) {
                mChallenge = response;
                mTitle.setText(mChallenge.name);
                mWinner.setText(mChallenge.owner);
                mDesc.setText(mChallenge.desc);

                String categories = "";
                for (int i = 0; i < mChallenge.categories.length; i++) {
                    categories += mChallenge.categories[i];
                    categories += ", ";
                }
                categories = categories.substring(0, categories.length()-2);
                mCategories.setText(categories);
            }

            @Override
            public void onFailure() {
            }
        });
    }
}
