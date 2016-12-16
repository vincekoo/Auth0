package com.auth0.logindemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.PasswordlessType;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.Credentials;

import butterknife.BindView;
import butterknife.OnClick;

public class CustomLoginActivity extends BaseActitvity {
    @BindView(R.id.phone_number)
    EditText phoneNumberEdittext;

    EditText verifyNumberEditText;

    Auth0 account;
    AuthenticationAPIClient authentication;

    @Override
    public int getLayoutId() {
        return R.layout.custom_login_activity;
    }

    @Override
    public void init() {
        account = new Auth0(getApplicationContext());
        authentication = new AuthenticationAPIClient(account);
    }

    @OnClick(R.id.sign_in_button)
    void signIn() {
        BaseCallback<Void, AuthenticationException> baseCallback = new BaseCallback<Void, AuthenticationException>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d("asdf", "SIGN IN onSuccess");
                CustomLoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createVerifyDialog().show();
                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {
                Log.d("asdf", "SIGN IN onFailure error is " + error.getMessage());
            }
        };

        authentication
                .passwordlessWithSMS(phoneNumberEdittext.getText().toString(), PasswordlessType.CODE, "sms")
                .start(baseCallback);
    }

    private AlertDialog createVerifyDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CustomLoginActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setMessage("verify plz")
                .setTitle("verify plz");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.verify_dialog_layout, null))
                // Add action buttons
                .setPositiveButton("Verify!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        verifyNumberEditText = (EditText) ((AlertDialog) dialog).findViewById(R.id.verify_number_edit_text);
                        authentication
                                .loginWithPhoneNumber(phoneNumberEdittext.getText().toString(), verifyNumberEditText.getText().toString())
                                .start(new BaseCallback<Credentials, AuthenticationException>() {
                                    @Override
                                    public void onSuccess(final Credentials credentials) {
                                        Log.d("asdf", "VERIFY onSuccess");
                                        CustomLoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();

                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                SharedPreferenceUtils.setIdToken(CustomLoginActivity.this, credentials.getIdToken());
                                                SharedPreferenceUtils.setRefreshToken(CustomLoginActivity.this, credentials.getRefreshToken());
                                                finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(AuthenticationException error) {
                                        Log.d("asdf", "VERIFY onFailure error is " + error.getCause().toString());
                                    }
                                });
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}