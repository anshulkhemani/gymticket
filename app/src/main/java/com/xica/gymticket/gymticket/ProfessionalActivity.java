package com.xica.gymticket.gymticket;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ProfessionalActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;
    Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professional_layout);

        CardView logout = findViewById(R.id.professional_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(ProfessionalActivity.this);
                SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
                editor.putBoolean("signin", false);
                editor.apply();
                editor.commit();
                finish();
            }
        });

        CardView manageProfile = findViewById(R.id.manage_professional_profile);
        manageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfessionalActivity.this,ManageUsers.class);
                startActivity(intent);
            }
        });

        final CardView article = findViewById(R.id.article_details);
        article.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.article_create_layout);

                findViewById(R.id.select_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    }
                });
                findViewById(R.id.publish_article).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String heading,content;
                        EditText headingT,contentT;
                        headingT=findViewById(R.id.create_article_heading);
                        heading=headingT.getText().toString();
                        contentT=findViewById(R.id.create_article_content);
                        content=contentT.getText().toString();

                        DatabaseReference articleRef;
                        articleRef = FirebaseDatabase.getInstance().getReference().child("Article").child(heading);
                        if(heading!=null && content!=null && filePath!=null)
                        {
                            articleRef.child("Heading").setValue(heading);
                            articleRef.child("Content").setValue(content);
                            try {
                                //getting image from gallery
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                                uploadFile(bitmap,filePath,heading);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }



                    }
                });
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();


        }
    }
    private void uploadFile(Bitmap bitmap,Uri fp,String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://gymticket.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/"+name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
            }
        });

    }
}
