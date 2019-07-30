package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    Button btnSignIn,btnSignUp;
    TextView txtSlogan;

    private FirebaseAuth mAuth;
    //  private ProgressDialog progressDialog;
    private FirebaseUser fUser;
    private String yetki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn=(Button) findViewById(R.id.btnSignIn);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);



       if(FirebaseAuth.getInstance().getCurrentUser() != null){
           Query query = FirebaseDatabase.getInstance().getReference().child("User").orderByChild("phone").equalTo(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
           query.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   Log.d("girdi ", "asdasdasdasdasdasdasdasd");
                   if (dataSnapshot.exists()) {
                       //TODO: Burdaki loglar program bittiğinde silinecek.
                       Log.d("email: ", "childerns::::" + " - " + dataSnapshot.getChildren().toString());
                       // dataSnapshot is the "issue" node with all children with id 0
                       for(DataSnapshot datas : dataSnapshot.getChildren()) {
                           yetki=datas.child("yetki").getValue().toString();
                           kontrol(yetki);
                           ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                           progressDialog.setMessage("Bilgileriniz yükleniyor");
                           progressDialog.show();
                       }
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
       }



        //txtSlogan=(TextView) findViewById(R.id.txtSlogan);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent signIn=new Intent(MainActivity.this,LoginEkran.class);
                startActivity(signIn);

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUp=new Intent(MainActivity.this,KayitOlEkran.class);
                startActivity(signUp);

            }
        });

    }

    private void kontrol(String yetki) {

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            if(yetki.equals("satici")){
                Intent intent=new Intent(MainActivity.this,Admin_Activity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent=new Intent(MainActivity.this,Home.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
