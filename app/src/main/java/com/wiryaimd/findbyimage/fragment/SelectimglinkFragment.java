package com.wiryaimd.findbyimage.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.wiryaimd.findbyimage.MainActivity;
import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.adapter.LoadimgAdapter;
import com.wiryaimd.findbyimage.model.ImagedataModel;
import com.wiryaimd.findbyimage.util.DialogMessage;
import com.wiryaimd.findbyimage.util.StoreHistory;

import java.util.ArrayList;

public class SelectimglinkFragment extends Fragment {

    private Context context;
    private String link;
    private boolean loadmore = true;
    public static ArrayList<ImagedataModel> arrimg = null;

    private RecyclerView recyclerView;
    private TextView tvloadmoreimg, tvlink;
    private ImageView imglink;

    private RewardedAd rewardedAd;

    public SelectimglinkFragment(Context context, String link){
        this.context = context;
        this.link = link;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selectimglink, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        rewardedAd = new RewardedAd(context, getString(R.string.ad1));

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                System.out.println("success load ads");
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                Toast.makeText(context, "Failed load ads", Toast.LENGTH_SHORT).show();
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        tvloadmoreimg = view.findViewById(R.id.selectimglink_loadmoreimg);
        tvlink = view.findViewById(R.id.selectimglink_tvlink);
        imglink = view.findViewById(R.id.selectimglink_img);
        imglink.setClipToOutline(true);
        recyclerView = view.findViewById(R.id.selectimglink_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        tvloadmoreimg.setVisibility(View.GONE);

        Glide.with(context).load(link).placeholder(R.drawable.ic_gallery).error(R.drawable.ic_brokenimg).into(imglink);

        MainActivity.loading.setVisibility(View.VISIBLE);
        MainActivity.tvloadinfo.setVisibility(View.VISIBLE);
        new Loadimglink().execute(link);
        StoreHistory.saveLink(getActivity(), link);
        tvlink.setText(link);

        tvloadmoreimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrimg != null){
                    ArrayList<ImagedataModel> imgdatamodel = new ArrayList<>();
                    if (loadmore){
                        for (int i = (arrimg.size() % 2 == 0 ? arrimg.size() / 2 : (arrimg.size() - 1) / 2); i < arrimg.size(); i++){
                            imgdatamodel.add(arrimg.get(i));
                        }
                        System.out.println("img size: " + imgdatamodel.size());
                        LoadimgAdapter adapter = new LoadimgAdapter(context, imgdatamodel);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                        recyclerView.setAdapter(adapter);
                        loadmore = false;
                    }else{
                        for (int i = 0; i < (arrimg.size() % 2 == 0 ? arrimg.size() / 2 : (arrimg.size() - 1) / 2); i++){
                            imgdatamodel.add(arrimg.get(i));
                        }
                        System.out.println("img size false: " + imgdatamodel.size());
                        LoadimgAdapter adapter = new LoadimgAdapter(context, imgdatamodel);
                        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                        recyclerView.setAdapter(adapter);
                        loadmore = true;
                    }
                }else{
                    DialogMessage.showMessage(context, getLayoutInflater(), "Cannot load more image, please try again");
                }
            }
        });

    }

    class Loadimglink extends AsyncTask<String, Void, ArrayList<ImagedataModel>>{

        @Override
        protected ArrayList<ImagedataModel> doInBackground(String... strings) {
            return SelectedimgFragment.loadimgjsoup(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<ImagedataModel> imagedataModels) {
            if (imagedataModels.size() != 0){
                arrimg = imagedataModels;
                System.out.println("arrimg size link: " + imagedataModels.size());
                ArrayList<ImagedataModel> imgdatamodel = new ArrayList<>();
                try {
                    for (int i = 0; i < (arrimg.size() % 2 == 0 ? arrimg.size() / 2 : (arrimg.size() - 1) / 2); i++) {
                        imgdatamodel.add(arrimg.get(i));
                    }
                }catch (StringIndexOutOfBoundsException e){
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    System.out.println(e);
                }
                System.out.println("post img size link: " + imgdatamodel.size());
                LoadimgAdapter adapter = new LoadimgAdapter(context, imgdatamodel);
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                recyclerView.setAdapter(adapter);
                MainActivity.loading.setVisibility(View.GONE);
                MainActivity.tvloadinfo.setVisibility(View.GONE);
                tvloadmoreimg.setVisibility(View.VISIBLE);

                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {

                    }
                };
                rewardedAd.show(getActivity(), adCallback);
            }else{
                DialogMessage.showMessage(context, getLayoutInflater(), "Cannot find Image, please try another image");
                MainActivity.loading.setVisibility(View.GONE);
                MainActivity.tvloadinfo.setVisibility(View.GONE);
            }
        }
    }
}
