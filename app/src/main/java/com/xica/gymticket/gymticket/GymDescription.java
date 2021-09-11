package com.xica.gymticket.gymticket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class GymDescription extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_gym_layout);
        Intent intent=getIntent();
        String gym_name = intent.getStringExtra("gym_name");
        TextView text=findViewById(R.id.gym_name_expanded);
        text.setText(gym_name);
    }
}
