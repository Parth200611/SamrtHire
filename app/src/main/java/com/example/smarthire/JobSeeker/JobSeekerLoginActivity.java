package com.example.smarthire.JobSeeker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class JobSeekerLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvCreateAccount, tvForgotPassword;

    private RequestQueue requestQueue;

    private static final String API_URL = Urls.Login_User_user;

    private static final String PREF_NAME = "SmartHirePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_seeker_login);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        /*
         * Initialize Views
         */
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        /*
         * Check Existing Login Session
         */
        SharedPreferences preferences =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String savedRole = preferences.getString("userRole", "");

        if (isLoggedIn && "jobseeker".equals(savedRole)) {
            openHomeDashboard();
            return;
        }

        /*
         * Login Button
         */
        btnLogin.setOnClickListener(v -> loginJobSeeker());

        /*
         * Registration Page
         */
        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(
                    JobSeekerLoginActivity.this,
                    JobSeekerRegister.class
            );
            startActivity(intent);
        });

        /*
         * Forgot Password
         * Connect your password-reset screen here when implemented.
         */
        tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(
                        JobSeekerLoginActivity.this,
                        "Forgot password feature will be available soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void loginJobSeeker() {

        String email = etEmail.getText() == null
                ? ""
                : etEmail.getText().toString().trim();

        String password = etPassword.getText() == null
                ? ""
                : etPassword.getText().toString();

        /*
         * Validate Email
         */
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email address");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        /*
         * Validate Password
         */
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() > 72) {
            etPassword.setError("Invalid email or password");
            etPassword.requestFocus();
            return;
        }

        /*
         * Prepare JSON Request
         */
        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (JSONException e) {
            Toast.makeText(this,
                    "Unable to prepare login request",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        /*
         * Prevent Multiple Clicks
         */
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_URL,
                requestBody,

                response -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");

                    try {

                        boolean status = response.optBoolean(
                                "status", false
                        );

                        if (status) {

                            JSONObject user = response.optJSONObject("user");

                            if (user == null) {
                                Toast.makeText(
                                        this,
                                        "Invalid server response",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            /*
                             * Get User Details
                             */
                            String userId = user.optString("id", "");
                            String fullName = user.optString(
                                    "full_name", ""
                            );
                            String userEmail = user.optString(
                                    "email", email
                            );
                            String mobile = user.optString(
                                    "mobile", ""
                            );
                            String state = user.optString(
                                    "state", ""
                            );
                            String city = user.optString(
                                    "city", ""
                            );

                            if (userId.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        "User ID missing from server response",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            /*
                             * Save Login Session
                             */
                            SharedPreferences preferences =
                                    getSharedPreferences(
                                            PREF_NAME,
                                            MODE_PRIVATE
                                    );

                            preferences.edit()
                                    .putBoolean("isLoggedIn", true)
                                    .putString("userRole", "jobseeker")
                                    .putString("userId", userId)
                                    .putString("full_name", fullName)
                                    .putString("email", userEmail)
                                    .putString("mobile", mobile)
                                    .putString("state", state)
                                    .putString("city", city)
                                    .apply();

                            Toast.makeText(
                                    JobSeekerLoginActivity.this,
                                    "Welcome, " + fullName,
                                    Toast.LENGTH_SHORT
                            ).show();

                            /*
                             * Open Home Dashboard
                             */
                            openHomeDashboard();

                        } else {

                            String message = response.optString(
                                    "message",
                                    "Invalid email or password"
                            );

                            Toast.makeText(
                                    JobSeekerLoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                JobSeekerLoginActivity.this,
                                "Error processing login response",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },

                error -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");

                    String message = "Unable to connect to server";

                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;

                        if (statusCode == 401) {
                            message = "Invalid email or password";
                        } else if (statusCode == 404) {
                            message = "Login API not found. Check API URL.";
                        } else if (statusCode >= 500) {
                            message = "Server error. Please try again.";
                        } else if (statusCode == 400) {
                            message = "Invalid login request";
                        }
                    }

                    Toast.makeText(
                            JobSeekerLoginActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    private void openHomeDashboard() {

        Intent intent = new Intent(
                JobSeekerLoginActivity.this,
                JobSeekerHomeActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
