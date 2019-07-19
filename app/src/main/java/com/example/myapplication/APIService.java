package com.example.myapplication;

import com.example.myapplication.Notification.MyResponse;
import com.example.myapplication.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAALSx3HkQ:APA91bHCI8Od5JMmcmN9RgVqbhxSihlJaevajPISbgbGNgzxIgkR-CeH8MrbIcvOjIjfngei8C9X5JFygunCS8mgH3aXHmdoPgHVeorv_-zr_U1KkJ5_WpWuhNveO-132rB2MmiUcK_n"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
