package com.example.dhwani.letschat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    TextView DisplayName, DisplayStatus, DisplayFriends;
    ImageView DisplayProfile;
    ProgressDialog progressDialog;
    Button RequestSend, RequestCancel;
    private String current_state;
    private FirebaseUser current_user;
    private DatabaseReference userDataBase;
    private DatabaseReference friendRequestDataBase;
    private DatabaseReference friendDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");

        userDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendRequestDataBase = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendDataBase = FirebaseDatabase.getInstance().getReference().child("Friends");
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not_friends";

        DisplayName = findViewById(R.id.pr_name);
        DisplayStatus = findViewById(R.id.pr_status);
        DisplayFriends = findViewById(R.id.pr_total_friends);
        DisplayProfile = findViewById(R.id.pr_image);
        RequestSend = findViewById(R.id.pr_request);
        RequestCancel = findViewById(R.id.pr_decline);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        userDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retriving data to profile
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                DisplayName.setText(name);
                DisplayStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.user).into(DisplayProfile);

                //friend list or request feature
                friendRequestDataBase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                current_state = "request_received";

                                RequestSend.setText("Accept Friend request");
                                RequestCancel.setVisibility(View.VISIBLE);
                                RequestCancel.setEnabled(true);
                            } else if (current_state.equals("sent")) {
                                current_state = "req_sent";
                                RequestSend.setText("Cancel friend request");
                                RequestCancel.setVisibility(View.INVISIBLE);
                                RequestCancel.setEnabled(false);
                            }
                        } else {
                            friendDataBase.child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        current_state = "friends";
                                        RequestSend.setText("Unfriend");
                                        RequestCancel.setVisibility(View.INVISIBLE);
                                        RequestCancel.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

             }
        });

        RequestSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestSend.setEnabled(false);

                //first State(Request and Cancel Button) ie not_friend state;

                if (current_state.equals("not_friends")) {
                    friendRequestDataBase.child(current_user.getUid())
                            .child(user_id).child("request_type")
                            .setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        friendRequestDataBase.child(user_id).child(current_user.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                RequestSend.setEnabled(true);
                                                current_state = "req_sent";
                                                RequestSend.setText("Cancel Friend Request");
                                                //RequestSend.setBackgroundColor(R.color.colorAccent);
                                                RequestCancel.setVisibility(View.INVISIBLE);
                                                RequestCancel.setEnabled(false);
                                                Toast.makeText(ProfileActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

                //Cancel Request

                if (current_state.equals("req_sent")) {
                    friendRequestDataBase.child(current_user.getUid()).child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDataBase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    RequestSend.setEnabled(true);
                                    current_state = "not_friends";
                                    RequestSend.setText("Send Friend Request");
                                    RequestCancel.setVisibility(View.INVISIBLE);
                                    RequestCancel.setEnabled(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    RequestSend.setEnabled(true);
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                if (current_state.equals("request_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    friendDataBase.child(current_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDataBase.child(user_id).child(current_user.getUid()).setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendRequestDataBase.child(current_user.getUid()).child(user_id).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            friendRequestDataBase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    RequestSend.setEnabled(true);
                                                                    current_state = "friends";
                                                                    RequestSend.setText("Unfriend");
                                                                    RequestCancel.setVisibility(View.INVISIBLE);
                                                                    RequestCancel.setEnabled(false);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    RequestSend.setEnabled(true);
                                                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
                //remove friend
                if (current_state.equals("friends")) {
                    friendDataBase.child(current_user.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendDataBase.child(user_id).child(current_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            RequestSend.setEnabled(true);
                                            current_state = "not_friends";
                                            RequestSend.setText("Send friend request");
                                            RequestCancel.setVisibility(View.INVISIBLE);
                                            RequestCancel.setEnabled(false);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            RequestSend.setEnabled(true);
                                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });

    }
}
