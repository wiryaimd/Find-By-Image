package com.wiryaimd.findbyimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wiryaimd.findbyimage.fragment.HistoryFragment;
import com.wiryaimd.findbyimage.fragment.HomeFragment;
import com.wiryaimd.findbyimage.fragment.SelectedimgFragment;
import com.wiryaimd.findbyimage.util.StoreHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomnav;
    public static ProgressBar loading;
    public static TextView tvloadinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "name");
        config.put("api_key", "key");
        config.put("api_secret", "secretapi");
        MediaManager.init(MainActivity.this, config);

        loading = findViewById(R.id.main_loading);
        tvloadinfo = findViewById(R.id.main_tvloadinfo);
        bottomnav = findViewById(R.id.main_bottomnav);

        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.menumain_home:
                        loading.setVisibility(View.GONE);
                        tvloadinfo.setVisibility(View.GONE);
                        fragment = new HomeFragment(MainActivity.this, false, "");
                        break;
                    case R.id.menumain_history:
                        if (!SelectedimgFragment.requestid.isEmpty()){
                            MediaManager.get().cancelRequest(SelectedimgFragment.requestid);
                            SelectedimgFragment.requestid = "";
                            Toast.makeText(MainActivity.this, "Request canceled", Toast.LENGTH_SHORT).show();
                        }
                        loading.setVisibility(View.GONE);
                        tvloadinfo.setVisibility(View.GONE);
                        fragment = new HistoryFragment(MainActivity.this);
                        break;
                }

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment);
                ft.commit();
                return true;
            }
        });

        try {
            ArrayList<String> link = StoreHistory.getSavedlink(MainActivity.this);
            Collections.reverse(link);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment(MainActivity.this, true, link.get(0)));
            ft.commit();
        }catch (Exception e) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment(MainActivity.this, false, ""));
            ft.commit();
        }
    }
}
