package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Food;
import com.example.myapplication.Model.foods;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {

    Context context;
    ArrayList<foods> list;
    String [] url=new String[50];

    public OrderDetailAdapter(Context c,ArrayList<foods> a){
        context=c;
        list=a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.orderdetails,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

    myViewHolder.price.setText("Birim Fiyat :"+list.get(i).getPrice()+" ₺");
    int toplam=Integer.parseInt(list.get(i).getPrice())*Integer.parseInt(list.get(i).getQuantity());
    myViewHolder.toplamtutar.setText("Toplam : "+toplam+" TL");
    myViewHolder.miktar.setText("Miktar :"+list.get(i).getQuantity()+" adet");
    myViewHolder.name.setText(list.get(i).getProductName());

        FirebaseDatabase.getInstance().getReference("Food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Food f=snapshot.getValue(Food.class);
                    if(snapshot.getKey().equals(list.get(i).getProductId())){
                        url[i]=f.getImage().toString();
                        Glide.with(context).load(url[i]).into(myViewHolder.photoImage);
                    }

                }
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

        TextView name,price,toplamtutar,miktar;
        ImageView photoImage;

        public MyViewHolder(View itemview){
            super(itemview);

            photoImage=itemview.findViewById(R.id.orderDetailImage);
            name=itemview.findViewById(R.id.orderDetailName);
            price=itemview.findViewById(R.id.orderDetailPrice);
            toplamtutar=itemview.findViewById(R.id.orderDetailToplamfiyat);
            miktar=itemview.findViewById(R.id.miktarorderdetail);

        }
    }
}
