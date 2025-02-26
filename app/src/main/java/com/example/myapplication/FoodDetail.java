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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.myapplication.ViewHolder.FoodAdapter;
import com.example.myapplication.ViewHolder.YorumlarAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener{

    private DatabaseReference reference;
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
    private RecyclerView yorumlarRecyler;
    private ArrayList<Rating> list;
    private ArrayList<Food> flist;
    //FirebaseRecyclerAdapter<Food, FoodViewHolder> fadapter;
    private YorumlarAdapter adapter;
    private TextView yorumlarıGor;
    private RecyclerView sizinicinRecyler;
    TextView food_name,food_price,food_description,mRatingScale;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    // FloatingActionButton btnCart;
    Button btnCart;
    ElegantNumberButton numberButton;
    String foodId;
    FirebaseDatabase database;
    DatabaseReference food;
    Food currentFood;
    private String key;
    private String populerkey;
    private String kisikey;
    String keyim;
    private String currentName;
     FoodAdapter fadapter;
    private DatabaseReference foodList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //.Firebase
        database =FirebaseDatabase.getInstance();
        food=database.getReference("Food");
        rating_ref=FirebaseDatabase.getInstance().getReference("Rating");
        list=new ArrayList<Rating>();
        flist=new ArrayList<Food>();



        foodList=database.getReference("Food");

        epicdialog=new Dialog(FoodDetail.this,R.style.AppTheme_NoActionBar);
        yorumlarıGor=findViewById(R.id.yorumlarıGör);
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

        yorumlarRecyler=findViewById(R.id.detailYorumlarRecycler);
        sizinicinRecyler=findViewById(R.id.sizinicinRecyler);

        yorumlarıGor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoodDetail.this,Yorumlar.class);
                intent.putExtra("FoodId",foodId);
                startActivity(intent);
            }
        });

        yorumlarRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        sizinicinRecyler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentName=dataSnapshot.getValue().toString();
                    System.out.println("Current"+currentName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference= FirebaseDatabase.getInstance().getReference().child("Rating");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    key=snapshot.getKey();
                    goster(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        FirebaseDatabase.getInstance().getReference("Favori").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         for (DataSnapshot snapshot : dataSnapshot.getChildren()){
             kisikey=snapshot.getKey();
             System.out.println("kisikey"+kisikey);

             FirebaseDatabase.getInstance().getReference("Favori").child(kisikey).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     for( DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                         populerkey=snapshot1.getKey();
                         populerlist(populerkey);
                         //populer(populerkey);
                         System.out.println("Populer key"+populerkey);
                     }

                 }
                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });

         }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void populerlist(final String populerkey) {

        FirebaseDatabase.getInstance().getReference("Food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


        if(dataSnapshot.exists()){



        for (DataSnapshot snapshot  : dataSnapshot.getChildren()){
            Food f= snapshot.getValue(Food.class);
            if( snapshot.getKey().equals(populerkey)){
                flist.add(f);
            }

            }
           fadapter=new FoodAdapter( FoodDetail.this,flist);
           sizinicinRecyler.setAdapter(fadapter);

            }


                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    private void goster(String key) {

        FirebaseDatabase.getInstance().getReference("Rating").child(key).orderByChild("time").limitToLast(2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Rating r= snapshot.getValue(Rating.class);

                    if(r.getFoodId().equals(foodId)){
                        list.add(r);
                    }
                }
                    adapter=new YorumlarAdapter(FoodDetail.this,list);
                    yorumlarRecyler.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                currentName,
                foodId,
                String.valueOf(i),
                s,
                String.valueOf(System.currentTimeMillis())
        );

        rating_ref.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(foodId).exists()){
                 rating_ref.child(foodId).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).removeValue();
                 rating_ref.child(foodId).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(rating);
             }else{
                 rating_ref.child(foodId).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(rating);
             }
                Toast.makeText(FoodDetail.this, "Derecelendirginiz icin tesekkürler", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
