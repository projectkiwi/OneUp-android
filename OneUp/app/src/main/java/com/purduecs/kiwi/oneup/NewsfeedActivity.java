package com.purduecs.kiwi.oneup;

/* Newsfeed Activity : Scrolling Activity

 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.purduecs.kiwi.oneup.cardViewModels.CardAdapter;
import com.purduecs.kiwi.oneup.cardViewModels.Challenge;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class NewsfeedActivity extends OneUpActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    CardAdapter adapter;
    List<Challenge> challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

        /* mTextView = (TextView) findViewById(R.id.tempTextYo);
        Button makeRequest = (Button) findViewById(R.id.makeRequestButton);
        makeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChallengesWebRequest(new RequestHandler<ChallengesWebRequest.Challenge[]>() {
                    @Override
                    public void onSuccess(ChallengesWebRequest.Challenge[] response) {
                        String s = "got from api:\n";
                        for (int i = 0; i < response.length; i++) {
                            s += "challenge: " + response[i].title + "\n";
                        }
                        mTextView.setText(s);
                    }

                    @Override
                    public void onFailure() {
                        mTextView.setText("failed");
                    }
                });
            }
        }); */

        recyclerView = (RecyclerView) findViewById(R.id.newsfeed_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        initializeData();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void initializeData() {

        challenges = new ArrayList<>();
        adapter = new CardAdapter(challenges);
        recyclerView.setAdapter(adapter);

        new ChallengesWebRequest(new RequestHandler<ChallengesWebRequest.Challenge[]>() {
            @Override
            public void onSuccess(ChallengesWebRequest.Challenge[] response) {
                for (int i = 0; i < response.length; i++) {
                    challenges.add(new Challenge(response[i].title, R.drawable.doge_with_sunglasses));
                }
                adapter.notifyItemRangeInserted(challenges.size() - response.length, response.length);
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Our challenge webrequest in newsfeed failed");
            }
        });
        challenges.add(new Challenge("Challenge 1", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 2", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 4", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 5", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 6", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 7", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 8", R.drawable.doge_with_sunglasses));
        /*challenges.add(new Challenge("Challenge 9", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 10", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 11", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 12", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 13", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 14", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 15", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 16", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 17", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Challenge 18", R.drawable.doge_with_sunglasses));*/

    }

    private void refreshContent(){
        new ChallengesWebRequest(new RequestHandler<ChallengesWebRequest.Challenge[]>() {
            @Override
            public void onSuccess(ChallengesWebRequest.Challenge[] response) {
                challenges = new ArrayList<>();
                for (int i = 0; i < response.length; i++) {
                    challenges.add(new Challenge(response[i].title, R.drawable.doge_with_sunglasses));
                }
                adapter = new CardAdapter(challenges);
                recyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Our challenge webrequest in newsfeed failed");
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.one_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_newsfeed) {
            this.onBackPressed();
        } else if (id == R.id.nav_bookmarks) {
            Intent intent = new Intent(this, BookmarksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notifs) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
