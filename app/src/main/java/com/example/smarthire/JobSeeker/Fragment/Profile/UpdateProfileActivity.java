package com.example.smarthire.JobSeeker.Fragment.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    // Change this URL to your actual XAMPP API folder.
    private static final String BASE_URL =
            "http://10.0.2.2/SmartHireAPI/";

    private String userEmail = "";

    private RequestQueue requestQueue;

    // Personal information
    private TextInputEditText etFullName, etMobile, etDob, etCity;
    private MaterialAutoCompleteTextView actState;

    // Education
    private MaterialAutoCompleteTextView actQualification;
    private TextInputEditText actCourse, etCollege, etPercentage;
    private MaterialAutoCompleteTextView actGraduationYear, actEducationStatus;

    // Skills
    private TextInputEditText actTechnicalSkills, actSoftSkills;

    // Experience
    private MaterialAutoCompleteTextView actExperienceType;
    private TextInputEditText etExperienceYears, etPreviousCompany;
    private TextInputEditText actPreviousJobTitle;

    // Projects and certifications
    private TextInputEditText etProjects, etCertifications;

    // Professional links
    private TextInputEditText etLinkedin, etGithub, etPortfolio;

    // Job preferences
    private MaterialAutoCompleteTextView actDesiredRole, actIndustry;
    private TextInputEditText etPreferredLocation, etSalaryMin, etSalaryMax;
    private MaterialAutoCompleteTextView actWorkMode, actEmploymentType;

    private MaterialButton btnSave;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        requestQueue = Volley.newRequestQueue(this);

        // Get email from SharedPreferences
        preferences =
                getSharedPreferences("SmartHirePrefs", MODE_PRIVATE);

        userEmail = preferences.getString("email", "");

        if (userEmail == null || userEmail.trim().isEmpty()) {
            Toast.makeText(this, "User email not found. Please login again.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupDropdowns();

        // Load existing profile details
        loadProfileData();

        btnSave.setOnClickListener(v -> collectAndUpdateProfile());
    }

    private void initializeViews() {

        // Personal information
        etFullName = findViewById(R.id.etFullName);
        etMobile = findViewById(R.id.etMobile);
        etDob = findViewById(R.id.etDob);
        actState = findViewById(R.id.actState);
        etCity = findViewById(R.id.etCity);

        // Education
        actQualification = findViewById(R.id.actQualification);
        actCourse = findViewById(R.id.actCourse);
        etCollege = findViewById(R.id.etCollege);
        actGraduationYear = findViewById(R.id.actGraduationYear);
        etPercentage = findViewById(R.id.etPercentage);
        actEducationStatus = findViewById(R.id.actEducationStatus);

        // Skills
        actTechnicalSkills = findViewById(R.id.actTechnicalSkills);
        actSoftSkills = findViewById(R.id.actSoftSkills);

        // Experience
        actExperienceType = findViewById(R.id.actExperienceType);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etPreviousCompany = findViewById(R.id.etPreviousCompany);
        actPreviousJobTitle = findViewById(R.id.actPreviousJobTitle);

        // Projects
        etProjects = findViewById(R.id.etProjects);
        etCertifications = findViewById(R.id.etCertifications);

        // Links
        etLinkedin = findViewById(R.id.etLinkedin);
        etGithub = findViewById(R.id.etGithub);
        etPortfolio = findViewById(R.id.etPortfolio);

        // Job preferences
        actDesiredRole = findViewById(R.id.actDesiredRole);
        actIndustry = findViewById(R.id.actIndustry);
        etPreferredLocation = findViewById(R.id.etPreferredLocation);
        etSalaryMin = findViewById(R.id.etSalaryMin);
        etSalaryMax = findViewById(R.id.etSalaryMax);
        actWorkMode = findViewById(R.id.actWorkMode);
        actEmploymentType = findViewById(R.id.actEmploymentType);

        btnSave = findViewById(R.id.btnSave);
    }

    private void setDropdown(MaterialAutoCompleteTextView view, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                items
        );
        view.setAdapter(adapter);
        view.setOnClickListener(v -> view.showDropDown());
    }

    private void setupDropdowns() {

        setDropdown(actState, new String[]{
                "Maharashtra", "Delhi", "Haryana", "Gujarat",
                "Karnataka", "Madhya Pradesh", "Rajasthan",
                "Uttar Pradesh", "Telangana", "Tamil Nadu"
        });

        setDropdown(actQualification, new String[]{
                "10th", "12th", "Diploma", "Graduation",
                "Post Graduation", "PhD"
        });

        setDropdown(actGraduationYear, new String[]{
                "2024", "2025", "2026", "2027", "2028",
                "2029", "2030", "2031"
        });

        setDropdown(actEducationStatus, new String[]{
                "Studying", "Graduated"
        });

        setDropdown(actExperienceType, new String[]{
                "Fresher", "Experienced"
        });

        setDropdown(actDesiredRole, new String[]{
                "Android Developer", "Java Developer",
                "Python Developer", "Web Developer",
                "Full Stack Developer", "Software Engineer",
                "Data Analyst", "AI/ML Engineer",
                "UI/UX Designer", "Software Tester"
        });

        setDropdown(actIndustry, new String[]{
                "Information Technology",
                "Software Development",
                "Education",
                "Banking and Finance",
                "Healthcare",
                "Automobile",
                "E-commerce",
                "Telecommunication"
        });

        setDropdown(actWorkMode, new String[]{
                "On-site", "Remote", "Hybrid"
        });

        setDropdown(actEmploymentType, new String[]{
                "Full-time", "Part-time", "Internship"
        });
    }

    private String getValue(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private String getDropdownValue(MaterialAutoCompleteTextView view) {
        return view.getText().toString().trim();
    }

    // Load the existing profile from the database
    private void loadProfileData() {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                BASE_URL + "get_jobseeker_profile.php",

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.optBoolean("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            fillProfileData(data);
                        } else {
                            Toast.makeText(this,
                                    jsonObject.optString("message", "Profile not found"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Invalid server response: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },

                error -> Toast.makeText(this,
                        "Unable to load profile. Check your API connection.",
                        Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                return params;
            }
        };

        requestQueue.add(request);
    }

    private void fillProfileData(JSONObject data) {

        // Personal information
        etFullName.setText(data.optString("full_name", ""));
        etMobile.setText(data.optString("mobile", ""));
        etDob.setText(data.optString("date_of_birth", ""));
        actState.setText(data.optString("state", ""), false);
        etCity.setText(data.optString("city", ""));

        // Education
        actQualification.setText(
                data.optString("highest_qualification", ""), false);
        actCourse.setText(data.optString("course_name", ""));
        etCollege.setText(data.optString("college_name", ""));
        actGraduationYear.setText(
                data.optString("graduation_year", ""), false);
        etPercentage.setText(data.optString("percentage", ""));
        actEducationStatus.setText(
                data.optString("education_status", "Studying"), false);

        // Skills
        actTechnicalSkills.setText(data.optString("technical_skills", ""));
        actSoftSkills.setText(data.optString("soft_skills", ""));

        // Experience
        actExperienceType.setText(
                data.optString("experience_type", "Fresher"), false);
        etExperienceYears.setText(data.optString("experience_years", ""));
        etPreviousCompany.setText(data.optString("previous_company", ""));
        actPreviousJobTitle.setText(data.optString("previous_job_title", ""));

        // Projects and certifications
        etProjects.setText(data.optString("projects", ""));
        etCertifications.setText(data.optString("certifications", ""));

        // Professional links
        etLinkedin.setText(data.optString("linkedin_url", ""));
        etGithub.setText(data.optString("github_url", ""));
        etPortfolio.setText(data.optString("portfolio_url", ""));

        // Job preferences
        actDesiredRole.setText(data.optString("desired_job_role", ""), false);
        actIndustry.setText(data.optString("preferred_industry", ""), false);
        etPreferredLocation.setText(data.optString("preferred_location", ""));
        etSalaryMin.setText(data.optString("expected_salary_min", ""));
        etSalaryMax.setText(data.optString("expected_salary_max", ""));
        actWorkMode.setText(data.optString("work_mode", ""), false);
        actEmploymentType.setText(data.optString("employment_type", ""), false);
    }

    // Collect every editable field and send it to the update method
    private void collectAndUpdateProfile() {

        String fullName = getValue(etFullName);
        String mobile = getValue(etMobile);
        String dob = getValue(etDob);
        String state = getDropdownValue(actState);
        String city = getValue(etCity);

        String qualification = getDropdownValue(actQualification);
        String course = getValue(actCourse);
        String college = getValue(etCollege);
        String graduationYear = getDropdownValue(actGraduationYear);
        String percentage = getValue(etPercentage);
        String educationStatus = getDropdownValue(actEducationStatus);

        String technicalSkills = getValue(actTechnicalSkills);
        String softSkills = getValue(actSoftSkills);

        String experienceType = getDropdownValue(actExperienceType);
        String experienceYears = getValue(etExperienceYears);
        String previousCompany = getValue(etPreviousCompany);
        String previousJobTitle = getValue(actPreviousJobTitle);

        String projects = getValue(etProjects);
        String certifications = getValue(etCertifications);

        String linkedin = getValue(etLinkedin);
        String github = getValue(etGithub);
        String portfolio = getValue(etPortfolio);

        String desiredRole = getDropdownValue(actDesiredRole);
        String industry = getDropdownValue(actIndustry);
        String preferredLocation = getValue(etPreferredLocation);
        String salaryMin = getValue(etSalaryMin);
        String salaryMax = getValue(etSalaryMax);
        String workMode = getDropdownValue(actWorkMode);
        String employmentType = getDropdownValue(actEmploymentType);

        if (fullName.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Name and mobile are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        updateProfile(
                fullName, mobile, dob, state, city,
                qualification, course, college, graduationYear,
                percentage, educationStatus,
                technicalSkills, softSkills,
                experienceType, experienceYears,
                previousCompany, previousJobTitle,
                projects, certifications,
                linkedin, github, portfolio,
                desiredRole, industry, preferredLocation,
                salaryMin, salaryMax, workMode, employmentType
        );
    }

    private void updateProfile(
            String fullName,
            String mobile,
            String dob,
            String state,
            String city,
            String qualification,
            String course,
            String college,
            String graduationYear,
            String percentage,
            String educationStatus,
            String technicalSkills,
            String softSkills,
            String experienceType,
            String experienceYears,
            String previousCompany,
            String previousJobTitle,
            String projects,
            String certifications,
            String linkedin,
            String github,
            String portfolio,
            String desiredRole,
            String industry,
            String preferredLocation,
            String salaryMin,
            String salaryMax,
            String workMode,
            String employmentType
    ) {





        if (userEmail == null || userEmail.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User email not found. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // API URL
        String url = Urls.update__user;

        try {

            // Create JSON Object
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", userEmail);

            jsonObject.put("full_name", fullName);
            jsonObject.put("mobile", mobile);
            jsonObject.put("dob", dob);

            jsonObject.put("state", state);
            jsonObject.put("city", city);

            jsonObject.put("qualification", qualification);
            jsonObject.put("course", course);
            jsonObject.put("college", college);
            jsonObject.put("graduationYear", graduationYear);
            jsonObject.put("percentage", percentage);
            jsonObject.put("educationStatus", educationStatus);

            jsonObject.put("technicalSkills", technicalSkills);
            jsonObject.put("softSkills", softSkills);

            jsonObject.put("experienceType", experienceType);
            jsonObject.put("experienceYears", experienceYears);
            jsonObject.put("previousCompany", previousCompany);
            jsonObject.put("previousJobTitle", previousJobTitle);

            jsonObject.put("projects", projects);
            jsonObject.put("certifications", certifications);

            jsonObject.put("linkedin", linkedin);
            jsonObject.put("github", github);
            jsonObject.put("portfolio", portfolio);

            jsonObject.put("desiredRole", desiredRole);
            jsonObject.put("industry", industry);
            jsonObject.put("preferredLocation", preferredLocation);

            jsonObject.put("salaryMin", salaryMin);
            jsonObject.put("salaryMax", salaryMax);

            jsonObject.put("workMode", workMode);
            jsonObject.put("employmentType", employmentType);

            // Volley StringRequest
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,

                    response -> {

                        try {

                            JSONObject responseObject =
                                    new JSONObject(response);

                            boolean status = responseObject.getBoolean("status");

                            String message =
                                    responseObject.getString("message");

                            if (status) {

                                // Get updated profile completion
                                int completion = responseObject.optInt(
                                        "profile_completion",
                                        0
                                );

                                Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();

                                // Update profile completion locally
                                preferences.edit()
                                        .putInt("profile_completion", completion)
                                        .apply();

                                // Finish activity after successful update
                                setResult(RESULT_OK);
                                finish();

                            } else {

                                Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                            Toast.makeText(
                                    this,
                                    "Invalid server response",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    },

                    error -> {

                        error.printStackTrace();

                        String errorMessage = "Unable to connect to server";

                        if (error.networkResponse != null) {

                            int statusCode = error.networkResponse.statusCode;

                            errorMessage = "Server error: " + statusCode;

                            if (error.networkResponse.data != null) {

                                String errorBody = new String(
                                        error.networkResponse.data,
                                        java.nio.charset.StandardCharsets.UTF_8
                                );

                                android.util.Log.e(
                                        "SMART_HIRE_UPDATE",
                                        "Response: " + errorBody
                                );
                            }
                        }

                        Toast.makeText(
                                this,
                                errorMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    }
            ) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {

                    return jsonObject.toString().getBytes(
                            java.nio.charset.StandardCharsets.UTF_8
                    );
                }
            };

            // Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            requestQueue.add(stringRequest);

        } catch (JSONException e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Error preparing profile data",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Update method is in the next section.
}