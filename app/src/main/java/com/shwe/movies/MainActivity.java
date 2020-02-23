package com.shwe.movies;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixidev.gdpr.GDPRChecker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.fragment.ChannelFragment;
import com.shwe.fragment.DownloadedFragment;
import com.shwe.fragment.FavouriteTabFragment;
import com.shwe.fragment.HomeFragment;
import com.shwe.fragment.MovieTabFragment;
import com.shwe.fragment.SeriesFragment;
import com.shwe.fragment.SettingFragment;
import com.shwe.util.API;
import com.shwe.util.BannerAds;
import com.shwe.util.Constant;
import com.shwe.util.IsRTL;
import com.shwe.util.LocaleManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

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
        if (!checkPermissions()) {
            return;
        }

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

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.menu_go_home:
                    HomeFragment homeFragment1 = new HomeFragment();
                    loadFrag(homeFragment1, getString(R.string.menu_home), fragmentManager);
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
                    Bundle bundle = new Bundle();
                    bundle.putString("Id", "3");
                    ChannelFragment channelFragment = new ChannelFragment();
                    channelFragment.setArguments(bundle);
                    loadFrag(channelFragment, getString(R.string.menu_channel), fragmentManager);
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

    private boolean checkPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1000);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
                Toast.makeText(this, "HELLO", Toast.LENGTH_LONG).show();
            } else {
                checkPermissions();
                Toast.makeText(this, "You need to allow this permission!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void checkLicense() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_app_details");
        if (myApplication.getIsLogin()) {
            jsObj.addProperty("user_id", myApplication.getUserId());
        } else {
            jsObj.addProperty("user_id", "");
        }
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    if (objJson.has(Constant.STATUS)) {
                        Toast.makeText(MainActivity.this, getString(R.string.something_went), Toast.LENGTH_SHORT).show();
                    } else {
                        String packageName = objJson.getString("package_name");
                        Constant.isBanner = objJson.getBoolean("banner_ad");
                        Constant.isInterstitial = objJson.getBoolean("interstital_ad");
                        Constant.adMobBannerId = objJson.getString("banner_ad_id");
                        Constant.adMobInterstitialId = objJson.getString("interstital_ad_id");
                        Constant.adMobPublisherId = objJson.getString("publisher_id");
                        Constant.AD_COUNT_SHOW = objJson.getInt("interstital_ad_click");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }

        });
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

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                Intent intent = new Intent(MainActivity.this, SearchHorizontalActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
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

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}
