package com.example.myapplication.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Model.Rating;
import com.example.myapplication.Model.User;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        long tarih=Long.parseLong(list.get(i).getTime());
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm ");
        Date date = new Date(tarih);
     //   System.out.println();
        myViewHolder.name.setText(list.get(i).getUserName());
        myViewHolder.yorum.setText(list.get(i).getComment());
        myViewHolder.rating.setText(list.get(i).getRateValues()+".0");
        myViewHolder.ratingBar.setRating(Integer.parseInt(list.get(i).getRateValues()));
        myViewHolder.time.setText(formatter.format(date)+"");

        FirebaseDatabase.getInstance().getReference("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for ( DataSnapshot snapshot: dataSnapshot.getChildren()){
                User u=snapshot.getValue(User.class);
        if(snapshot.getKey().equals(list.get(i).getUserPhone())){


        if(u.getPhotourl().equals("default")){
        myViewHolder.photoImage.setImageResource(R.drawable.ic_male_user_52px);
        }else{
        Glide.with(context).load(u.getPhotourl()).into(myViewHolder.photoImage);
        }


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

        TextView name,yorum,rating,time;
        RatingBar ratingBar;
        ImageView photoImage;

        public MyViewHolder(View itemview){
            super(itemview);
            name=itemview.findViewById(R.id.yorumlarName);
            yorum=itemview.findViewById(R.id.yorumlarYorum);
            rating=itemview.findViewById(R.id.ratingYorumlar);
            ratingBar=itemview.findViewById(R.id.ratingBarYorumlar);
            time=itemview.findViewById(R.id.timeYorumTextView);
            photoImage=itemview.findViewById(R.id.photoImage);
        }
    }
}
