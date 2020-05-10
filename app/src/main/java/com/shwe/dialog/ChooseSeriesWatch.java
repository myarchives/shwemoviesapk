package com.shwe.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.item.ItemEpisode;
import com.shwe.movies.R;
import com.shwe.movies.SimpleVideoPlayer;
import com.shwe.util.API;
import com.shwe.util.Constant;
import com.shwe.util.NetworkUtils;
import com.shwe.util.XDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import cz.msebera.android.httpclient.Header;


public class ChooseSeriesWatch {
    XGetter xGetter, xGetterDownload;
    String die_url = "HELLO";
    ProgressDialog progressDialog;
    XDownloader xDownloader;
    Context context;
    Activity activity;
    ItemEpisode itemEpisode;
    LinkedList<String> sdlinklists, hdlinklists;

    public ChooseSeriesWatch(Context context, Activity activity, ItemEpisode itemEpisode) {
        sdlinklists = new LinkedList<>();
        hdlinklists = new LinkedList<>();
        this.context = context;
        this.activity = activity;
        this.itemEpisode = itemEpisode;
        hdlinklists.addAll(this.itemEpisode.getEpisodeHDLink());
        Collections.shuffle(hdlinklists);
    }

    public void onCreate() {
        if (checkInternet()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            xGetter = new XGetter(context);
            xGetter.onFinish(new XGetter.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                    progressDialog.dismiss();
                    if (multiple_quality) {
                        if (vidURL != null) {
                            //This video you can choose qualities
                            for (XModel model : vidURL) {
                                //If google drive video you need to set cookie for play or download
                            }

                            try {
                                multipleQualityDialog(vidURL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else done(null);
                    } else {
                        System.out.println(vidURL.get(0).getUrl());
                        done(vidURL.get(0));
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(context, context.getString(R.string.try_another_link), Toast.LENGTH_LONG).show();
                    sentReport("die" + itemEpisode.getEpisodeTitle() + " episode  url : [" + die_url + "]");
                    letGo();
                    progressDialog.dismiss();
                    done(null);
                }
            });

            xDownloader = new XDownloader(activity);
            xDownloader.OnDownloadFinishedListerner(new XDownloader.OnDownloadFinished() {
                @Override
                public void onCompleted(String path) {

                }
            });


        } else {
            showToast(context.getResources().getString(R.string.conne_msg1));
        }
        letGo();
    }

    private void letGo() {
        if (checkInternet()) {
            progressDialog.show();
            if (hdlinklists.size() > 0) {
                die_url = hdlinklists.get(hdlinklists.size() - 1);
                xGetter.find(hdlinklists.get(hdlinklists.size() - 1));
                hdlinklists.remove(hdlinklists.size() - 1);
            } else {
                Toast.makeText(context, context.getString(R.string.link_die_error), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
    }

    public boolean checkInternet() {
        boolean what;
        if (NetworkUtils.isConnected(context)) {
            what = true;
        } else {
            what = false;
            Toast.makeText(activity, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        return what;
    }

    private void done(XModel xModel) {
        String url = null;
        if (xModel != null) {
            url = xModel.getUrl();
        }
        Intent intent = new Intent(context, SimpleVideoPlayer.class);
        intent.putExtra("url", xModel.getUrl());
        //If google drive you need to put cookie
        if (xModel.getCookie() != null) {
            intent.putExtra("cookie", xModel.getCookie());
        }
        activity.startActivity(intent);
    }





    private void multipleQualityDialog(ArrayList<XModel> model) throws IOException {
        CharSequence[] name = new CharSequence[model.size()];

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality();
//            name[i] = model.get(i).getQuality() + " " + getRemoteFileSize(model.get(i).getUrl(),model.get(i).getCookie());
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Quality!")
                .setItems(name, (dialog, which) -> done(model.get(which)))
                .setPositiveButton("OK", null);
        builder.show();
    }


    private void sentReport(String report) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_report");
        jsObj.addProperty("post_id", itemEpisode.getId());
        jsObj.addProperty("report", report);
        jsObj.addProperty("type", "series");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objComment = jsonArray.getJSONObject(0);
                    if (objComment.getString(Constant.SUCCESS).equals("1")) {
                        String strMessage = objComment.getString(Constant.MSG);
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

    public void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

}
