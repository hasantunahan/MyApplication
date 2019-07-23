package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.FoodDetail;
import com.example.myapplication.Model.Food;
import com.example.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.MyViewHolder> {

    Context context;
    ArrayList<Food> list;
    String foodid;

    public FoodAdapter(Context c,ArrayList<Food> a){
        context=c;
        list=a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.food_mini_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.food_name.setText(list.get(i).getName());
        myViewHolder.price.setText(list.get(i).getPrice()+" ₺");
               /* Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);*/

        //Glide.with(FoodList.this).load(model.getImage()).into(viewHolder.food_image);

        // Picasso.get().load(model.getImage()).resize(100,100).centerCrop().into(viewHolder.food_image);

        Picasso.get().load(list.get(i).getImage()).resize(1000,1000).centerCrop().into(myViewHolder.food_image);




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView food_name,price,yorumsayisi,ratingOrt;
        public ImageView food_image,fav_image,sepet_image,share_image,yorumlar_image,kalitekontrol,ratingStar;
        public ConstraintLayout layout;
        public RatingBar miniBar;



        public MyViewHolder(View itemview){
            super(itemview);

            food_image=(ImageView) itemView.findViewById(R.id.mini_food_image);
            food_name=(TextView) itemView.findViewById(R.id.mini_food_name);
            fav_image=(ImageView) itemView.findViewById(R.id.fav);
            price=itemView.findViewById(R.id.mini_priceFoodList);
            sepet_image=itemView.findViewById(R.id.sepeteEkleView);
            share_image=itemView.findViewById(R.id.shareFoodlist);
           // yorumlar_image=itemView.findViewById(R.id.yorumlarImage);
            yorumsayisi=itemView.findViewById(R.id.mini_yorumSayisiTextView);
            kalitekontrol=itemView.findViewById(R.id.kalitekontrol);
            ratingOrt=itemView.findViewById(R.id.mini_ratingTextView);
           // ratingStar=itemView.findViewById(R.id.ratingStarrr);
            layout=itemView.findViewById(R.id.ratingConstrait);
            miniBar= itemview.findViewById(R.id.mini_ratingBarFoodd);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent ( context, FoodDetail.class);
                    intent.putExtra("FoodId",list.get(getAdapterPosition()).getFoodId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }
    }
}
