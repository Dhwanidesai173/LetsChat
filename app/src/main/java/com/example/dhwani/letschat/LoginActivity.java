package com.example.dhwani.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText lo_email,lo_pass;
    Button log_btn;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar ltoolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lo_email = (EditText) findViewById(R.id.l_email);
        lo_pass = (EditText) findViewById(R.id.l_password);
        log_btn = (Button) findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(LoginActivity.this);

        ltoolBar =findViewById(R.id.toolBar);
        setSupportActionBar(ltoolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =lo_email.getText().toString();
                String password = lo_pass.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mProgress.setTitle("Logging");
                    mProgress.setMessage("Please Wait!");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    loginUser(email,password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgress.dismiss();
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            mProgress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void setSupportActionBar(Toolbar ltoolBar) {
    }
}
