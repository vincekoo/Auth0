package com.auth0.logindemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.PasswordlessLock;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Lock mLock;
    private PasswordlessLock pLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        Auth0 auth0 = new Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain));

        Map<String, Object> authParams = new HashMap<>();
        authParams.put("prompt", "select_account");

        mLock = Lock.newBuilder(auth0, mCallback)
                .withAuthenticationParameters(authParams)
                //Add parameters to the builder
                .build(this);

        pLock = PasswordlessLock.newBuilder(auth0, mCallback)
                //Customize Lock
                .build(this);

        startActivity(pLock.newIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        mLock.onDestroy(this);
        mLock = null;

        pLock.onDestroy(this);
        pLock = null;
    }

    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            SharedPreferenceUtils.setIdToken(LoginActivity.this, credentials.getIdToken());
            SharedPreferenceUtils.setRefreshToken(LoginActivity.this, credentials.getRefreshToken());
            finish();
        }

        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(), "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(getApplicationContext(), "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };
}
