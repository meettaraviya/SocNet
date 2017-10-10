package com.lab.dbis.socnet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Bundle bundle;
    private String SessionID;
    private LogoutUserTask logoutUserTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SessionID = getIntent().getStringExtra("SessionID");
        bundle = new Bundle();
        bundle.putString("SessionID",SessionID);
        bundle.putString("location", "SeePosts");
        ViewPostFragment newFragment = new ViewPostFragment();
        newFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_placeholder, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//         Handle action bar item clicks here. The action bar will
//         automatically handle clicks on the Home/Up button, so long
//         as you specify a parent activity in AndroidManifest.xml.
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

        if(id == R.id.menu_item_home){
            bundle.putString("location", "SeePosts");
            ViewPostFragment newFragment = new ViewPostFragment();
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.menu_item_myposts) {
            bundle.putString("location", "SeeMyPosts");
            ViewPostFragment newFragment = new ViewPostFragment();
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.menu_item_newpost) {
            NewPostFragment newFragment = new NewPostFragment();
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.menu_item_search) {
            SearchFragment newFragment = new SearchFragment();
            newFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_placeholder, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.menu_item_logout) {
            if (logoutUserTask == null) {
                logoutUserTask = new LogoutUserTask();
                logoutUserTask.execute((Void) null);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class LogoutUserTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String,String> paramsMap = new HashMap<>();
            RequestHandler requestHandler = new RequestHandler();
            requestHandler.setSessionID(SessionID);
            Log.i("SessionID", SessionID);
            JSONObject response = requestHandler.handle(getString(R.string.base_url)+"Logout","POST", paramsMap);
            try {

                return response.getBoolean("status");

            } catch (JSONException e) {
                e.printStackTrace();
                //ViewPostFragment.this.toast("Server error", Toast.LENGTH_SHORT);
            } catch (NullPointerException e){
                e.printStackTrace();
                //ViewPostFragment.this.toast("Not connected to internet", Toast.LENGTH_SHORT);
            }
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                Log.i("Logout","done");
            }
            logoutUserTask = null;
        }

        @Override
        protected void onCancelled() {
            logoutUserTask = null;
        }
    }
}
