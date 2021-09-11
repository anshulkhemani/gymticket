package com.xica.gymticket.gymticket;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GymProList extends AppCompatActivity {

    private List<User> itemList;

    private GymProListAdapter gymProListAdapter;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    DatabaseReference dbr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setTitle("Professionals List");
        setContentView(R.layout.gym_pro_list);
        final String type;
        Intent intent=getIntent();
        if(intent.getStringExtra("gymPro").equals("gym"))
        {
            type="gym";
        }
        else
        {
            type="professional";
        }
        itemList=new ArrayList<>();
        recyclerView = findViewById(R.id.professional_recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        progressDialog = new ProgressDialog(GymProList.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        dbr = FirebaseDatabase.getInstance().getReference("Users");
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final User item = new User();
                    if (ds.child("usertype").getValue().toString().equals(type)) {
                        item.setName(ds.child("name").getValue().toString());
                        item.setUsertype(ds.child("usertype").getValue().toString());
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef =
                                storage.getReferenceFromUrl("gs://gymticket.appspot.com").child("images/" + ds.getKey());
                        if(storageRef!=null)
                        {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setPic(uri);
                                    itemList.add(item);
                                    if(gymProListAdapter!=null)
                                    {
                                        gymProListAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
                gymProListAdapter = new GymProListAdapter(GymProList.this, itemList);
                recyclerView.setAdapter(gymProListAdapter);
                recyclerView.scrollToPosition(0);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart(); }
}