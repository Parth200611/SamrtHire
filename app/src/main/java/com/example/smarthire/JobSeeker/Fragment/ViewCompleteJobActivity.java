
package com.example.smarthire.JobSeeker.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.JobSeeker.ApplyForJob;
import com.example.smarthire.R;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewCompleteJobActivity extends AppCompatActivity {

    private static final String TAG = "ViewCompleteJob";

    private RequestQueue requestQueue;
    private String jobId = "";
    private JSONObject currentJob;

    // Toolbar
    private ImageView btnBack, btnShareJob;

    // Job header
    private TextView tvJobCategory, tvJobStatus, tvJobTitle;
    private TextView tvJobDepartment, tvJobLocation;
    private TextView tvJobExperience, tvEmploymentType;
    private TextView tvWorkMode, tvJobSalary;

    // Job details
    private TextView tvDetailDepartment;
    private TextView tvJobDescription, tvJobResponsibilities;
    private TextView tvRequiredSkills, tvSoftSkills;
    private TextView tvMinimumQualification, tvPreferredCourse;
    private TextView tvJobOpenings, tvJobBenefits;
    private TextView tvApplicationDeadline;
    private TextView tvHrEmail, tvCompanyEmail;
    private TextView tvJobCreatedAt;

    // Bottom section
    private TextView tvBottomSalary;
    private Button btnApplyNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complete_job);

        initializeViews();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        jobId = getIntent().getStringExtra("job_id");

        if (jobId == null || jobId.trim().isEmpty()) {
            Toast.makeText(this, "Invalid job ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupClickListeners();
        loadJobDetails();
    }

    private void initializeViews() {

        // Toolbar
        btnBack = findViewById(R.id.btn_back);
        btnShareJob = findViewById(R.id.btn_share_job);

        // Header
        tvJobCategory = findViewById(R.id.tv_job_category);
        tvJobStatus = findViewById(R.id.tv_job_status);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvJobDepartment = findViewById(R.id.tv_job_department);
        tvJobLocation = findViewById(R.id.tv_job_location);
        tvJobExperience = findViewById(R.id.tv_job_experience);
        tvEmploymentType = findViewById(R.id.tv_employment_type);
        tvWorkMode = findViewById(R.id.tv_work_mode);
        tvJobSalary = findViewById(R.id.tv_job_salary);

        // Details
        tvDetailDepartment = findViewById(R.id.tv_detail_department);
        tvJobDescription = findViewById(R.id.tv_job_description);
        tvJobResponsibilities = findViewById(R.id.tv_job_responsibilities);
        tvRequiredSkills = findViewById(R.id.tv_required_skills);
        tvSoftSkills = findViewById(R.id.tv_soft_skills);
        tvMinimumQualification = findViewById(R.id.tv_minimum_qualification);
        tvPreferredCourse = findViewById(R.id.tv_preferred_course);
        tvJobOpenings = findViewById(R.id.tv_job_openings);
        tvJobBenefits = findViewById(R.id.tv_job_benefits);
        tvApplicationDeadline = findViewById(R.id.tv_application_deadline);
        tvHrEmail = findViewById(R.id.tv_hr_email);
        tvCompanyEmail = findViewById(R.id.tv_company_email);
        tvJobCreatedAt = findViewById(R.id.tv_job_created_at);

        // Bottom apply section
        tvBottomSalary = findViewById(R.id.tv_bottom_salary);
        btnApplyNow = findViewById(R.id.btn_apply_now);
    }

    private void setupClickListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnShareJob.setOnClickListener(v -> shareJob());

        tvHrEmail.setOnClickListener(v ->
                openEmail(getTextValue(tvHrEmail)));

        tvCompanyEmail.setOnClickListener(v ->
                openEmail(getTextValue(tvCompanyEmail)));

        btnApplyNow.setOnClickListener(v -> {

            Intent i = new Intent(
                    ViewCompleteJobActivity.this,
                    ApplyForJob.class
            );

            i.putExtra("job_id", jobId);
            i.putExtra("job_title", tvJobTitle.getText().toString());
            i.putExtra("hr_mail", tvCompanyEmail.getText().toString());

            startActivity(i);
        });
    }

    // ------------------------------------------------
    // FETCH JOB DETAILS
    // ------------------------------------------------

    private void loadJobDetails() {

        String apiUrl = Urls.get_job_details_id + "?id=" + Uri.encode(jobId);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,

                response -> {

                    Log.d(TAG, "Job response: " + response);

                    boolean status = response.optBoolean("status", false);

                    if (!status) {
                        String message = response.optString(
                                "message", "Unable to fetch job details.");

                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    currentJob = response.optJSONObject("job");

                    if (currentJob == null) {
                        Toast.makeText(this,
                                "Job details not found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    displayJobDetails(currentJob);
                },

                error -> {

                    Log.e(TAG, "Job API error", error);

                    String message = "Unable to load job details.";

                    if (error.networkResponse == null) {
                        message = "Network error. Check your internet connection and API URL.";
                    } else {
                        message = "Server error: " + error.networkResponse.statusCode;
                    }

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                }
        );

        request.setShouldCache(false);
        request.setTag(TAG);
        requestQueue.add(request);
    }

    // ------------------------------------------------
    // DISPLAY JOB DETAILS
    // ------------------------------------------------

    private void displayJobDetails(JSONObject job) {

        String jobTitle = getValue(job, "job_title");
        String department = getValue(job, "department");
        String category = getValue(job, "job_category");

        String location = buildLocation(
                getValue(job, "job_location"),
                getValue(job, "state"),
                getValue(job, "country")
        );

        String experience = buildExperience(
                getValue(job, "min_experience"),
                getValue(job, "max_experience")
        );

        String salary = formatSalary(
                getValue(job, "min_salary"),
                getValue(job, "max_salary"),
                getValue(job, "salary_period")
        );

        // Header
        tvJobTitle.setText(showValue(jobTitle));
        tvJobCategory.setText(showValue(category));
        tvJobStatus.setText(showValue(getValue(job, "job_status")));

        tvJobDepartment.setText(showValue(department));
        tvJobLocation.setText(showValue(location));
        tvJobExperience.setText(showValue(experience));

        tvEmploymentType.setText(showValue(
                getValue(job, "employment_type")));

        tvWorkMode.setText(showValue(
                getValue(job, "work_mode")));

        tvJobSalary.setText(salary);
        tvBottomSalary.setText(salary);

        // Job information
        tvDetailDepartment.setText(showValue(department));

        tvJobDescription.setText(showValue(
                getValue(job, "job_description")));

        tvJobResponsibilities.setText(showValue(
                getValue(job, "responsibilities")));

        tvRequiredSkills.setText(showValue(
                getValue(job, "required_skills")));

        tvSoftSkills.setText(showValue(
                getValue(job, "soft_skills")));

        tvMinimumQualification.setText(showValue(
                getValue(job, "minimum_qualification")));

        tvPreferredCourse.setText(showValue(
                getValue(job, "preferred_course")));

        tvJobOpenings.setText(showValue(
                getValue(job, "openings")));

        tvJobBenefits.setText(showValue(
                getValue(job, "benefits")));

        tvApplicationDeadline.setText(formatDate(
                getValue(job, "application_deadline")));

        tvJobCreatedAt.setText(formatDate(
                getValue(job, "created_at")));

        tvHrEmail.setText(showValue(
                getValue(job, "hr_email")));

        tvCompanyEmail.setText(showValue(
                getValue(job, "company_email")));

        // Disable Apply button if job is not published
        String status = getValue(job, "job_status");

        boolean isPublished = status.equalsIgnoreCase("Published");

        btnApplyNow.setEnabled(isPublished);
        btnApplyNow.setAlpha(isPublished ? 1f : 0.5f);

        Log.d(TAG, "Job details displayed successfully");
    }

    // ------------------------------------------------
    // SHARE JOB
    // ------------------------------------------------

    private void shareJob() {

        if (currentJob == null) {
            Toast.makeText(this,
                    "Job details are not loaded yet.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String title = getValue(currentJob, "job_title");
        String companyEmail = getValue(currentJob, "company_email");
        String location = getValue(currentJob, "job_location");

        String shareText =
                "SmartHire Job Opportunity\n\n" +
                        "Job: " + title + "\n" +
                        "Location: " + location + "\n" +
                        "Company Email: " + companyEmail + "\n\n" +
                        "View this job on SmartHire.\n" +
                        "Job ID: " + jobId;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(
                shareIntent, "Share Job"));
    }

    // ------------------------------------------------
    // OPEN EMAIL
    // ------------------------------------------------

    private void openEmail(String email) {

        if (email.isEmpty() || email.equals("Not available")) {
            Toast.makeText(this,
                    "Email address not available.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});

        try {
            startActivity(Intent.createChooser(
                    intent, "Send email"));
        } catch (Exception e) {
            Toast.makeText(this,
                    "No email application found.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------------------------
    // HELPER METHODS
    // ------------------------------------------------

    private String getValue(JSONObject object, String key) {

        if (object == null || object.isNull(key)) {
            return "";
        }

        String value = object.optString(key, "");

        if (value == null || value.equalsIgnoreCase("null")) {
            return "";
        }

        return value.trim();
    }

    private String showValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not specified";
        }

        return value;
    }

    private String getTextValue(TextView textView) {

        String value = textView.getText().toString().trim();

        if (value.equalsIgnoreCase("Not specified")) {
            return "";
        }

        return value;
    }

    private String buildLocation(
            String location,
            String state,
            String country) {

        StringBuilder result = new StringBuilder();

        if (!location.isEmpty()) {
            result.append(location);
        }

        if (!state.isEmpty() &&
                !state.equalsIgnoreCase(location)) {

            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(state);
        }

        if (!country.isEmpty()) {

            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(country);
        }

        return result.toString();
    }

    private String buildExperience(
            String minimum,
            String maximum) {

        if (minimum.isEmpty() && maximum.isEmpty()) {
            return "Not specified";
        }

        if (maximum.isEmpty()) {
            return minimum;
        }

        return minimum + " - " + maximum + " years";
    }

    private String formatSalary(
            String minimum,
            String maximum,
            String period) {

        if (minimum.isEmpty() && maximum.isEmpty()) {
            return "Salary not disclosed";
        }

        String formattedMin = formatCurrency(minimum);
        String formattedMax = formatCurrency(maximum);

        String result;

        if (!minimum.isEmpty() && !maximum.isEmpty()) {
            result = formattedMin + " - " + formattedMax;
        } else if (!minimum.isEmpty()) {
            result = formattedMin;
        } else {
            result = "Up to " + formattedMax;
        }

        if (!period.isEmpty()) {
            result += " / " + period;
        }

        return result;
    }

    private String formatCurrency(String amount) {

        try {
            double value = Double.parseDouble(amount);

            NumberFormat formatter =
                    NumberFormat.getCurrencyInstance(
                            new Locale("en", "IN"));

            return formatter.format(value);

        } catch (Exception e) {
            return amount;
        }
    }

    private String formatDate(String dateValue) {

        if (dateValue.isEmpty()) {
            return "Not specified";
        }

        try {
            String inputPattern;

            if (dateValue.length() >= 19) {
                inputPattern = "yyyy-MM-dd HH:mm:ss";
            } else {
                inputPattern = "yyyy-MM-dd";
            }

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(inputPattern, Locale.getDefault());

            inputFormat.setLenient(false);

            Date date = inputFormat.parse(dateValue);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            return outputFormat.format(date);

        } catch (Exception e) {
            return dateValue;
        }
    }

    @Override
    protected void onDestroy() {

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }

        super.onDestroy();
    }
}
