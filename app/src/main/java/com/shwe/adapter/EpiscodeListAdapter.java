package com.shwe.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shwe.item.ItemEpisode;
import com.shwe.movies.R;

import java.util.ArrayList;

public class EpiscodeListAdapter extends RecyclerView.Adapter<EpisodeItemViewHolder> {
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
    public EpisodeItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.from(context).inflate(R.layout.item_episcode, parent, false);
        return new EpisodeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeItemViewHolder holder, int position) {
        final ItemEpisode current = ary.get(position);
        holder.bindData(current);
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return ary.size();
    }




}//end