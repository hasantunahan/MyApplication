package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Model.Food;
import com.example.myapplication.ViewHolder.FavorilerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favoriler extends AppCompatActivity {

    FavorilerAdapter adapter;
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Food> list;
    ImageView geri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoriler);
        recyclerView=findViewById(R.id.fav_recyler);
        reference= FirebaseDatabase.getInstance().getReference("Favori").child(Common.currentUser.getName());
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        list=new ArrayList<Food>();

        geri=findViewById(R.id.geriButtonfav);

        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Favoriler.this,Home.class);
                startActivity(intent);
            }
        });



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.exists()){
                    for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Food f= snapshot.getValue(Food.class);

                       list.add(f);
                    }
                    adapter=new FavorilerAdapter(Favoriler.this,list);
                    recyclerView.setAdapter(adapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
