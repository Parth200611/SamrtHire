
package com.example.smarthire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthire.JobSeeker.JobSeekerHomeActivity;
import com.example.smarthire.Recruiter.RecruiterDashboardActivity;

public class Splashactivity extends AppCompatActivity {

    private static final String PREF_NAME = "SmartHirePrefs";

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final Runnable navigateRunnable = new Runnable() {
        @Override
        public void run() {

            SharedPreferences preferences =
                    getSharedPreferences(
                            PREF_NAME,
                            MODE_PRIVATE
                    );

            boolean isLoggedIn =
                    preferences.getBoolean("isLoggedIn", false);

            String userRole =
                    preferences.getString("userRole", "");

            Intent intent;

            if (isLoggedIn && "jobseeker".equalsIgnoreCase(userRole)) {

                // Navigate to JobSeeker Home
                intent = new Intent(
                        Splashactivity.this,
                        JobSeekerHomeActivity.class
                );

            } else if (isLoggedIn && "recruiter".equalsIgnoreCase(userRole)) {

                // Navigate to Recruiter Dashboard
                intent = new Intent(
                        Splashactivity.this,
                        RecruiterDashboardActivity.class
                );

            } else {

                // Not logged in or invalid role
                intent = new Intent(
                        Splashactivity.this,
                        WelcomeActivity.class
                );
            }

            // Clear previous activities from the back stack
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // White status bar and navigation bar
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        // Use dark icons on white system bars
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        // Wait 3 seconds, then check login status and role
        handler.postDelayed(navigateRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(navigateRunnable);
        super.onDestroy();
    }
}
