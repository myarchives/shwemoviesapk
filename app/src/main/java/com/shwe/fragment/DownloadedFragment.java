package com.shwe.fragment;

import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.shwe.adapter.DownVideoAdapter;
import com.shwe.item.ItemDown;
import com.shwe.movies.R;

import java.io.File;
import java.util.ArrayList;


public class DownloadedFragment extends Fragment {

    public RecyclerView recyclerView;
    ArrayList<ItemDown> mListItem;
    DownVideoAdapter downVideoAdapter;
    boolean isFirst = true, isOver = false;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private int pageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview_download, container, false);
        mListItem = new ArrayList<>();
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        new getAllDown().execute();
        return rootView;
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {

            lyt_not_found.setVisibility(View.GONE);
            if (getActivity() != null) {
                downVideoAdapter = new DownVideoAdapter(getActivity(), mListItem);
                recyclerView.setAdapter(downVideoAdapter);
            }


        }
    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class getAllDown extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String path = Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.save_folder_name);
            File directory = new File(path);
            boolean success = true;
            if (!directory.exists()) {
                success = directory.mkdirs();
            }
            if (success) {
                File[] files = directory.listFiles();
                for (File file : files) {
                    if (file.isFile() && file.getName().contains("_" + getString(R.string.save_folder_name))) {
                        ItemDown itemDown = new ItemDown();
                        itemDown.setName(file.getName().replace("_" + getString(R.string.save_folder_name), ""));
                        itemDown.setFilepath(file.getAbsolutePath());
                        itemDown.setThumbnailpath(file.getAbsolutePath());
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getContext(), Uri.fromFile(file));
                        Long time = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                        long hour = time / 3600;
                        long minute = (time % 3600) / 60;
                        long second = (time % 3600) % 60;
                        itemDown.setDuration(hour + ":" + minute + ":" + second);
                        itemDown.setSize((file.length() / (1024 * 1024)) + "MB");
                        mListItem.add(itemDown);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.GONE);
            showProgress(true);
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setVisibility(View.VISIBLE);
            showProgress(false);
            displayData();
        }
    }
}

