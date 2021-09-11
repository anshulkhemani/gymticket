package com.xica.gymticket.gymticket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.viewmodel.email.EmailLinkSendEmailHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //public static final String ANONYMOUS = "anonymous";
    //private String mUsername;


    //private ChildEventListener mChildEventListener;
    //private FirebaseAuth mFirebaseAuth;
    // private FirebaseAuth.AuthStateListener mAuthStateListener;
    //public static final int RC_SIGN_IN = 1;
    private TextInputEditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.link_signup).setOnClickListener(this);
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // Sign-in succeeded, set up the UI
            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
            editor.putBoolean("signin", true);
            editor.apply();
            editor.commit();

        } else if (resultCode == RESULT_CANCELED) {
            // Sign in was canceled by the user, finish the activity
            Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
            editor.putBoolean("exit", true);
            editor.apply();
            editor.commit();
            finish();
        }
    }

    private void onSignedInInitialize(String username) {
        //verifyEmail();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        mUsername = username;
        finish();

    }

    private void verifyEmail() {

        Log.d("TAG", "Outside null");
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            if (!mFirebaseUser.isEmailVerified()) {
                mFirebaseUser.sendEmailVerification().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Verification Email Sent To: " + mFirebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Failed To Send Verification Email!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if (mFirebaseUser != null) {
            if (mFirebaseUser.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_in_button:
                loginUser();
                break;
            case R.id.link_signup:
                finish();
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.forgot_password:
                forgotPassword();
                break;
        }
    }

    private void loginUser() {
        String password = editTextPassword.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email/Phone is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!Patterns.PHONE.matcher(email).matches()) {
                editTextEmail.setError("Please enter a valid email or phone");
                editTextEmail.requestFocus();
                return;
            }
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        if (Patterns.PHONE.matcher(email).matches()) {
            email="+91"+email+"@gmail.com";
        }
        final String em = email;
        SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
        editor.putString("email", em);
        editor.putString("password", password);
        editor.apply();
        editor.commit();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //progressBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    if (em.contains("+91")) {
                        signInWithPhone();
                    } else {
                        signIn();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void signIn() {
        FirebaseUser user1 = mAuth.getCurrentUser();
        if (user1 != null) {
            if (!user1.isEmailVerified()) {
                user1.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(LoginActivity.this, "Email not verified! Verify your email to log in!", Toast.LENGTH_LONG).show();
                    }
                });
            } else if (user1.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                startActivity(intent);
            }
        }
    }

    private void signInWithPhone() {
        FirebaseUser user1 = mAuth.getCurrentUser();
        if (user1 != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void forgotPassword() {
        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            editTextEmail.requestFocus();
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password change link sent to email address", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}


