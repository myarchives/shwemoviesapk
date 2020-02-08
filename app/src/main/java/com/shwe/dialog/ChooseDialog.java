package com.shwe.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.ornolfr.ratingview.RatingView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.item.ItemMovie;
import com.shwe.movies.MovieDetailsActivity;
import com.shwe.movies.MyApplication;
import com.shwe.movies.R;
import com.shwe.movies.SimpleVideoPlayer;
import com.shwe.util.API;
import com.shwe.util.Constant;
import com.shwe.util.NetworkUtils;
import com.shwe.util.XDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ChooseDialog extends BaseDialog {
    TextView title,btn_hd,btn_sd;
    Boolean condition;//true for play and false for download
    String hd_url,sd_url;


    XGetter xGetter, xGetterDownload;
    ProgressDialog progressDialog;
    XDownloader xDownloader;
    XModel current_Xmodel = null;
    Context context;
    Activity activity;
    ItemMovie itemMovie;


    public ChooseDialog(Context context,Activity activity,Boolean condition,String hd_url,String sd_url,ItemMovie itemMovie) {
        super(context);
        this.condition=condition;
        this.hd_url=hd_url;
        this.sd_url=sd_url;
        this.context=context;
        this.activity=activity;
        this.itemMovie=itemMovie;




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose);
        title=findViewById(R.id.tv_co_title);
        btn_hd=findViewById(R.id.btn_hd);
        btn_sd=findViewById(R.id.btn_sd);

        title.setText(R.string.choose_one);

        if (NetworkUtils.isConnected(context)) {
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
                                String url = model.getUrl();
                                //If google drive video you need to set cookie for play or download
                                String cookie = model.getCookie();
                            }
                            doneExoPlaly(vidURL.get(0));
//                            multipleQualityDialog(vidURL, true);
                        } else doneExoPlaly(null);
                    } else {
                        doneExoPlaly(vidURL.get(0));
                    }
                }

                @Override
                public void onError() {
                    progressDialog.dismiss();
                    doneExoPlaly(null);
                }
            });

            xGetterDownload = new XGetter(context);
            xGetterDownload.onFinish(new XGetter.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                    progressDialog.dismiss();
                    if (multiple_quality) {
                        if (vidURL != null) {

                            //This video you can choose qualities
                            for (XModel model : vidURL) {
                                String url = model.getUrl();
                                //If google drive video you need to set cookie for play or download
                                String cookie = model.getCookie();
                            }
                            doneDonwload(vidURL.get(0));
//                            multipleQualityDialog(vidURL, false);
                        } else doneDonwload(null);
                    } else {
                        doneDonwload(vidURL.get(0));
                    }
                }

                @Override
                public void onError() {
                    progressDialog.dismiss();
                    doneDonwload(null);
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

        if(condition){
            btn_hd.setText(R.string.hd_play);
            btn_sd.setText(R.string.sd_play);
            btn_hd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    letPlay(hd_url);
                    dismiss();
                }
            });
            btn_sd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    letPlay(sd_url);
                    dismiss();
                }
            });
        }
        else{
            btn_hd.setText(R.string.hd_download);
            btn_sd.setText(R.string.sd_download);
            btn_hd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    letDownload(hd_url);
                    dismiss();
                }
            });
            btn_sd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    letDownload(sd_url);
                    dismiss();
                }
            });

        }

    }


    private void doneExoPlaly(XModel xModel) {
        String url = null;
        if (xModel != null) {
            url = xModel.getUrl();
            Intent intent = new Intent(context, SimpleVideoPlayer.class);
            intent.putExtra("url", xModel.getUrl());
            intent.putExtra("item_movie", itemMovie);
            //If google drive you need to put cookie
            if (xModel.getCookie() != null) {
                intent.putExtra("cookie", xModel.getCookie());
            }
            context.startActivity(intent);
        }

    }

    private void doneDonwload(XModel xModel) {
        String url = null;
        if (xModel != null) {
            url = xModel.getUrl();
            downloadDialog(xModel);
        }
    }


    private void letPlay(String url) {
        if (NetworkUtils.isConnected(context)) {
            progressDialog.show();
            xGetter.find(url);
        }
    }

    private void letDownload(String url) {

        if (NetworkUtils.isConnected(context)) {
            progressDialog.show();
            xGetterDownload.find(url);
        }
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

    private void downloadFile(XModel xModel) {
        current_Xmodel = xModel;
        if (checkPermissions()) {
            xDownloader.download(current_Xmodel, context.getResources().getString(R.string.save_folder_name), itemMovie);
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
    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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

}
