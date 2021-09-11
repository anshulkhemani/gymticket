package com.xica.gymticket.gymticket;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ArticleActivity extends AppCompatActivity {
    DatabaseReference arr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_layout);
        final TextView heading1,content1;
        final ImageView img;
        Intent intent=getIntent();
        final String head=intent.getStringExtra("article_heading");
        img=findViewById(R.id.article_image);
        heading1=findViewById(R.id.article_heading);
        content1=findViewById(R.id.content);

        arr = FirebaseDatabase.getInstance().getReference("Article");
        arr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String heading = (dataSnapshot.child(head).child("Heading").getValue().toString());
                    String content=(dataSnapshot.child(head).child("Content").getValue().toString());
                    heading1.setText(heading);
                    content1.setText(content);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef =
                            storage.getReferenceFromUrl("gs://gymticket.appspot.com").child("images/" + heading);
                    try {
                        final File localFile = File.createTempFile("images", "jpg");
                        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                img.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "download failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                    }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
