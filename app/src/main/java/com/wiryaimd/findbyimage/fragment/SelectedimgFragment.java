package com.wiryaimd.findbyimage.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.squareup.picasso.Picasso;
import com.wiryaimd.findbyimage.MainActivity;
import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.adapter.LoadimgAdapter;
import com.wiryaimd.findbyimage.model.ImagedataModel;
import com.wiryaimd.findbyimage.util.DialogMessage;
import com.wiryaimd.findbyimage.util.StoreHistory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class SelectedimgFragment extends Fragment {

    private Uri imguri;
    private Bitmap imgbitmap;

    private ImageView selectimg, imgdone, imgcancel;
    private ProgressBar loading, loading2;
    private TextView tvupload, tvloadmoreimg;

    private RecyclerView recyclerView;

    private Context context;
    public static String requestid = "";
    public static ArrayList<ImagedataModel> arrimg = null;
    private boolean loadmore = true;

    private RewardedAd rewardedAd;

    public static final String BASEIMGURL = "https://res.cloudinary.com/username/image/upload/";

    public SelectedimgFragment(Context context, Uri imguri) {
        this.context = context;
        this.imguri = imguri;
    }

    public SelectedimgFragment(Context context, Bitmap imgbitmap) {
        this.context = context;
        this.imgbitmap = imgbitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selectedimg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

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

        selectimg = view.findViewById(R.id.selectimg_img);
        selectimg.setClipToOutline(true);
        imgdone = view.findViewById(R.id.selectimg_imgdone);
        loading = view.findViewById(R.id.selectimg_loading);
        loading2 = view.findViewById(R.id.selectimg_loading2);
        tvupload = view.findViewById(R.id.selectimg_uploading);
        tvloadmoreimg = view.findViewById(R.id.selectimg_loadmoreimg);
        imgcancel = view.findViewById(R.id.selectimg_cancel);
        recyclerView = view.findViewById(R.id.selectimg_recyclerview);
        recyclerView.setNestedScrollingEnabled(false);

        if (imguri != null) {
            final String fileid = UUID.randomUUID().toString();
            Picasso.get().load(imguri).into(selectimg);
            loading.setVisibility(View.VISIBLE);
            tvloadmoreimg.setVisibility(View.GONE);

            requestid = MediaManager.get().upload(imguri).unsigned("username")
                    .option("public_id", fileid)
                    .option("connect_timeout", 180)
                    .option("read_timeout", 360)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            System.out.println("starting..");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progresscount = (100.00 * bytes / totalBytes);
                            loading.setProgress((int) progresscount);
                            if ((int) progresscount == 90) {
                                loading2.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            System.out.println("link: " + BASEIMGURL + fileid + ".jpg");
                            System.out.println("done");
                            loading.setVisibility(View.GONE);
                            loading2.setVisibility(View.GONE);
                            tvupload.setText("");

                            imgcancel.setVisibility(View.GONE);
                            imgdone.setImageResource(R.drawable.ic_done);

                            StoreHistory.saveLink(getActivity(), BASEIMGURL + fileid + ".jpg");
                            new LoadImage().execute(BASEIMGURL + fileid + ".jpg");
                            MainActivity.loading.setVisibility(View.VISIBLE);
                            MainActivity.tvloadinfo.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            try {
                                DialogMessage.showMessage(context, getLayoutInflater(), "Failed to upload data, please try again");
                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                            System.out.println("err: " + error.toString());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();

            String restemp = MediaManager.get().upload(imguri).dispatch();
            MediaManager.get().cancelRequest(restemp);

        } else if (imgbitmap != null) {
            final String fileid = UUID.randomUUID().toString();
            Glide.with(context).load(imgbitmap).into(selectimg);
            loading.setVisibility(View.VISIBLE);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imgbitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            requestid = MediaManager.get().upload(stream.toByteArray()).unsigned("username")
                    .option("public_id", fileid)
                    .option("connect_timeout", 180)
                    .option("read_timeout", 360)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            System.out.println("starting bitmap..");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            double progresscount = (100.00 * bytes / totalBytes);
                            loading.setProgress((int) progresscount);
                            if ((int) progresscount == 90) {
                                loading2.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            loading.setVisibility(View.GONE);
                            loading2.setVisibility(View.GONE);
                            tvupload.setText("");

                            imgcancel.setVisibility(View.GONE);
                            imgdone.setImageResource(R.drawable.ic_done);

                            StoreHistory.saveLink(getActivity(), BASEIMGURL + fileid + ".jpg");
                            new LoadImage().execute(BASEIMGURL + fileid + ".jpg");

                            MainActivity.loading.setVisibility(View.VISIBLE);
                            MainActivity.tvloadinfo.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            try {
                                DialogMessage.showMessage(context, getLayoutInflater(), "Failed to upload data, please try again");
                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }

                            System.out.println("err: " + error.toString());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();

            String restemp = MediaManager.get().upload(stream.toByteArray()).dispatch();
            MediaManager.get().cancelRequest(restemp);

        } else {
            DialogMessage.showMessage(context, getLayoutInflater(), "Failed to load data, please try again");
        }

        imgcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!requestid.isEmpty()) {
                        if (getFragmentManager() != null) {
                            MediaManager.get().cancelRequest(requestid);
                            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.home_imgsrc)).commit();
                        } else {
                            Toast.makeText(context, "Cannot cancel request, please try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Cannot cancel request, please try again", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(context, "Cannot cancel request, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public static ArrayList<ImagedataModel> loadimgjsoup(String link){
        ArrayList<ImagedataModel> arrimg = new ArrayList<>();
        try {
            System.out.println("url + " + "https://images.google.com/searchbyimage?image_url=" + link);
            Document doc = Jsoup.connect("https://images.google.com/searchbyimage?image_url=" + link).get();
            Elements elements = doc.select("a.ekf0x.hSQtef");
            Document doc2 = Jsoup.connect(elements.attr("abs:href")).get();
            Elements script = doc2.getElementsByTag("script");

            StringBuilder strb = new StringBuilder();
            for (Element element : script) {
                for (DataNode node : element.dataNodes()) {
                    strb.append(node.getWholeData());
                }
            }

            if (strb.toString().contains(".jpg\"")) {
                int finindex = strb.toString().indexOf(".jpg\"");
                int myindex = strb.toString().indexOf(".jpg\"");

                try {
                    for (int i = 0; i < 30; i++) {
                        while (true) {
                            if (strb.toString().substring(myindex - 5, myindex).equalsIgnoreCase("\"http")) {
                                System.out.println(strb.substring(myindex - 4, finindex) + ".jpg");
                                arrimg.add(new ImagedataModel(strb.substring(myindex - 4, finindex) + ".jpg"));
                                myindex = strb.toString().indexOf(".jpg\"", finindex + 5);
                                finindex = strb.toString().indexOf(".jpg\"", finindex + 5);
                                break;
                            } else {
                                myindex -= 1;
                            }
                        }
                    }
                }catch (StringIndexOutOfBoundsException e){
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    System.out.println(e);
                }
            }

            if (strb.toString().contains(".png\"")) {
                int finindex = strb.toString().indexOf(".png\"");
                int myindex = strb.toString().indexOf(".png\"");

                try {
                    for (int i = 0; i < 15; i++) {
                        while (true) {
                            if (strb.toString().substring(myindex - 5, myindex).equalsIgnoreCase("\"http")) {
                                System.out.println(strb.substring(myindex - 4, finindex) + ".png");
                                arrimg.add(new ImagedataModel(strb.substring(myindex - 4, finindex) + ".png"));
                                myindex = strb.toString().indexOf(".png\"", finindex + 5);
                                finindex = strb.toString().indexOf(".png\"", finindex + 5);
                                break;
                            } else {
                                myindex -= 1;
                            }
                        }
                    }
                }catch (StringIndexOutOfBoundsException e){
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.out.println(e);
        } catch (RuntimeException e){
            System.out.println(e);
        }
        return arrimg;
    }

    class LoadImage extends AsyncTask<String, Void, ArrayList<ImagedataModel>> {

        @Override
        protected ArrayList<ImagedataModel> doInBackground(String... strings) {
            return loadimgjsoup(strings[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<ImagedataModel> imagedataModels) {
            if (imagedataModels.size() != 0){
                arrimg = imagedataModels;
                System.out.println("arrimg size: " + imagedataModels.size());
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
                System.out.println("post img size: " + imgdatamodel.size());
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
