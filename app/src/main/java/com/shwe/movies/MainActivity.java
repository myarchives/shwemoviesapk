package com.shwe.movies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ixidev.gdpr.GDPRChecker;
import com.shwe.fragment.CategoryFragment;
import com.shwe.fragment.DownloadedFragment;
import com.shwe.fragment.FavouriteTabFragment;
import com.shwe.fragment.HomeFragment;
import com.shwe.fragment.MovieTabFragment;
import com.shwe.fragment.SeriesFragment;
import com.shwe.fragment.SettingFragment;
import com.shwe.util.BannerAds;
import com.shwe.util.Constant;
import com.shwe.util.IsRTL;
import com.shwe.util.LocaleManager;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce = false;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();
        myApplication = MyApplication.getInstance();

        new GDPRChecker()
                .withContext(MainActivity.this)
                .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
                .withPublisherIds(Constant.adMobPublisherId) // your admob account Publisher id
                .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                .check();

        LinearLayout mAdViewLayout = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(this, mAdViewLayout);

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_go_home:
                        HomeFragment homeFragment = new HomeFragment();
                        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                        return true;
                    case R.id.menu_go_movie:
                        MovieTabFragment movieTabFragment = new MovieTabFragment();
                        loadFrag(movieTabFragment, getString(R.string.menu_movie), fragmentManager);
                        return true;
                    case R.id.menu_go_tv_series:
                        SeriesFragment seriesFragment = new SeriesFragment();
                        loadFrag(seriesFragment, getString(R.string.menu_tv_series), fragmentManager);
                        return true;
                    case R.id.menu_go_tv_channel:
                        CategoryFragment categoryFragment = new CategoryFragment();
                        loadFrag(categoryFragment, getString(R.string.menu_tv_channel), fragmentManager);
                        return true;
                    case R.id.menu_go_favourite:
                        FavouriteTabFragment favouriteTabFragment = new FavouriteTabFragment();
                        loadFrag(favouriteTabFragment, getString(R.string.menu_favourite), fragmentManager);
                        return true;
                    case R.id.menu_go_download_manager:
                        DownloadedFragment downloadedFragment = new DownloadedFragment();
                        loadFrag(downloadedFragment, getString(R.string.menu_download_manager), fragmentManager);
                        return true;
                    case R.id.menu_go_setting:
                        SettingFragment settingFragment = new SettingFragment();
                        loadFrag(settingFragment, getString(R.string.menu_setting), fragmentManager);
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    public void setHeader() {
        if (myApplication.getIsLogin() && navigationView != null) {
            View header = navigationView.getHeaderView(0);
            TextView txtHeaderName = header.findViewById(R.id.nav_name);
            TextView txtHeaderEmail = header.findViewById(R.id.nav_email);

            txtHeaderName.setText(myApplication.getUserName());
            txtHeaderEmail.setText(myApplication.getUserEmail());
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        setHeader();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.lan_change){
            String cur_lang = LocaleManager.getLocale(getResources()).toString();
            if (cur_lang.equals("my")){
                setNewLocale(this, LocaleManager.ENGLISH);
            }
            else
                setNewLocale(this, LocaleManager.MYANMAR);


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNewLocale(BaseActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        String cur_lang = LocaleManager.getLocale(getResources()).toString();
        if (cur_lang.equals("en"))
        {
            int drawableResourceId = getResources().getIdentifier("my", "drawable", getPackageName());
            menu.findItem(R.id.lan_change).setIcon(drawableResourceId);
        }
        else{
            int drawableResourceId = getResources().getIdentifier("en", "drawable", getPackageName());
            menu.findItem(R.id.lan_change).setIcon(drawableResourceId);
        }

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchHorizontalActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
