package com.example.dhwani.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterationActivity extends AppCompatActivity {

    private EditText reg_name,reg_email,reg_password;
    String final_name,final_email,final_password;
    private Button reg_btn;
    private android.support.v7.widget.Toolbar mtoolBar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private DatabaseReference mdataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(RegisterationActivity.this);

        mtoolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reg_name = (EditText) findViewById(R.id.name);
        reg_email = (EditText) findViewById(R.id.email);
        reg_password = (EditText) findViewById(R.id.password);
        reg_btn = (Button) findViewById(R.id.register);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final_name = reg_name.getText().toString();
                final_email = reg_email.getText().toString();
                final_password = reg_password.getText().toString();

                if(!TextUtils.isEmpty(final_name) || !TextUtils.isEmpty(final_email) || !TextUtils.isEmpty(final_password)){
                    mProgress.setTitle("Registeration");
                    mProgress.setMessage("Please Wait!");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    reg_user(final_name,final_email,final_password);
                }
            }
        });


    }

    private void reg_user(final String final_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();

                            mdataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                            HashMap<String ,String > userMap = new HashMap<>();
                            userMap.put("name",final_name);
                            userMap.put("status","Hello! Let's Start Chatting.");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");

                            mdataBase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        mProgress.dismiss();
                                        Intent intent = new Intent(RegisterationActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                        } else {
                            mProgress.hide();
                            //String error = task.getException().getMessage();
                            Toast.makeText(RegisterationActivity.this, "Can't Sign In.Please check and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
