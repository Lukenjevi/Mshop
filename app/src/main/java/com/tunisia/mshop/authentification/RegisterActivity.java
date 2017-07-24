package com.tunisia.mshop.authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tunisia.mshop.MainActivity;
import com.tunisia.mshop.R;


public class RegisterActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    Button register;
    Button cancel;

    private final String TAG=RegisterActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        name=(EditText) findViewById(R.id.regname);
        email=(EditText) findViewById(R.id.resemail);
        password=(EditText) findViewById(R.id.regpass);
        register=(Button) findViewById(R.id.Register);
        cancel=(Button) findViewById(R.id.Annuler);

        register.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                String nametxt=name.getText().toString();
                String emailtxt=email.getText().toString();
                String passtxt=password.getText().toString();

                Log.d(TAG, "nametxt" + nametxt);
                Log.d(TAG, "emailtxt" + emailtxt);
                Log.d(TAG, "passtxt" + passtxt);

                if (!isValidEmail(emailtxt) || nametxt.length() < 3 || passtxt.length() < 6) {
                    Toast.makeText(getApplicationContext(),  getString(R.string.invalidereg), Toast.LENGTH_SHORT).show();

                }else {
                    showProgressDialog();
                    signUpUser(emailtxt, passtxt);

                }
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                LoginIntent.putExtra("name", email.getText().toString()); //Optional parameters1
                startActivity(LoginIntent);
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

    public void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, R.string.Regfailed,
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }else {
                            Intent LoginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(LoginIntent);
                            finish();
                            hideProgressDialog();

                        }

                    }
                });
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
