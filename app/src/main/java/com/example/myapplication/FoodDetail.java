package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.example.myapplication.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener{

    private Dialog epicdialog;
    private Button alisveriseDevam;
    private Button sepeteGitDevam;
    private ImageView alertDismissButton;
    private FloatingActionButton btnRating;
    private RatingBar ratingBar;
    private DatabaseReference rating_ref;
    private TextView avgText;
    private int yorum_sayisi;
    private int sum;
    TextView food_name,food_price,food_description,mRatingScale;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
 //   FloatingActionButton btnCart;
  Button btnCart;

    ElegantNumberButton numberButton;
    String foodId;
    FirebaseDatabase database;
    DatabaseReference food;
    Food currentFood;
    String key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //.Firebase
        database =FirebaseDatabase.getInstance();
        food=database.getReference("Food");
        rating_ref=FirebaseDatabase.getInstance().getReference("Rating");

        epicdialog=new Dialog(FoodDetail.this,R.style.AppTheme_NoActionBar);

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart=  findViewById(R.id.btnCart);

        btnRating=findViewById(R.id.btnRating);
        ratingBar=findViewById(R.id.ratingBar);
        avgText=findViewById(R.id.avg);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });



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
                if(Common.isConnectedToInternet(getBaseContext())){
                    getDetailFood(foodId);
                    getRatingFood(foodId);}
                else {
                    Toast.makeText(FoodDetail.this, "İnternet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show();
                    return;
                }
            }



    }

    private void getRatingFood(final String foodId) {

        FirebaseDatabase.getInstance().getReference("Rating").child(foodId).addValueEventListener(new ValueEventListener() {
            int count=0;
            float sum=0;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Rating r= snapshot.getValue(Rating.class);
                    if(r.getFoodId().equals(foodId)){
                        count++;
                        sum += Float.parseFloat(r.getRateValues());
                    }
                }
                if( count !=0){
                    float avg=(sum/count);
                    String x = new DecimalFormat("#,#0.0").format(avg);
                    ratingBar.setRating(avg);
                    avgText.setText(x+"");
                }

            }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void showRatingDialog() {
        new AppRatingDialog.Builder().
                setPositiveButtonText("Gonder")
                .setNegativeButtonText("Iptal")
                .setNoteDescriptions(Arrays.asList("Cok kotu","kotu","iyi","Cok iyi","Mükemmel"))
                .setDefaultRating(1)
                .setTitle("Derecelendir")
                .setDescription("Lütfen seceneginizi belirtiniz")
                .setTitleTextColor(R.color.colorAccent)
                .setHint("Yorumunuzu buraya giriniz")
                .setHintTextColor(R.color.colorPrimaryDark)
                .setCommentTextColor(R.color.colorPrimaryDark)
                .setCommentBackgroundColor(R.color.acikGri)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .setDescriptionTextColor(R.color.colorPrimaryDark)
                .create(FoodDetail.this)
                .show();


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


               // collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName().toUpperCase());

                food_description.setText(currentFood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {

        final Rating rating=new Rating(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                foodId,
                String.valueOf(i),
                s
        );

        rating_ref.child(Common.currentUser.getPhone()).child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.child(Common.currentUser.getPhone()).child(foodId).exists()){
                 rating_ref.child(foodId).child(Common.currentUser.getPhone()).removeValue();
                 rating_ref.child(foodId).child(Common.currentUser.getPhone()).setValue(rating);
             }else{
                 rating_ref.child(foodId).child(Common.currentUser.getPhone()).setValue(rating);
             }
                Toast.makeText(FoodDetail.this, "Derecelendirginiz icin tesekkürler", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
