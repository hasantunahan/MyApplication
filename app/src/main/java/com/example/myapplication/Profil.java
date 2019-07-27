package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Profil extends AppCompatActivity {

    ImageView imgProfil,geriProfil;
    DatabaseReference reference;
    FirebaseUser fuser;
    private Button kaydetPhoto;
    TextView kullaniciAdi;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageuri;
    private StorageTask uploadTask;
    private EditText profilname;
    private Button guncelle;
    private String currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        imgProfil=findViewById(R.id.photoProfilImageView);
        kaydetPhoto=findViewById(R.id.kaydetButtonProfil);
        geriProfil=findViewById(R.id.geriButtonprofil);
        kullaniciAdi=findViewById(R.id.kullaniciAdi);
        profilname=findViewById(R.id.profilName);
        guncelle=findViewById(R.id.isimkaydetButtonProfil);


        guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference =FirebaseDatabase.getInstance().getReference("User").child(fuser.getDisplayName());
                HashMap<String,Object> map = new HashMap<>();
                map.put("name",profilname.getText().toString());
                reference.updateChildren(map);
                profilname.setText("");
            }
        });


        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentName=dataSnapshot.child("name").getValue().toString();
                    kullaniciAdi.setText(currentName.toUpperCase());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            geriProfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Profil.this,Home.class);
                    startActivity(intent);
                }
            });
        storageReference= FirebaseStorage.getInstance().getReference("profil");



        fuser= FirebaseAuth.getInstance().getCurrentUser();


        reference= FirebaseDatabase.getInstance().getReference("User").child(fuser.getDisplayName());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getPhotourl().equals("default")){
                    imgProfil.setImageResource(R.drawable.photochange);
                }else{
                    Picasso.with(getApplicationContext()).load(user.getPhotourl()).into(imgProfil);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


      kaydetPhoto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              openImage();
          }
      });


    }//onCreate


    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd=new ProgressDialog(getApplicationContext());
        pd.setMessage("Uploading");
      //  pd.show();
        System.out.println("bosmu");
        if(imageuri !=null){
            final StorageReference filereference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageuri));
            System.out.println("0.denme");
            uploadTask=filereference.putFile(imageuri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    System.out.println("1.deneme");
                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        System.out.println("2.deneme");
                        Uri downloadUri=task.getResult();
                        System.out.println("3.deneme");
                        String mUri=downloadUri.toString();

                        reference =FirebaseDatabase.getInstance().getReference("User").child(fuser.getDisplayName());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("photourl",mUri);
                        reference.updateChildren(map);
                        pd.dismiss();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Yüklenemedi", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Fotograf secilmedi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST  &&  resultCode == Activity.RESULT_OK && data != null && data.getData() !=null){
            imageuri=data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "Güncelleniyor", Toast.LENGTH_SHORT).show();
            }else{
                System.out.println("uploadonu");
                uploadImage();
            }
        }
    }

}
