package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Food;
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
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CounterFab fab;
    FirebaseDatabase database;
    DatabaseReference foodList;
    private Intent intent;
    private TextView urunlistesi_ismi;
    String categoryId="";

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
        intent=getIntent();
        String kategori_ismi=intent.getStringExtra("kategori_ismi");
        urunlistesi_ismi.setText(kategori_ismi);

        //Fİrebase
        database = FirebaseDatabase.getInstance();
        foodList=database.getReference("Food");

        localDB =new Database(this);

        recyclerView =(RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
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
                foodList.orderByChild("Name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

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
        foodList.orderByChild("MenuId").equalTo(categoryId)

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
                foodList.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                if(localDB.isFavorite(adapter.getRef(position).getKey()))
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
                });

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                      Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                      foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                      startActivity(foodDetail);

                    }
                });

            }
        };


        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());


    }

}
