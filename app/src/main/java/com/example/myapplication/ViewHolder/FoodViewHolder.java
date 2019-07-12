package com.example.myapplication.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name,price,yorumsayisi;
    public ImageView food_image,fav_image,sepet_image,share_image,yorumlar_image,kalitekontrol;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_image=(ImageView) itemView.findViewById(R.id.food_image);
        food_name=(TextView) itemView.findViewById(R.id.food_name);
        fav_image=(ImageView) itemView.findViewById(R.id.fav);
        price=itemView.findViewById(R.id.priceFoodList);
        sepet_image=itemView.findViewById(R.id.sepeteEkleView);
       share_image=itemView.findViewById(R.id.shareFoodlist);
       yorumlar_image=itemView.findViewById(R.id.yorumlarImage);
        yorumsayisi=itemView.findViewById(R.id.yorumSayisiTextView);
        kalitekontrol=itemView.findViewById(R.id.kalitekontrol);
        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
