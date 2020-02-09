package com.shwe.movies;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.adapter.EpiscodeListAdapter;
import com.shwe.item.ItemEpisode;
import com.shwe.util.API;
import com.shwe.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EpiscodeList extends BaseActivity {
    RecyclerView recyclerView;
    ArrayList<ItemEpisode> mListItemEpisode;
    ArrayList<String> hdlinks, sdlinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episcode_list);
        recyclerView = findViewById(R.id.episcode_list_rv);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mListItemEpisode = new ArrayList<>();
        sdlinks = new ArrayList<>();
        hdlinks = new ArrayList<>();
        String season_id = getIntent().getStringExtra("SEASON_ID");
        String series_id = getIntent().getStringExtra("SERIES_ID");
        String label = getIntent().getStringExtra("LABEL");
        TextView tv_label = findViewById(R.id.label);
        tv_label.setText(label);

        getEpisode(season_id, series_id);

    }

    private void getEpisode(String seasonId, String seriesId) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_episodes");
        jsObj.addProperty("series_id", seriesId);
        jsObj.addProperty("season_id", seasonId);

        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //for progress start
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //progress gone

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objJson = jsonArray.getJSONObject(i);
                            ItemEpisode itemEpisode = new ItemEpisode();
                            JSONArray jsonArrayHDLinks = objJson.getJSONArray(Constant.MOVIE_HDLINK);
                            if (jsonArrayHDLinks.length() != 0) {
                                for (int j = 0; j < jsonArrayHDLinks.length(); j++) {
                                    JSONObject objChild = jsonArrayHDLinks.getJSONObject(j);
                                    hdlinks.add(objChild.getString(Constant.URL));
                                }
                            }
                            itemEpisode.setEpisodeHDLink(hdlinks);
                            JSONArray jsonArraySDLinks = objJson.getJSONArray(Constant.MOVIE_SDLINK);
                            if (jsonArraySDLinks.length() != 0) {
                                for (int j = 0; j < jsonArraySDLinks.length(); j++) {
                                    JSONObject objChild = jsonArraySDLinks.getJSONObject(j);
                                    sdlinks.add(objChild.getString(Constant.URL));
                                }
                            }
                            itemEpisode.setGetEpisodeSDLink(sdlinks);
                            itemEpisode.setId(objJson.getString(Constant.EPISODE_ID));
                            itemEpisode.setEpisodeTitle(objJson.getString(Constant.EPISODE_TITLE));
                            itemEpisode.setEpisodePoster(objJson.getString(Constant.EPISODE_POSTER));
                            itemEpisode.setEpisodeUrl(objJson.getString(Constant.EPISODE_URL));
                            itemEpisode.setEpisodeType(objJson.getString(Constant.EPISODE_TYPE));
                            itemEpisode.setPlaying(false);
                            mListItemEpisode.add(itemEpisode);
                        }
                        Log.d("mListItemEpisode", mListItemEpisode.toString());
                        EpiscodeListAdapter episcodeListAdapter = new EpiscodeListAdapter(EpiscodeList.this, EpiscodeList.this, mListItemEpisode);
                        recyclerView.setAdapter(episcodeListAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    } else {

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
}
