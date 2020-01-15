package com.neoend.gms.check;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.neoend.gms.check.feature.FeatureFragment;
import com.neoend.gms.check.version.VersionCheckFragment;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        VersionCheckFragment.OnFragmentInteractionListener,
        FeatureFragment.OnFragmentInteractionListener {

    enum NavMenu {
        VER_CHECK, FEATURE
    }

    private static final String TAG = "gms";
    private NavMenu mNavMenu;
    private FragmentManager mFragmentManager;
    private VersionCheckFragment mVersionCheckFragment;
    private FeatureFragment mFeatureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        mVersionCheckFragment = VersionCheckFragment.newInstance(null, null);
        mFeatureFragment = FeatureFragment.newInstance(null, null);

        mNavMenu = NavMenu.VER_CHECK;
        openVersionCheck();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
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

        if (id == R.id.nav_version_info) {
            if (mNavMenu != NavMenu.VER_CHECK) {
                openVersionCheck();
            }
        } else if (id == R.id.nav_feature) {
            if (mNavMenu != NavMenu.FEATURE) {
                openFeature();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void openVersionCheck() {
        mNavMenu = NavMenu.VER_CHECK;
        mFragmentManager.beginTransaction().replace(R.id.contentFragment, mVersionCheckFragment).commit();
    }

    private void openFeature() {
        mNavMenu = NavMenu.FEATURE;
        mFragmentManager.beginTransaction().replace(R.id.contentFragment, mFeatureFragment).commit();
    }
}
