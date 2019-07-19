package com.example.myapplication;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Database.Database;
import com.example.myapplication.Helper.RecyclerItemTouchHelper;
import com.example.myapplication.Interface.RecyclerItemTouchHelperListener;
import com.example.myapplication.Model.Order;
import com.example.myapplication.Model.Request;
import com.example.myapplication.Notification.Client;
import com.example.myapplication.Notification.Data;
import com.example.myapplication.Notification.MyResponse;
import com.example.myapplication.Notification.Sender;
import com.example.myapplication.Notification.Tokens;
import com.example.myapplication.ViewHolder.CartAdapter;
import com.example.myapplication.ViewHolder.CartViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private Dialog epicdialog;
    private Button siparisBitir;
    private Button adresEkle;
    private ImageView kapat;
    private TextView siparisKisi;
    private TextView adresListesi;
    private ConstraintLayout siparisTamamla;
    private ConstraintLayout adresConstrait;
    private Button ekleAdres;
    private EditText iledittext;
    private EditText ilceEdittext;
    private EditText mahalleEdittext;
    private EditText sokakEdittext;
    private EditText numaraEditext;
    private  String adresid;
    private List<Request> nlist;
    private String firebaseAdres;
    private Spinner adresSpinner;
    private ArrayList<String> listAdres;

    APIService apiService;
    boolean notify=false;

    String key;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    private TextView hosgeldinizKisisi;
    Button btnPlace;

    List<Order> cart=new ArrayList<>();
    CartAdapter adapter;

    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //Firebase
        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        adresid=UUID.randomUUID().toString();
        nlist=new ArrayList<>();
        listAdres=new ArrayList<>();

        //adresSpinner=findViewById(R.id.spinnerAdres);

        recyclerView=(RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this  );
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback simpleCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        txtTotalPrice=(TextView) findViewById(R.id.total);

        epicdialog=new Dialog(Cart.this,R.style.AppTheme_NoActionBar);

        btnPlace=(Button) findViewById(R.id.btnPlaceOrder);

        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);

       FirebaseDatabase.getInstance().getReference("Adres").child(Common.currentUser.getName()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if(!dataSnapshot.exists()){
               HashMap<String,Object> hashMap=new HashMap<>();
               hashMap.put("Adres","");

               DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Adres");
               reference2.child(Common.currentUser.getName()).setValue(hashMap);
           }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.size()>0){
                    showAlertDialog();
                }
                else
                    Toast.makeText(Cart.this, "Sepetiniz boş", Toast.LENGTH_SHORT).show();
            }
        });
        
        loadListFood();


    }

    private void showAlertDialog() {

      /*  AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Son Adım!");
        alertDialog.setMessage("Adresinizi giriniz");

        final EditText edtAddress=new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );


                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

              //  FirebaseDatabase.getInstance().getReference("Adresler").child(request.getName()).setValue(edtAddress.getText().toString());


                new Database(getBaseContext()).clearChart();
                Toast.makeText(Cart.this,"Teşekkürler,Adres eklendi",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();*/
        epicdialog.setContentView(R.layout.order_finish_dialog);
        epicdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        epicdialog.show();

        siparisBitir=epicdialog.findViewById(R.id.siparisBitirButton);
        adresEkle=epicdialog.findViewById(R.id.adresEkleButton);
        kapat=epicdialog.findViewById(R.id.siparisCloseButton);
        siparisKisi=epicdialog.findViewById(R.id.siparisTamamlaKisiAdi);
        adresListesi=epicdialog.findViewById(R.id.adresListesiSpinner);
        siparisTamamla=epicdialog.findViewById(R.id.tamamlaEkranConstrait);
        adresConstrait=epicdialog.findViewById(R.id.adresEkleConstrait);
        ekleAdres=epicdialog.findViewById(R.id.ekleAdres);
        iledittext=epicdialog.findViewById(R.id.ilEdittext);
        ilceEdittext=epicdialog.findViewById(R.id.ilceEdittext);
        mahalleEdittext=epicdialog.findViewById(R.id.mahalleEdittext);
        sokakEdittext=epicdialog.findViewById(R.id.sokaEdittext);
        numaraEditext=epicdialog.findViewById(R.id.numaraEdittext);
        adresListesi=epicdialog.findViewById(R.id.adresListesiSpinner);
        hosgeldinizKisisi=epicdialog.findViewById(R.id.siparisTamamlaKisiAdi);

        hosgeldinizKisisi.setText(Common.currentUser.getName());

        //// Kapatma
        kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicdialog.dismiss();
            }
        });
         //// Kapatma
        adresEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siparisTamamla.setVisibility(View.GONE);
                adresConstrait.setVisibility(View.VISIBLE);
            }
        });



        FirebaseDatabase.getInstance().getReference("Adres").child(Common.currentUser.getName()).child("Adres").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String adres=dataSnapshot.getValue().toString();
                if(adres.equals("")){
                    adresListesi.setText("Adresiniz bulunmamaktadır");
                    siparisBitir.setEnabled(false);
                }else{
                    siparisBitir.setEnabled(true);
                    adresListesi.setText(adres);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        siparisBitir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Adres").child(Common.currentUser.getName()).child("Adres").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.toString());

                       // System.out.println("Value :"+ad);

                        if(dataSnapshot.exists()){
                            String ad=dataSnapshot.getValue().toString();
                            Request request=new Request(
                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    ad,
                                    txtTotalPrice.getText().toString(),
                                    cart
                            );

                            requests.child(String.valueOf(System.currentTimeMillis()))
                                    .setValue(request);

                            //  FirebaseDatabase.getInstance().getReference("Adresler").child(request.getName()).setValue(edtAddress.getText().toString());


                            new Database(getBaseContext()).clearChart();
                          //  Toast.makeText(Cart.this,"Teşekkürler,Adres eklendi",Toast.LENGTH_SHORT).show();

                                System.out.println("gondercek");
                                sendNotification("5438858037",Common.currentUser.getName(),String.valueOf(System.currentTimeMillis())+" nolu siparişiniz bulunmaktadır.");





                            finish();
                            epicdialog.dismiss();

                        }else
                        {
                            Toast.makeText(Cart.this, "Kayıtlı Adresiniz bulunmamaktadır", Toast.LENGTH_SHORT).show();
                            epicdialog.dismiss();
                  /*          siparisTamamla.setVisibility(View.GONE);
                            adresConstrait.setVisibility(View.VISIBLE);*/
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        });


        ekleAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                HashMap<String,Object> hashMap=new HashMap<>();
                String adres=mahalleEdittext.getText().toString()+" "+sokakEdittext.getText().toString()+" "+numaraEditext.getText().toString()+" "+ilceEdittext.getText().toString()+"/"+iledittext.getText().toString();
                hashMap.put("Adres",adres);

                DatabaseReference reference2=FirebaseDatabase.getInstance().getReference("Adres");
                reference2.child(Common.currentUser.getName()).setValue(hashMap);
                Toast.makeText(Cart.this, "Adresiniz eklendi", Toast.LENGTH_SHORT).show();


                adresConstrait.setVisibility(View.GONE);
                siparisTamamla.setVisibility(View.VISIBLE);

            }
        });


    }


    private void sendNotification(final String receiver, final String username, final String message){
        System.out.println("sendicinde");
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo("5438858037");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Tokens tokens =snapshot.getValue(Tokens.class);
                    Data data=new Data(Common.currentUser.getPhone(),R.mipmap.ic_launcher,username+":"+message,"Yeni Siparis","5438858037");
                  //  System.out.println("karsi"+userid2);
                    Sender sender=new Sender(data, tokens.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() ==200){
                                        if(response.body().success!=1){
                                            Toast.makeText(Cart.this, "Bildirim Hatasi", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {

        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);



        int total=0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("tr","TR");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }




    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder)
        {
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId());

            int total=0;
            List<Order> orders=new Database(getBaseContext()).getCarts();
            for(Order item:orders)
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale=new Locale("tr","TR");
            NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));



          Snackbar snackbar=Snackbar.make(rootLayout,"Silindi",Snackbar.LENGTH_LONG);
            snackbar.setAction("Geri", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToChart(deleteItem);


                    int total=0;
                    List<Order> orders=new Database(getBaseContext()).getCarts();
                    for(Order item:orders)
                        total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale locale=new Locale("tr","TR");
                    NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

                    txtTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
