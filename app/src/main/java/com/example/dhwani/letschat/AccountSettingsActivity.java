package com.example.dhwani.letschat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettingsActivity extends AppCompatActivity {
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;

    private static final int GALLERY_PICK=1;

    private StorageReference mImageStorage;
    private ProgressDialog mProgress;

    private CircleImageView aImage;
    private TextView aName,aStatus;
    byte[] thumb_byte;
    private Button aChane_image,aChange_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        aImage = findViewById(R.id.profile_image);
        aName = findViewById(R.id.user_name);
        aStatus = findViewById(R.id.user_status);
        aChane_image = findViewById(R.id.chage_profile);
        aChange_status = findViewById(R.id.change_status);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        userDatabase.keepSynced(true);

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Toast.makeText(AccountSettingsActivity.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                aName.setText(name);
                aStatus.setText(status);
                //Picasso.get().load(image).placeholder(R.drawable.user).into(aImage);

                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(aImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.user).into(aImage);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        aChange_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Status_value = aStatus.getText().toString();
                Intent intent = new Intent(AccountSettingsActivity.this,StatusActivity.class);
                intent.putExtra("Status_value",Status_value);
                startActivity(intent);
            }
        });

        aChane_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery_intent,"SELECT IMAGE"),GALLERY_PICK);
                //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(AccountSettingsActivity.this);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(500,500)
                    .start(AccountSettingsActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(AccountSettingsActivity.this);
                mProgress.setTitle("Uploading Image");
                mProgress.setMessage("Please Wait!");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri updateImageUri = result.getUri();
                final File thumb_filePath = new File(updateImageUri.getPath());

                String current_user_id = currentUser.getUid();

                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    thumb_byte = byteArrayOutputStream.toByteArray();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


                StorageReference file = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_file = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                file.putFile(updateImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_file.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                   if(thumb_task.isSuccessful()){

                                       Map update_hashMap = new HashMap();
                                       update_hashMap.put("image",download_url);
                                       update_hashMap.put("thumb_image",thumb_downloadUrl);
                                       userDatabase.updateChildren(update_hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                               if(task.isSuccessful()){
                                                   mProgress.dismiss();
                                                   Toast.makeText(AccountSettingsActivity.this, "Successfull", Toast.LENGTH_SHORT).show();


                                               }
                                           }
                                       });
                                   }else{
                                       Toast.makeText(AccountSettingsActivity.this, "Error in uploading", Toast.LENGTH_SHORT).show();
                                       mProgress.dismiss();
                                   }
                                }
                            });

                        }
                        else{
                            Toast.makeText(AccountSettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();

                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                //Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
