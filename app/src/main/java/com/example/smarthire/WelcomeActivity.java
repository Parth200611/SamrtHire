package com.example.smarthire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthire.JobSeeker.JobSeekerLoginActivity;
import com.example.smarthire.Recruiter.RecruiterLoginActivity;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {
    private MaterialButton btnContinueJobSeeker;
    private MaterialButton btnContinueRecruiter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        getWindow().setNavigationBarColor(android.graphics.Color.WHITE);

        // Initialize UI Elements
        btnContinueJobSeeker = findViewById(R.id.btnContinueJobSeeker);
        btnContinueRecruiter = findViewById(R.id.btnContinueRecruiter);

        // Job Seeker Click Listener
        btnContinueJobSeeker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WelcomeActivity.this, "Proceeding as Job Seeker...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(WelcomeActivity.this, JobSeekerLoginActivity.class);
                 startActivity(intent);
            }
        });

        // Recruiter Click Listener
        btnContinueRecruiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WelcomeActivity.this, "Proceeding as Recruiter...", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(WelcomeActivity.this, RecruiterLoginActivity.class);
                 startActivity(intent);
            }
        });

    }
}