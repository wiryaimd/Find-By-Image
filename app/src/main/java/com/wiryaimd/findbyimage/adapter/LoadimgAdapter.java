package com.wiryaimd.findbyimage.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.ShowfullimgActivity;
import com.wiryaimd.findbyimage.model.ImagedataModel;

import java.util.ArrayList;

public class LoadimgAdapter extends RecyclerView.Adapter<LoadimgAdapter.Myholder> {

    private Context context;
    private ArrayList<ImagedataModel> arrimg;

    public LoadimgAdapter(Context context, ArrayList<ImagedataModel> arrimg){
        this.context = context;
        this.arrimg = arrimg;
    }

    @NonNull
    @Override
    public LoadimgAdapter.Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imgshow, parent, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadimgAdapter.Myholder holder, int position){
        final ImagedataModel imgdata = arrimg.get(position);

        Glide.with(context).load(Uri.parse(imgdata.getUrlimg())).placeholder(R.drawable.ic_gallery).error(R.drawable.ic_brokenimg).into(holder.imgshow);
        holder.imgshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowfullimgActivity.class);
                intent.putExtra("imguri", imgdata.getUrlimg());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrimg.size();
    }

    public class Myholder extends RecyclerView.ViewHolder {

        public ImageView imgshow;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            imgshow = itemView.findViewById(R.id.itemimg_img);
            imgshow.setClipToOutline(true);
        }

    }
}
