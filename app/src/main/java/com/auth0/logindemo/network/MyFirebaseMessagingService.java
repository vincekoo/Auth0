package com.auth0.logindemo.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import com.auth0.logindemo.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    static int notificationCounter = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("asdf", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("asdf", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("asdf", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        Notification n  = new Notification.Builder(this)
                .setContentTitle("FCM Test")
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .build();
//                .setSmallIcon(R.drawable.icon)
//                .setContentIntent(pIntent)
//                .setAutoCancel(true)
//                .addAction(R.drawable.icon, "Call", pIntent)
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //need unique ID evertime
        notificationManager.notify(notificationCounter, n);
        Log.d("asdf", String.valueOf(notificationCounter));
        notificationCounter++;
    }

}
