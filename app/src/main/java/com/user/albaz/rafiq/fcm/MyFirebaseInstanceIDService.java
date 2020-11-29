package com.user.albaz.rafiq.fcm;

import com.user.albaz.rafiq.helper.SharedHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName(); // Get Name Of Class

    // Method To Getting Device Token & Set It In SharedPreference
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken(); // Getting Device Token
        SharedHelper.putKey(getApplicationContext(),"device_token",""+s); // Set Device Token

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }

}