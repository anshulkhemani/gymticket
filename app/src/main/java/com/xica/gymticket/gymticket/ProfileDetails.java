package com.xica.gymticket.gymticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class ProfileDetails extends AppCompatActivity {
    DatabaseReference dbr;
    ImageView profile_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.my_toolbar, null);
        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);

        customView.findViewById(R.id.text_city).setVisibility(View.GONE);
        customView.findViewById(R.id.action_profile).setVisibility(View.GONE);
        customView.findViewById(R.id.text_locality).setX(300);

        dbr = FirebaseDatabase.getInstance().getReference("Users");

        ImageView img = findViewById(R.id.icon_loc);
        img.setImageResource(R.mipmap.back_arrow_perpal_ic);

        TextView tv = findViewById(R.id.text_locality);
        tv.setText("My Profile");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        profile_img = findViewById(R.id.profile_image);
        TextView profileEdit = findViewById(R.id.profile_edit);
        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDetails.this, ManageUsers.class);
                startActivity(intent);
            }
        });
        TextView about = findViewById(R.id.profile_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileDetails.this, AboutApp.class);
                startActivity(intent);
            }
        });
        TextView logout = findViewById(R.id.profile_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(ProfileDetails.this);
                SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
                editor.putBoolean("signins", false);
                editor.apply();
                editor.commit();
                finish();
            }
        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://gymticket.appspot.com");
        storageRef.child("images/" + FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProfileDetails.this /* context */)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        final String userID = FirebaseAuth.getInstance().getUid();
        Toast.makeText(getApplicationContext(), userID, Toast.LENGTH_LONG).show();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getting user
                User user = dataSnapshot.child(userID).getValue(User.class);
                //adding user to the list
                TextView name, email, city;
                name = findViewById(R.id.profile_name);
                name.setText(user.getName());
                email = findViewById(R.id.profile_email);
                email.setText(user.getEmail());
                city = findViewById(R.id.profile_city);
                city.setText(user.getCity1());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
