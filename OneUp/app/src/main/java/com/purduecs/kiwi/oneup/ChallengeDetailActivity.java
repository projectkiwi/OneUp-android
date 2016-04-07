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
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.purduecs.kiwi.oneup.models.Attempt;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.views.CenterIconButton;
import com.purduecs.kiwi.oneup.web.BookmarkChallengeWebRequest;
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
    private AttemptAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Challenge mChallenge;
    ImageView mMedia;
    TextView mTitle, mWinner, mDesc, mCategories;
    CenterIconButton mLikeButton;
    CenterIconButton mBookmarkButton;

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
        mBookmarkButton = (CenterIconButton) findViewById(R.id.bookmark_button);

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

                mBookmarkButton.setChecked(mChallenge.bookmarked);
                mBookmarkButton.setOnClickListener(bookmarkListener);


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
                mLikeButton.setOnClickListener(likeListener);

                String categories = "";
                for (int i = 0; i < mChallenge.categories.length; i++) {
                    categories += mChallenge.categories[i];
                    categories += ", ";
                }
                categories = categories.substring(0, categories.length()-2);
                mCategories.setText(categories);


                mAdapter = new AttemptAdapter(mChallenge.attempts);
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

    private View.OnClickListener attemptClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String)v.findViewById(R.id.attempt_like_button).getTag();
            Attempt a = new Attempt();
            for (int i = 0; i < mChallenge.attempts.length; i++) {
                if (mChallenge.attempts[i].id.equals(id)) {
                    a = mChallenge.attempts[i];
                    if (i != mAdapter.focusedItem) {
                        int temp = mAdapter.focusedItem;
                        mAdapter.focusedItem = i;
                        mAdapter.notifyItemChanged(temp);
                        mAdapter.notifyItemChanged(i);
                    }
                    break;
                }
            }

            mChallenge.attempt_main = a;

            Glide.with(ChallengeDetailActivity.this)
                    .load(a.gif)
                    .error(R.drawable.doge_with_sunglasses)
                    .into(mMedia);

            mWinner.setText(a.owner);

            // Want to leave the total likes here, not the individual
            if (mLikeButton.isChecked() && !a.has_liked) {
                mChallenge.likes++;
            } else if (!mLikeButton.isChecked() && a.has_liked) {
                mChallenge.likes--;
            }
            mLikeButton.setTextOff(Integer.toString(mChallenge.likes));
            mLikeButton.setTextOn(Integer.toString(mChallenge.likes + 1));
            mLikeButton.setChecked(a.has_liked);

        }
    };

    private View.OnClickListener bookmarkListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompoundButton buttonView = (CompoundButton) v;
                    new BookmarkChallengeWebRequest(mChallenge.id, buttonView.isChecked(), new RequestHandler<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                        }

                        @Override
                        public void onFailure() {
                            Log.d("HEY", "we failed to bookmark the post :(");
                            mBookmarkButton.toggle();
                        }
                    });
                }
            };

    private View.OnClickListener likeListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CompoundButton buttonView = (CompoundButton) v;
                    new LikeWebRequest(mChallenge.attempt_main.id, buttonView.isChecked(), new RequestHandler<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                            mChallenge.attempt_main.has_liked = buttonView.isChecked();
                            mAdapter.notifyItemChanged(mChallenge.attempt_main.place - 1);
                        }

                        @Override
                        public void onFailure() {
                            Log.d("HEY", "we failed to like the post :(");
                            mLikeButton.toggle();
                        }
                    });
                }
            };

    private View.OnClickListener attemptLikeListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CompoundButton buttonView = (CompoundButton) v;
                    new LikeWebRequest((String)buttonView.getTag(), buttonView.isChecked(), new RequestHandler<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                            String id = (String)buttonView.getTag();
                            Attempt a = new Attempt();
                            for (int i = 0; i < mChallenge.attempts.length; i++) {
                                if (mChallenge.attempts[i].id.equals(id)) {
                                    a = mChallenge.attempts[i];
                                    break;
                                }
                            }

                            a.has_liked = buttonView.isChecked();

                            // If it's this one, toggle the button, else update the likes
                            if (id.equals(mChallenge.attempt_main.id)) {
                                mLikeButton.toggle();
                            } else {
                                if (a.has_liked) mChallenge.likes++;
                                else mChallenge.likes--;
                                mLikeButton.setTextOff(Integer.toString(mChallenge.likes));
                                mLikeButton.setTextOn(Integer.toString(mChallenge.likes+1));
                                mLikeButton.setChecked(mLikeButton.isChecked());//update the text
                            }
                        }

                        @Override
                        public void onFailure() {
                            Log.d("HEY", "we failed to like the attempt :(");
                            buttonView.toggle();
                        }
                    });
                }
            };

    public class AttemptAdapter extends RecyclerView.Adapter<AttemptAdapter.ViewHolder> {
        // Start with first item selected
        public int focusedItem = 0;

        private Attempt[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            View mView;
            public ImageView mImageView;
            public TextView mRecord;
            public TextView mWinner;
            public TextView mTime;
            public ToggleButton mLikeButton;
            public ViewHolder(View v) {
                super(v);
                mView = v;
                mImageView = (ImageView)v.findViewById(R.id.image);
                mRecord = (TextView)v.findViewById(R.id.record);
                mWinner = (TextView)v.findViewById(R.id.winner);
                mTime = (TextView)v.findViewById(R.id.time);
                mLikeButton = (ToggleButton)v.findViewById(R.id.attempt_like_button);
                v.setOnClickListener(attemptClickListener);
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
            holder.mWinner.setText(a.owner);
            holder.mTime.setText(a.time);

            holder.mLikeButton.setTag(a.id);
            holder.mLikeButton.setTextOn(Integer.toString(a.likes_num + 1));
            holder.mLikeButton.setTextOff(Integer.toString(a.likes_num));
            holder.mLikeButton.setText(Integer.toString(a.likes_num));
            holder.mLikeButton.setChecked(a.has_liked);
            holder.mLikeButton.setOnClickListener(attemptLikeListener);

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
                    holder.mRecord.setTextColor(getResources().getColor(R.color.darkGreyText));
                    break;
            }

            if (position == focusedItem)
                holder.mView.setBackgroundResource(R.drawable.attempt_item_background_selected);
            else
                holder.mView.setBackgroundResource(R.drawable.attempt_item_background);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
