package com.example.smarthire.JobSeeker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.smarthire.JobSeeker.Fragment.Application.ApplicatFragment;
import com.example.smarthire.JobSeeker.Fragment.Explore.ExploreFragment;
import com.example.smarthire.JobSeeker.Fragment.HomeFragment;
import com.example.smarthire.JobSeeker.Fragment.Profile.ProfileFragment;
import com.example.smarthire.R;
import com.example.smarthire.Recruiter.Application.ApplicationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class JobSeekerHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_home);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        setContentView(R.layout.activity_job_seeker_home);

        bottomNavigationView = findViewById(
                R.id.jobSeekerBottomNav
        );

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();

            } else if (id == R.id.nav_explore) {
                fragment = new ExploreFragment();

            } else if (id == R.id.nav_applications) {
                fragment = new ApplicatFragment();

            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.jobSeekerContainer, fragment)
                .commit();
    }
}