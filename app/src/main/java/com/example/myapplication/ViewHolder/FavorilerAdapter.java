package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Favoriler;
import com.example.myapplication.FoodList;
import com.example.myapplication.Model.Food;
import com.example.myapplication.Model.Order;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;

public class FavorilerAdapter extends RecyclerView.Adapter<FavorilerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Food> list;
    String foodid;

    public FavorilerAdapter(Context c,ArrayList<Food> a){
        context=c;
        list=a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.fav_food_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        myViewHolder.name.setText(list.get(i).getName().toUpperCase());
        myViewHolder.price.setText(list.get(i).getPrice());
        Glide.with(context).load(list.get(i).getImage()).into(myViewHolder.food_image);

        FirebaseDatabase.getInstance().getReference("Favori").child(Common.currentUser.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodid= dataSnapshot.getKey();


                myViewHolder.sepetEkleImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Database(context).addToChart(new Order(


                                foodid,
                                 list.get(i).getName(),
                                "1",
                                list.get(i).getPrice().toString(),
                                list.get(i).getDiscount()



                        ));

                        Toast.makeText(context,"Sepete eklendi",Toast.LENGTH_SHORT).show();
                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,price;
        ImageView food_image,sepetEkleImage;


        public MyViewHolder(View itemview){
            super(itemview);
            name=itemview.findViewById(R.id.fav_name);
            price=itemview.findViewById(R.id.fav_price);
            food_image=itemview.findViewById(R.id.fav_iamge);
            sepetEkleImage=itemview.findViewById(R.id.fav_speteEkle);

        }
    }
}

