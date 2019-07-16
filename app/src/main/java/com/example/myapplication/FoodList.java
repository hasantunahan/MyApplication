package com.example.myapplication;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Food;
import com.example.myapplication.Model.Order;
import com.example.myapplication.Model.Rating;
import com.example.myapplication.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CounterFab fab;
    FirebaseDatabase database;
    DatabaseReference foodList;
    private Intent intent;
    private TextView urunlistesi_ismi;
    private ImageView geriButton;
    String categoryId="";
    private int begenisayisi;
    private String foodidKey;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //arama
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar searchView;

    Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);



        //categori ismi icin
        urunlistesi_ismi=findViewById(R.id.urunlistesi);
        geriButton=findViewById(R.id.geriFoodlist);

        intent=getIntent();
        String kategori_ismi=intent.getStringExtra("kategori_ismi");
        urunlistesi_ismi.setText(kategori_ismi);

        geriButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoodList.this,Home.class);
                startActivity(intent);
            }
        });

        //Fİrebase
        database = FirebaseDatabase.getInstance();
        foodList=database.getReference("Food");

        localDB =new Database(this);

        recyclerView =(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        //intent çapırdık

        if(getIntent()!=null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId !=null)
        {
            if(Common.isConnectedToInternet(getApplicationContext()))
                loadListFood(categoryId);
            else{
                Toast.makeText(FoodList.this, "İnternet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        searchView=(MaterialSearchBar) findViewById(R.id.searchBar);
        searchView.setHint("Buradan arayabilirsiniz");

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(FoodList.this,Cart.class);
                startActivity(cartIntent);

            }
        });

        fab.setCount(new Database(this).getCountCart());

        //Önerileri yükle
       loadSuggest();

       searchView.setLastSuggestions(suggestList);
       searchView.setCardViewElevation(10);
       searchView.addTextChangeListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

               List<String> suggest=new ArrayList<String>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(searchView.getText().toLowerCase()))
                        suggest.add(search);
                }

                searchView.setLastSuggestions(suggest);
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
           @Override
           public void onSearchStateChanged(boolean enabled) {
               if(!enabled)
                   recyclerView.setAdapter(adapter);
           }

           @Override
           public void onSearchConfirmed(CharSequence text) {
               startSearch(text);

           }

           @Override
           public void onButtonClicked(int buttonCode) {

           }
       });
    }

    private void startSearch(CharSequence text) {
        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                String buyuk=model.getName().toUpperCase();
                viewHolder.food_name.setText(buyuk);
              /*  Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);*/

             Picasso.get().load(model.getImage()).resize(100,100).centerCrop().into(viewHolder.food_image);
                 //    Glide.with(getApplicationContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });
            }
        };//

        recyclerView.setAdapter(searchAdapter);
    }//


    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {

        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName().toUpperCase());
                viewHolder.price.setText(model.getPrice()+" ₺");
               /* Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);*/

               //Glide.with(FoodList.this).load(model.getImage()).into(viewHolder.food_image);

               // Picasso.get().load(model.getImage()).resize(100,100).centerCrop().into(viewHolder.food_image);

                Picasso.get().load(model.getImage()).resize(1000,1000).centerCrop().into(viewHolder.food_image);

                viewHolder.sepet_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        new Database(getBaseContext()).addToChart(new Order(


                                adapter.getRef(position).getKey(),
                                model.getName(),
                               "1",
                                model.getPrice().toString(),
                                model.getDiscount()



                        ));

                        Toast.makeText(FoodList.this,"Sepete eklendi",Toast.LENGTH_SHORT).show();
                        fab.setCount(new Database(getApplicationContext()).getCountCart());

                    }
                });

                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


               Intent shareIntent =new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String sharedBody="body";
                String shareSub="sub";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,sharedBody);
                startActivity(Intent.createChooser(shareIntent,"Paylaş"));

                    }
                });

                viewHolder.yorumlar_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(FoodList.this,Yorumlar.class);
                        intent.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });


//likeeee
               /* if(localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, "Favorilerden silindi", Toast.LENGTH_SHORT).show();

                        }
                    }
                });*/




                ////fav_basla


                //viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                                final DatabaseReference fav_Ref=FirebaseDatabase.getInstance().getReference("Favori");

                                fav_Ref.child(Common.currentUser.getName()).child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if( dataSnapshot.exists()){
                                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    fav_Ref.child(Common.currentUser.getName()).child(adapter.getRef(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.exists()){
                                                HashMap<String,Object> hashMap=new HashMap<>();
                                                hashMap.put("name",model.getName());
                                                hashMap.put("image",model.getImage());
                                                hashMap.put("description",model.getDescription());
                                                hashMap.put("price",model.getPrice());
                                                hashMap.put("discount",model.getDiscount());
                                                hashMap.put("menuId",model.getMenuId());
                                                DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Favori");
                                                reference2.child(Common.currentUser.getName()).child(adapter.getRef(position).getKey()).setValue(hashMap);
                                                viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                                                Toast.makeText(FoodList.this, "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(FoodList.this,Favoriler.class);
                                                intent.putExtra("FoodId",adapter.getRef(position).getKey());
                                               // startActivity(intent);

                                            }else{
                                                fav_Ref.child(Common.currentUser.getName()).child(adapter.getRef(position).getKey()).removeValue();
                                                viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                                Toast.makeText(FoodList.this, "Favorilerden silindi", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });





                ///fav_bitti


                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                      Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                      foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                      startActivity(foodDetail);

                    }
                });

                FirebaseDatabase.getInstance().getReference("Rating").child(adapter.getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                    int count=0;
                    float sum=0;
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Rating r= snapshot.getValue(Rating.class);
                                if(r.getFoodId().equals(adapter.getRef(position).getKey())){
                                    count++;
                                    sum += Float.parseFloat(r.getRateValues());
                                }
                            }
                            if( count !=0){
                                float avg=sum/count;
                                String x = new DecimalFormat("#,#0.0").format(avg);
                                viewHolder.yorumsayisi.setText("("+count+" yorum)");
                                if(avg >=4){
                                    //iewHolder.kalitekontrol.setVisibility(View.VISIBLE);
                                  viewHolder.layout.setVisibility(View.VISIBLE);
                                  viewHolder.ratingStar.setVisibility(View.VISIBLE);
                                  viewHolder.ratingOrt.setVisibility(View.VISIBLE);
                                  viewHolder.ratingOrt.setText(x);

                                }
                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }//viewHolder bitiş
        };//fonk




        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());


    }

}
