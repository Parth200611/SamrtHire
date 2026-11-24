
package com.example.smarthire.JobSeeker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONObject;

public class ApplyForJob extends AppCompatActivity {

    private static final String TAG = "ApplyForJob";
    private static final String PREF_NAME = "SmartHirePrefs";

    // Job information
    private String jobId;
    private String jobTitle;
    private String hr_mail;

    // Candidate information
    private String userId;
    private String fullName;
    private String userEmail;
    private String mobile;
    private String state;
    private String city;

    // Views
    private TextView tvJobTitle, tvJobId;

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etMobile;
    private TextInputEditText etState;
    private TextInputEditText etCity;
    private TextInputEditText etCoverLetter;

    private ImageView btnBack;
    private MaterialButton btnApplyNow;

    // Volley
    private RequestQueue requestQueue;

    private boolean isSubmitting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_job);

        initializeViews();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        loadJobDetails();
        loadCandidateDetails();

        btnBack.setOnClickListener(v -> finish());

        btnApplyNow.setOnClickListener(v -> submitApplication());
    }

    // -----------------------------------------
    // INITIALIZE VIEWS
    // -----------------------------------------

    private void initializeViews() {

        tvJobTitle = findViewById(R.id.tv_job_title);
        tvJobId = findViewById(R.id.tv_job_id);

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile);
        etState = findViewById(R.id.et_state);
        etCity = findViewById(R.id.et_city);
        etCoverLetter = findViewById(R.id.et_cover_letter);

        btnBack = findViewById(R.id.btn_back);
        btnApplyNow = findViewById(R.id.btn_apply_now);
    }

    // -----------------------------------------
    // GET JOB DETAILS FROM INTENT
    // -----------------------------------------

    private void loadJobDetails() {

        jobId = getIntent().getStringExtra("job_id");
        jobTitle = getIntent().getStringExtra("job_title");
        hr_mail = getIntent().getStringExtra("hr_mail");

        if (jobId == null || jobId.trim().isEmpty() ||
                jobTitle == null || jobTitle.trim().isEmpty()) {

            Toast.makeText(this,
                    "Job information is missing.",
                    Toast.LENGTH_LONG).show();

            finish();
            return;
        }

        tvJobTitle.setText(jobTitle);
        tvJobId.setText("Job ID: " + jobId);
    }

    // -----------------------------------------
    // GET CANDIDATE DETAILS FROM SHAREDPREFERENCES
    // -----------------------------------------

    private void loadCandidateDetails() {

        SharedPreferences preferences =
                getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        userId = preferences.getString("userId", "");
        fullName = preferences.getString("full_name", "");
        userEmail = preferences.getString("email", "");
        mobile = preferences.getString("mobile", "");
        state = preferences.getString("state", "");
        city = preferences.getString("city", "");

        etFullName.setText(fullName);
        etEmail.setText(userEmail);
        etMobile.setText(mobile);
        etState.setText(state);
        etCity.setText(city);

        // Candidate details are read-only
        etFullName.setFocusable(false);
        etEmail.setFocusable(false);
        etMobile.setFocusable(false);
        etState.setFocusable(false);
        etCity.setFocusable(false);
    }

    // -----------------------------------------
    // VALIDATE APPLICATION
    // -----------------------------------------

    private boolean validateApplication() {

        if (jobId == null || jobId.trim().isEmpty()) {
            Toast.makeText(this,
                    "Invalid job information.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(this,
                    "Please login again to apply for this job.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (fullName == null || fullName.trim().isEmpty() ||
                userEmail == null || userEmail.trim().isEmpty() ||
                mobile == null || mobile.trim().isEmpty()) {

            Toast.makeText(this,
                    "Please complete your profile before applying.",
                    Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    // -----------------------------------------
    // SUBMIT APPLICATION
    // -----------------------------------------

    private void submitApplication() {

        if (isSubmitting) {
            return;
        }

        if (!validateApplication()) {
            return;
        }

        String coverLetter = "";

        if (etCoverLetter.getText() != null) {
            coverLetter = etCoverLetter.getText()
                    .toString()
                    .trim();
        }

        try {

            JSONObject params = new JSONObject();

            params.put("job_id", jobId);
            params.put("user_id", userId);
            params.put("hr_mail", hr_mail);
            params.put("cover_letter", coverLetter);

            sendApplication(params);

        } catch (Exception e) {

            Log.e(TAG, "JSON Error: " + e.getMessage());

            Toast.makeText(this,
                    "Unable to prepare application.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------------------------------
    // VOLLEY API REQUEST
    // -----------------------------------------

    private void sendApplication(JSONObject params) {

        isSubmitting = true;

        btnApplyNow.setEnabled(false);
        btnApplyNow.setText("Submitting Application...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.add_apply_for_job,
                params,

                response -> {

                    isSubmitting = false;

                    boolean status = response.optBoolean("status", false);

                    String message = response.optString(
                            "message",
                            "Something went wrong."
                    );

                    if (status) {

                        JSONObject application =
                                response.optJSONObject("application");

                        String applicationId = "";

                        if (application != null) {
                            applicationId = application.optString("id", "");
                        }

                        Log.d(TAG, "Application submitted. ID: "
                                + applicationId);

                        showSuccessDialog(message, applicationId);

                    } else {

                        btnApplyNow.setEnabled(true);
                        btnApplyNow.setText("Submit Application");

                        Toast.makeText(
                                ApplyForJob.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    isSubmitting = false;

                    btnApplyNow.setEnabled(true);
                    btnApplyNow.setText("Submit Application");

                    String message = "Unable to submit application. Please try again.";

                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;

                        try {

                            String responseBody = new String(
                                    error.networkResponse.data,
                                    java.nio.charset.StandardCharsets.UTF_8
                            );

                            JSONObject errorObject =
                                    new JSONObject(responseBody);

                            message = errorObject.optString(
                                    "message",
                                    message
                            );

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing API response", e);
                        }

                        Log.e(TAG, "HTTP Status: " + statusCode);

                    } else {

                        Log.e(TAG, "Network error", error);
                        message = "Network error. Check your internet connection.";
                    }

                    Toast.makeText(
                            ApplyForJob.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        request.setShouldCache(false);
        request.setTag(TAG);

        requestQueue.add(request);
    }

    // -----------------------------------------
    // SUCCESS DIALOG
    // -----------------------------------------

    private void showSuccessDialog(
            String message,
            String applicationId) {

        String displayMessage = message;

        if (applicationId != null && !applicationId.isEmpty()) {
            displayMessage += "\n\nApplication ID: " + applicationId;
        }

        new AlertDialog.Builder(this)
                .setTitle("Application Submitted!")
                .setMessage(displayMessage)
                .setCancelable(false)
                .setPositiveButton("Done", (dialog, which) -> {

                    // Tell the previous activity that application succeeded
                    setResult(RESULT_OK);

                    // Return to ViewCompleteJobActivity
                    finish();
                })
                .show();
    }

    // -----------------------------------------
    // CANCEL REQUEST
    // -----------------------------------------

    @Override
    protected void onDestroy() {

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }

        super.onDestroy();
    }
}
