package com.example.myapplication.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress,tarih,toplamtutar,urunler;
    public ImageView durumimage;
    public Button detaylariGor,detaylariGizle;
    public RecyclerView detaylarRecyler;
    public ItemClickListener itemClickListener;

    public ImageView btn_delete;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress=(TextView) itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone=(TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus=(TextView) itemView.findViewById(R.id.order_status);
        btn_delete=(ImageView)itemView.findViewById(R.id.btn_delete);
        toplamtutar=itemView.findViewById(R.id.toplamtutar);
        tarih=itemView.findViewById(R.id.siparisTarihi);
        durumimage=itemView.findViewById(R.id.durumImage);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {



    }
}
