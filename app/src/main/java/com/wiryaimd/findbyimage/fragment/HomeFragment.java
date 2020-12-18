package com.wiryaimd.findbyimage.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wiryaimd.findbyimage.R;
import com.wiryaimd.findbyimage.util.DialogMessage;

public class HomeFragment extends Fragment {

    public static final int PERM_CODEG = 1000;
    public static final int PERM_CODEC = 1100;
    public static final int PICKIMG_CODE = 1001;
    public static final int PICKCAM_CODE = 1002;

    private ImageView imggallery, imgcamera, imglink;
    private Activity activity;

    private AlertDialog dialog;

    private boolean cekfrom;
    private String link;

    public HomeFragment(Activity activity, boolean cekfrom, String link){
        this.link = link;
        this.cekfrom = cekfrom;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imggallery = view.findViewById(R.id.home_gallery);
        imgcamera = view.findViewById(R.id.home_camera);
        imglink = view.findViewById(R.id.home_link);

        imggallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionGalleryCek(activity)){
                    openGallery();
                }
            }
        });

        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionCameraCek(activity)){
                    openCamera();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogview = getLayoutInflater().inflate(R.layout.dialog_insertlink, null, false);
        final EditText edtlink = dialogview.findViewById(R.id.dialoglink_edt);
        Button btnok = dialogview.findViewById(R.id.dialoglink_btnok);
        builder.setView(dialogview);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(URLUtil.isValidUrl(edtlink.getText().toString())) {
                    System.out.println("valid");
                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_imgsrc, new SelectimglinkFragment(activity, edtlink.getText().toString()));
                    ft.commit();
                    dialog.dismiss();
                }else{
                    System.out.println("invalid url");
                    dialog.dismiss();
                    DialogMessage.showMessage(activity, getLayoutInflater(), "Invalid url, please try another image url");
                }
            }
        });
        dialog = builder.create();

        imglink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtlink.setText("");
                dialog.show();
            }
        });

        if (cekfrom){
            FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_imgsrc, new SelectimglinkFragment(activity, link));
            ft.commit();
        }

    }

    public boolean permissionGalleryCek(Activity activity){
        boolean cekperm = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE};
                activity.requestPermissions(perm, PERM_CODEG);
            }else{
                cekperm = true;
            }
        }else{
            cekperm = true;
        }
        return cekperm;
    }

    public boolean permissionCameraCek(Activity activity){
        boolean cekperm = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                String[] perm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                activity.requestPermissions(perm, PERM_CODEC);
            }else{
                cekperm = true;
            }
        }else{
            cekperm = true;
        }
        return cekperm;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICKIMG_CODE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICKCAM_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case PICKIMG_CODE:
                if(resultCode == Activity.RESULT_OK){
                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_imgsrc, new SelectedimgFragment(activity, data.getData()));
                    ft.commit();
                }
                break;
            case PICKCAM_CODE:
                if (resultCode == Activity.RESULT_OK){
                    Bitmap img = (Bitmap) data.getExtras().get("data");
                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.home_imgsrc, new SelectedimgFragment(activity, img));
                    ft.commit();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERM_CODEG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }else{
                    Toast.makeText(activity, "Permission to open Gallery is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERM_CODEC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }else{
                    Toast.makeText(activity, "Permission to open Camera is denied", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
