package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn_covid19 = (ImageButton)findViewById(R.id.btn_covid19);
        btn_covid19.setOnClickListener(bt -> {
            Intent nextPage = new Intent(MainActivity.this, CovidMain.class);
            startActivity(nextPage);
        });



//        ImageButton (yourDrawableimage) = (ImageButton)findViewById(R.id.btn_covid19);
//        btn_covid19.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, YourClass.class);
//            startActivity(nextPage);
//        });


        //        ImageButton (yourDrawableimage) = (ImageButton)findViewById(R.id.btn_covid19);
//        btn_covid19.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, YourClass.class);
//            startActivity(nextPage);
//        });


//        ImageButton btn_covid19 = (ImageButton)findViewById(R.id.btn_covid19);
//        btn_covid19.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, CovidMain.class);
//            startActivity(nextPage);
//        });



    }
}