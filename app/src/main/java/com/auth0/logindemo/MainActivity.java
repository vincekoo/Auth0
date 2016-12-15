package com.auth0.logindemo;

import android.content.Intent;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.result.UserProfile;
import com.auth0.logindemo.model.User;
import com.auth0.logindemo.network.ApiInterface;
import com.auth0.logindemo.network.RetrofitClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActitvity {
    @BindView(R.id.turn_on_notifications_toggle)
    Switch mySwitch;

    FirebaseDatabase database;
    DatabaseReference myRef;
    ApiInterface apiInterface = RetrofitClient.createService(ApiInterface.class);

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        AuthenticationAPIClient client = new AuthenticationAPIClient(
                new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain)));
        Log.d("asdf", "auth token is " + SharedPreferenceUtils.getIdToken(this));

        client.tokenInfo(SharedPreferenceUtils.getIdToken(this))
                .start(new BaseCallback<UserProfile, AuthenticationException>() {
                    @Override
                    public void onSuccess(final UserProfile payload) {
                        //if user successfully authenticated then write data to payload
                        database = FirebaseDatabase.getInstance();
                        //check for user in database, payload getName is used as the Key
                        myRef = database.getReference("users/" + payload.getId());

                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("asdf", "firebase onDataChange");

                                if (dataSnapshot.getValue() == null) {
//                                  if null then user doesnt exists and is a new user,
//                                  save payload with user name as the key
                                    User user = new User();
                                    user.setEmail(payload.getEmail());
                                    user.setFamilyName(payload.getFamilyName());
                                    user.setGivenName(payload.getGivenName());
                                    user.setId(payload.getId());
                                    user.setPictureURL(payload.getPictureURL());

                                    myRef.setValue(user);
                                } else {
                                    User user = new User();
                                    Log.d("asdf", dataSnapshot.getValue().toString());
                                }
                                //look at data snapshots(Database) to check for data,
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("asdf", "firebase onCancelled");
                            }
                        });
                    }

                    @Override
                    public void onFailure(AuthenticationException error) {
                    }
                });

        //set the switch to ON
        mySwitch.setChecked(true);

        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("all");
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("all");
                }

            }
        });
    }

    @OnClick(R.id.sign_out_button)
    protected void signOutButtonClickEvent() {
        SharedPreferenceUtils.clearSharedPreferences(MainActivity.this);
        startActivity(new Intent(MainActivity.this, StartActivity.class));
    }

    @OnClick(R.id.push_notifications)
    protected void pushNotificationButtonEvent() {
        apiInterface
                .sendNotification()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                        Log.d("asdf", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("asdf", "error is " + e.getMessage());
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Log.d("asdf", "onNext");
                    }
                });
    }
}
