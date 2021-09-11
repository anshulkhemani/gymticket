package com.xica.gymticket.gymticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

import com.firebase.ui.auth.AuthUI;

public class GymActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gym_layout);

        CardView logout = findViewById(R.id.gym_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(GymActivity.this);
                SharedPreferences.Editor editor = getSharedPreferences("GymTicket", MODE_PRIVATE).edit();
                editor.putBoolean("signin", false);
                editor.apply();
                editor.commit();
                finish();
            }
        });
        CardView manageProfile = findViewById(R.id.manage_gym_profile);
        manageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GymActivity.this,ManageUsers.class);
                startActivity(intent);
            }
        });
    }
}
