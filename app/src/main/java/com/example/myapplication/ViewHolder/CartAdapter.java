package com.example.myapplication.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.myapplication.Cart;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Order;
import com.example.myapplication.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData=new ArrayList<>();
    private Cart cart;
    int price;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(cart);
        View itemView = layoutInflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int position) {

       // TextDrawable drawable=TextDrawable.builder()
        //        .buildRound(""+listData.get(position).getQuantity(), Color.RED);
     //   cartViewHolder.img_cart_count.setImageDrawable(drawable);
        Locale locale=new Locale("tr","TR");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));


        cartViewHolder.txt_price.setText(fmt.format(price));
        cartViewHolder.btn_quantity.setNumber(listData.get(position).getQuantity());
        cartViewHolder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order=listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);


                int total=0;

                List<Order> orders=new Database(cart).getCarts();
                for(Order item:orders){
                    total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    price=(Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));}

                Locale locale=new Locale("tr","TR");
                NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

                cart.txtTotalPrice.setText(fmt.format(total));
                cartViewHolder.txt_price.setText(fmt.format(price));






            }
        });


        cartViewHolder.txt_cart_name.setText(listData.get(position).getProductName());



    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){return listData.get(position);}

    public void removeItem(int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }


}
