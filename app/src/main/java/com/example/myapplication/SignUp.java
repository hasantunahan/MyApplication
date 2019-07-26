package com.example.myapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.Common.Common;
import com.example.myapplication.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone,edtName,edtPassword;
    Button btnSignUp;
    private Spinner yetkiSpinner;
    private Dialog epicdialog;
    private Button sirketbilgilerikaydetButton;
    private EditText sirketAdi;
    private EditText vergiNumarasi;
    private EditText sirketAdresi;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName=(MaterialEditText) findViewById(R.id.edtName);
        edtPassword=(MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone=(MaterialEditText) findViewById(R.id.edtPhone);
        yetkiSpinner=findViewById(R.id.yetkiSpinner);
        btnSignUp=(Button) findViewById(R.id.btnSignUp);
        epicdialog=new Dialog(SignUp.this,R.style.AppTheme_NoActionBar);




        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.yetki,R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        yetkiSpinner.setAdapter(adapter);







        //Firebase
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");






        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(yetkiSpinner.getSelectedItem().toString().equals("satici")){
                    epicdialog.setContentView(R.layout.sirket_bilgileri);
                    epicdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    epicdialog.show();

                    sirketbilgilerikaydetButton=epicdialog.findViewById(R.id.sirketKaydetButton);
                    sirketAdi=epicdialog.findViewById(R.id.sirketAdiEdittext);
                    sirketAdresi=epicdialog.findViewById(R.id.sirketAdresiEdittext);
                    vergiNumarasi=epicdialog.findViewById(R.id.VergiNumarasiEdittext);


                    sirketbilgilerikaydetButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            User user = new User(edtName.getText().toString(), edtPassword.getText().toString(),"satici","default");
                            table_user.child(edtPhone.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Kayıt Başarılı !", Toast.LENGTH_SHORT).show();
                           // finish();

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("SirketSahibi",edtName.getText().toString());
                            hashMap.put("SirketAdi",sirketAdi.getText().toString());
                            hashMap.put("VergiNumarasi",vergiNumarasi.getText().toString());
                            hashMap.put("SirketAdresi",sirketAdresi.getText().toString());
                            hashMap.put("TelefonNumarasi",edtPhone.getText().toString());
                            FirebaseDatabase.getInstance().getReference("SirketBilgileri").child(edtName.getText().toString()).setValue(hashMap);
                            Toast.makeText(SignUp.this, "Basari ile eklediniz kayit isleminiz tamamlandi", Toast.LENGTH_SHORT).show();


                            epicdialog.dismiss();
                            finish();


                        }
                    });


                }else{

                    if(Common.isConnectedToInternet(getBaseContext())) {
                        final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                        mDialog.setMessage("Lütfen Bekleyiniz...");
                        mDialog.show();

                        table_user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                System.out.println("CHİLD"+dataSnapshot.child(edtPhone.getText().toString()));

                                //numara zaten kayıtlıysa
                                if (dataSnapshot.child(edtPhone.getText().toString()).equals(edtPhone.getText().toString())) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Bu numara zaten kayıtlı", Toast.LENGTH_SHORT).show();

                                } else if (edtName.getText().toString().isEmpty() || edtPhone.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Boşlukları doldurunuz ", Toast.LENGTH_SHORT).show();


                                } else {

                                    if(yetkiSpinner.getSelectedItem().toString().equals("satici")){
                                        mDialog.dismiss();
                                        User user = new User(edtName.getText().toString(), edtPassword.getText().toString(),"satici","default");
                                        table_user.child(edtPhone.getText().toString()).setValue(user);
                                        Toast.makeText(SignUp.this, "Kayıt Başarılı !", Toast.LENGTH_SHORT).show();
                                        finish();


                                    }else{
                                        mDialog.dismiss();
                                        User user = new User(edtName.getText().toString(), edtPassword.getText().toString(),"musteri","default");
                                        table_user.child(edtPhone.getText().toString()).setValue(user);
                                        Toast.makeText(SignUp.this, "Kayıt Başarılı !", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }



                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Toast.makeText(SignUp.this, "İnternet bağlantınızı kontrol ediniz", Toast.LENGTH_SHORT).show();
                        return;}//sonelse

                }




            }
        });





    }
}
