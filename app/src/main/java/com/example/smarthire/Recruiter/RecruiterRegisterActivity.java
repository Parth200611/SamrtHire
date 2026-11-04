
package com.example.smarthire.Recruiter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class RecruiterRegisterActivity extends AppCompatActivity {

    private static final String TAG = "RecruiterRegister";
    private static final String REGISTER_URL = Urls.register_rec;

    private EditText etFullName, etEmail, etMobile;
    private EditText etDesignation, etDepartment, etExperience;
    private EditText etCompanyName, etIndustry, etCompanyWebsite;
    private EditText etCompanyDescription, etAddress;
    private EditText etCity, etState, etCountry, etPincode;
    private EditText etPassword, etConfirmPassword;

    private CheckBox cbTerms;
    private Button btnRegister;
    private TextView tvLogin;

    private RequestQueue requestQueue;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_register);

        // Status bar and navigation bar
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        initializeViews();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        btnRegister.setOnClickListener(v -> registerRecruiter());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(
                    RecruiterRegisterActivity.this,
                    RecruiterLoginActivity.class
            ));
            finish();
        });
    }

    private void initializeViews() {

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);

        etDesignation = findViewById(R.id.etDesignation);
        etDepartment = findViewById(R.id.etDepartment);
        etExperience = findViewById(R.id.etExperience);

        etCompanyName = findViewById(R.id.etCompanyName);
        etIndustry = findViewById(R.id.etIndustry);
        etCompanyWebsite = findViewById(R.id.etCompanyWebsite);
        etCompanyDescription = findViewById(R.id.etCompanyDescription);

        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etCountry = findViewById(R.id.etCountry);
        etPincode = findViewById(R.id.etPincode);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

    private boolean validateInputs() {

        String fullName = getText(etFullName);
        String email = getText(etEmail);
        String mobile = getText(etMobile);
        String companyName = getText(etCompanyName);

        // Do not trim passwords: spaces may be part of a password.
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (fullName.isEmpty()) {
            etFullName.setError("Enter your full name");
            etFullName.requestFocus();
            return false;
        }

        if (email.isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (!mobile.matches("\\+?[0-9]{7,15}")) {
            etMobile.setError("Enter a valid mobile number");
            etMobile.requestFocus();
            return false;
        }

        if (companyName.isEmpty()) {
            etCompanyName.setError("Enter your company name");
            etCompanyName.requestFocus();
            return false;
        }

        String experienceText = getText(etExperience);

        if (!experienceText.isEmpty()) {
            try {
                double experience = Double.parseDouble(experienceText);

                if (Double.isNaN(experience)
                        || Double.isInfinite(experience)
                        || experience < 0
                        || experience > 99.9) {

                    etExperience.setError(
                            "Experience must be between 0 and 99.9"
                    );
                    etExperience.requestFocus();
                    return false;
                }

                if (experienceText.contains(".")
                        && experienceText.substring(
                        experienceText.indexOf(".") + 1
                ).length() > 1) {

                    etExperience.setError(
                            "Enter experience with at most one decimal place"
                    );
                    etExperience.requestFocus();
                    return false;
                }

            } catch (NumberFormatException e) {
                etExperience.setError("Enter valid experience in years");
                etExperience.requestFocus();
                return false;
            }
        }

        if (password.length() < 8 || password.length() > 72) {
            etPassword.setError("Password must be 8 to 72 characters");
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(
                    this,
                    "Please accept the terms and conditions",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true;
    }

    private void registerRecruiter() {

        if (isRegistering) {
            return;
        }

        if (!validateInputs()) {
            return;
        }

        JSONObject requestData = new JSONObject();

        try {
            // Required fields expected by recruiter_register.php
            requestData.put("full_name", getText(etFullName));
            requestData.put(
                    "email",
                    getText(etEmail).toLowerCase(Locale.ROOT)
            );
            requestData.put("mobile", getText(etMobile));
            requestData.put("company_name", getText(etCompanyName));

            requestData.put(
                    "password",
                    etPassword.getText().toString()
            );

            requestData.put(
                    "confirm_password",
                    etConfirmPassword.getText().toString()
            );

            // Optional text fields
            putOptionalText(requestData, "designation", etDesignation);
            putOptionalText(requestData, "department", etDepartment);
            putOptionalText(requestData, "industry", etIndustry);
            putOptionalText(requestData, "company_website", etCompanyWebsite);
            putOptionalText(
                    requestData,
                    "company_description",
                    etCompanyDescription
            );
            putOptionalText(requestData, "address", etAddress);
            putOptionalText(requestData, "city", etCity);
            putOptionalText(requestData, "state", etState);
            putOptionalText(requestData, "country", etCountry);
            putOptionalText(requestData, "pincode", etPincode);

            // IMPORTANT: Send experience_years as a JSON number,
            // not as a string.
            String experienceText = getText(etExperience);

            if (!experienceText.isEmpty()) {
                requestData.put(
                        "experience_years",
                        Double.parseDouble(experienceText)
                );
            }

        } catch (JSONException | NumberFormatException e) {
            Log.e(TAG, "Could not prepare request data", e);

            Toast.makeText(
                    this,
                    "Unable to prepare registration data",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Log the URL and field names, but never log passwords.
        Log.d(TAG, "Registration URL: " + REGISTER_URL);
        Log.d(TAG, "Request fields: " + requestData.names());

        isRegistering = true;
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating Account...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                REGISTER_URL,
                requestData,

                response -> {
                    finishLoading();

                    boolean success = response.optBoolean(
                            "status",
                            false
                    );

                    String message = response.optString(
                            "message",
                            "Unexpected server response"
                    );

                    Log.d(TAG, "Registration response: " + response);

                    Toast.makeText(
                            RecruiterRegisterActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    if (success) {
                        Intent intent = new Intent(
                                RecruiterRegisterActivity.this,
                                RecruiterLoginActivity.class
                        );

                        intent.putExtra(
                                "email",
                                getText(etEmail)
                        );

                        startActivity(intent);
                        finish();
                    }
                },

                error -> {
                    finishLoading();

                    int statusCode = -1;
                    String responseBody = "";

                    if (error.networkResponse != null) {
                        statusCode = error.networkResponse.statusCode;

                        if (error.networkResponse.data != null) {
                            responseBody = new String(
                                    error.networkResponse.data,
                                    StandardCharsets.UTF_8
                            );
                        }
                    }

                    Log.e(
                            TAG,
                            "HTTP status: " + statusCode
                                    + ", response body: " + responseBody,
                            error
                    );

                    String message = getServerErrorMessage(
                            statusCode,
                            responseBody,
                            error
                    );

                    Toast.makeText(
                            RecruiterRegisterActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        // Do not cache registration requests or automatically retry POST.
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

    private void putOptionalText(
            JSONObject jsonObject,
            String key,
            EditText editText
    ) throws JSONException {

        String value = getText(editText);

        if (!value.isEmpty()) {
            jsonObject.put(key, value);
        }
    }

    private String getServerErrorMessage(
            int statusCode,
            String responseBody,
            com.android.volley.VolleyError error
    ) {

        // Prefer the useful message returned by PHP.
        if (!responseBody.isEmpty()) {
            try {
                JSONObject errorJson = new JSONObject(responseBody);

                String serverMessage = errorJson.optString(
                        "message",
                        ""
                );

                if (!serverMessage.isEmpty()) {
                    return serverMessage;
                }

            } catch (JSONException e) {
                Log.w(TAG, "Server returned non-JSON error content");
            }
        }

        if (statusCode == 400) {
            return "Invalid registration data. Check the required fields.";
        }

        if (statusCode == 409) {
            return "This email is already registered.";
        }

        if (statusCode == 404) {
            return "Registration API not found. Check the API URL.";
        }

        if (statusCode >= 500) {
            return "Server error. Check PHP and database logs.";
        }

        if (statusCode > 0) {
            return "Request failed with HTTP " + statusCode;
        }

        if (error.getMessage() != null) {
            return "Network error: " + error.getMessage();
        }

        return "Unable to connect to the server.";
    }

    private void finishLoading() {
        isRegistering = false;
        btnRegister.setEnabled(true);
        btnRegister.setText("Create Account");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
