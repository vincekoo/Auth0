package com.auth0.logindemo;

import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;

/**
 * Activity Class that will check if the user is logged in or not.
 * If they are not we will redirect them to the login screen
 */
public class StartActivity extends BaseActitvity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public void init() {
        AuthenticationAPIClient client = new AuthenticationAPIClient(
                new Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain)));
        String token = SharedPreferenceUtils.getIdToken(this);
        if (token != null) {
            client.tokenInfo(SharedPreferenceUtils.getIdToken(this))
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(UserProfile payload) {
                            // Valid ID > Navigate to the app's MainActivity
                            startActivity(new Intent(StartActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            // Invalid ID Scenario
                            startActivity(new Intent(StartActivity.this, LoginActivity.class));
                        }
                    });
        } else {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }
    }
}
