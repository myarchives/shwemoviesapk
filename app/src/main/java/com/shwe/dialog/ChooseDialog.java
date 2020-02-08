package com.shwe.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ornolfr.ratingview.RatingView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shwe.movies.MyApplication;
import com.shwe.movies.R;
import com.shwe.util.API;
import com.shwe.util.Constant;
import com.shwe.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ChooseDialog extends BaseDialog {
    TextView title,btn_hd,btn_sd;
    Boolean condition;//true for play and false for download


    public ChooseDialog(Context context,Boolean condition) {
        super(context);
        this.condition=condition;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose);
        title=findViewById(R.id.tv_co_title);
        btn_hd=findViewById(R.id.btn_hd);
        btn_sd=findViewById(R.id.btn_sd);

        title.setText(R.string.choose_one);

        if(condition){
            btn_hd.setText(R.string.hd_play);
            btn_sd.setText(R.string.sd_play);
        }





    }
}
