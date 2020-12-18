package com.wiryaimd.findbyimage.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.fragment.HomeFragment;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyHolder> {

    private Activity activity;
    private ArrayList<String> arrlink;

    private FragmentManager fm;

    public HistoryAdapter(FragmentManager fm, Activity activity, ArrayList<String> arrlink) {
        this.fm = fm;
        this.activity = activity;
        this.arrlink = arrlink;
    }

    @NonNull
    @Override
    public HistoryAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imghistory, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.MyHolder holder, int position) {
        final String link = arrlink.get(position);
        Glide.with(activity).load(Uri.parse(link)).placeholder(R.drawable.ic_gallery).error(R.drawable.ic_brokenimg).into(holder.imghistory);
        holder.tvlink.setText(link);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction().replace(R.id.main_frame, new HomeFragment(activity, true, link));
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrlink.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView imghistory;
        public TextView tvlink;
        public CardView cardView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imghistory = itemView.findViewById(R.id.history_img);
            imghistory.setClipToOutline(true);
            tvlink = itemView.findViewById(R.id.history_link);
            cardView = itemView.findViewById(R.id.history_card);

        }
    }
}
