package com.example.myapplication;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class KayitOlEkran extends AppCompatActivity {

    private Spinner yetkispinner;
    private TextView dobEditText;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    Button signUpOkeyButton;
    EditText emailEditText;
    EditText passwordEditText;
    EditText usernameEditText;
    TextInputLayout passwordWrapper;
    TextInputLayout emailWrapper;
    TextInputLayout usernameWrapper;
    TextInputLayout nameWrapper;
    TextInputLayout surnameWrapper;
    EditText nameEditText;
    EditText surnameEditText;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private long longDOB;
    private FirebaseUser fUser;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String yetki;
    private boolean usernameKontrol = false;
    private boolean emailKontrol = false;
    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()){
                case R.id.usernameEditText:
                    if(!hasFocus) {
                        usernameKontrolFonksiyon();
                    }
                    break;

                case R.id.emailEditText:
                    if(!hasFocus){
                        emailKontrolFonksiyon();
                    }
                    break;
            }
        }
    };

    private void emailKontrolFonksiyon() {

    }

    private void usernameKontrolFonksiyon() {

    }

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
            //        "(?=.*[0-9])" +
                    "(?=.*[a-zA-Z])" +      //any letter
            //        "(?=.*['*!@#$%^&+=.,_-])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol_ekran);

        usernameWrapper = findViewById(R.id.usernameWrapper);
        emailEditText = findViewById(R.id.emailEditText);
        signUpOkeyButton = findViewById(R.id.signUpOkeyButton);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailWrapper = findViewById(R.id.emailWrapper);
        passwordWrapper = findViewById(R.id.passwordWrappper);
        yetkispinner = findViewById(R.id.genderSpinner);
        nameEditText = findViewById(R.id.nameEditText);
        nameWrapper = findViewById(R.id.nameWrapper);
        usernameEditText = findViewById(R.id.usernameEditText);
        database = FirebaseDatabase.getInstance();
       // progressDialog = new ProgressDialog();
        //progressDialog.setStyle(R.style.CustomAlertDialogStyle,R.style.CustomDialogTheme);
      //  progressDialog.setMessage("Lütfen Bekleyiniz");

        usernameEditText.setOnFocusChangeListener(focusChangeListener);
        emailEditText.setOnFocusChangeListener(focusChangeListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sex, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        yetkispinner.setAdapter(adapter);


        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();

        signUpOkeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString().trim();
//                surname = surnameEditText.getText().toString().trim();
                username = usernameEditText.getText().toString().trim();
                email = emailEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                yetki = yetkispinner.getSelectedItem().toString();
                if (!tumKontroller()){
//                    progressDialog.dismiss();
                    return;
                }
                if(internetBaglantiKontrol())
                    registerNewUser(email,password);
                else{
            //        progressDialog.dismiss();
                    Toast.makeText(KayitOlEkran.this, "Baglantinizi kontrol ediniz", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void registerNewUser(final String email ,final String password){

     //   progressDialog.setMessage(getResources().getString(R.string.username_kontrol));

        database.getReference("User").orderByChild("phone").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
               //     progressDialog.dismiss();
                    usernameWrapper.setErrorEnabled(true);
                    usernameWrapper.setError(getResources().getString(R.string.username_already));
                } else {
                    usernameWrapper.setErrorEnabled(false);
              //      progressDialog.setMessage(getResources().getString(R.string.email_control));
//                    if(!progressDialog.isShowing())
//                        progressDialog.show(getSupportFragmentManager(),"kayitol ekran");
                    database.getReference("User").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                            //    progressDialog.dismiss();
                                emailWrapper.setErrorEnabled(true);
                                emailWrapper.setError(getResources().getString(R.string.email_already));
                            } else {
                                emailWrapper.setErrorEnabled(false);
                                kayitTamamla();
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
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void kayitTamamla(){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        fUser = mAuth.getCurrentUser();
                        Objects.requireNonNull(fUser).reload();
                        final User user = new User(name,password,username,yetki,email,"default");
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(user.getPhone()).build();
                        fUser.updateProfile(userProfileChangeRequest);
                        Log.d("İşlem: ", "createUserWithEmail:success");
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(username)
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                        /*VeriTabaniDb db = VeriTabaniDb.getInstance(getApplicationContext());
                                        db.open();
                                        KullaniciIslemler kIslemler = new KullaniciIslemler(db.dbAl());
                                        kIslemler.yeniKullaniciKaydet(user);
                                        db.close();*/
                             //   progressDialog.dismiss();
                             //   fUser.sendEmailVerification();
                               /* if(!fUser.isEmailVerified()) {
                                    startActivity(new Intent(getApplicationContext(), EmailDogrulamaEkran.class));
                                    finish();
                                }*/
                               /* else {*/
                                  if(yetki.equals("satici")){
                                      startActivity(new Intent(getApplicationContext(),Admin_Activity.class));
                                      finish();
                                  }else{
                                      startActivity(new Intent(getApplicationContext(),Home.class));
                                      finish();
                                  }
                               // }
                            }
                            else{
                                progressDialog.dismiss();
                                fUser.delete();
                                Log.d("userhata:", task1.getException().toString());
                                //TODO: Stringe çevrilecek
                                Toast.makeText(this, "bir hata oluştu", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        if(fUser != null)
                            fUser.delete();
                        // If sign in fails, display a message to the user.
                        Log.w("İşlem:","createUserWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_registering_fail),
                                Toast.LENGTH_SHORT).show();
                    }

                    // ...
                });
    }



    private boolean internetBaglantiKontrol() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are checking whether connect to a network
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    private boolean tumKontroller(){
        // --  E-mail Kontrol  --
        String emailInput = emailEditText.getEditableText().toString().trim();
        boolean kontrol = true;
        if (emailInput.isEmpty()) {
            emailWrapper.setErrorEnabled(true);
            emailWrapper.setError(getResources().getString(R.string.empty_warning));
            kontrol = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailWrapper.setErrorEnabled(true);
            emailWrapper.setError(getResources().getString(R.string.email_warning));
            kontrol = false;
        } else {
            emailWrapper.setError(null);
        }

        // --  Password kontrol  --
        String passwordInput = passwordEditText.getText().toString().trim();
        if (passwordInput.isEmpty()) {
            passwordWrapper.setError(getResources().getString(R.string.empty_warning));
            kontrol = false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordWrapper.setError(getResources().getString(R.string.password_warning));
            kontrol = false;
        } else {
            passwordWrapper.setError(null);
        }

        // --  Username Kontrol  --
        String usernameInput = usernameEditText.getEditableText().toString().trim();
        if (usernameInput.isEmpty()) {
            usernameWrapper.setError(getResources().getString(R.string.empty_warning));
            kontrol = false;
        }
        if (usernameInput.contains("@")){
            //TODO: Stringe Çevrilecek.
            usernameWrapper.setError("Kullanıcı Adı @ İşareti içeremez.");
            kontrol = false;
        }
        else if (usernameInput.length() > 11) {
            usernameWrapper.setError(getResources().getString(R.string.username_warning));
            kontrol = false;
        }
        else if(usernameInput.length() <10){
            usernameWrapper.setError("Telefon numarasını 10 yada 11 haneli olarak giriniz");
            kontrol=false;

        } else {
            usernameWrapper.setError(null);
        }

   /*     // --  Date Kontrol  --
        if (!DATE_PATTERN.matcher(dobEditText.getText()).matches()) {
            dobEditText.setText(getResources().getString(R.string.dob_warning));
            kontrol = false;
        }*/

        // --  Name Kontrol  --
        String nameInput = nameEditText.getEditableText().toString().trim();
        if (nameInput.isEmpty()) {
            nameWrapper.setError(getResources().getString(R.string.empty_warning));
            kontrol = false;
        } else {
            nameWrapper.setError(null);
        }

      /*  // --  Surname Kontrol  --
        String surnameInput = surnameEditText.getEditableText().toString().trim();
        if (surnameInput.isEmpty()) {
            surnameWrapper.setError(getResources().getString(R.string.empty_warning));
            kontrol = false;
        } else {
            nameWrapper.setError(null);
        }*/
        return kontrol;
    }
}