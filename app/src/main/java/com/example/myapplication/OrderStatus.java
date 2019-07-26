package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.Model.Request;
import com.example.myapplication.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


    }

    private void loadOrders(String phone) {
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatus.this, "Bu sipariş silinemez", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);


    }


    private void deleteOrder(final String key) {

        DialogInterface.OnClickListener dialog=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        requests.child(key)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(OrderStatus.this, new StringBuilder("Order")
                                                .append(key)
                                                .append(" Silindi!").toString()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(OrderStatus.this, "Silinmedi", Toast.LENGTH_SHORT).show();
                        
                        break;
                }

            }
        };

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Silmek istediğinize emin misiniz?").setPositiveButton("Evet",dialog)
                .setNegativeButton("Hayır",dialog).show();


    }

    private String  convertCodeToStatus(String status) {

        if(status.equals("0"))
            return "Hazırlanıyor";
        else if(status.equals("1"))
            return "Yolda";
        else
            return "Teslim edildi";

    }
}
