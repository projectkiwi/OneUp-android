package com.purduecs.kiwi.oneup;

import android.content.Intent;
import android.support.design.widget.NavigationView;

/* Notifications Activity : Scrolling Activity

 */

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.purduecs.kiwi.oneup.cardViewModels.CardAdapter;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest.Challenge;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    CardAdapter adapter;
    List<Challenge> challenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ////////////////////////SETUP TOOLBAR/////////////////////////////////
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);

        ////////////////////////SETUP NAV DRAWER///////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notifications);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////SETUP NAV VIEW/////////////////////////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_notifications);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////SETUP RECYCLER VIEW////////////////////////////
        recyclerView = (RecyclerView) findViewById(R.id.notifications_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        //getContent(0);
        initializeData();

        ////////////////////////REFRESH VIEW///////////////////////////////////
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_notifications);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //getContent(0);
            }
        });

        ////////////////////////CLEAN TOOLBAR SCROLL/////////////////////////////
        final View toolbarContainer = findViewById(R.id.toolbar_container_notifications);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (toolbarContainer.getTranslationY() == 0) {
                        toolbar.setVisibility(View.INVISIBLE);
                    }
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //TODO: Special Card for Notifications
    private void initializeData() {

        challenges = new ArrayList<Challenge>();

        adapter = new CardAdapter(recyclerView, challenges, new CardAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(CardAdapter.FinishedLoadingListener listener) {
                //loadMoreContent(listener);
            }
        });
        recyclerView.setAdapter(adapter);

        /*challenges.add(new Challenge("Notification 1", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 2", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 4", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 5", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 6", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 7", R.drawable.doge_with_sunglasses));
        challenges.add(new Challenge("Notification 8", R.drawable.doge_with_sunglasses));*/
    }

    /*//TODO: Request Notifications by category
    private void getContent(int category){
        new ChallengesWebRequest("", new RequestHandler<ChallengesWebRequest.Challenge[]>() {
            @Override
            public void onSuccess(ChallengesWebRequest.Challenge[] response) {
                challenges = new ArrayList<Challenge>();
                for (int i = 0; i < response.length; i++) {
                    challenges.add(new Challenge(response[i].title, R.drawable.doge_with_sunglasses));
                }
                adapter.resetItems(challenges);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Our challenge webrequest in newsfeed failed");
            }
        });
    }

    private void loadMoreContent(final CardAdapter.FinishedLoadingListener listener) {
        new ChallengesWebRequest("", new RequestHandler<ChallengesWebRequest.Challenge[]>() {
            @Override
            public void onSuccess(ChallengesWebRequest.Challenge[] response) {
                challenges = new ArrayList<Challenge>();
                for (int i = 0; i < response.length; i++) {
                    challenges.add(new Challenge(response[i].title, R.drawable.doge_with_sunglasses));
                }
                adapter.addItems(challenges);
                listener.finishedLoading();
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Our challenge webrequest in newsfeed failed");
            }
        });
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notifications);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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
            Intent intent = new Intent(this, NewsfeedActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bookmarks) {
            Intent intent = new Intent(this, BookmarksActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notifs) {
            this.onBackPressed();
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_notifications);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
