
package com.example.smarthire.JobSeeker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class JobSeekerRegister extends AppCompatActivity {

    // Replace this with your actual PHP API URL.
    // Android Emulator: http://10.0.2.2/SmartHireAPI/register_jobseeker.php
    // Physical phone: http://YOUR_PC_IP/SmartHireAPI/register_jobseeker.php
    private static final String REGISTER_URL =
            Urls.register_User;

    private TextInputLayout tilFullName, tilMobile, tilEmail,
            tilState, tilCity, tilPassword, tilConfirmPassword;

    private TextInputEditText etFullName, etMobile, etEmail,
            etCity, etPassword, etConfirmPassword;

    private AutoCompleteTextView actvState;
    private CheckBox cbTerms;
    private MaterialButton btnCreateAccount;
    private TextView tvSignIn;
    private Toolbar toolbarRegister;

    private RequestQueue requestQueue;
    private boolean isRegistering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_job_seeker_register);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.toolbar_register),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            view.getPaddingLeft(),
                            systemBars.top,
                            view.getPaddingRight(),
                            view.getPaddingBottom()
                    );

                    return insets;
                }
        );

        initializeViews();
        setupToolbar();
        setupStateDropdown();

        requestQueue = Volley.newRequestQueue(
                getApplicationContext()
        );

        btnCreateAccount.setOnClickListener(v -> registerJobSeeker());

        tvSignIn.setOnClickListener(v -> openLoginPage());
    }

    private void initializeViews() {

        toolbarRegister = findViewById(R.id.toolbar_register);

        tilFullName = findViewById(R.id.til_full_name);
        tilMobile = findViewById(R.id.til_mobile);
        tilEmail = findViewById(R.id.til_email);
        tilState = findViewById(R.id.til_state);
        tilCity = findViewById(R.id.til_city);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(
                R.id.til_confirm_password
        );

        etFullName = findViewById(R.id.et_full_name);
        etMobile = findViewById(R.id.et_mobile);
        etEmail = findViewById(R.id.et_email);
        actvState = findViewById(R.id.actv_state);
        etCity = findViewById(R.id.et_city);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(
                R.id.et_confirm_password
        );

        cbTerms = findViewById(R.id.cb_terms);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvSignIn = findViewById(R.id.tv_sign_in);
    }

    private void setupToolbar() {

        toolbarRegister.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupStateDropdown() {

        List<String> states = Arrays.asList(
                "Andhra Pradesh",
                "Arunachal Pradesh",
                "Assam",
                "Bihar",
                "Chhattisgarh",
                "Goa",
                "Gujarat",
                "Haryana",
                "Himachal Pradesh",
                "Jharkhand",
                "Karnataka",
                "Kerala",
                "Madhya Pradesh",
                "Maharashtra",
                "Manipur",
                "Meghalaya",
                "Mizoram",
                "Nagaland",
                "Odisha",
                "Punjab",
                "Rajasthan",
                "Sikkim",
                "Tamil Nadu",
                "Telangana",
                "Tripura",
                "Uttar Pradesh",
                "Uttarakhand",
                "West Bengal",
                "Delhi",
                "Jammu and Kashmir",
                "Ladakh",
                "Puducherry",
                "Chandigarh",
                "Andaman and Nicobar Islands",
                "Lakshadweep",
                "Dadra and Nagar Haveli and Daman and Diu"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                states
        );

        actvState.setAdapter(adapter);
        actvState.setKeyListener(null);
        actvState.setOnClickListener(v ->
                actvState.showDropDown()
        );
    }

    private void registerJobSeeker() {

        if (isRegistering) {
            return;
        }

        clearErrors();

        String fullName = getText(etFullName);
        String mobile = getText(etMobile);
        String email = getText(etEmail).toLowerCase(Locale.ROOT);
        String state = actvState.getText().toString().trim();
        String city = getText(etCity);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        // Validate full name
        if (fullName.length() < 2 || fullName.length() > 100) {
            tilFullName.setError(
                    "Enter a name containing 2 to 100 characters"
            );
            etFullName.requestFocus();
            return;
        }

        // Validate mobile number
        if (!mobile.matches("^[6-9][0-9]{9}$")) {
            tilMobile.setError(
                    "Enter a valid 10-digit mobile number"
            );
            etMobile.requestFocus();
            return;
        }

        // Validate email
        if (email.length() > 150
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Validate state
        if (TextUtils.isEmpty(state)) {
            tilState.setError("Please select your state");
            actvState.requestFocus();
            return;
        }

        // Validate city
        if (city.isEmpty() || city.length() > 100) {
            tilCity.setError("Enter a valid city name");
            etCity.requestFocus();
            return;
        }

        // Validate password
        if (password.length() < 8 || password.length() > 72) {
            tilPassword.setError(
                    "Password must contain 8 to 72 bytes"
            );
            etPassword.requestFocus();
            return;
        }

        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(
                    "Passwords do not match"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Validate terms
        if (!cbTerms.isChecked()) {
            Toast.makeText(
                    this,
                    "Please accept the Terms and Conditions",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        sendRegistrationRequest(
                fullName,
                mobile,
                email,
                password,
                confirmPassword,
                state,
                city
        );
    }

    private void sendRegistrationRequest(
            String fullName,
            String mobile,
            String email,
            String password,
            String confirmPassword,
            String state,
            String city
    ) {

        JSONObject requestBody = new JSONObject();

        try {
            requestBody.put("full_name", fullName);
            requestBody.put("mobile", mobile);
            requestBody.put("email", email);
            requestBody.put("password", password);
            requestBody.put("confirm_password", confirmPassword);
            requestBody.put("state", state);
            requestBody.put("city", city);

        } catch (JSONException e) {
            Toast.makeText(
                    this,
                    "Unable to prepare registration data",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        isRegistering = true;
        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Creating Account...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                REGISTER_URL,
                requestBody,

                response -> {

                    isRegistering = false;
                    btnCreateAccount.setEnabled(true);
                    btnCreateAccount.setText("Create Account");

                    boolean success = response.optBoolean(
                            "status",
                            false
                    );

                    String message = response.optString(
                            "message",
                            "Unexpected server response"
                    );

                    if (success) {

                        JSONObject user = response.optJSONObject("user");

                        String registeredEmail = email;

                        if (user != null) {
                            registeredEmail = user.optString(
                                    "email",
                                    email
                            );
                        }

                        Toast.makeText(
                                JobSeekerRegister.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();

                        openLoginPage(registeredEmail);

                    } else {

                        Toast.makeText(
                                JobSeekerRegister.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    isRegistering = false;
                    btnCreateAccount.setEnabled(true);
                    btnCreateAccount.setText("Create Account");

                    String message = "Unable to connect to the server";

                    if (error.networkResponse != null
                            && error.networkResponse.data != null) {

                        try {
                            String body = new String(
                                    error.networkResponse.data,
                                    java.nio.charset.StandardCharsets.UTF_8
                            );

                            JSONObject errorJson = new JSONObject(body);

                            message = errorJson.optString(
                                    "message",
                                    message
                            );

                        } catch (Exception ignored) {
                            // Keep the default error message.
                        }

                    } else if (error instanceof
                            com.android.volley.TimeoutError) {

                        message = "Request timed out. Please try again.";

                    } else if (error instanceof
                            com.android.volley.NoConnectionError) {

                        message = "No connection. Check your network and API URL.";
                    }

                    Toast.makeText(
                            JobSeekerRegister.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        15000,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requestQueue.add(request);
    }

    private String getText(TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    private void clearErrors() {

        tilFullName.setError(null);
        tilMobile.setError(null);
        tilEmail.setError(null);
        tilState.setError(null);
        tilCity.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void openLoginPage() {
        openLoginPage("");
    }

    private void openLoginPage(String email) {

        // Replace JobSeekerLogin.class with your actual
        // Job Seeker Login Activity class if named differently.

        Intent intent = new Intent(
                JobSeekerRegister.this,
                JobSeekerLoginActivity.class
        );

        if (!TextUtils.isEmpty(email)) {
            intent.putExtra("registered_email", email);
        }

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
