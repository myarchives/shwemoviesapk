package com.shwe.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shwe.adapter.FavouriteChannelAdapter;
import com.shwe.db.DatabaseHelper;
import com.shwe.item.ItemChannel;
import com.shwe.movies.R;
import com.shwe.movies.TVDetailsActivity;
import com.shwe.util.RvOnClickListener;

import java.util.ArrayList;


public class FavoriteChannelFragment extends Fragment {

    ArrayList<ItemChannel> mListItem;
    public RecyclerView recyclerView;
    FavouriteChannelAdapter adapter;
    TextView textView;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        mListItem = new ArrayList<>();
        textView = rootView.findViewById(R.id.text_no);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavouriteChannel();
        displayData();
    }

    private void displayData() {

        adapter = new FavouriteChannelAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RvOnClickListener() {
            @Override
            public void onItemClick(int position) {
                String tvId = mListItem.get(position).getId();
                Intent intent = new Intent(getActivity(), TVDetailsActivity.class);
                intent.putExtra("Id", tvId);
                startActivity(intent);
            }
        });

        if (adapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
