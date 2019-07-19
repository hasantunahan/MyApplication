package com.example.myapplication.Notification;

import com.example.myapplication.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        //FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();

        System.out.println("token1");
            updateToken(refreshToken);
        System.out.println("token2");
        System.out.println("refresh"+refreshToken);
    }

    private void updateToken(String refreshToken) {
      //  FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Tokens tokens =new Tokens(refreshToken);
        reference.child(Common.currentUser.getPhone()).setValue(tokens);
        System.out.println("tokens olusturuldu");

    }

}
