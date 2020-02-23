package com.shwe.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

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
                                        new getAllDown().execute();
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

    private class getAllDown extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            dataList.clear();
            String path = Environment.getExternalStorageDirectory().toString() + "/" + mContext.getString(R.string.save_folder_name);
            File directory = new File(path);
            boolean success = true;
            if (!directory.exists()) {
                success = directory.mkdirs();
            }
            if (success) {
                File[] files = directory.listFiles();
                for (File file : files) {
                    if (file.isFile() && file.getName().contains("_" + mContext.getString(R.string.save_folder_name))) {
                        ItemDown itemDown = new ItemDown();
                        itemDown.setName(file.getName().replace("_" + mContext.getString(R.string.save_folder_name), ""));
                        itemDown.setFilepath(file.getAbsolutePath());
                        itemDown.setThumbnailpath(file.getAbsolutePath());
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(mContext, Uri.fromFile(file));
                        Long time = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                        long hour = time / 3600;
                        long minute = (time % 3600) / 60;
                        long second = (time % 3600) % 60;
                        itemDown.setDuration(hour + ":" + minute + ":" + second);
                        itemDown.setSize((file.length() / (1024 * 1024)) + "MB");
                        dataList.add(itemDown);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            notifyDataSetChanged();
        }
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