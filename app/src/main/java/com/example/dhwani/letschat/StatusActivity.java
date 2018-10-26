package com.example.dhwani.letschat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    TextInputLayout mstatus;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser currentUser;
    private ProgressDialog mProgress;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mProgress = new ProgressDialog(StatusActivity.this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        String status_value = getIntent().getStringExtra("Status_value");
        mstatus = findViewById(R.id.status_layout);
        mButton = findViewById(R.id.update_status);
        mstatus.getEditText().setText(status_value);

        mToolbar = findViewById(R.id.status_apppbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Uploading Status");
                mProgress.setMessage("Please Wait!");
                mProgress.show();

                String status = mstatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){

                            mProgress.dismiss();
                        }else{

                            Toast.makeText(StatusActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
