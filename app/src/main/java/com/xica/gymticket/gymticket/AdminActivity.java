package com.xica.gymticket.gymticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.firebase.ui.auth.AuthUI;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        CardView registerNewGym = findViewById(R.id.register_gym);
        registerNewGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                intent.putExtra("admin","gym");
                startActivity(intent);
            }
        });

        CardView registerNewPro= findViewById(R.id.register_professional);
        registerNewPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                intent.putExtra("admin","pro");
                startActivity(intent);
            }
        });
        CardView manageUsers= findViewById(R.id.Manage_users);
        manageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });
        CardView manageArticles= findViewById(R.id.manage_articles);
        manageArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                intent.putExtra("admin","pro");
                startActivity(intent);
            }
        });
        CardView updateMemberships= findViewById(R.id.update_membership);
        updateMemberships.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                intent.putExtra("admin","pro");
                startActivity(intent);
            }
        });
        CardView generateReports= findViewById(R.id.generate_reports);
        generateReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminActivity.this,RegistrationActivity.class);
                intent.putExtra("admin","pro");
                startActivity(intent);
            }
        });
        CardView logout= findViewById(R.id.admin_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
            AuthUI.getInstance().signOut(AdminActivity.this);
            SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
            editor.putBoolean("signin", false);
            editor.apply();
            editor.commit();
            finish();
        }
    });
    }
}
