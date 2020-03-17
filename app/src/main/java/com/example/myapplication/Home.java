package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Interface.ItemClickListener;
import com.example.myapplication.Model.Banner;
import com.example.myapplication.Model.Category;
import com.example.myapplication.Notification.Tokens;
import com.example.myapplication.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    ImageView headerImage;


    TextView txtFullName;

    CounterFab fab;
   FirebaseAuth auth;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    private HashMap<String,String> imageList;
    private SliderLayout mSlider;
    private ImageView homeuser;
    private String currentName;
    FirebaseDatabase fbs;
    DatabaseReference drf;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //new Database(getApplicationContext()).clearChart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("");


        // ortalamk için uğraşş



        setSupportActionBar(toolbar);





        //firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        fbs=FirebaseDatabase.getInstance();
        drf=fbs.getReference("User");

         fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(Home.this,Cart.class);
                startActivity(cartIntent);

            }
        });

        fab.setCount(new Database(this).getCountCart());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        homeuser=findViewById(R.id.home_user);

        homeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Home.this, Profil.class);
                startActivity(intent);
            }
        });



        //menü
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recycler_menu.setLayoutManager(layoutManager);

        if(Common.isConnectedToInternet(this))
            loadMenu();
        else
        {
            Toast.makeText(Home.this, "İnternet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show();
            return;
        }

      //  updateToken(FirebaseInstanceId.getInstance().getToken());

        slider();
        menuheader();

    }

    private void menuheader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //kullanıcı adı
        View headerView=navigationView.getHeaderView(0);
        txtFullName=(TextView) headerView.findViewById(R.id.txtFullName);
        headerImage=headerView.findViewById(R.id.headerImage);
       /*drf.child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentName=dataSnapshot.child("name").getValue().toString();
                    txtFullName.setText(currentName.toUpperCase());

                    if(dataSnapshot.child("photourl").getValue().toString().equals("default")){
                        headerImage.setImageResource(R.drawable.ic_male_user_52px);
                    }else{
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("photourl").getValue().toString()).into(headerImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void slider() {

        mSlider=findViewById(R.id.slider);
        imageList=new HashMap<>();

        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Banner");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for ( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Banner banner= snapshot.getValue(Banner.class);
                    imageList.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for(String keyi:imageList.keySet()){
                    String[] keySplit =keyi.split("@@@");
                String nameFood= keySplit[0];
                String idofFoof= keySplit[1];

                    final TextSliderView textSliderView= new TextSliderView(getBaseContext());
                    textSliderView.description(nameFood)
                            .image(imageList.get(keyi))
                            .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                   Intent intent=new Intent(Home.this,FoodDetail.class);
                                   intent.putExtras(textSliderView.getBundle());
                                   startActivity(intent);
                                }
                            });

                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idofFoof);
                    mSlider.addSlider(textSliderView);

                    ref.removeEventListener(this);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });//ref

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);

    }//sliderdunction

    private void updateToken(String token){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Tokens token1 = new Tokens(token);
        reference.child(Common.currentUser.getPhone()).setValue(token1);
    }

    private void loadMenu() {

         adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category.orderByChild("name")) {
            @Override
            protected void populateViewHolder(@NonNull MenuViewHolder viewHolder,@NonNull final Category model, int position) {
                viewHolder.txtMenuName.setText(model.getName());
                /*Picasso.with(getBaseContext()).load(model.getImage())
                         .into(viewHolder.imageView);*/

                System.out.println("İmageURL"+model.getImage());

            //    Picasso.get().load(model.getImage()).resize(100,100).centerCrop().into(viewHolder.imageView);


               /* if(model.getImage().equals("")){
                    Toast.makeText(Home.this, "Url boş", Toast.LENGTH_SHORT).show();
                }else{*/
                    Glide.with(Home.this).load(model.getImage()).into(viewHolder.imageView);

             //   }



                final Category clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //categoryid alındıve foodliste gönderildi
                        Intent foodList=new Intent(Home.this,FoodList.class);
                        //
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        foodList.putExtra("kategori_ismi",model.getName());
                        startActivity(foodList);

                    }
                });


            }
        };

        //recycler_menu.setAdapter(adapter);
        adapter.startListening();
        recycler_menu.setAdapter(adapter);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        if(item.getItemId()==R.id.refresh)
            loadMenu();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id==R.id.nav_menu){

        }else if (id==R.id.nav_cart){

            Intent cartIntent=new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        }else if (id==R.id.nav_orders){

            Intent orderIntent=new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        }else if (id==R.id.nav_log_out) {


            FirebaseAuth.getInstance().signOut();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }else if(id==R.id.nav_change_pwd){
            showChangePasswordDialog();
        }
        else if( id==R.id.nav_fav){
                Intent intent=new Intent(Home.this,Favoriler.class);
                startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Parola Güncelle");
        alertDialog.setMessage("Lütfen bilgileri doldurunuz");

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View layout_pwd = layoutInflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText edtPassword =(MaterialEditText)layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword =(MaterialEditText)layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword =(MaterialEditText)layout_pwd.findViewById(R.id.edtRepeatPassword);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final android.app.AlertDialog waitingDialog=new SpotsDialog(Home.this);
                waitingDialog.show();

                if(edtPassword.getText().toString().equals(Common.currentUser.getPassword())){
                    if(edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())){
                        Map<String,Object> passwordUpdate=new HashMap<>();
                        passwordUpdate.put("password",edtNewPassword.getText().toString());

                        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this,"Parola güncellendi",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this, "Yeni parolalar eşleşmiyor!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this,"Eski parola yanlış !!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart());

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSlider.stopAutoCycle();
    }
}
