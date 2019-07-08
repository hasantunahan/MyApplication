package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.Common.Common;

public class Admin_Activity extends AppCompatActivity {

    private TextView kullanici_ismi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_);

    ///findbyViewid//
    kullanici_ismi=findViewById(R.id.main2usernameEdittext);

    ///////////////

    ///appbarKullaniciİsmi gosterme
        kullanici_ismi.setText(Common.currentUser.getName().toUpperCase());
    ///////////////////////


    }
}
