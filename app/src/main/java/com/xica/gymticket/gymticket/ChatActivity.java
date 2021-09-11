package com.xica.gymticket.gymticket;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    public static final String ANONYMOUS = "anonymous";
    public static int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER =  1;

    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ProgressBar chatProgressBar;
    private ImageButton chatPhotoPicker;
    private EditText chatEditText;
    private DatabaseReference dbr;
    private Button chatSendButton;
    private String mUsername,professionalName;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private CardView messageCard;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        mUsername = ANONYMOUS;
        Intent intent=getIntent();
        professionalName=intent.getStringExtra("professional_name");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseStorage=FirebaseStorage.getInstance();
        mChatPhotosStorageReference=mFirebaseStorage.getReference().child("chat_photos");





        dbr = FirebaseDatabase.getInstance().getReference("Users");
        final String userID = FirebaseAuth.getInstance().getUid();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //getting artist
                User user = dataSnapshot.child(userID).getValue(User.class);
                //adding artist to the list
                if (user != null) {
                    mUsername=user.getName();
                    mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages/"+mUsername+professionalName);
                    attachDatabaseReadListener();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });





        // Initialize references to views
        chatProgressBar = findViewById(R.id.chat_progressbar);
        chatListView =     findViewById(R.id.chat_listview);
        chatPhotoPicker =  findViewById(R.id.chat_photo_picker);
        chatEditText = findViewById(R.id.chat_edittext);
        chatSendButton =  findViewById(R.id.chat_send_button);
        messageCard=findViewById(R.id.message_card);

        // Initialize message ListView and its adapter
        List<ChatData> friendlyMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(ChatActivity.this, R.layout.item_message, friendlyMessages);
        chatListView.setAdapter(chatAdapter);
        // Initialize progress bar
        chatProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        chatPhotoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });






        // Enable Send button when there's text to send
        chatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    chatSendButton.setEnabled(true);
                } else {
                    chatSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        chatEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatData friendlyMessage = new ChatData(chatEditText.getText().toString(), mUsername, null);

                FirebaseDatabase.getInstance().getReference("messages")
                        .child(mUsername+professionalName)
                        .push().setValue(friendlyMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                // Clear input box
                chatEditText.setText("");
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //When the image has successfully uploaded, get its download URL
                            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri dlUri = uri;
                                    ChatData friendlyMessage = new ChatData(null, mUsername, dlUri.toString());
                                    FirebaseDatabase.getInstance().getReference("messages")
                                            .child(mUsername+professionalName)
                                            .push()
                                            .setValue(friendlyMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChatActivity.this, "Success", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    }
                            });
                        }
                    });
        }
    }

    private void attachDatabaseReadListener()
    {
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //if (dataSnapshot.getChildrenCount() > 1) {
                        //for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            //if (dataSnapshot.getKey().equals(mUsername + professionalName)) {
                                ChatData friendlyMessage = dataSnapshot.getValue(ChatData.class);
                                chatAdapter.add(friendlyMessage);
                                Toast.makeText(ChatActivity.this, "Username = " + mUsername, Toast.LENGTH_SHORT).show();
                            //}
                        //}
                    /*} else {
                        if (dataSnapshot.getKey().equals(mUsername + professionalName)) {
                            ChatData friendlyMessage=dataSnapshot.getValue(ChatData.class);
                            chatAdapter.add(friendlyMessage);
                        }
                    }*/
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }




    private void detachDatabaseReadListener()
    {
        if(mChildEventListener!=null)
        {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        detachDatabaseReadListener();
    }
}
