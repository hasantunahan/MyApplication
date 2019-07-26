package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Profil extends AppCompatActivity {

    ImageView imgProfil;
    Button kaydetPhoto;
    private Uri imageuri;
    private final int PICK_IMAGE_REQUEST = 1;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        imgProfil=findViewById(R.id.photoProfilImageView);
        kaydetPhoto=findViewById(R.id.kaydetButtonProfil);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        resimIzniAl();

        imgProfil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseimage();
            }
        });

        kaydetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }//onCreate

    private void uploadImage() {
        if(imageuri != null){
            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Yukleniyor");
            progressDialog.show();

            StorageReference ref= storageReference.child(System.currentTimeMillis()+"."+imageuri);
            ref.putFile(imageuri)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           progressDialog.dismiss();
                           Toast.makeText(Profil.this, "tamam", Toast.LENGTH_SHORT).show();
                       }
                   })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Profil.this, "yok", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Yukleniyor"+(int)progress+"&");
                        }
                    });
        }

    }


    private void chooseimage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Lütfen  seciniz"),PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST  &&  resultCode == Activity.RESULT_OK && data != null && data.getData() !=null){
            imageuri=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageuri);
                imgProfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*if(uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Güncelleniyor", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }*/
        }
    }


    public boolean resimIzniAl(){

        if(PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PICK_IMAGE_REQUEST);
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PICK_IMAGE_REQUEST);
                return false;
            }
        }
        else {
            return true;
        }
    }


}
