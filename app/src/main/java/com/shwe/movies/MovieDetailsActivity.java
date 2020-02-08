package com.shwe.movies;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.ornolfr.ratingview.RatingView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.adapter.CommentAdapter;
import com.shwe.adapter.HomeMovieAdapter;
import com.shwe.cast.Casty;
import com.shwe.cast.MediaData;
import com.shwe.db.DatabaseHelper;
import com.shwe.dialog.ChooseDialog;
import com.shwe.dialog.DialogUtil;
import com.shwe.dialog.RateDialog;
import com.shwe.fragment.ChromecastScreenFragment;
import com.shwe.fragment.EmbeddedImageFragment;
import com.shwe.fragment.ExoPlayerFragment;
import com.shwe.fragment.ReportFragment;
import com.shwe.item.ItemComment;
import com.shwe.item.ItemMovie;
import com.shwe.util.API;
import com.shwe.util.BannerAds;
import com.shwe.util.Constant;
import com.shwe.util.Events;
import com.shwe.util.GlobalBus;
import com.shwe.util.IsRTL;
import com.shwe.util.NetworkUtils;
import com.shwe.util.RvOnClickListener;
import com.shwe.util.XDownloader;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieDetailsActivity extends BaseActivity implements RateDialog.RateDialogListener {
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytParent;
    WebView webView;
    RatingView ratingView;
    LinearLayout textReport;
    TextView  textCategory, textRate,  textRelViewAll, textComViewAll, textNoComment, textCount;
    ImageView imageEditRate, imageFav,imageCover;
    RecyclerView rvRelated, rvComment;
    ItemMovie itemMovie;
    ArrayList<ItemMovie> mListItemRelated;
    ArrayList<ItemComment> mListItemComment;
    HomeMovieAdapter homeMovieAdapter;
    CommentAdapter commentAdapter;
    String Id;
    LinearLayout lytRelated;
    EditText editTextComment;
    ProgressDialog pDialog;
    MyApplication myApplication;
    DatabaseHelper databaseHelper;
    private FragmentManager fragmentManager;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    private int playerHeight;
    boolean isFullScreen = false;
    boolean isPlayerIsYt = false;
    private YouTubePlayer youTubePlayer;
    public boolean isYouTubePlayerFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    private Casty casty;

    XGetter xGetter, xGetterDownload;
    ProgressDialog progressDialog;
    String org;
    EditText edit_query;
    XDownloader xDownloader;
    XModel current_Xmodel = null;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        IsRTL.ifSupported(this);
        GlobalBus.getBus().register(this);

        FadingEdgeLayout feRecent = findViewById(R.id.feRecent);
        IsRTL.changeShadowInRtl(this, feRecent);
        mAdViewLayout = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        casty = Casty.create(this)
                .withMiniController();

        myApplication = MyApplication.getInstance();
        databaseHelper = new DatabaseHelper(this);
        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }
        mListItemRelated = new ArrayList<>();
        mListItemComment = new ArrayList<>();
        itemMovie = new ItemMovie();

        pDialog = new ProgressDialog(this);
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytParent = findViewById(R.id.lytParent);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        webView = findViewById(R.id.webView);
        ratingView = findViewById(R.id.ratingView);
        editTextComment = findViewById(R.id.editText_comment_md);

        textCategory = findViewById(R.id.textCategory);
        textRate = findViewById(R.id.textRate);
        textReport = findViewById(R.id.textReport);
        textRelViewAll = findViewById(R.id.textRelViewAll);
        textComViewAll = findViewById(R.id.textComViewAll);
        textNoComment = findViewById(R.id.textView_noComment_md);
        textCount = findViewById(R.id.textViews);
        imageCover=findViewById(R.id.imageCover_md);


        editTextComment.setClickable(true);
        editTextComment.setFocusable(false);

        rvRelated = findViewById(R.id.rv_related);
        rvComment = findViewById(R.id.rv_comment);

        imageEditRate = findViewById(R.id.imageEditRate);
        imageFav = findViewById(R.id.imageFav);
        webView.setBackgroundColor(Color.TRANSPARENT);

        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);

        rvComment.setHasFixedSize(true);
        rvComment.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        rvComment.setFocusable(false);
        rvComment.setNestedScrollingEnabled(false);

        BannerAds.ShowBannerAds(this, mAdViewLayout);

        if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
            getDetails();

            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            xGetter = new XGetter(this);
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

            xGetterDownload = new XGetter(this);
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

            xDownloader = new XDownloader(this);
            xDownloader.OnDownloadFinishedListerner(new XDownloader.OnDownloadFinished() {
                @Override
                public void onCompleted(String path) {

                }
            });
        } else {
            showToast(getString(R.string.conne_msg1));
        }


    }

    private void multipleQualityDialog(ArrayList<XModel> model, boolean status) {
        CharSequence[] name = new CharSequence[model.size()];

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Quality!")
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (status)
                            doneExoPlaly(model.get(which));
                        else
                            doneDonwload(model.get(which));
                    }
                })
                .setPositiveButton("OK", null);
        builder.show();
    }


    private void doneExoPlaly(XModel xModel) {
        String url = null;
        if (xModel != null) {
            url = xModel.getUrl();
            Intent intent = new Intent(getApplicationContext(), SimpleVideoPlayer.class);
            intent.putExtra("url", xModel.getUrl());
            intent.putExtra("item_movie", itemMovie);
            //If google drive you need to put cookie
            if (xModel.getCookie() != null) {
                intent.putExtra("cookie", xModel.getCookie());
            }
            startActivity(intent);
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
        org = url;
        if (NetworkUtils.isConnected(this)) {
            progressDialog.show();
            xGetter.find(url);
        }
    }

    private void letDownload(String url) {
        org = url;
        if (NetworkUtils.isConnected(this)) {
            progressDialog.show();
            xGetterDownload.find(url);
        }
    }


    private void downloadDialog(XModel xModel) {
        MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(this);
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
            xDownloader.download(current_Xmodel, getString(R.string.save_folder_name), itemMovie);
        }
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
                downloadFile(current_Xmodel);
            } else {
                checkPermissions();
                Toast.makeText(this, "You need to allow this permission!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public boolean appInstalledOrNot(String str) {
        try {
            getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //Example Download Google Drive Video with ADM
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

                startActivity(intent);
                return;
            } catch (Exception e) {
                return;
            }
        }
        str3 = "com.dv.adm";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + str3)));
        } catch (ActivityNotFoundException e2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + str3)));
        }
    }

    //Example Open Google Drive Video with MX Player
    private void openWithMXPlayer(XModel xModel) {
        boolean appInstalledOrNot = appInstalledOrNot("com.mxtech.videoplayer.ad");
        boolean appInstalledOrNot2 = appInstalledOrNot("com.mxtech.videoplayer.pro");
        String str2;
        if (appInstalledOrNot || appInstalledOrNot2) {
            String str3;
            if (appInstalledOrNot2) {
                str2 = "com.mxtech.videoplayer.pro";
                str3 = "com.mxtech.videoplayer.ActivityScreen";
            } else {
                str2 = "com.mxtech.videoplayer.ad";
                str3 = "com.mxtech.videoplayer.ad.ActivityScreen";
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(xModel.getUrl()), "application/x-mpegURL");
                intent.setPackage(str2);
                intent.setClassName(str2, str3);
                if (xModel.getCookie() != null) {
                    intent.putExtra("headers", new String[]{"cookie", xModel.getCookie()});
                    intent.putExtra("secure_uri", true);
                }
                startActivity(intent);
                return;
            } catch (Exception e) {
                e.fillInStackTrace();
                Log.d("errorMx", e.getMessage());
                return;
            }
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mxtech.videoplayer.ad")));
        } catch (ActivityNotFoundException e2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad")));
        }
    }


    private void getDetails() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_movie");
        jsObj.addProperty("movie_id", Id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        JSONObject objJson;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                lyt_not_found.setVisibility(View.VISIBLE);
                            } else {

                                itemMovie.setId(objJson.getString(Constant.MOVIE_ID));
                                itemMovie.setMovieTitle(objJson.getString(Constant.MOVIE_TITLE));
                                itemMovie.setMovieDesc(objJson.getString(Constant.MOVIE_DESC));
                                itemMovie.setMoviePoster(objJson.getString(Constant.MOVIE_POSTER));
                                itemMovie.setMovieCover(objJson.getString(Constant.MOVIE_COVER));
                                Picasso.get().load(objJson.getString(Constant.MOVIE_COVER)).placeholder(R.drawable.place_holder_slider).into(imageCover);
                                itemMovie.setLanguageName(objJson.getString(Constant.MOVIE_LANGUAGE));
                                itemMovie.setLanguageBackground(objJson.getString(Constant.MOVIE_LANGUAGE_BACK));
                                itemMovie.setLanguageId(objJson.getString(Constant.MOVIE_LANGUAGE_ID));
                                itemMovie.setRateAvg(objJson.getString(Constant.MOVIE_RATE));
                                itemMovie.setMovieUrl(objJson.getString(Constant.MOVIE_URL));

                                itemMovie.setMovieHDLink(objJson.getString(Constant.MOVIE_HDLINK));
                                itemMovie.setMovieSDLink(objJson.getString(Constant.MOVIE_SDLINK));
                                itemMovie.setMovieType(objJson.getString(Constant.MOVIE_TYPE));
                                itemMovie.setTotalViews(objJson.getString(Constant.MOVIE_TOTAL_VIEW));

                                JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_ITEM_ARRAY_NAME);
                                if (jsonArrayChild.length() != 0) {
                                    for (int j = 0; j < jsonArrayChild.length(); j++) {
                                        JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                        ItemMovie item = new ItemMovie();
                                        item.setId(objChild.getString(Constant.MOVIE_ID));
                                        item.setMovieTitle(objChild.getString(Constant.MOVIE_TITLE));
                                        item.setMoviePoster(objChild.getString(Constant.MOVIE_POSTER));
                                        item.setLanguageName(objChild.getString(Constant.MOVIE_LANGUAGE));
                                        item.setLanguageBackground(objChild.getString(Constant.MOVIE_LANGUAGE_BACK));
                                        mListItemRelated.add(item);
                                    }
                                }

                                JSONArray jsonArrayComment = objJson.getJSONArray(Constant.COMMENT_ARRAY);
                                if (jsonArrayComment.length() != 0) {
                                    for (int j = 0; j < jsonArrayComment.length(); j++) {
                                        JSONObject objComment = jsonArrayComment.getJSONObject(j);
                                        ItemComment itemComment = new ItemComment();
                                        itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                                        itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                                        itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                                        mListItemComment.add(itemComment);
                                    }
                                }
                            }
                        }
                        displayData();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        setTitle(itemMovie.getMovieTitle());
        textCategory.setText(itemMovie.getLanguageName());
        textRate.setText(itemMovie.getRateAvg());
        ratingView.setRating(Float.parseFloat(itemMovie.getRateAvg()));
        textCount.setText(getString(R.string.count, NetworkUtils.viewFormat(Integer.parseInt(itemMovie.getTotalViews()))));

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemMovie.getMovieDesc();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #9f9f9f;font-size:14px;margin-left:0px;line-height:1.3}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);




        if (!mListItemRelated.isEmpty()) {
            homeMovieAdapter = new HomeMovieAdapter(MovieDetailsActivity.this, mListItemRelated);
            rvRelated.setAdapter(homeMovieAdapter);

            homeMovieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = mListItemRelated.get(position).getId();
                    Intent intent = new Intent(MovieDetailsActivity.this, MovieDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }

        if (!mListItemComment.isEmpty()) {
            commentAdapter = new CommentAdapter(MovieDetailsActivity.this, mListItemComment);
            rvComment.setAdapter(commentAdapter);
        } else {
            textNoComment.setVisibility(View.VISIBLE);
        }

        editTextComment.setOnClickListener(v -> {

                showCommentBox();

        });

        textComViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailsActivity.this, AllCommentActivity.class);
            intent.putExtra("postId", Id);
            intent.putExtra("postType", "movie");
            startActivity(intent);
        });

        textRelViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, RelatedAllMovieActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("postId", Id);
                intent.putExtra("postCatId", itemMovie.getLanguageId());
                startActivity(intent);
            }
        });

        ratingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    DialogUtil.showRateDialog(MovieDetailsActivity.this, MovieDetailsActivity.this, Id, "movie");


            }
        });

        textReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("postId", Id);
                    bundle.putString("postType", "movie");
                    ReportFragment reportFragment = new ReportFragment();
                    reportFragment.setArguments(bundle);
                    reportFragment.show(getSupportFragmentManager(), reportFragment.getTag());
            }
        });

        isFavourite();
        imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id, DatabaseHelper.TABLE_MOVIE)) {
                    databaseHelper.removeFavouriteById(Id, DatabaseHelper.TABLE_MOVIE);
                    imageFav.setImageResource(R.drawable.ic_favorite_border);
                    showToast(getString(R.string.favourite_remove));
                } else {
                    fav.put(DatabaseHelper.MOVIE_ID, Id);
                    fav.put(DatabaseHelper.MOVIE_TITLE, itemMovie.getMovieTitle());
                    fav.put(DatabaseHelper.MOVIE_POSTER, itemMovie.getMoviePoster());
                    fav.put(DatabaseHelper.MOVIE_LANGUAGE, itemMovie.getLanguageName());
                    fav.put(DatabaseHelper.MOVIE_LANGUAGE_BACK, itemMovie.getLanguageBackground());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_MOVIE, fav, null);
                    imageFav.setImageResource(R.drawable.ic_favorited);
                    showToast(getString(R.string.favourite_add));
                }
            }
        });

        saveRecent();

        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        casty.addMediaRouteMenuItem(menu);
        getMenuInflater().inflate(R.menu.menu_details, menu);
        if (casty.isConnected()) {
            menu.findItem(R.id.menu_cast_play).setVisible(true);
        } else {
            menu.findItem(R.id.menu_cast_play).setVisible(false);
        }
        return true;
    }


    private void playViaCast() {
        if (itemMovie.getMovieType().equals("server_url") || itemMovie.getMovieType().equals("local_url")) {
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(itemMovie.getMovieUrl(), itemMovie.getMovieTitle(), itemMovie.getMovieCover()));

            ChromecastScreenFragment chromecastScreenFragment = new ChromecastScreenFragment();
            fragmentManager.beginTransaction().replace(R.id.playerSection, chromecastScreenFragment).commitAllowingStateLoss();
        } else {
            showToast(getResources().getString(R.string.cast_youtube));
        }
    }

    private MediaData createSampleMediaData(String videoUrl, String videoTitle, String videoImage) {
        return new MediaData.Builder(videoUrl)
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                .setContentType(getType(videoUrl))
                .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                .setTitle(videoTitle)
                .setSubtitle(getString(R.string.app_name))
                .addPhotoUrl(videoImage)
                .build();
    }

    private String getType(String videoUrl) {
        if (videoUrl.endsWith(".mp4")) {
            return "videos/mp4";
        } else if (videoUrl.endsWith(".m3u8")) {
            return "application/x-mpegurl";
        } else {
            return "application/x-mpegurl";
        }
    }


    public void showToast(String msg) {
        Toast.makeText(MovieDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_cast_play:
                playViaCast();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void showCommentBox() {
        final Dialog mDialog = new Dialog(MovieDetailsActivity.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.dialog_comment);
        final EditText edt_comment = mDialog.findViewById(R.id.edt_comment);
        final ImageView img_sent = mDialog.findViewById(R.id.image_sent);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        img_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_comment.getText().toString();
                if (!comment.isEmpty()) {
                    if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                        sentComment(comment);
                        mDialog.dismiss();
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                }
            }
        });
        mDialog.show();
    }

    private void sentComment(String comment) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "user_comment");
        jsObj.addProperty("post_id", Id);
        jsObj.addProperty("user_id", myApplication.getUserId());
        jsObj.addProperty("comment_text", comment);
        jsObj.addProperty("type", "movie");
        jsObj.addProperty("is_limit", "true");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    String strMessage = mainJson.getString(Constant.MSG);
                    showToast(strMessage);

                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() != 0) {
                        mListItemComment.clear();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject objComment = jsonArray.getJSONObject(j);
                            ItemComment itemComment = new ItemComment();
                            itemComment.setUserName(objComment.getString(Constant.COMMENT_NAME));
                            itemComment.setCommentText(objComment.getString(Constant.COMMENT_DESC));
                            itemComment.setCommentDate(objComment.getString(Constant.COMMENT_DATE));
                            mListItemComment.add(itemComment);
                        }
                    }

                    if (!mListItemComment.isEmpty()) {
                        commentAdapter = new CommentAdapter(MovieDetailsActivity.this, mListItemComment);
                        rvComment.setAdapter(commentAdapter);
                        textNoComment.setVisibility(View.GONE);
                    } else {
                        textNoComment.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void showProgressDialog() {
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getComment(Events.Comment comment) {
        if (comment.getPostType().equals("movie")) {
            ArrayList<ItemComment> itemComments = comment.getItemComments();
            CommentAdapter commentAdapter = new CommentAdapter(MovieDetailsActivity.this, itemComments);
            rvComment.setAdapter(commentAdapter);
            textNoComment.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View v1 = findViewById(R.id.view_fake);
        v1.requestFocus();
    }

    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        mAdViewLayout.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        if (isPlayerIsYt) {
            if (isYouTubePlayerFullScreen && youTubePlayer != null) {
                youTubePlayer.setFullscreen(false);
            } else {
                if (isFromNotification) {
                    Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            if (isFullScreen) {
                Events.FullScreen fullScreen = new Events.FullScreen();
                fullScreen.setFullScreen(false);
                GlobalBus.getBus().post(fullScreen);
            } else {
                if (isFromNotification) {
                    Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void playYoutube(String videoId) {
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.playerSection, youTubePlayerFragment).commitAllowingStateLoss();
        youTubePlayerFragment.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.loadVideo(videoId);
                    youTubePlayer.play();
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean _isFullScreen) {
                            isYouTubePlayerFullScreen = _isFullScreen;
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                String errorMessage = youTubeInitializationResult.toString();
                Log.d("errorMessage:", errorMessage);
            }
        });
    }

    @Override
    public void confirm(String rateAvg) {
        ratingView.setRating(Float.parseFloat(rateAvg));
        textRate.setText(rateAvg);
    }

    @Override
    public void cancel() {

    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id, DatabaseHelper.TABLE_MOVIE)) {
            imageFav.setImageResource(R.drawable.ic_favorited);
        } else {
            imageFav.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void saveRecent() {
        if (!databaseHelper.getRecentById(Id, "movie")) {
            ContentValues recent = new ContentValues();
            recent.put(DatabaseHelper.RECENT_ID, Id);
            recent.put(DatabaseHelper.RECENT_TITLE, itemMovie.getMovieTitle());
            recent.put(DatabaseHelper.RECENT_IMAGE, itemMovie.getMoviePoster());
            recent.put(DatabaseHelper.RECENT_TYPE, "movie");
            databaseHelper.addRecent(DatabaseHelper.TABLE_RECENT, recent, null);
        }
    }

    public void HDPlay(View view) {
       // letPlay(itemMovie.getMovieHDLink());
        ChooseDialog chooseDialog=new ChooseDialog(view.getContext(),this,true,itemMovie.getMovieHDLink(),itemMovie.getMovieSDLink(),itemMovie);
        chooseDialog.show();
    }

    public void SDPlay(View view) {
        letPlay(itemMovie.getMovieSDLink());
    }

    public void SDDownload(View view) {
        letDownload(itemMovie.getMovieSDLink());
    }

    public void HDDownload(View view) {
        ChooseDialog chooseDialog=new ChooseDialog(view.getContext(),this,false,itemMovie.getMovieHDLink(),itemMovie.getMovieSDLink(),itemMovie);
        chooseDialog.show();
    }
}
