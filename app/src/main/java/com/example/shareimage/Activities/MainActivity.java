package com.example.shareimage.Activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private NavController navController;
    FirebaseUser firebaseUser;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser=repository.instance.getAuthInstance().getCurrentUser();
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("profileid", firebaseUser.getUid());
        editor.apply();

        navController= Navigation.findNavController(this,R.id.main_navigation);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        /*
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
                String profile = prefs.getString("profileid", "none");
                if(profile!=firebaseUser.getUid()){
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", firebaseUser.getUid());
                    editor.apply();
                }
            }
        });
        */

    }
}
