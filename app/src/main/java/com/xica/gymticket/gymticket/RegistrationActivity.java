package com.xica.gymticket.gymticket;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText editTextEmail, editTextPassword, userType, dob1,name,phone,weight,height,otp,address;
    private RadioButton genderM,genderF;
    private TextView header;
    private RadioGroup radioGrp;
    private CheckBox dietitian,trainer,yoga;
    private LinearLayout profession;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TextInputLayout addressV;

    String[] cities = { "Ahmedabad","Surat","Vadodara"};
    String codeSent,admin;
    Spinner city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);


        city =  findViewById(R.id.pref_city);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, cities);
        city.setAdapter(adapter);

        editTextEmail = findViewById(R.id.emailR);
        editTextPassword = findViewById(R.id.passwordR);
        userType = findViewById(R.id.user_type);
        name=findViewById(R.id.nameR);
        dob1=findViewById(R.id.dob);
        dob1.setFocusable(false);

        phone =findViewById(R.id.phoneR);
        city=findViewById(R.id.pref_city);
        genderM=findViewById(R.id.radioM);
        genderF=findViewById(R.id.radioF);
        radioGrp=findViewById(R.id.radioGrp);

        header=findViewById(R.id.header);

        dietitian=findViewById(R.id.dietitian_check);
        trainer=findViewById(R.id.trainer_check);
        yoga=findViewById(R.id.yoga_check);


        weight=findViewById(R.id.weightR);
        height=findViewById(R.id.heightR);
        address=findViewById(R.id.addressR);
        addressV=findViewById(R.id.addressV);
        profession=findViewById(R.id.profession);
        Intent intent = getIntent();
        if(intent.getStringExtra("admin")!=null) {
            admin = intent.getStringExtra("admin");
            if(admin==null)
            {
                admin="customer";
                Toast.makeText(RegistrationActivity.this, "Admin value - "+admin, Toast.LENGTH_LONG).show();

            }
            if (admin.equals("gym")) {
                weight.setVisibility(View.GONE);
                height.setVisibility(View.GONE);
                dob1.setVisibility(View.GONE);
                addressV.setVisibility(View.VISIBLE);
                radioGrp.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
            }
            else if(admin.equals("pro"))
            {
                height.setVisibility(View.GONE);
                weight.setVisibility(View.GONE);
                profession.setVisibility(View.VISIBLE);
                addressV.setVisibility(View.VISIBLE);
            }
        }
            findViewById(R.id.email_register_button).setOnClickListener(this);
            findViewById(R.id.dob).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_register_button:
                registerUser();
                break;
            case R.id.dob:
                dob();
                break;
        }
    }

    private void sendVerificationCode(String phone)
    {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };




    private void dob() {
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month+=1;
                String date = month + "/" + day + "/" + year;
                dob1.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                RegistrationActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void registerUser() {
        final String name1 = name.getText().toString().trim();
        final String usertype = userType.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String city1 = city.getSelectedItem().toString().trim();
        final String phone1 = phone.getText().toString().trim();

        final String dob = dob1.getText().toString().trim();
        final String height1 = height.getText().toString().trim();
        final String weight1 = weight.getText().toString().trim();
        final String address1=address.getText().toString().trim();

        dob1.setFocusable(true);
        dob1.setFocusableInTouchMode(true);
        Intent intent = getIntent();
        admin = intent.getStringExtra("admin");
        if(admin==null)
        {
            admin="customer";
            Toast.makeText(RegistrationActivity.this, "Admin value - "+admin, Toast.LENGTH_LONG).show();

        }



        if (name1.isEmpty()) {

            name.setError("Please enter your name");
            name.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            if(phone1.isEmpty()) {
                editTextEmail.setError("Enter email or phone");
                editTextEmail.requestFocus();
                return;
            }
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
            return;
        }

        if(!phone1.isEmpty() && ( phone1.length()<10 ||!Patterns.PHONE.matcher(phone1).matches()))
        {
            phone.setError("Enter valid phone number");
            phone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            editTextPassword.requestFocus();
            return;
        }
        if (dob.isEmpty() && !admin.equals("gym")) {

            dob1.setError("Please set dob");
            dob1.requestFocus();
            return;
        }
        if (city1.isEmpty() && !admin.equals("pro")) {

            ((TextView)city.getSelectedView()).setError("Please select city");
            city.requestFocus();
            return;
        }

        String genderS="m";
        final String d,t,y;

        if(genderM.isChecked())
        {
            genderS = genderM.getText().toString().trim();
        }
        else if(genderF.isChecked())
        {
            genderS = genderF.getText().toString().trim();
        }
        else
        {
            if(!admin.equals("gym")) {
                genderM = findViewById(R.id.radioM);
                genderM.setError("Please select gender");
                return;
            }
        }
        if(address1.isEmpty() && (admin.equals("pro") || admin.equals("gym")))
        {
            address.setError("Please enter address");
            address.requestFocus();
            return;
        }
        if(!dietitian.isChecked() && !trainer.isChecked() && !yoga.isChecked() && admin.equals("pro"))
        {
            yoga.setError("Please select any one!");
            return;
        }
        if(dietitian.isChecked())
        {
            d="Dietitian";
        }
        else
        {
            d="null";
        }
        if(yoga.isChecked())
        {
            y="Yoga";

        }
        else
        {
            y="null";
        }
        if(trainer.isChecked())
        {
            t="Trainer";

        }
        else
        {
            t="null";
        }
        final String g=genderS;
        final String h , w, phone;
        if (height1.isEmpty()) {
            h = "0";
        } else {
            h = height1;
        }
        if (weight1.isEmpty()) {
            w = "0";
        } else {
            w = weight1;
        }
        if (phone1.isEmpty()) {
            phone = " ";
        }
        else
        {
            phone=phone1;
        }


        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        if(!phone1.isEmpty()) {
            final PopupWindow popupWindow;
            LinearLayout linearLayout1=findViewById(R.id.linearLayout1);
            //instantiate the popup.xml layout file
            LayoutInflater layoutInflater = (LayoutInflater) RegistrationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View customView = layoutInflater.inflate(R.layout.popup_otp, null);
            //instantiate popup window
            popupWindow = new PopupWindow(customView, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 700);
            //display the popup window
            popupWindow.showAtLocation(linearLayout1, Gravity.CENTER, 0, 0);
            //close the popup window on button click
            progressDialog.dismiss();
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.update();
            sendVerificationCode(phone1);

            final TextView otpT = customView.findViewById(R.id.otpR);
            otpT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    otpT.requestFocus();
                }
            });

            customView.findViewById(R.id.verifyOTP).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    otp=customView.findViewById(R.id.otpR);
                    String code = otp.getText().toString();
                    final String phone2="+91"+phone1+"@gmail.com";
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
                    mAuth.signInWithCredential(credential)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final FirebaseUser currentUser = mAuth.getCurrentUser();

                                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });


                                        if(!admin.equals("gym") && !admin.equals("pro")) {
                                            Toast.makeText(RegistrationActivity.this, "User type in 1 - "+admin, Toast.LENGTH_LONG).show();

                                            mAuth.createUserWithEmailAndPassword(phone2, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                User user = new User(
                                                                        name1,
                                                                        phone2,
                                                                        usertype,
                                                                        dob,
                                                                        h,
                                                                        w,
                                                                        g,
                                                                        city1,
                                                                        phone
                                                                );

                                                                FirebaseDatabase.getInstance().getReference("Users")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                                            signInWithPhone();
                                                                        } else {
                                                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                                    Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                                }
                                                            }
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                        }
                                        else if(admin.equals("gym"))
                                        {
                                            Toast.makeText(RegistrationActivity.this, "User type in 2 - "+admin, Toast.LENGTH_LONG).show();

                                            mAuth.createUserWithEmailAndPassword(phone2, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                User user = new User(
                                                                        name1,
                                                                        phone2,
                                                                        usertype,
                                                                        address1,
                                                                        city1,
                                                                        phone
                                                                );

                                                                FirebaseDatabase.getInstance().getReference("Users")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                                    Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                                }
                                                            }
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                        }

                                        else if(admin.equals("pro"))
                                        {
                                            Toast.makeText(RegistrationActivity.this, "User type in 3 - "+admin, Toast.LENGTH_LONG).show();
                                            mAuth.createUserWithEmailAndPassword(phone2, password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                                            if (task.isSuccessful()) {
                                                                User user = new User(
                                                                        name1,
                                                                        phone2,
                                                                        usertype,
                                                                        d,
                                                                        t,
                                                                        y,
                                                                        g,
                                                                        city1,
                                                                        phone,
                                                                        address1
                                                                );



                                                                FirebaseDatabase.getInstance().getReference("Users")
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                                            finish();
                                                                        } else {
                                                                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                                    Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                                                }
                                                            }
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                        }





                                    } else {
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            });
                }
            });


        }
        else {
            if(!admin.equals("gym") && !admin.equals("pro")) {
                Toast.makeText(RegistrationActivity.this, "User type in 1 - "+admin, Toast.LENGTH_LONG).show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(
                                            name1,
                                            email,
                                            usertype,
                                            dob,
                                            h,
                                            w,
                                            g,
                                            city1,
                                            phone
                                    );
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                signIn();
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
            else if(admin.equals("gym"))
            {
                Toast.makeText(RegistrationActivity.this, "User type in 2 - "+admin, Toast.LENGTH_LONG).show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(
                                            name1,
                                            email,
                                            usertype,
                                            address1,
                                            city1,
                                            phone
                                    );
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(RegistrationActivity.this,"Verification e-mail sent! Verify your email to log in!",Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                                progressDialog.dismiss();
                            }
                        });
            }

            else if(admin.equals("pro"))
            {
                Toast.makeText(RegistrationActivity.this, "User type in 3 - "+admin, Toast.LENGTH_LONG).show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    User user = new User(
                                            name1,
                                            email,
                                            usertype,
                                            d,
                                            t,
                                            y,
                                            g,
                                            city1,
                                            phone,
                                            address1
                                    );


                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(RegistrationActivity.this,"Verification e-mail sent! Verify your email to log in!",Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(RegistrationActivity.this, "User Already Exists! Login to continue.", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        }
    }

    private void signIn() {
        FirebaseUser user1=mAuth.getCurrentUser();
        if(user1!=null) {
            if (!user1.isEmailVerified())
            {
                user1.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RegistrationActivity.this,"Verification e-mail sent! Verify your email to log in!",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    }
    private void signInWithPhone() {
        FirebaseUser user1=mAuth.getCurrentUser();
        if(user1!=null) {
                        Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}



