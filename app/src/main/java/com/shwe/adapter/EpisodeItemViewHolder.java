package com.shwe.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htetznaing.xgetter.Model.XModel;
import com.htetznaing.xgetter.XGetter;
import com.shwe.dialog.ChooseSeriesWatch;
import com.shwe.item.ItemEpisode;
import com.shwe.movies.R;
import com.shwe.util.XDownloader;
import com.squareup.picasso.Picasso;


public class EpisodeItemViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView textView;
    private LinearLayout btnPlay, btnDownload;
    private XGetter xGetter;
    private XModel current_Xmodel = null;
    private XDownloader xDownloader;
    private ItemEpisode itemEpisode = null;
    private ProgressDialog progressDialog;

    public EpisodeItemViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img_poster);
        btnPlay = itemView.findViewById(R.id.btn_ll_play);
        btnDownload = itemView.findViewById(R.id.btn_ll_download);
        textView = itemView.findViewById(R.id.e_name);


    }

    public void bindData(ItemEpisode itemEpisode) {
        this.itemEpisode = itemEpisode;
        Picasso.get().load(itemEpisode.getEpisodePoster()).placeholder(R.drawable.place_holder_channel).into(imageView);
        textView.setText(itemEpisode.getEpisodeTitle());
        btnPlay.setOnClickListener(view -> {
            ChooseSeriesWatch chooseDialog = new ChooseSeriesWatch(itemView.getContext(), (Activity) itemView.getContext(), itemEpisode);
            chooseDialog.onCreate();

        });
        btnDownload.setOnClickListener(view -> {
//            ChooseSeriesDialog chooseDialog = new ChooseSeriesDialog(context, activity, true, itemMovie);
        });
    }
}