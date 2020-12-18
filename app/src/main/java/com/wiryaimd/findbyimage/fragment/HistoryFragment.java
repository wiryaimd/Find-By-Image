package com.wiryaimd.findbyimage.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.adapter.HistoryAdapter;
import com.wiryaimd.findbyimage.util.StoreHistory;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryFragment extends Fragment {

    private Activity activity;
    private RecyclerView recyclerView;
    private ImageView imgnoavailable;
    private TextView tvnoavailable;

    public HistoryFragment(Activity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.history_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        imgnoavailable = view.findViewById(R.id.history_imgnoav);
        tvnoavailable = view.findViewById(R.id.history_tvnoav);
        imgnoavailable.setVisibility(View.GONE);
        tvnoavailable.setVisibility(View.GONE);

        try {
            ArrayList<String> link = StoreHistory.getSavedlink(activity);
            Collections.reverse(link);
            HistoryAdapter adapter = new HistoryAdapter(getFragmentManager(), activity, link);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
        }catch (Exception e){
            imgnoavailable.setVisibility(View.VISIBLE);
            tvnoavailable.setVisibility(View.VISIBLE);
            System.out.println("theres no one history");
        }
    }
}
