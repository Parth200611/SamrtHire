
package com.example.smarthire.Recruiter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smarthire.R;
import com.example.smarthire.Recruiter.Ai.RecruiterAIFragment;
import com.example.smarthire.Recruiter.Application.ApplicationsFragment;
import com.example.smarthire.Recruiter.Candidate.CandidatesFragment;
import com.example.smarthire.Recruiter.Post.PostJobFragment;
import com.example.smarthire.Recruiter.Profile.RecruiterProfileFragment;
import com.example.smarthire.WelcomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class RecruiterDashboardActivity extends AppCompatActivity {

    public static final String PREF_NAME = "SmartHirePrefs";

    SharedPreferences preferences;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_dashboard);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        bottomNavigation = findViewById(R.id.recruiterBottomNavigation);

        MaterialCardView profileCard =
                findViewById(R.id.cardRecruiterProfile);

        profileCard.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recruiterContent,
                            new RecruiterProfileFragment())
                    .commit();
        });

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment;

            int id = item.getItemId();

            if (id == R.id.nav_applications) {
                fragment = new ApplicationsFragment();
            } else if (id == R.id.nav_post_job) {
                fragment = new PostJobFragment();
            } else if (id == R.id.nav_candidates) {
                fragment = new CandidatesFragment();
            } else if (id == R.id.nav_ai) {
                fragment = new RecruiterAIFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recruiterContent, fragment)
                    .commit();

            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_applications);
        }
    }

    public void logout() {
        preferences.edit().clear().apply();

        Intent intent = new Intent(
                RecruiterDashboardActivity.this,
                WelcomeActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
