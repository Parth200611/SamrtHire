package com.example.smarthire.Recruiter.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateRect extends AppCompatActivity {

    private MaterialToolbar toolbar;

    // Personal information
    private EditText etFullName, etMobile, etDesignation;
    private EditText etDepartment, etExperienceYears;

    // Company information
    private EditText etCompanyName, etIndustry;
    private EditText etCompanyWebsite, etCompanyDescription;

    // Address information
    private EditText etAddress, etCity, etState;
    private EditText etCountry, etPincode;

    private MaterialButton btnSaveChanges, btnCancel;

    private RequestQueue requestQueue;

    private String userEmail = "";

    private static final String PREF_NAME = "SmartHirePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rect);

        initializeViews();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Get logged-in recruiter's email
        SharedPreferences preferences = getSharedPreferences(
                PREF_NAME,
                MODE_PRIVATE
        );

        userEmail = preferences.getString("email", "");

        // Toolbar back button
        toolbar.setNavigationOnClickListener(v -> finish());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());

        // Save button
        btnSaveChanges.setOnClickListener(v -> validateAndUpdateProfile());

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(
                    this,
                    "Recruiter email not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // Load existing profile information
        fetchRecruiterProfile();
    }

    private void initializeViews() {

        toolbar = findViewById(R.id.toolbar_update);

        etFullName = findViewById(R.id.et_full_name);
        etMobile = findViewById(R.id.et_mobile);
        etDesignation = findViewById(R.id.et_designation);
        etDepartment = findViewById(R.id.et_department);
        etExperienceYears = findViewById(R.id.et_experience_years);

        etCompanyName = findViewById(R.id.et_company_name);
        etIndustry = findViewById(R.id.et_industry);
        etCompanyWebsite = findViewById(R.id.et_company_website);
        etCompanyDescription = findViewById(R.id.et_company_description);

        etAddress = findViewById(R.id.et_address);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etCountry = findViewById(R.id.et_country);
        etPincode = findViewById(R.id.et_pincode);

        btnSaveChanges = findViewById(R.id.btn_save_changes);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    // ---------------------------------------------------------
    // FETCH EXISTING RECRUITER PROFILE
    // ---------------------------------------------------------

    private void fetchRecruiterProfile() {

        try {

            JSONObject requestData = new JSONObject();
            requestData.put("email", userEmail);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Urls.get_rect,
                    requestData,

                    response -> {

                        boolean status = response.optBoolean("status", false);

                        if (status) {

                            JSONObject recruiter =
                                    response.optJSONObject("recruiter");

                            if (recruiter != null) {
                                setProfileData(recruiter);
                            } else {
                                Toast.makeText(
                                        this,
                                        "Recruiter profile data not found",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        } else {

                            Toast.makeText(
                                    this,
                                    response.optString(
                                            "message",
                                            "Unable to fetch recruiter profile"
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    },

                    error -> {

                        Toast.makeText(
                                this,
                                "Network error while loading profile",
                                Toast.LENGTH_LONG
                        ).show();
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // SET FETCHED DATA INTO EDITTEXT FIELDS
    // ---------------------------------------------------------

    private void setProfileData(JSONObject recruiter) {

        etFullName.setText(recruiter.optString("full_name", ""));
        etMobile.setText(recruiter.optString("mobile", ""));
        etDesignation.setText(recruiter.optString("designation", ""));
        etDepartment.setText(recruiter.optString("department", ""));
        etExperienceYears.setText(
                recruiter.optString("experience_years", "")
        );

        etCompanyName.setText(recruiter.optString("company_name", ""));
        etIndustry.setText(recruiter.optString("industry", ""));
        etCompanyWebsite.setText(
                recruiter.optString("company_website", "")
        );
        etCompanyDescription.setText(
                recruiter.optString("company_description", "")
        );

        etAddress.setText(recruiter.optString("address", ""));
        etCity.setText(recruiter.optString("city", ""));
        etState.setText(recruiter.optString("state", ""));
        etCountry.setText(recruiter.optString("country", ""));
        etPincode.setText(recruiter.optString("pincode", ""));
    }

    // ---------------------------------------------------------
    // VALIDATE FIELDS BEFORE UPDATE
    // ---------------------------------------------------------

    private void validateAndUpdateProfile() {

        String fullName = getValue(etFullName);
        String mobile = getValue(etMobile);
        String designation = getValue(etDesignation);
        String department = getValue(etDepartment);
        String experienceYears = getValue(etExperienceYears);

        String companyName = getValue(etCompanyName);
        String industry = getValue(etIndustry);
        String companyWebsite = getValue(etCompanyWebsite);
        String companyDescription = getValue(etCompanyDescription);

        String address = getValue(etAddress);
        String city = getValue(etCity);
        String state = getValue(etState);
        String country = getValue(etCountry);
        String pincode = getValue(etPincode);

        // Required field validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Enter full name");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Enter mobile number");
            etMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(companyName)) {
            etCompanyName.setError("Enter company name");
            etCompanyName.requestFocus();
            return;
        }

        // Experience validation
        if (!TextUtils.isEmpty(experienceYears)) {

            try {

                double experience = Double.parseDouble(experienceYears);

                if (experience < 0 || experience > 999.9) {
                    etExperienceYears.setError(
                            "Experience must be between 0 and 999.9"
                    );
                    etExperienceYears.requestFocus();
                    return;
                }

            } catch (NumberFormatException e) {

                etExperienceYears.setError(
                        "Enter a valid experience"
                );
                etExperienceYears.requestFocus();
                return;
            }
        }

        // Website validation if provided
        if (!TextUtils.isEmpty(companyWebsite)
                && !Patterns.WEB_URL.matcher(companyWebsite).matches()) {

            etCompanyWebsite.setError("Enter a valid website URL");
            etCompanyWebsite.requestFocus();
            return;
        }

        // Prepare all profile data
        JSONObject updateData = new JSONObject();

        try {

            // Email is used only to identify the recruiter
            updateData.put("email", userEmail);

            // Personal information
            updateData.put("full_name", fullName);
            updateData.put("mobile", mobile);
            updateData.put("designation", designation);
            updateData.put("department", department);
            updateData.put("experience_years", experienceYears);

            // Company information
            updateData.put("company_name", companyName);
            updateData.put("industry", industry);
            updateData.put("company_website", companyWebsite);
            updateData.put("company_description", companyDescription);

            // Address information
            updateData.put("address", address);
            updateData.put("city", city);
            updateData.put("state", state);
            updateData.put("country", country);
            updateData.put("pincode", pincode);

            updateRecruiterProfile(updateData);

        } catch (JSONException e) {
            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to prepare profile data",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ---------------------------------------------------------
    // SEND PROFILE DATA TO PHP API
    // ---------------------------------------------------------

    private void updateRecruiterProfile(JSONObject updateData) {

        btnSaveChanges.setEnabled(false);
        btnSaveChanges.setText("Updating...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.update_rec,
                updateData,

                response -> {

                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");

                    boolean status = response.optBoolean("status", false);

                    String message = response.optString(
                            "message",
                            "No response message"
                    );

                    Toast.makeText(
                            UpdateRect.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    if (status) {

                        // Update name and mobile in the local session
                        SharedPreferences preferences =
                                getSharedPreferences(
                                        PREF_NAME,
                                        MODE_PRIVATE
                                );

                        preferences.edit()
                                .putString(
                                        "full_name",
                                        updateData.optString("full_name", "")
                                )
                                .putString(
                                        "mobile",
                                        updateData.optString("mobile", "")
                                )
                                .apply();

                        // Return to recruiter profile screen
                        setResult(RESULT_OK);
                        finish();
                    }
                },

                error -> {

                    btnSaveChanges.setEnabled(true);
                    btnSaveChanges.setText("Save Changes");

                    String errorMessage =
                            "Unable to update profile. Check your connection.";

                    if (error.networkResponse != null) {
                        errorMessage += " HTTP "
                                + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            UpdateRect.this,
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(request);
    }

    // ---------------------------------------------------------
    // GET EDITTEXT VALUE
    // ---------------------------------------------------------

    private String getValue(EditText editText) {
        return editText.getText().toString().trim();
    }
}
