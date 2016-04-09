package com.purduecs.kiwi.oneup.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.purduecs.kiwi.oneup.ChallengeDetailActivity;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest;
import com.purduecs.kiwi.oneup.web.OneUpWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 4/4/16.
 */
public class ChallengeListLayout extends SwipeRefreshLayout {

    private static int REQUEST_SIZE = 10;
    private static String TAG = "OneUP";

    Activity mContext;
    String mChallengeType;

    RecyclerView recyclerView;
    ChallengesAdapter adapter;
    List<Challenge> challenges;

    private int numbLoaded;

    OneUpWebRequest mWebRequest;

    public ChallengeListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mContext = (Activity)context;
        mChallengeType = context.obtainStyledAttributes(attrs,
                R.styleable.challenges_list).getString(R.styleable.challenges_list_type);

        numbLoaded = 0;

        ////////////////////////SETUP RECYCLER VIEW////////////////////////////
        recyclerView = new RecyclerView(context);
        recyclerView.setMinimumHeight(300);
        recyclerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        initializeData();

        ////////////////////////REFRESH VIEW///////////////////////////////////
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    public void onStop() {
        if (mWebRequest != null) mWebRequest.cancelRequest();
    }

    public void setChallengeType(String type) {
        mChallengeType = type;
    }

    public String getChallengeType() {
        return mChallengeType;
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        recyclerView.addOnScrollListener(listener);
    }

    public void refreshContent(){
        numbLoaded = 0;
        adapter.resetItems(new ArrayList<Challenge>());
        loadMoreContent(new ChallengesAdapter.FinishedLoadingListener() {
            @Override
            public void finishedLoading() {
                ChallengeListLayout.this.setRefreshing(false);
            }
        });
    }

    private void initializeData() {

        challenges = new ArrayList<Challenge>();
        adapter = new ChallengesAdapter(mContext, recyclerView, challenges, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v.findViewById(R.id.card_id);
                mContext.startActivity(ChallengeDetailActivity.intentFor(mContext,
                        (String) view.getText()));
            }
        }, new ChallengesAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(ChallengesAdapter.FinishedLoadingListener listener) {
                loadMoreContent(listener);
            }
        });
        refreshContent();
        recyclerView.setAdapter(adapter);

    }

    private void loadMoreContent(final ChallengesAdapter.FinishedLoadingListener listener) {
        mWebRequest = new ChallengesWebRequest(mChallengeType, numbLoaded, REQUEST_SIZE, new RequestHandler<ArrayList<Challenge>>() {
            @Override
            public void onSuccess(ArrayList<Challenge> response) {
                challenges = response;
                adapter.addItems(challenges);
                numbLoaded += challenges.size();
                listener.finishedLoading();
                mWebRequest = null;
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Our challenge webrequest in newsfeed failed");
            }
        });
    }

}
