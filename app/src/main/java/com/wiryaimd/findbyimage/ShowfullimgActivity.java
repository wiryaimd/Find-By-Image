package com.wiryaimd.findbyimage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.wiryaimd.findbyimage.util.DialogMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ShowfullimgActivity extends AppCompatActivity {

    private String link;

    private ImageView imgshow, imgback;
    private LinearLayout linearlink, lineardownload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showimgfull);

        imgshow = findViewById(R.id.showimgfull_img);
        imgback = findViewById(R.id.showimgfull_back);
        lineardownload = findViewById(R.id.showimgfull_lineardownload);
        linearlink = findViewById(R.id.showimgfull_linearcopy);

        if (getIntent().getStringExtra("imguri") != null){
            this.link = getIntent().getStringExtra("imguri");
            System.out.println("link activity: " + link);
        }else{
            Toast.makeText(ShowfullimgActivity.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            finish();
        }

        Glide.with(ShowfullimgActivity.this).load(Uri.parse(link)).placeholder(R.drawable.ic_gallery).error(R.drawable.ic_brokenimg).into(imgshow);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lineardownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (imgshow.getDrawable() != null) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) imgshow.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();

                        FileOutputStream fos = null;
                        File dir = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/findbyimage");
                        dir.mkdir();

                        String filename;
                        boolean fileex = false;
                        if (link.substring(link.length() - 3, link.length()).equalsIgnoreCase("png")) {
                            fileex = true;
                            filename = "findbyimg-" + UUID.randomUUID() + ".png";
                        } else {
                            filename = "findbyimg-" + UUID.randomUUID() + ".jpg";
                        }

                        File outfile = new File(dir, filename);
                        try {
                            fos = new FileOutputStream(outfile);
                        } catch (Exception e) {
                            DialogMessage.showMessage(ShowfullimgActivity.this, getLayoutInflater(), "Failed to save Image, please try again");
                        }

                        if (fileex) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } else {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        }

                        try {
                            fos.flush();
                            fos.close();
                            Toast.makeText(ShowfullimgActivity.this, "Image stored on " + getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/findbyimage", Toast.LENGTH_SHORT).show();
                            System.out.println("saved img");
                        } catch (IOException e) {
                            DialogMessage.showMessage(ShowfullimgActivity.this, getLayoutInflater(), "Failed to save Image, please try again");
                        }

                    } else {
                        Toast.makeText(ShowfullimgActivity.this, "Please wait until image loaded", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    DialogMessage.showMessage(ShowfullimgActivity.this, getLayoutInflater(), "Please wait until image loaded");
                }
            }
        });

        linearlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("text", link);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(ShowfullimgActivity.this, "Copied " + link, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){
                    Toast.makeText(ShowfullimgActivity.this, "Failed Copy Link, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
