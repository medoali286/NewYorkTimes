package com.example.newyorktimes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.newyorktimes.UI.NewYorkTimes;
import com.example.newyorktimes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        navigation();
    }






    void navigation(){






        binding.newYork.setOnClickListener(clk->{
            Intent NewYorkPage=new Intent(this, NewYorkTimes.class);

            startActivity(NewYorkPage);


        });










    }



}