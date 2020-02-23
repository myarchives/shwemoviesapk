package com.shwe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.shwe.item.ItemRecent;
import com.shwe.movies.R;
import com.shwe.util.PopUpAds;
import com.shwe.util.RvOnClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeRecentAdapter extends RecyclerView.Adapter<HomeRecentAdapter.ItemRowHolder> {

    private ArrayList<ItemRecent> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;

    public HomeRecentAdapter(Context context, ArrayList<ItemRecent> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_tv_series_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ItemRecent singleItem = dataList.get(position);
        holder.text.setText(singleItem.getRecentTitle());
        Picasso.get().load(singleItem.getRecentImage()).placeholder(R.drawable.place_holder_movie).into(holder.image);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView text;
        CardView cardView;
        View view;

        ItemRowHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_movie_adapter);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            cardView = itemView.findViewById(R.id.click_play);
        }
    }
}
