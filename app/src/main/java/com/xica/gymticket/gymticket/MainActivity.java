package com.xica.gymticket.gymticket;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pkmmte.view.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    String usertype = "customer";
    DatabaseReference dbr, arr;
    private List<User> itemList = null;
    private List<User> articleList = null;
    private RecyclerView recyclerView, articleView;
    private CustomRecyclerViewDataAdapter customRecyclerViewDataAdapter = null;
    private ArticleRecyclerViewDataAdapter articleRecyclerViewDataAdapter = null;
    private CardView card_chat, card_buym;
    private FirebaseAuth mAuth;
    ImageView tool_pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_refresh_recycler_view);
        mAuth = FirebaseAuth.getInstance();

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater li = LayoutInflater.from(this);
        View customView = li.inflate(R.layout.my_toolbar, null);
        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);

        card_chat = findViewById(R.id.Chat);
        recyclerView = findViewById(R.id.custom_refresh_recycler_view);
        articleView = findViewById(R.id.articleRecyclerView);
        card_buym = findViewById(R.id.BuyM);


        // Set layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        itemList = new ArrayList<>();

        GridLayoutManager articleManager = new GridLayoutManager(this, 1);
        articleView.setLayoutManager(articleManager);
        articleManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        articleList = new ArrayList<>();

        card_chat.setBackgroundResource(R.drawable.chat);
        card_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GymProList.class);
                intent.putExtra("gymPro", "professional");
                startActivity(intent);
            }
        });
        TextView viewAll = findViewById(R.id.viewAll);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GymProList.class);
                intent.putExtra("gymPro", "gym");
                startActivity(intent);
            }
        });
        tool_pro = findViewById(R.id.action_profile);
        tool_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileDetails.class);
                startActivity(intent);
            }
        });

        card_buym.setBackgroundResource(R.drawable.membership);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef =
                storage.getReferenceFromUrl("gs://gymticket.appspot.com");
        storageRef.child("images/" + FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(MainActivity.this /* context */)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(tool_pro);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        dbr = FirebaseDatabase.getInstance().getReference("Users");
        final String userID = FirebaseAuth.getInstance().getUid();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getting artist
                User user = dataSnapshot.child(userID).getValue(User.class);
                //adding artist to the list
                if (user != null) {
                    usertype = user.getUsertype();
                    if (usertype != null) {
                        if (usertype.equals("admin")) {
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            finish();
                            startActivity(intent);
                        } else if (usertype.equals("professional")) {
                            Intent intent = new Intent(MainActivity.this, ProfessionalActivity.class);
                            finish();
                            startActivity(intent);
                        } else if (usertype.equals("gym")) {
                            Intent intent = new Intent(MainActivity.this, GymActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });


        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final User item = new User();
                    if (ds.child("usertype").getValue().toString().equals("gym")) {
                        item.setName(ds.child("name").getValue().toString());
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef =
                                storage.getReferenceFromUrl("gs://gymticket.appspot.com").child("images/" + ds.getKey());
                        if (storageRef != null) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setPic(uri);
                                    itemList.add(item);
                                    if (customRecyclerViewDataAdapter != null) {
                                        customRecyclerViewDataAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                }
                customRecyclerViewDataAdapter = new CustomRecyclerViewDataAdapter(MainActivity.this, itemList);
                recyclerView.setAdapter(customRecyclerViewDataAdapter);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        arr = FirebaseDatabase.getInstance().getReference("Article");
        arr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final User item = new User();
                    String heading = (ds.child("Heading").getValue().toString());
                    //String content=(ds.child("Heading").child("Content").getValue().toString());
                    item.setHeading(heading);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef =
                            storage.getReferenceFromUrl("gs://gymticket.appspot.com").child("images/" + heading);
                    if (storageRef != null) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                item.setPic(uri);
                                articleList.add(item);
                                if (articleRecyclerViewDataAdapter != null) {
                                    articleRecyclerViewDataAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
                articleRecyclerViewDataAdapter = new ArticleRecyclerViewDataAdapter(MainActivity.this, articleList);
                articleView.setAdapter(articleRecyclerViewDataAdapter);
                articleView.scrollToPosition(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (mAuth.getCurrentUser() == null || (!mAuth.getCurrentUser().isEmailVerified() && !mAuth.getCurrentUser().getEmail().contains("+91"))) {
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        SharedPreferences prefs = getSharedPreferences("GymTicket", MODE_PRIVATE);
        String em = prefs.getString("email", " ");
        String pwd = prefs.getString("password", " ");

        AuthCredential credential = EmailAuthProvider
                .getCredential(em, pwd);

        // Prompt the user to re-provide their sign-in credentials
        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Success reauthenticate ", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("GymTicket", MODE_PRIVATE);
        Boolean exit = prefs.getBoolean("exit", true);
        if (exit) {
            finish();
            SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
            editor.putBoolean("exit", false);
            editor.apply();
            editor.commit();
        } else if (mAuth.getCurrentUser() == null || (!mAuth.getCurrentUser().isEmailVerified() && !mAuth.getCurrentUser().getEmail().contains("+91"))) {
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }
}