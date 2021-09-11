package com.xica.gymticket.gymticket;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ManageUsers extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    String[] cities = { "Ahmedabad","Surat","Vadodara"};
    private TextInputEditText dob1,name,weight,height,address;
    private RadioButton genderM,genderF;
    private TextView header;
    private RadioGroup radioGrp;
    private CheckBox dietitian,trainer,yoga;
    private LinearLayout profession;
    private FirebaseAuth mAuth;
    private ImageView profile_img;
    DatabaseReference dbr;

    Uri filePath;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_users);

        dob1=findViewById(R.id.dob_edit);
        profile_img= findViewById(R.id.profile_image_edit);
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        dbr=FirebaseDatabase.getInstance().getReference("Users");
        final String userID=FirebaseAuth.getInstance().getUid();


        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ImageView profile_img= findViewById(R.id.profile_image_edit);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef =
                        storage.getReferenceFromUrl("gs://gymticket.appspot.com");
                storageRef.child("images/" + FirebaseAuth.getInstance().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ManageUsers.this /* context */)
                                .load(uri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profile_img);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                //getting artist
                User user = dataSnapshot.child(userID).getValue(User.class);
                //adding artist to the list
                TextView name,dob,city,weight,height,genderEdit,editCity,addressE,timings;
                TextInputLayout address;
                LinearLayout hwEdit,professionEdit;
                hwEdit=findViewById(R.id.hw_edit);
                RadioGroup editGenderRadio;
                RadioButton male,female;
                male=findViewById(R.id.radioM_edit);
                female=findViewById(R.id.radioF_edit);
                String usertype,genderS,d,t,y;
                CheckBox dietitianCheck,trainerCheck,yogaCheck;
                dietitianCheck=findViewById(R.id.dietitian_check);
                trainerCheck=findViewById(R.id.trainer_check);
                yogaCheck=findViewById(R.id.yoga_check);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ManageUsers.this, R.layout.support_simple_spinner_dropdown_item, cities);
                Spinner cityEditSpin=findViewById(R.id.city_edit_spin);

                genderEdit=findViewById(R.id.gender_edit);
                editGenderRadio=findViewById(R.id.edit_gender_radio);
                name= findViewById(R.id.edit_name);
                name.setText(user.getName());
                professionEdit=findViewById(R.id.profession_edit);
                timings= findViewById(R.id.timings_edit);


                dob= findViewById(R.id.dob_edit);
                dob.setText(user.getDob());

                city= findViewById(R.id.edit_city);
                cityEditSpin.setAdapter(adapter);

                usertype= user.getUsertype();
                genderS= user.getGenderS();
                if(genderS!=null) {
                    if (genderS.equals("Male")) {
                        male.setChecked(true);
                    } else {
                        female.setChecked(true);
                    }
                }
                weight=findViewById(R.id.weightR_edit);
                weight.setText(user.getW());

                height=findViewById(R.id.heightR_edit);
                height.setText(user.getH());

                d=user.getDietitian();
                t=user.getTrainer();
                y=user.getYoga();

                address=findViewById(R.id.addressV_edit);
                addressE=findViewById(R.id.addressR_edit);
                addressE.setText(user.getAddress());


                if(usertype.equals("gym"))
                {
                    genderEdit.setVisibility(View.GONE);
                    editGenderRadio.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    cityEditSpin.setVisibility(View.GONE);
                    dob.setVisibility(View.GONE);
                    hwEdit.setVisibility(View.GONE);
                    address.setVisibility(View.VISIBLE);
                    timings.setVisibility(View.VISIBLE);
                }
                else if(usertype.equals("professional"))
                {
                    dob.setVisibility(View.GONE);
                    hwEdit.setVisibility(View.GONE);
                    address.setVisibility(View.VISIBLE);
                    professionEdit.setVisibility(View.VISIBLE);
                    if(d.equals("Dietitian"))
                    {
                        dietitianCheck.setChecked(true);
                    }
                    if(t.equals("Trainer"))
                    {
                        trainerCheck.setChecked(true);
                    }
                    if(y.equals("Yoga"))
                    {
                        yogaCheck.setChecked(true);
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;
    }
    private void dob() {
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = month + "/" + day + "/" + year;
                dob1.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                ManageUsers.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                //profile_img.setImageBitmap(bitmap);
                uploadFile(bitmap,filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(Bitmap bitmap,Uri fp) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://gymticket.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/"+FirebaseAuth.getInstance().getUid());
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
