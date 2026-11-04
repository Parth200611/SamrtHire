
package com.example.smarthire.Recruiter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

public class RecruiterLoginActivity extends AppCompatActivity {

    private static final String TAG = "RecruiterLogin";
    private static final String PREF_NAME = "SmartHirePrefs";

    // Set this constant in Urls.java to your actual login API URL.
    private static final String LOGIN_URL = Urls.Login_rec;

    private EditText etEmail, etPassword;
    private View btnLogin, tvForgotPassword;
    private LinearLayout layoutRegister;

    private RequestQueue requestQueue;
    private boolean isLoggingIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_login);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        initializeViews();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        layoutRegister.setOnClickListener(v -> {
            startActivity(new Intent(
                    RecruiterLoginActivity.this,
                    RecruiterRegisterActivity.class
            ));
        });

        btnLogin.setOnClickListener(v -> loginRecruiter());

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            "Forgot password feature is not implemented yet.",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }
    }

    private void initializeViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // These IDs must match activity_recruiter_login.xml.
        btnLogin = findViewById(R.id.btnLogin);
        layoutRegister = findViewById(R.id.layoutRegister);

        // Optional: works if this ID exists in your XML.
        int forgotId = getResources().getIdentifier(
                "tvForgotPassword",
                "id",
                getPackageName()
        );

        if (forgotId != 0) {
            tvForgotPassword = findViewById(forgotId);
        }
    }

    private void loginRecruiter() {

        if (isLoggingIn) {
            return;
        }

        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString();

        if (email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        JSONObject requestData = new JSONObject();

        try {
            requestData.put("email", email);
            requestData.put("password", password);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to prepare login request",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        isLoggingIn = true;
        btnLogin.setEnabled(false);

        Log.d(TAG, "Login URL: " + LOGIN_URL);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                requestData,

                response -> {
                    finishLoading();

                    boolean success = response.optBoolean("status", false);
                    String message = response.optString(
                            "message",
                            "Unexpected server response"
                    );

                    if (!success) {
                        Toast.makeText(
                                RecruiterLoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    JSONObject data = response.optJSONObject("data");

                    if (data == null
                            || data.optString("userId").isEmpty()
                            || data.optString("email").isEmpty()) {

                        Toast.makeText(
                                RecruiterLoginActivity.this,
                                "Invalid login response from server",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    saveRecruiterSession(data);

                    Toast.makeText(
                            RecruiterLoginActivity.this,
                            "Welcome, " + data.optString("full_name", "Recruiter"),
                            Toast.LENGTH_SHORT
                    ).show();

                    // TODO: Replace with your actual recruiter dashboard.
                    // Example:
                     startActivity(new Intent(this, RecruiterDashboardActivity.class));
                     finish();
                },

                error -> {
                    finishLoading();

                    String message = "Unable to connect to server";

                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;
                        String body = "";

                        if (error.networkResponse.data != null) {
                            body = new String(
                                    error.networkResponse.data,
                                    java.nio.charset.StandardCharsets.UTF_8
                            );
                        }

                        Log.e(
                                TAG,
                                "HTTP " + statusCode + ": " + body,
                                error
                        );

                        try {
                            JSONObject errorJson = new JSONObject(body);
                            message = errorJson.optString(
                                    "message",
                                    "Login failed: HTTP " + statusCode
                            );
                        } catch (Exception ignored) {
                            message = "Login failed: HTTP " + statusCode;
                        }

                    } else if (error.getMessage() != null) {
                        message = error.getMessage();
                    }

                    Toast.makeText(
                            RecruiterLoginActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setShouldCache(false);

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,
                        0,
                        1.0f
                )
        );

        requestQueue.add(request);
    }

    private void saveRecruiterSession(JSONObject data) {

        String userId = data.optString("userId", "");
        String fullName = data.optString("full_name", "");
        String userEmail = data.optString("email", "");
        String mobile = data.optString("mobile", "");
        String state = data.optString("state", "");
        String city = data.optString("city", "");

        SharedPreferences preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        preferences.edit()
                .putBoolean("isLoggedIn", true)
                .putString("userRole", "recruiter")
                .putString("userId", userId)
                .putString("full_name", fullName)
                .putString("email", userEmail)
                .putString("mobile", mobile)
                .putString("state", state)
                .putString("city", city)
                .apply();
    }

    private void finishLoading() {
        isLoggingIn = false;
        btnLogin.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
