package com.purduecs.kiwi.oneup;

/* Newsfeed Activity : Scrolling Activity

 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.purduecs.kiwi.oneup.views.ChallengeListLayout;

public class BookmarksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ChallengeListLayout challengesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        ////////////////////////SETUP TOOLBAR/////////////////////////////////
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bookmarks);
        setSupportActionBar(toolbar);

        ////////////////////////SETUP NAV DRAWER///////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_bookmarks);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////SETUP NAV VIEW/////////////////////////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_bookmarks);
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////REFRESH VIEW///////////////////////////////////
        challengesLayout = (ChallengeListLayout) findViewById(R.id.challenges_layout);

    }

    @Override
    protected void onStop() {
        challengesLayout.onStop();

        super.onStop();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_bookmarks);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_bookmarks);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

