package com.tunisia.mshop.authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.tunisia.mshop.MainActivity;
import com.tunisia.mshop.R;


public class LoginActivity extends AppCompatActivity {

    EditText emailEdit;
    EditText passwordEdit;
    Button okButton;
    Button RegisterButton;
    TextView forgetpass;
    RelativeLayout rl;
    CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private final String TAG=LoginActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);



        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);


        if(isLoggedIn()){
            LoginManager.getInstance().logOut();
        }
        //*************** Callback Facebook
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                showProgressDialog();

                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(FacebookException error) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
/*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.test.salimism.mytestapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

*/


        mAuth = FirebaseAuth.getInstance();

        emailEdit=(EditText) findViewById(R.id.editMail);
        passwordEdit=(EditText) findViewById(R.id.editPass);
        okButton=(Button) findViewById(R.id.buttonOk);
        RegisterButton=(Button) findViewById(R.id.buttonCancel);
        rl=(RelativeLayout) findViewById(R.id.relatLay);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String pass = passwordEdit.getText().toString();
                boolean bOk= isValidEmail(email);
                boolean passOk=true;
                if ((pass==null)||(pass.length()<6)) passOk=false;

                if (bOk&&passOk) {
                    showProgressDialog();
                    signIn(email, pass);
                }
                else Toast.makeText(getApplicationContext(),  getString(R.string.invalidpass), Toast.LENGTH_SHORT).show();
            }
        });


        RegisterButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);

            }
        });

        forgetpass=(TextView)findViewById(R.id.forgetpass);

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(LoginActivity.this, ResetPassActivity.class);
                startActivity(LoginIntent);

            }
        });

    }

    public void signIn(String email,String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            hideProgressDialog();
                            Snackbar snackbar = Snackbar
                                    .make(rl, getString(R.string.invalide), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.register), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                                            //myIntent.putExtra("key", value); //Optional parameters
                                            startActivity(myIntent);
                                        }
                                    });

                            snackbar.show();
                        }else
                        {
                            Intent LoginIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(LoginIntent);
                            finish();
                            hideProgressDialog();
                        }


                    }
                });


    }

    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            hideProgressDialog();

                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void showProgressDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
    }

    private void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}

