package com.example.shareimage.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.example.shareimage.Models.Repository;
import com.example.shareimage.R;


public class StartActivity extends AppCompatActivity {

    Repository repository;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        navController= Navigation.findNavController(this,R.id.start_navigation);

    }
    protected void onStart() {
        super.onStart();

        if(repository.instance.getAuthInstance().getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }



}