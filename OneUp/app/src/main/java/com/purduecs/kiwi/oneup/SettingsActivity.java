package com.purduecs.kiwi.oneup;

/* Settings Activity

 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.facebook.login.LoginManager;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected String TAG = "OneUP";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ////////////////////////SETUP TOOLBAR/////////////////////////////////
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        ////////////////////////SETUP NAV DRAWER///////////////////////////////
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ////////////////////////SETUP NAV VIEW/////////////////////////////////
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_settings);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            this.onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_settings);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void fb_logout (View v) {
        final Intent intent = new Intent(this, LoginActivity.class);
        SharedPreferences preferences;
        String USERID = "fb_user_id";

        ////////////////////////SHARED PREFS/////////////////////////////
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString(USERID, "not_found");
        Log.d(TAG, String.format("Logging out name is " + name));

        if(name != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            name = preferences.getString(USERID, "not_found");
            Log.d(TAG, String.format("Logging out name after clear is is " + name));
            LoginManager.getInstance().logOut();
            startActivity(intent);
        }
    }
}
