package com.example.dhwani.letschat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    DatabaseReference userDatabse;
    RecyclerView mRecyclerView;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        mtoolbar = findViewById(R.id.user_tab);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDatabse = FirebaseDatabase.getInstance().getReference().child("users");
        mRecyclerView = findViewById(R.id.recycler_user_list);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(AllUserActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("users");
        FirebaseRecyclerOptions<users> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<users>()
                .setQuery(query,users.class)
                .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users,UserViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_info, parent, false);
                return new UserViewHolder(v);

            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull users model) {
                holder.setDisplayName(model.getName());
                holder.setDisplayStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image());

                final String user_id = getRef(position).getKey();

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profile = new Intent(AllUserActivity.this,ProfileActivity.class);
                        profile.putExtra("user_id",user_id);
                        startActivity(profile);
                    }
                });
            }


        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public class UserViewHolder extends  RecyclerView.ViewHolder {

        View mview;

        public UserViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }
        public void setDisplayName(String name) {
            TextView userName = (TextView) mview.findViewById(R.id.all_user_name);
            userName.setText(name);

        }
        public void setDisplayStatus(String status){
            TextView userStatus = (TextView) mview.findViewById(R.id.all_user_status);
            userStatus.setText(status);
        }

        public void setUserImage(String thumb_image){
            CircleImageView userImage = (CircleImageView) mview.findViewById(R.id.all_user_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.user).into(userImage);
        }


    }

}
