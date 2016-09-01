package ru.megazlo.apnea;

import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Map;

import ru.megazlo.apnea.entity.TableApnea;
import ru.megazlo.apnea.frag.*;

@EActivity(R.layout.activity_main)
public class MainAct extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {

    private final static String FRAGMENT_TAG = "CURRENT_FRAG_TAG";

    public InfoFragment infoFragment = new InfoFragment_();
    private TableListFragment tabList = new TableListFragment_();
    private TableDetailFragment tabDet = new TableDetailFragment_();

    @ViewById(R.id.fab)
    public FloatingActionButton fab;
    @ViewById(R.id.toolbar)
    public Toolbar toolbar;
    @ViewById(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewById(R.id.nav_view)
    NavigationView navigationView;

    @AfterViews
    protected void init() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        final Map<String, ?> map = PreferenceManager.getDefaultSharedPreferences(this).getAll();
        if (map.size() < 5) {
            PreferenceManager.setDefaultValues(this, R.xml.pref_main, true);
        }

        setFragment(tabList);
        tabList.setOnItemClickListener(this);
    }

    @Click(R.id.fab)
    public void clickFab(View view) {
        FabClickListener listener = (FabClickListener) getVisibleFragment();
        listener.clickByContext(view);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (tabList != getVisibleFragment()) {
            BackPressHandler handler = (BackPressHandler) getVisibleFragment();
            handler.backPressed();
            setFragment(tabList);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApneaForeService_.intent(getApplication()).start();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();// Handle navigation view item clicks here.
        if (id == R.id.nav_settings) {
            setFragment(new SettingsFragment());
        } else if (id == R.id.nav_info) {
            setFragment(infoFragment);
        } else if (id == R.id.nav_tables) {
            setFragment(tabList);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TableApnea table = (TableApnea) parent.getItemAtPosition(position);
        tabDet.setTableApnea(table);
        setFragment(tabDet);
    }

    public Fragment getVisibleFragment() {
        return getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    public void setFragment(Fragment fragment) {
        FabClickListener listener = (FabClickListener) fragment;
        listener.modifyToContext(fab);
        getFragmentManager().beginTransaction().replace(R.id.main_content, fragment, FRAGMENT_TAG).commit();
    }
}
