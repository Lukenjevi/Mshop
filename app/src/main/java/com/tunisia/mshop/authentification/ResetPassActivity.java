package com.tunisia.mshop.authentification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tunisia.mshop.R;
import com.tunisia.mshop.authentification.LoginActivity;


public class ResetPassActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button send;
    private final String TAG=ResetPassActivity.class.getSimpleName();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        getSupportActionBar().hide();

        send=(Button) findViewById(R.id.sendreset);
        resetEmail=(EditText) findViewById(R.id.resetMail);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=resetEmail.getText().toString();
                showProgressDialog();
                if(!email.equals("")&& isValidEmail(email)) {

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = email;
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(getApplicationContext(), getString(R.string.recsend), Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();
                                        Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(getApplicationContext(), getString(R.string.invemail), Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.invemail), Toast.LENGTH_SHORT).show();
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
