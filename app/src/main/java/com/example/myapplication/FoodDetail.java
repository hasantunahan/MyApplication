package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Model.Food;
import com.example.myapplication.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FoodDetail extends AppCompatActivity {

    private Dialog epicdialog;
    private Button alisveriseDevam;
    private Button sepeteGitDevam;
    private ImageView alertDismissButton;

    TextView food_name,food_price,food_description,mRatingScale;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;
    String foodId;
    FirebaseDatabase database;
    DatabaseReference food;
    Food currentFood;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //.Firebase
        database =FirebaseDatabase.getInstance();
        food=database.getReference("Food");
        epicdialog=new Dialog(FoodDetail.this,R.style.AppTheme_NoActionBar);

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart= (FloatingActionButton) findViewById(R.id.btnCart);


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToChart(new Order(


                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount()



                ));

                Toast.makeText(FoodDetail.this,"Sepete eklendi",Toast.LENGTH_SHORT).show();

                sepetDialog();


            }
        });

        food_description=(TextView)findViewById(R.id.food_description);
        food_name=(TextView)findViewById(R.id.food_name);
        food_price=(TextView)findViewById(R.id.food_price);
        food_image=(ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        if(getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty())
            {
                if(Common.isConnectedToInternet(getBaseContext()))
                    getDetailFood(foodId);
                else {
                    Toast.makeText(FoodDetail.this, "İnternet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show();
                    return;
                }
            }



    }

    private void sepetDialog(){
        epicdialog.setContentView(R.layout.shopping_finish_or_add_cart);
        epicdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicdialog.show();
        alisveriseDevam=epicdialog.findViewById(R.id.alisveriseDevamButton);
        sepeteGitDevam=epicdialog.findViewById(R.id.sepeteGitButton);
        alertDismissButton=epicdialog.findViewById(R.id.alertDismissButton);


        alertDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicdialog.dismiss();
            }
        });


        alisveriseDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
                epicdialog.dismiss();
            }
        });

        sepeteGitDevam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Cart.class);
                startActivity(intent);
                epicdialog.dismiss();
            }
        });



    }


    private void getDetailFood(String foodId) {

        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              currentFood=dataSnapshot.getValue(Food.class);


                /*    Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);*/

                //Picasso.get().load(currentFood.getImage()).resize(100,100).centerCrop().into(food_image);
                Glide.with(getApplicationContext()).load(currentFood.getImage()).into(food_image);


                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
