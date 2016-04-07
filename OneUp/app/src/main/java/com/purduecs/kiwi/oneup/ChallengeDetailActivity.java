package com.purduecs.kiwi.oneup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purduecs.kiwi.oneup.models.Attempt;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.views.CenterIconButton;
import com.purduecs.kiwi.oneup.web.ChallengeWebRequest;
import com.purduecs.kiwi.oneup.web.LikeWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

public class ChallengeDetailActivity extends AppCompatActivity {

    private static String EXTRA_CHALLENGE_ID = "com.purduecs.kiwiw.challenge_id";

    public static Intent intentFor(Context context, String challengeId) {
        Intent intent = new Intent(context, ChallengeDetailActivity.class);
        intent.putExtra(EXTRA_CHALLENGE_ID, challengeId);
        return intent;
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Challenge mChallenge;
    ImageView mMedia;
    TextView mTitle, mWinner, mDesc, mCategories;
    CenterIconButton mLikeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        setUpActionBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.attempt_history);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMedia = (ImageView) findViewById(R.id.challenge_media);
        mTitle = (TextView) findViewById(R.id.challenge_name);
        mWinner = (TextView) findViewById(R.id.challenge_winner);
        mDesc = (TextView) findViewById(R.id.challenge_desc);
        mCategories = (TextView) findViewById(R.id.challenge_categories);
        mLikeButton = (CenterIconButton) findViewById(R.id.like_button);

        String challengeId = getIntent().getStringExtra(EXTRA_CHALLENGE_ID);

        new ChallengeWebRequest(challengeId, new RequestHandler<Challenge>() {
            @Override
            public void onSuccess(Challenge response) {
                mChallenge = response;
                mTitle.setText(mChallenge.name);
                mWinner.setText(mChallenge.owner);
                mDesc.setText(mChallenge.desc);

                Glide.with(ChallengeDetailActivity.this)
                        .load(mChallenge.image)
                        .error(R.drawable.doge_with_sunglasses)
                        .into(mMedia);


                mLikeButton.setText(Integer.toString(mChallenge.likes));
                mLikeButton.setTextOff(Integer.toString(mChallenge.likes));
                mLikeButton.setTextOn(Integer.toString(mChallenge.likes + 1));
                mLikeButton.setPastLiked(false);
                switch (mChallenge.liked) {
                    case 0:
                        break;
                    case 1:
                        mLikeButton.toggle();
                        break;
                    case 2:
                        mLikeButton.setPastLiked(true);
                        break;
                }
                mLikeButton.setOnCheckedChangeListener(likeListener);

                String categories = "";
                for (int i = 0; i < mChallenge.categories.length; i++) {
                    categories += mChallenge.categories[i];
                    categories += ", ";
                }
                categories = categories.substring(0, categories.length()-2);
                mCategories.setText(categories);

                Attempt[] as = new Attempt[] { new Attempt(1, "https://pbs.twimg.com/profile_images/675404869885276160/6Ybu2ZpU.jpg",
                        3024, "people", "Purdue Hackers", "2 days"), new Attempt(2, "https://pbs.twimg.com/profile_images/675404869885276160/6Ybu2ZpU.jpg",
                        2956, "people", "MHacks", "8 days"), new Attempt(3, "https://pbs.twimg.com/profile_images/675404869885276160/6Ybu2ZpU.jpg",
                        2287, "people", "PennApps", "1 month"), new Attempt(4, "https://pbs.twimg.com/profile_images/675404869885276160/6Ybu2ZpU.jpg",
                        1058, "people", "HackIllinois", "3 months") };
                mAdapter = new AttemptAdapter(as);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Need this so the oncheckedchange listener doesn't loop when it fails
    private boolean failed = false;

    private CompoundButton.OnCheckedChangeListener likeListener =
            new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (failed) { failed = false; return; }
                    new LikeWebRequest(mChallenge.attempt_id, isChecked, new RequestHandler<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                        }

                        @Override
                        public void onFailure() {
                            Log.d("HEY", "we failed to like the post :(");
                            failed = true;
                            mLikeButton.toggle();
                        }
                    });
                }
            };

    public class AttemptAdapter extends RecyclerView.Adapter<AttemptAdapter.ViewHolder> {
        private Attempt[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView mImageView;
            public TextView mRecord;
            public TextView mWinner;
            public TextView mTime;
            public ViewHolder(View v) {
                super(v);
                mImageView = (ImageView)v.findViewById(R.id.image);
                mRecord = (TextView)v.findViewById(R.id.record);
                mWinner = (TextView)v.findViewById(R.id.winner);
                mTime = (TextView)v.findViewById(R.id.time);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public AttemptAdapter(Attempt[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AttemptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.challenge_attempt_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Attempt a = mDataset[position];
            Glide.with(ChallengeDetailActivity.this)
                    .load(a.image)
                    .error(R.drawable.doge_with_sunglasses)
                    .into(holder.mImageView);
            holder.mRecord.setText(a.number + " " + a.desc);
            holder.mWinner.setText(a.winner);
            holder.mTime.setText(a.time);

            switch (a.place) {
                case 1:
                    holder.mRecord.setTextColor(getResources().getColor(R.color.firstGold));
                    break;
                case 2:
                    holder.mRecord.setTextColor(getResources().getColor(R.color.secondSilver));
                    break;
                case 3:
                    holder.mRecord.setTextColor(getResources().getColor(R.color.thirdBronze));
                    break;
                default:
                    //holder.mRecord.setTextColor(getResources().getColor(R.color.firstGold));
                    break;
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
