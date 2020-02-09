package com.shwe.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwe.dialog.ChooseDialog;
import com.shwe.item.ItemEpisode;
import com.shwe.item.ItemMovie;
import com.shwe.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EpiscodeListAdapter extends RecyclerView.Adapter<EpiscodeListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ItemEpisode> ary;
    LayoutInflater layoutInflater;
    Activity activity;


    public EpiscodeListAdapter(Context context, Activity activity, ArrayList<ItemEpisode> ary) {
        this.context = context;
        this.ary = ary;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.from(context).inflate(R.layout.item_episcode, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ItemEpisode current = ary.get(position);
        Picasso.get().load(current.getEpisodePoster()).placeholder(R.drawable.place_holder_channel).into(holder.img);
        holder.ename.setText(current.getEpisodeTitle());
        ItemMovie itemMovie = new ItemMovie();
        itemMovie.setMovieTitle(current.getEpisodeTitle());
        itemMovie.setMovieSDLink(current.getGetEpisodeSDLink());
        itemMovie.setMovieHDLink(current.getEpisodeHDLink());

        holder.btn_play.setOnClickListener(view -> {
            ChooseDialog chooseDialog = new ChooseDialog(context, activity, true, itemMovie);
            chooseDialog.show();
        });
        holder.btn_download.setOnClickListener(view -> {
            ChooseDialog chooseDialog = new ChooseDialog(context, activity, false, itemMovie);
            chooseDialog.show();
        });


    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return ary.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView ename;
        LinearLayout btn_play, btn_download;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_poster);
            btn_play = itemView.findViewById(R.id.btn_ll_play);
            btn_download = itemView.findViewById(R.id.btn_ll_download);
            ename = itemView.findViewById(R.id.e_name);
        }
    }//MyViewHolder

}//end