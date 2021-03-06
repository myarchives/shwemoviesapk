package com.shwe.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.item.ItemEpisode;
import com.shwe.movies.R;
import com.shwe.util.API;
import com.shwe.util.Constant;
import com.shwe.util.NetworkUtils;
import com.shwe.util.XDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static com.shwe.util.NetworkUtils.calculateFileSize;


public class ChooseSeriesDownload {
    int count = 0;
    private XGetter xGetter;
    private String die_url = "";
    private ProgressDialog progressDialog;
    private XDownloader xDownloader;
    private Context context;
    private Activity activity;
    private ItemEpisode itemEpisode;
    private LinkedList<String> videoLinks;
    private XModel current_Xmodel;

    public ChooseSeriesDownload(Context context, Activity activity, ItemEpisode itemEpisode) {
        videoLinks = new LinkedList<>();
        this.context = context;
        this.activity = activity;
        this.itemEpisode = itemEpisode;
        videoLinks.addAll(this.itemEpisode.getEpisodeHDLink());
        Collections.shuffle(videoLinks);
    }

    public void show() {
        if (checkInternet()) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            xGetter = new XGetter(context);
            xGetter.onFinish(new XGetter.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                    if (multiple_quality) {
                        if (vidURL != null) {
                            //This video you can choose qualities
                            for (XModel model : vidURL) {
                                //If google drive video you need to set cookie for play or download
                            }

                            try {
                                multipleQualityDialog(vidURL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        } else showToast(context.getString(R.string.link_die_error));
                    } else {
                        System.out.println(vidURL.get(0).getUrl());
                        done(vidURL.get(0));
                    }
                }

                @Override
                public void onError() {
                    letGo();
                }
            });

            xDownloader = new XDownloader(activity);
            xDownloader.OnDownloadFinishedListerner(path -> {

            });


        } else {
            showToast(context.getResources().getString(R.string.conne_msg1));
        }
        letGo();
    }

    private void letGo() {
        if (checkInternet()) {
            progressDialog.show();
            if (videoLinks.size() > 0) {
                if (count > 0) {
                    showToast(context.getString(R.string.try_another_link));
                    sentReport("die" + itemEpisode.getEpisodeTitle() + " episode  url : [" + die_url + "]");
                }
                count++;
                String curUrl = videoLinks.get(videoLinks.size() - 1);
                die_url = curUrl;
                videoLinks.remove(videoLinks.size() - 1);
                xGetter.find(curUrl);
            } else {
                showToast(context.getString(R.string.link_die_error));
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
        downloadDialog(xModel);
    }

    private void downloadDialog(XModel xModel) {
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(context);
        builder.setTitle("Notice!")
                .setDescription("Choose your downloader")
                .setStyle(Style.HEADER_WITH_ICON)
                .setIcon(R.drawable.right)
                .withDialogAnimation(true)
                .setPositiveText("Built in downloader")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        downloadFile(xModel);
                    }
                })
                .setNegativeText("ADM")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        downloadWithADM(xModel);
                    }
                });
        MaterialStyledDialog dialog = builder.build();
        dialog.show();
    }

    private void multipleQualityDialog(ArrayList<XModel> model) throws ExecutionException, InterruptedException {
        CharSequence[] name = new CharSequence[model.size()];
        List<String> fileSizes = new GetFileSizeAsync().execute(model).get();

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality() + "  (" + fileSizes.get(i) + ")";

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

    private void downloadFile(XModel xModel) {
        current_Xmodel = xModel;
        if (checkPermissions()) {
            xDownloader.download(current_Xmodel, context.getResources().getString(R.string.save_folder_name), itemEpisode);
        }
    }

    private boolean checkPermissions() {
        int storage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1000);
            return false;
        }
        return true;
    }

    public boolean appInstalledOrNot(String str) {
        try {
            context.getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void downloadWithADM(XModel xModel) {
        boolean appInstalledOrNot = appInstalledOrNot("com.dv.adm");
        boolean appInstalledOrNot2 = appInstalledOrNot("com.dv.adm.pay");
        boolean appInstalledOrNot3 = appInstalledOrNot("com.dv.adm.old");
        String str3;
        if (appInstalledOrNot || appInstalledOrNot2 || appInstalledOrNot3) {
            if (appInstalledOrNot2) {
                str3 = "com.dv.adm.pay";
            } else if (appInstalledOrNot) {
                str3 = "com.dv.adm";
            } else {
                str3 = "com.dv.adm.old";
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(xModel.getUrl()), "application/x-mpegURL");
                intent.setPackage(str3);
                if (xModel.getCookie() != null) {
                    intent.putExtra("Cookie", xModel.getCookie());
                    intent.putExtra("Cookies", xModel.getCookie());
                    intent.putExtra("cookie", xModel.getCookie());
                    intent.putExtra("cookies", xModel.getCookie());
                }

                context.startActivity(intent);
                return;
            } catch (Exception e) {
                return;
            }
        }
        str3 = "com.dv.adm";
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + str3)));
        } catch (ActivityNotFoundException e2) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + str3)));
        }
    }

    public void showToast(String msg) {
        Toasty.info(activity, msg, Toast.LENGTH_SHORT, true).show();
    }

    private class GetFileSizeAsync extends AsyncTask<List<XModel>, String, List<String>> {
        @Override
        protected List<String> doInBackground(List<XModel>... xModels) {
            List<String> file_sizes = new ArrayList<>();
            for (XModel x : xModels[0]) {
                if (x.getUrl() != null) {
                    try {
                        URLConnection connection = new URL(x.getUrl()).openConnection();
                        if (x.getCookie() != null) {
                            connection.setRequestProperty("Cookie", x.getCookie());
                        }
                        connection.connect();
                        file_sizes.add(calculateFileSize(connection.getContentLength()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return file_sizes;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<String> s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

}
