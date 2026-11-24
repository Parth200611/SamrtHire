
package com.example.smarthire.Recruiter.Application;

import static com.example.smarthire.Coman.Urls.urls;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class ViewCandidate extends AppCompatActivity {

    private static final String TAG = "ViewCandidate";

    private String candidateEmail;
    private String resumePath = "";

    private ImageView ivCandidatePhoto;

    private TextView tvCandidateName, tvCandidateRole, tvCandidateEmail;
    private TextView tvCandidateLocation, tvCandidateMobile;

    private TextView tvCompletionPercentage, tvCompletionMessage;
    private ProgressBar profileProgress;

    private TextView tvPersonalDob;

    private TextView tvQualification, tvCourse, tvCollege;
    private TextView tvEducationDetails;

    private TextView tvTechnicalSkills, tvSoftSkills;

    private TextView tvExperienceType, tvExperienceDetails;

    private TextView tvProjects, tvCertifications;

    private TextView tvJobPreferences;

    private TextView tvLinkedin, tvGithub, tvPortfolio;

    private View cardPersonal, cardEducation, cardSkills, cardExperience;
    private View cardProjects, cardPreferences, cardLinks, cardResume;

    private View layoutResumeAvailable;

    private TextView tvResumeName, tvResumeMissing;

    private View btnViewResume, btnBack;

    private Candidate candidate;

    MaterialButton btn_shortlist_candidate,btn_reject_candidate;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_candidate);

        initializeViews();

        candidateEmail = getIntent().getStringExtra("Can_email");

        if (candidateEmail == null || candidateEmail.trim().isEmpty()) {
            Toast.makeText(this, "Candidate email not received",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        // Shortlist candidate
        btn_shortlist_candidate.setOnClickListener(v -> {
            updateApplicationStatus("Shortlisted");
        });

// Reject candidate
        btn_reject_candidate.setOnClickListener(v -> {
            updateApplicationStatus("Rejected");
        });

        loadCandidateDetails();
    }


    private void updateApplicationStatus(String applicationStatus) {

        try {

            JSONObject jsonObject = new JSONObject();

            // Candidate email
            jsonObject.put("email", candidateEmail);

            // New application status
            jsonObject.put("application_status", applicationStatus);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Urls.update_status,
                    jsonObject,

                    response -> {

                        boolean status = response.optBoolean("status", false);

                        String message = response.optString(
                                "message",
                                "Something went wrong"
                        );

                        Toast.makeText(
                                ViewCandidate.this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

                        if (status) {

                            Log.d(TAG,
                                    "Application status updated: "
                                            + applicationStatus);

                            if (applicationStatus.equals("Shortlisted")) {

                                Toast.makeText(
                                        ViewCandidate.this,
                                        "Candidate shortlisted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else if (applicationStatus.equals("Rejected")) {

                                Toast.makeText(
                                        ViewCandidate.this,
                                        "Candidate rejected successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                    },

                    error -> {

                        Log.e(TAG, "Status update error", error);

                        Toast.makeText(
                                ViewCandidate.this,
                                "Failed to update application status",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {

            Log.e(TAG, "Update status exception", e);

            Toast.makeText(
                    this,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void initializeViews() {

        ivCandidatePhoto = findViewById(R.id.iv_candidate_photo);
//        btn_shortlist_candidate,btn_reject_candidate;
        btn_reject_candidate = findViewById(R.id.btn_reject_candidate);
        btn_shortlist_candidate = findViewById(R.id.btn_shortlist_candidate);
        tvCandidateName = findViewById(R.id.tv_candidate_name);
        tvCandidateRole = findViewById(R.id.tv_candidate_role);
        tvCandidateEmail = findViewById(R.id.tv_candidate_email);
        tvCandidateLocation = findViewById(R.id.tv_candidate_location);
        tvCandidateMobile = findViewById(R.id.tv_candidate_mobile);

        tvCompletionPercentage = findViewById(R.id.tv_completion_percentage);
        tvCompletionMessage = findViewById(R.id.tv_completion_message);
        profileProgress = findViewById(R.id.profile_progress);

        tvPersonalDob = findViewById(R.id.tv_personal_dob);

        tvQualification = findViewById(R.id.tv_qualification);
        tvCourse = findViewById(R.id.tv_course);
        tvCollege = findViewById(R.id.tv_college);
        tvEducationDetails = findViewById(R.id.tv_education_details);

        tvTechnicalSkills = findViewById(R.id.tv_technical_skills);
        tvSoftSkills = findViewById(R.id.tv_soft_skills);

        tvExperienceType = findViewById(R.id.tv_experience_type);
        tvExperienceDetails = findViewById(R.id.tv_experience_details);

        tvProjects = findViewById(R.id.tv_projects);
        tvCertifications = findViewById(R.id.tv_certifications);

        tvJobPreferences = findViewById(R.id.tv_job_preferences);

        tvLinkedin = findViewById(R.id.tv_linkedin);
        tvGithub = findViewById(R.id.tv_github);
        tvPortfolio = findViewById(R.id.tv_portfolio);

        cardPersonal = findViewById(R.id.card_personal);
        cardEducation = findViewById(R.id.card_education);
        cardSkills = findViewById(R.id.card_skills);
        cardExperience = findViewById(R.id.card_experience);
        cardProjects = findViewById(R.id.card_projects);
        cardPreferences = findViewById(R.id.card_preferences);
        cardLinks = findViewById(R.id.card_links);
        cardResume = findViewById(R.id.card_resume);

        layoutResumeAvailable =
                findViewById(R.id.layout_resume_available);

        tvResumeName = findViewById(R.id.tv_resume_name);
        tvResumeMissing = findViewById(R.id.tv_resume_missing);

        btnViewResume = findViewById(R.id.btn_view_resume);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadCandidateDetails() {

        try {

            JSONObject requestObject = new JSONObject();
            requestObject.put("email", candidateEmail);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Urls.get_can_view,
                    requestObject,

                    response -> {

                        try {

                            boolean status =
                                    response.optBoolean("status", false);

                            if (!status) {

                                Toast.makeText(this,
                                        response.optString("message",
                                                "Unable to fetch candidate"),
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            JSONObject data =
                                    response.getJSONObject("candidate");

                            candidate = parseCandidate(data);

                            displayCandidate(candidate);

                        } catch (Exception e) {

                            Log.e(TAG, "Response parsing error", e);

                            Toast.makeText(this,
                                    "Unable to read candidate details",
                                    Toast.LENGTH_SHORT).show();
                        }
                    },

                    error -> {

                        Log.e(TAG, "API error", error);

                        String message = "Failed to connect to server";

                        if (error.networkResponse != null) {
                            message = "Server error: "
                                    + error.networkResponse.statusCode;
                        }

                        Toast.makeText(this, message,
                                Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {

            Log.e(TAG, "Request creation error", e);

            Toast.makeText(this,
                    "Unable to send request",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Candidate parseCandidate(JSONObject data) {

        Candidate c = new Candidate();

        c.setFullName(value(data, "full_name"));
        c.setEmail(value(data, "email"));
        c.setMobile(value(data, "mobile"));
        c.setProfilePhoto(value(data, "profile_photo"));
        c.setCity(value(data, "city"));
        c.setState(value(data, "state"));

        c.setDateOfBirth(value(data, "date_of_birth"));

        c.setHighestQualification(value(data, "highest_qualification"));
        c.setCourseName(value(data, "course_name"));
        c.setCollegeName(value(data, "college_name"));
        c.setGraduationYear(value(data, "graduation_year"));
        c.setPercentage(value(data, "percentage"));
        c.setEducationStatus(value(data, "education_status"));

        c.setTechnicalSkills(value(data, "technical_skills"));
        c.setSoftSkills(value(data, "soft_skills"));

        c.setExperienceType(value(data, "experience_type"));
        c.setExperienceYears(value(data, "experience_years"));
        c.setPreviousCompany(value(data, "previous_company"));
        c.setPreviousJobTitle(value(data, "previous_job_title"));

        c.setProjects(value(data, "projects"));
        c.setCertifications(value(data, "certifications"));
        c.setResumePath(value(data, "resume_path"));

        c.setLinkedinUrl(value(data, "linkedin_url"));
        c.setGithubUrl(value(data, "github_url"));
        c.setPortfolioUrl(value(data, "portfolio_url"));

        c.setDesiredJobRole(value(data, "desired_job_role"));
        c.setPreferredIndustry(value(data, "preferred_industry"));
        c.setPreferredLocation(value(data, "preferred_location"));

        c.setExpectedSalaryMin(value(data, "expected_salary_min"));
        c.setExpectedSalaryMax(value(data, "expected_salary_max"));

        c.setWorkMode(value(data, "work_mode"));
        c.setEmploymentType(value(data, "employment_type"));
        c.setProfileCompletion(value(data, "profile_completion"));

        return c;
    }

    private String value(JSONObject object, String key) {

        String result = object.optString(key, "");

        if (result.equalsIgnoreCase("null")) {
            return "";
        }

        return result.trim();
    }

    private void displayCandidate(Candidate c) {

        // Header
        tvCandidateName.setText(show(c.getFullName()));
        tvCandidateRole.setText(show(c.getDesiredJobRole()));
        tvCandidateEmail.setText(show(c.getEmail()));
        tvCandidateMobile.setText(show(c.getMobile()));

        String location = join(c.getCity(), c.getState());

        tvCandidateLocation.setText(location.isEmpty()
                ? "Location not added" : location);

        // Profile image
        String photo = c.getProfilePhoto();

        if (photo != null && !photo.trim().isEmpty()
                && !photo.equalsIgnoreCase("null")) {

            String imageUrl = makeFileUrl(photo);

            Log.d("CANDIDATE_IMAGE_URL", imageUrl);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_smarthire_logo)
                    .error(R.drawable.ic_smarthire_logo)
                    .into(ivCandidatePhoto);

        } else {

            ivCandidatePhoto.setImageResource(
                    R.drawable.ic_smarthire_logo
            );
        }

        // Profile completion
        int completion = 0;

        try {
            completion = Integer.parseInt(c.getProfileCompletion());
        } catch (Exception ignored) {
        }

        completion = Math.max(0, Math.min(completion, 100));

        tvCompletionPercentage.setText(completion + "%");
        profileProgress.setProgress(completion);

        if (completion >= 100) {
            tvCompletionMessage.setText("Profile completed");
        } else {
            tvCompletionMessage.setText(
                    "Candidate profile is " + completion + "% complete"
            );
        }

        // Personal information
        tvPersonalDob.setText(
                "Date of Birth: " + show(c.getDateOfBirth())
        );

        cardPersonal.setVisibility(
                c.getDateOfBirth().isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        // Education
        tvQualification.setText(
                "Qualification: " + show(c.getHighestQualification())
        );

        tvCourse.setText("Course: " + show(c.getCourseName()));
        tvCollege.setText("College: " + show(c.getCollegeName()));

        String educationDetails = "";

        if (!c.getGraduationYear().isEmpty()) {
            educationDetails +=
                    "Graduation Year: " + c.getGraduationYear();
        }

        if (!c.getPercentage().isEmpty()) {
            if (!educationDetails.isEmpty()) educationDetails += "\n";
            educationDetails += "Percentage: " + c.getPercentage() + "%";
        }

        if (!c.getEducationStatus().isEmpty()) {
            if (!educationDetails.isEmpty()) educationDetails += "\n";
            educationDetails += "Status: " + c.getEducationStatus();
        }

        tvEducationDetails.setText(educationDetails);

        boolean hasEducation = hasData(
                c.getHighestQualification(),
                c.getCourseName(),
                c.getCollegeName(),
                educationDetails
        );

        cardEducation.setVisibility(
                hasEducation ? View.VISIBLE : View.GONE
        );

        // Skills
        tvTechnicalSkills.setText(show(c.getTechnicalSkills()));
        tvSoftSkills.setText(show(c.getSoftSkills()));

        boolean hasSkills = hasData(
                c.getTechnicalSkills(),
                c.getSoftSkills()
        );

        cardSkills.setVisibility(
                hasSkills ? View.VISIBLE : View.GONE
        );

        // Experience
        tvExperienceType.setText(show(c.getExperienceType()));

        String experience = "";

        if (!c.getExperienceYears().isEmpty()) {
            experience +=
                    "Experience: " + c.getExperienceYears() + " years";
        }

        if (!c.getPreviousCompany().isEmpty()) {
            if (!experience.isEmpty()) experience += "\n";
            experience += "Previous Company: " + c.getPreviousCompany();
        }

        if (!c.getPreviousJobTitle().isEmpty()) {
            if (!experience.isEmpty()) experience += "\n";
            experience += "Previous Job Title: " + c.getPreviousJobTitle();
        }

        tvExperienceDetails.setText(experience);

        boolean hasExperience = hasData(
                c.getExperienceType(),
                c.getExperienceYears(),
                c.getPreviousCompany(),
                c.getPreviousJobTitle()
        );

        cardExperience.setVisibility(
                hasExperience ? View.VISIBLE : View.GONE
        );

        // Projects and certifications
        tvProjects.setText(show(c.getProjects()));
        tvCertifications.setText(show(c.getCertifications()));

        boolean hasProjects = hasData(
                c.getProjects(),
                c.getCertifications()
        );

        cardProjects.setVisibility(
                hasProjects ? View.VISIBLE : View.GONE
        );

        // Career preferences
        String preferences = "";

        preferences = addLine(
                preferences,
                "Desired Role: " + show(c.getDesiredJobRole())
        );

        preferences = addLine(
                preferences,
                "Industry: " + show(c.getPreferredIndustry())
        );

        preferences = addLine(
                preferences,
                "Preferred Location: " + show(c.getPreferredLocation())
        );

        String salary = getSalaryRange(
                c.getExpectedSalaryMin(),
                c.getExpectedSalaryMax()
        );

        if (!salary.isEmpty()) {
            preferences = addLine(
                    preferences,
                    "Expected Salary: " + salary
            );
        }

        if (!c.getWorkMode().isEmpty()) {
            preferences = addLine(
                    preferences,
                    "Work Mode: " + c.getWorkMode()
            );
        }

        if (!c.getEmploymentType().isEmpty()) {
            preferences = addLine(
                    preferences,
                    "Employment Type: " + c.getEmploymentType()
            );
        }

        tvJobPreferences.setText(preferences);

        boolean hasPreferences = hasData(
                c.getDesiredJobRole(),
                c.getPreferredIndustry(),
                c.getPreferredLocation(),
                salary,
                c.getWorkMode(),
                c.getEmploymentType()
        );

        cardPreferences.setVisibility(
                hasPreferences ? View.VISIBLE : View.GONE
        );

        // Professional links
        setLink(tvLinkedin, "LinkedIn", c.getLinkedinUrl());
        setLink(tvGithub, "GitHub", c.getGithubUrl());
        setLink(tvPortfolio, "Portfolio", c.getPortfolioUrl());

        boolean hasLinks = hasData(
                c.getLinkedinUrl(),
                c.getGithubUrl(),
                c.getPortfolioUrl()
        );

        cardLinks.setVisibility(
                hasLinks ? View.VISIBLE : View.GONE
        );

        // Resume
        resumePath = c.getResumePath();

        if (isFilled(resumePath)) {

            cardResume.setVisibility(View.VISIBLE);

            layoutResumeAvailable.setVisibility(View.VISIBLE);
            tvResumeMissing.setVisibility(View.GONE);

            btnViewResume.setVisibility(View.VISIBLE);

            String cleanPath = resumePath.replace("\\", "/");

            String[] parts = cleanPath.split("/");
            String fileName = parts[parts.length - 1];

            tvResumeName.setText(fileName);

            // Button click
            btnViewResume.setOnClickListener(v -> openCandidateResume());

            // Filename click
            tvResumeName.setOnClickListener(v -> openCandidateResume());

            // Entire resume layout click
            layoutResumeAvailable.setOnClickListener(v -> openCandidateResume());

        } else {

            cardResume.setVisibility(View.VISIBLE);

            layoutResumeAvailable.setVisibility(View.GONE);

            tvResumeMissing.setVisibility(View.VISIBLE);

            tvResumeMissing.setText(
                    "Candidate has not uploaded a resume."
            );

            btnViewResume.setVisibility(View.GONE);
        }
    }

    // Open candidate resume using the same direct URL method
    // used for opening the JobSeeker profile resume.
    private void openCandidateResume() {

        if (!isFilled(resumePath)) {

            Toast.makeText(this,
                    "Resume not available",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        String cleanPath = resumePath.trim();

        // Normalize path
        cleanPath = cleanPath.replace("\\", "/");

        // Remove leading slash
        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // Create complete resume URL
        String resumeUrl;

        if (cleanPath.startsWith("http://") ||
                cleanPath.startsWith("https://")) {

            resumeUrl = cleanPath;

        } else {

            resumeUrl = urls + cleanPath;
        }

        Log.d("CANDIDATE_RESUME_URL", resumeUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(
                Uri.parse(resumeUrl),
                "application/pdf"
        );

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {

            startActivity(
                    Intent.createChooser(
                            intent,
                            "Open Candidate Resume"
                    )
            );

        } catch (Exception e) {

            Log.e(TAG, "Resume opening error", e);

            Toast.makeText(this,
                    "Unable to open resume. Please install a PDF viewer.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private boolean isFilled(String value) {
        return value != null
                && !value.trim().isEmpty()
                && !value.trim().equalsIgnoreCase("null");
    }

    private String show(String value) {
        return isFilled(value) ? value.trim() : "Not provided";
    }

    private String join(String first, String second) {

        String result = "";

        if (isFilled(first)) {
            result = first.trim();
        }

        if (isFilled(second)) {
            if (!result.isEmpty()) result += ", ";
            result += second.trim();
        }

        return result;
    }

    private boolean hasData(String... values) {

        for (String value : values) {
            if (isFilled(value)) {
                return true;
            }
        }

        return false;
    }

    private String addLine(String current, String line) {

        if (current == null || current.isEmpty()) {
            return line;
        }

        return current + "\n" + line;
    }

    private String getSalaryRange(String min, String max) {

        if (!isFilled(min) && !isFilled(max)) {
            return "";
        }

        if (isFilled(min) && isFilled(max)) {
            return "₹" + min + " - ₹" + max;
        }

        if (isFilled(min)) {
            return "From ₹" + min;
        }

        return "Up to ₹" + max;
    }

    private void setLink(TextView textView, String label, String url) {

        if (!isFilled(url)) {
            textView.setVisibility(View.GONE);
            return;
        }

        textView.setVisibility(View.VISIBLE);
        textView.setText(label + ": " + url);

        textView.setOnClickListener(v -> openUrl(url));
    }

    // Same URL construction approach as profile image.
    private String makeFileUrl(String path) {

        if (!isFilled(path)) {
            return "";
        }

        path = path.trim().replace("\\", "/");

        if (path.startsWith("http://")
                || path.startsWith("https://")) {
            return path;
        }

        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        return urls + path;
    }

    private void openUrl(String url) {

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
            );

            startActivity(intent);

        } catch (Exception e) {

            Log.e(TAG, "Link opening error", e);

            Toast.makeText(this,
                    "Unable to open this link",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
