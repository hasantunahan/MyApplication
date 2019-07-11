package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Model.Rating;
import com.example.myapplication.R;
import com.example.myapplication.Yorumlar;

import java.util.ArrayList;
import java.util.Arrays;

public class YorumlarAdapter extends RecyclerView.Adapter<YorumlarAdapter.MyViewHolder> {

    Context context;
    ArrayList<Rating> list;

    public YorumlarAdapter(Context c,ArrayList<Rating> a){
        context=c;
        list=a;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.yorumlar_card,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.name.setText(list.get(i).getUserName());
        myViewHolder.yorum.setText(list.get(i).getComment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,yorum;

        public MyViewHolder(View itemview){
            super(itemview);
            name=itemview.findViewById(R.id.yorumlarName);
            yorum=itemview.findViewById(R.id.yorumlarYorum);
        }
    }
}
