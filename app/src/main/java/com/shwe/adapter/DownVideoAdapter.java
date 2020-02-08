package com.shwe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.shwe.fragment.DownloadedFragment;
import com.shwe.item.ItemDown;
import com.shwe.movies.R;
import com.shwe.movies.SimpleVideoPlayer;

import java.io.File;
import java.util.ArrayList;


public class DownVideoAdapter extends RecyclerView.Adapter<DownVideoAdapter.ItemRowHolder> {

    private ArrayList<ItemDown> dataList;
    private Context mContext;

    public DownVideoAdapter(Context context, ArrayList<ItemDown> dataList) {
        this.dataList = dataList;
        this.mContext = context;

    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_row_movie_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemDown singleItem = dataList.get(position);
        holder.text_title.setText(singleItem.getName());
        holder.text_time.setText(singleItem.getDuration());
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(singleItem.getThumbnailpath(), MediaStore.Images.Thumbnails.MINI_KIND);
        holder.image.setImageBitmap(thumb);
        holder.lyt_parent.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, SimpleVideoPlayer.class);
            intent.putExtra("url", singleItem.getFilepath());
            mContext.startActivity(intent);
        });
        holder.image_pop_up.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(mContext, holder.image_pop_up);
            popup.inflate(R.menu.delete_popup_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.option_delete:
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("Are you sure delete?")
                                .setPositiveButton("OK", (dialogInterface, i) -> {
                                    File file = new File(singleItem.getFilepath());
                                    if (file.delete()) {
                                        DownloadedFragment downloadedFragment = new DownloadedFragment();
                                        downloadedFragment.new getAllDown().execute();
                                    }

                                })
                                .setNegativeButton("Cancel", (dialogInterface, i) -> {

                                }).setCancelable(false);
                        dialog.show();

                        break;


                }
                return false;
            });
            popup.show();
        });


    }


    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private ImageView image, image_pop_up;
        private TextView text_time, text_title;
        private LinearLayoutCompat lyt_parent;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            lyt_parent = itemView.findViewById(R.id.click_play);
            text_time = itemView.findViewById(R.id.text_time);
            text_title = itemView.findViewById(R.id.text_title);
            image_pop_up = itemView.findViewById(R.id.image_pop_up);

        }
    }
}