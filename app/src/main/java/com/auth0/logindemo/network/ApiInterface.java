package com.auth0.logindemo.network;

//import com.auth0.logindemo.models.PushObject;

import retrofit2.http.GET;
import rx.Observable;

public interface ApiInterface {

    @GET("/push")
    Observable<Void> sendNotification();
}
