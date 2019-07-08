package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.Common.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Activity extends AppCompatActivity {

    private TextView sirketAdiAdminekran;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_);

    ///findbyViewid//
    sirketAdiAdminekran=findViewById(R.id.sirketiAdiAdminekran);
    ///////////////

        //ŞirketAdi alıyoruz
        FirebaseDatabase.getInstance().getReference("SirketBilgileri").child(Common.currentUser.getName()).child("SirketAdi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ad=dataSnapshot.getValue().toString();
                sirketAdiAdminekran.setText("HOSGELDINIZ , "+ad);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
////////////şirketadi

    }
}
