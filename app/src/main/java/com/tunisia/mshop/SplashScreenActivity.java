package com.tunisia.mshop;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import  com.tunisia.mshop.authentification.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.tunisia.mshop.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {


    private final String TAG=SplashScreenActivity.class.getSimpleName();

    private static int SPLASH_TIME_OUT = 3000;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                //AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (user != null) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // User is signed in
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            Intent LoginIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(LoginIntent);
                            finish();
                        }
                    }, SPLASH_TIME_OUT);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            Intent LoginIntent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                            startActivity(LoginIntent);
                            // close this activity
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                }

            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
