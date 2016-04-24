package com.purduecs.kiwi.oneup;

/* Profile Activity : Empty Activity

 */

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.purduecs.kiwi.oneup.views.ChallengeListLayout;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    private int lastTab;
    Animation rightTabAnimation, leftTabAnimation;

    ChallengeListLayout profileLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileLayout = (ChallengeListLayout) findViewById(R.id.profile_layout);


        ////////////////////////SETUP TOOLBAR/////////////////////////////////
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);

        ////////////////////////SETUP NAV DRAWER///////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profile);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////SETUP NAV VIEW/////////////////////////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_profile);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////SETUP TABS/////////////////////////////////
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_profile);
        tabLayout.addTab(tabLayout.newTab().setText("Challenges"));
        tabLayout.addTab(tabLayout.newTab().setText("Liked"));
        tabLayout.addTab(tabLayout.newTab().setText("Watching"));

        lastTab = 0;

        leftTabAnimation = AnimationUtils.loadAnimation(this, R.anim.tab_animation_left);
        leftTabAnimation.setAnimationListener(tabAnimationListener);
        rightTabAnimation = AnimationUtils.loadAnimation(this, R.anim.tab_animation_right);
        rightTabAnimation.setAnimationListener(tabAnimationListener);

        //TODO: Actually add functionality
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    profileLayout.setChallengeType("popular");

                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    profileLayout.setChallengeType("new");

                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    profileLayout.setChallengeType("global");
                }

                if (lastTab > tabLayout.getSelectedTabPosition()) {
                    profileLayout.startAnimation(rightTabAnimation);
                } else {
                    profileLayout.startAnimation(leftTabAnimation);
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

        ////////////////////////CLEAN TOOLBAR SCROLL/////////////////////////////
        final View toolbarContainer = findViewById(R.id.toolbar_container_profile);
        profileLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (toolbarContainer.getTranslationY() == 0) {
                        tabLayout.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profile);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
        } else if (id == R.id.nav_notifs) {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            this.onBackPressed();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_profile);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Animation.AnimationListener tabAnimationListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            profileLayout.refreshContent();
        }

        @Override
        public void onAnimationStart(Animation animation) {}
        @Override
        public void onAnimationRepeat(Animation animation) {}
    };
}
