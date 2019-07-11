package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Model.Rating;
import com.example.myapplication.ViewHolder.YorumlarAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Yorumlar extends AppCompatActivity {

    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Rating> list;
    Intent intent;
    String foodId;
    YorumlarAdapter adapter;
    ImageView geri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yorumlar);
        intent=getIntent();
        foodId=intent.getStringExtra("FoodId");

        recyclerView=findViewById(R.id.yorumlarRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<Rating>();
        geri=findViewById(R.id.geriButtonyorum);

        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Yorumlar.this,FoodList.class);
                startActivity(intent);
            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Rating");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Rating r= snapshot.getValue(Rating.class);

                    if(r.getFoodId().equals(foodId)){
                        list.add(r);
                    }
                }
                adapter=new YorumlarAdapter(Yorumlar.this,list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
