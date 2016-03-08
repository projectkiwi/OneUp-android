package com.purduecs.kiwi.oneup;

/* Newsfeed Activity : Scrolling Activity

 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.purduecs.kiwi.oneup.views.CardAdapter;
import com.purduecs.kiwi.oneup.web.ChallengesWebRequest;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class NewsfeedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static int REQUEST_SIZE = 1;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    CardAdapter adapter;
    List<Challenge> challenges;

    private String newsFeedType;
    private int lastTab;

    private int numbLoaded;

    Animation rightTabAnimation, leftTabAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        ////////////////////////SETUP TOOLBAR/////////////////////////////////
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        ////////////////////////SETUP TABS/////////////////////////////////
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("New"));
        tabLayout.addTab(tabLayout.newTab().setText("Global"));

        newsFeedType = "popular";
        lastTab = 0;

        numbLoaded = 0;

        leftTabAnimation = AnimationUtils.loadAnimation(this, R.anim.tab_animation_left);
        leftTabAnimation.setAnimationListener(tabAnimationListener);
        rightTabAnimation = AnimationUtils.loadAnimation(this, R.anim.tab_animation_right);
        rightTabAnimation.setAnimationListener(tabAnimationListener);

        //TODO: Actually add functionality
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    newsFeedType = "popular";

                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    newsFeedType = "new";

                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    newsFeedType = "global";
                }

                if (lastTab > tabLayout.getSelectedTabPosition()) {
                    recyclerView.startAnimation(rightTabAnimation);
                } else {
                    recyclerView.startAnimation(leftTabAnimation);
                }

                lastTab = tabLayout.getSelectedTabPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ////////////////////////SETUP NAV DRAWER///////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////SETUP NAV VIEW/////////////////////////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////SETUP RECYCLER VIEW////////////////////////////
        recyclerView = (RecyclerView) findViewById(R.id.newsfeed_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        initializeData();

        ////////////////////////REFRESH VIEW///////////////////////////////////
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        ////////////////////////CLEAN TOOLBAR SCROLL/////////////////////////////
        final View toolbarContainer = findViewById(R.id.toolbar_container);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if ( toolbarContainer.getTranslationY() == 0) {
                        tabLayout.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void initializeData() {

        challenges = new ArrayList<Challenge>();
        adapter = new CardAdapter(this, recyclerView, challenges, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v.findViewById(R.id.card_id);
                startActivity(ChallengeDetailActivity.intentFor(NewsfeedActivity.this,
                        (String) view.getText()));
            }
        }, new CardAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(CardAdapter.FinishedLoadingListener listener) {
                loadMoreContent(listener);
            }
        });
        refreshContent();
        recyclerView.setAdapter(adapter);

    }

    private void refreshContent(){
        numbLoaded = 0;
        new ChallengesWebRequest(newsFeedType, numbLoaded, REQUEST_SIZE, new RequestHandler<ArrayList<Challenge>>() {
            @Override
            public void onSuccess(ArrayList<Challenge> response) {
                challenges = response;
                adapter.resetItems(challenges);
                numbLoaded += challenges.size();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure() {
                Log.e("HEY", "Our challenge webrequest in newsfeed failed");
            }
        });
    }

    private void loadMoreContent(final CardAdapter.FinishedLoadingListener listener) {
        //temp 5 second delay
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ChallengesWebRequest(newsFeedType, numbLoaded, REQUEST_SIZE, new RequestHandler<ArrayList<Challenge>>() {
                    @Override
                    public void onSuccess(ArrayList<Challenge> response) {
                        challenges = response;
                        adapter.addItems(challenges);
                        numbLoaded += challenges.size();
                        listener.finishedLoading();
                    }

                    @Override
                    public void onFailure() {
                        Log.e("HEY", "Our challenge webrequest in newsfeed failed");
                    }
                });
            }
        }, 5000);
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

    public void goToMap(MenuItem menuItem) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void newChallenge(MenuItem menuItem) {
        Intent intent = new Intent(this, ChallengeCreationActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_newsfeed, menu);
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


    private Animation.AnimationListener tabAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            adapter.resetItems(new ArrayList<Challenge>());
            refreshContent();
        }

        @Override
        public void onAnimationStart(Animation animation) {}
        @Override
        public void onAnimationRepeat(Animation animation) {}
    };
}

