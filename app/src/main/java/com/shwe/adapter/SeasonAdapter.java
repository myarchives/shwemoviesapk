package com.shwe.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shwe.item.ItemSeason;
import com.shwe.movies.EpiscodeList;
import com.shwe.movies.R;
import com.shwe.util.RvOnClickListener;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ItemRowHolder> {

    private ArrayList<ItemSeason> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private String series_id;

    public SeasonAdapter(Context context, ArrayList<ItemSeason> dataList, String series_id) {
        this.dataList = dataList;
        this.mContext = context;
        this.series_id = series_id;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_season_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemSeason singleItem = dataList.get(position);
        holder.text.setText(singleItem.getSeasonName());
        holder.lytSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EpiscodeList.class);
                intent.putExtra("SEASON_ID", singleItem.getSeasonId());
                intent.putExtra("LABEL", singleItem.getSeasonName());
                intent.putExtra("SERIES_ID", series_id);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        LinearLayout lytSeason;

        ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textSeasonName);
            lytSeason = itemView.findViewById(R.id.rootLytSeason);
        }
    }
}
