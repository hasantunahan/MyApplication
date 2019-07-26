package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    Button btnSignIn,btnSignUp;
    TextView txtSlogan;

    private FirebaseAuth mAuth;
    //  private ProgressDialog progressDialog;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn=(Button) findViewById(R.id.btnSignIn);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(fUser != null){
            Intent intent=new Intent(MainActivity.this,Home.class);
            startActivity(intent);
            finish();
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

    @Override
    protected void onStart() {
        super.onStart();

    }

}
