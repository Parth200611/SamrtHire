package com.example.smarthire.JobSeeker.Fragment.Profile;

import static com.example.smarthire.Coman.Urls.urls;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.example.smarthire.WelcomeActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    // API URL
    private static final String PROFILE_URL = Urls.profilrdata;
    private final ExecutorService uploadExecutor =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    private CircleImageView ivProfilePhoto;

    private TextView tvName, tvRole, tvEmail;
    private TextView tvLocation, tvMobile;

    private TextView tvCompletionPercentage;
    private TextView tvCompletionMessage;

    private TextView tvQualification, tvCollege;
    private TextView tvEducationDetails;
    private ActivityResultLauncher<String> imagePicker;
    private TextView tvTechnicalSkills, tvSoftSkills;

    private TextView tvExperienceType, tvExperienceDetails;

    private TextView tvProjects, tvCertifications;

    private TextView tvJobPreferences;

    private TextView tvResumeName, tvResumeMissing;

    private LinearProgressIndicator profileProgress;

    private MaterialCardView cardEducation;
    private MaterialCardView cardSkills;
    private MaterialCardView cardExperience;
    private MaterialCardView cardProjects;
    private MaterialCardView cardPreferences;

    private View layoutResumeAvailable;

    private MaterialButton btnUpdateProfile;
    private MaterialButton btnAddResume;
    private MaterialButton btnLogout;

    private String userEmail;
    private String resumePath = "";

    private ActivityResultLauncher<String> resumePicker;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        initializeViews(view);

        // Get logged-in user email


        initializeResumePicker();

        setClickListeners();

        if (userEmail != null && !userEmail.trim().isEmpty()) {
            getProfileData();
        } else {
            Toast.makeText(
                    requireContext(),
                    "User email not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();
        }
        initializeImagePicker();

        return view;
    }

    // Initialize XML views
    private void initializeViews(View view) {

        ivProfilePhoto = view.findViewById(R.id.iv_profile_photo);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvRole = view.findViewById(R.id.tv_profile_role);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvLocation = view.findViewById(R.id.tv_profile_location);
        tvMobile = view.findViewById(R.id.tv_profile_mobile);

        tvCompletionPercentage =
                view.findViewById(R.id.tv_completion_percentage);

        tvCompletionMessage =
                view.findViewById(R.id.tv_completion_message);

        profileProgress =
                view.findViewById(R.id.profile_progress);

        cardEducation = view.findViewById(R.id.card_education);
        cardSkills = view.findViewById(R.id.card_skills);
        cardExperience = view.findViewById(R.id.card_experience);
        cardProjects = view.findViewById(R.id.card_projects);
        cardPreferences = view.findViewById(R.id.card_preferences);

        tvQualification = view.findViewById(R.id.tv_qualification);
        tvCollege = view.findViewById(R.id.tv_college);
        tvEducationDetails = view.findViewById(R.id.tv_education_details);

        tvTechnicalSkills =
                view.findViewById(R.id.tv_technical_skills);

        tvSoftSkills =
                view.findViewById(R.id.tv_soft_skills);

        tvExperienceType =
                view.findViewById(R.id.tv_experience_type);

        tvExperienceDetails =
                view.findViewById(R.id.tv_experience_details);

        tvProjects = view.findViewById(R.id.tv_projects);

        tvCertifications =
                view.findViewById(R.id.tv_certifications);

        tvJobPreferences =
                view.findViewById(R.id.tv_job_preferences);

        layoutResumeAvailable =
                view.findViewById(R.id.layout_resume_available);

        tvResumeName = view.findViewById(R.id.tv_resume_name);

        tvResumeMissing = view.findViewById(R.id.tv_resume_missing);

        btnUpdateProfile =
                view.findViewById(R.id.btn_update_profile);

        btnAddResume =
                view.findViewById(R.id.btn_add_resume);

        btnLogout =
                view.findViewById(R.id.btn_logout);
    }

    private void initializeImagePicker() {

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),

                uri -> {

                    if (uri != null) {

                        // Immediately show selected image
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.baseline_person_24)
                                .into(ivProfilePhoto);

                        // Upload selected image
                        uploadProfileImage(uri);
                    }
                }
        );
    }

    // Fetch profile from PHP API
    private void getProfileData() {

        try {

            JSONObject request = new JSONObject();

            request.put("email", userEmail);

            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(
                            Request.Method.POST,
                            PROFILE_URL,
                            request,

                            response -> {

                                try {

                                    boolean status =
                                            response.optBoolean("status");

                                    if (status) {

                                        JSONObject user =
                                                response.getJSONObject("user");

                                        int completion =
                                                response.optInt(
                                                        "profile_completion", 0
                                                );

                                        displayProfile(user, completion);

                                    } else {

                                        Toast.makeText(
                                                requireContext(),
                                                response.optString(
                                                        "message",
                                                        "Profile not found"
                                                ),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();

                                    Toast.makeText(
                                            requireContext(),
                                            "Error reading profile data",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            },

                            error -> {

                                error.printStackTrace();

                                Toast.makeText(
                                        requireContext(),
                                        "Unable to connect to server",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                    );

            Volley.newRequestQueue(requireContext())
                    .add(jsonObjectRequest);

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    // Display all available information
    private void displayProfile(
            JSONObject user,
            int completion) {

        // Basic information
        setText(tvName, user.optString("full_name"));
        setText(tvEmail, user.optString("email"));

        setTextOrHide(
                tvRole,
                user.optString("desired_job_role")
        );

        String city = user.optString("city");
        String state = user.optString("state");

        String location = combine(city, state);

        setTextOrHide(tvLocation, location);

        setTextOrHide(
                tvMobile,
                user.optString("mobile")
        );

        // Profile image

        String photo = user.optString("profile_photo", "");

        if (isFilled(photo)) {

            String imageUrl = getProfileImageUrl(photo);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_person_24)
                    .error(R.drawable.baseline_person_24)
                    .into(ivProfilePhoto);

        } else {

            Glide.with(this)
                    .load(R.drawable.baseline_person_24)
                    .into(ivProfilePhoto);
        }


        // Profile completion
        completion = Math.max(0, Math.min(100, completion));

        tvCompletionPercentage.setText(completion + "%");

        profileProgress.setProgress(completion);

        if (completion >= 100) {

            tvCompletionMessage.setText(
                    "Excellent! Your profile is complete."
            );

        } else {

            tvCompletionMessage.setText(
                    "Complete your profile to improve your job opportunities."
            );
        }

        // Education
        String qualification =
                user.optString("highest_qualification");

        String course = user.optString("course_name");

        String college = user.optString("college_name");

        String educationYear =
                user.optString("graduation_year");

        String percentage =
                user.optString("percentage");

        String educationStatus =
                user.optString("education_status");

        boolean hasEducation =
                isFilled(qualification) ||
                        isFilled(course) ||
                        isFilled(college) ||
                        isFilled(educationYear) ||
                        isFilled(percentage);

        if (hasEducation) {

            cardEducation.setVisibility(View.VISIBLE);

            setTextOrHide(tvQualification, qualification);
            setTextOrHide(tvCollege, college);

            String educationDetails = combine(
                    course,
                    educationYear,
                    percentage,
                    educationStatus
            );

            setTextOrHide(
                    tvEducationDetails,
                    educationDetails
            );

        } else {

            cardEducation.setVisibility(View.GONE);
        }

        // Technical and soft skills
        String technical =
                user.optString("technical_skills");

        String soft =
                user.optString("soft_skills");

        boolean hasSkills =
                isFilled(technical) || isFilled(soft);

        if (hasSkills) {

            cardSkills.setVisibility(View.VISIBLE);

            setTextOrHide(
                    tvTechnicalSkills,
                    isFilled(technical)
                            ? "Technical Skills\n" + technical : ""
            );

            setTextOrHide(
                    tvSoftSkills,
                    isFilled(soft)
                            ? "Soft Skills\n" + soft : ""
            );

        } else {

            cardSkills.setVisibility(View.GONE);
        }

        // Experience
        String experienceType =
                user.optString("experience_type");

        String experienceYears =
                user.optString("experience_years");

        String company =
                user.optString("previous_company");

        String jobTitle =
                user.optString("previous_job_title");

        boolean hasExperience =
                isFilled(company) || isFilled(jobTitle) ||
                        (isFilled(experienceYears) &&
                                !experienceYears.equals("0.0"));

        if (hasExperience || "Experienced".equalsIgnoreCase(experienceType)) {

            cardExperience.setVisibility(View.VISIBLE);

            setTextOrHide(
                    tvExperienceType,
                    experienceType
            );

            setTextOrHide(
                    tvExperienceDetails,
                    combine(
                            jobTitle,
                            company,
                            isFilled(experienceYears)
                                    ? experienceYears + " years" : ""
                    )
            );

        } else {

            cardExperience.setVisibility(View.GONE);
        }

        // Projects and certifications
        String projects = user.optString("projects");

        String certifications =
                user.optString("certifications");

        if (isFilled(projects) || isFilled(certifications)) {

            cardProjects.setVisibility(View.VISIBLE);

            setTextOrHide(
                    tvProjects,
                    isFilled(projects)
                            ? "Projects\n" + projects : ""
            );

            setTextOrHide(
                    tvCertifications,
                    isFilled(certifications)
                            ? "Certifications\n" + certifications : ""
            );

        } else {

            cardProjects.setVisibility(View.GONE);
        }

        // Career preferences
        String industry =
                user.optString("preferred_industry");

        String preferredLocation =
                user.optString("preferred_location");

        String workMode =
                user.optString("work_mode");

        String employment =
                user.optString("employment_type");

        String minSalary =
                user.optString("expected_salary_min");

        String maxSalary =
                user.optString("expected_salary_max");

        boolean hasPreferences =
                isFilled(industry) ||
                        isFilled(preferredLocation) ||
                        isFilled(workMode) ||
                        isFilled(employment) ||
                        isFilled(minSalary) ||
                        isFilled(maxSalary);

        if (hasPreferences) {

            cardPreferences.setVisibility(View.VISIBLE);

            String preferences = combine(
                    industry,
                    preferredLocation,
                    workMode,
                    employment,
                    salaryRange(minSalary, maxSalary)
            );

            setTextOrHide(
                    tvJobPreferences,
                    preferences
            );

        } else {

            cardPreferences.setVisibility(View.GONE);
        }

        // Resume
        // Resume
        resumePath = user.optString("resume_path");

        boolean hasResume = isFilled(resumePath);

        if (hasResume) {

            layoutResumeAvailable.setVisibility(View.VISIBLE);

            tvResumeMissing.setVisibility(View.GONE);

            tvResumeName.setText(
                    resumePath.substring(
                            resumePath.lastIndexOf("/") + 1
                    )
            );

            btnAddResume.setText("Replace Resume");

        } else {

            layoutResumeAvailable.setVisibility(View.GONE);

            tvResumeMissing.setVisibility(View.VISIBLE);

            btnAddResume.setText("Add / Upload Resume");
        }
    }
    private void updateResumeUI() {

        if (isFilled(resumePath)) {

            // Resume available
            layoutResumeAvailable.setVisibility(View.VISIBLE);

            tvResumeMissing.setVisibility(View.GONE);

            // Extract PDF filename
            String fileName = resumePath.substring(
                    resumePath.lastIndexOf("/") + 1
            );

            tvResumeName.setText(fileName);

            // Change button text
            btnAddResume.setText("Replace Resume");

        } else {

            // Resume not uploaded
            layoutResumeAvailable.setVisibility(View.GONE);

            tvResumeMissing.setVisibility(View.VISIBLE);

            btnAddResume.setText("Add / Upload Resume");
        }
    }
    // Set text
    private void setText(TextView textView, String value) {

        if (isFilled(value)) {
            textView.setText(value.trim());
            textView.setVisibility(View.VISIBLE);
        }
    }

    // Hide empty information
    private void setTextOrHide(
            TextView textView,
            String value) {

        if (isFilled(value)) {

            textView.setText(value.trim());
            textView.setVisibility(View.VISIBLE);

        } else {

            textView.setVisibility(View.GONE);
        }
    }

    private boolean isFilled(String value) {

        return value != null &&
                !value.trim().isEmpty() &&
                !value.trim().equalsIgnoreCase("null");
    }

    private String combine(String... values) {

        StringBuilder builder = new StringBuilder();

        for (String value : values) {

            if (isFilled(value)) {

                if (builder.length() > 0) {
                    builder.append(" • ");
                }

                builder.append(value.trim());
            }
        }

        return builder.toString();
    }

    private String salaryRange(String min, String max) {

        if (isFilled(min) && isFilled(max)) {
            return "Expected Salary: ₹" + min + " - ₹" + max;
        }

        if (isFilled(min)) {
            return "Minimum Salary: ₹" + min;
        }

        if (isFilled(max)) {
            return "Maximum Salary: ₹" + max;
        }

        return "";
    }

    // Resume picker
    private void initializeResumePicker() {

        resumePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),

                uri -> {

                    if (uri != null) {

                        uploadResume(uri);

                    }

                }
        );
    }

    private void uploadResume(Uri uri) {

        if (userEmail == null || userEmail.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ContentResolver resolver =
                requireContext().getContentResolver();

        String mimeType = resolver.getType(uri);

        if (mimeType != null &&
                !mimeType.equalsIgnoreCase("application/pdf")) {

            Toast.makeText(
                    requireContext(),
                    "Please select a PDF file",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnAddResume.setEnabled(false);
        btnAddResume.setText("Uploading...");

        uploadExecutor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String boundary = "SmartHireBoundary"
                        + System.currentTimeMillis();

                URL url = new URL(Urls.add_resume);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);

                connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                DataOutputStream output =
                        new DataOutputStream(
                                connection.getOutputStream()
                        );

                // Email field
                writeFormField(
                        output,
                        boundary,
                        "email",
                        userEmail
                );

                // PDF file field
                String fileName = getFileName(uri);

                if (fileName == null ||
                        !fileName.toLowerCase().endsWith(".pdf")) {

                    throw new Exception(
                            "Please select a valid PDF file"
                    );
                }

                output.writeBytes("--" + boundary + "\r\n");

                output.writeBytes(
                        "Content-Disposition: form-data; " +
                                "name=\"resume\"; filename=\"" +
                                fileName.replace("\"", "") + "\"\r\n"
                );

                output.writeBytes(
                        "Content-Type: application/pdf\r\n\r\n"
                );

                InputStream input =
                        resolver.openInputStream(uri);

                if (input == null) {
                    throw new Exception("Unable to read selected file");
                }

                byte[] buffer = new byte[8192];

                int bytesRead;
                long totalBytes = 0;
                long maxSize = 10L * 1024 * 1024;

                while ((bytesRead = input.read(buffer)) != -1) {

                    totalBytes += bytesRead;

                    if (totalBytes > maxSize) {
                        throw new Exception(
                                "PDF size must be below 10 MB"
                        );
                    }

                    output.write(buffer, 0, bytesRead);
                }

                input.close();

                output.writeBytes("\r\n");

                output.writeBytes("--" + boundary + "--\r\n");

                output.flush();
                output.close();

                int responseCode =
                        connection.getResponseCode();

                InputStream responseStream =
                        (responseCode >= 200 && responseCode < 300)
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                ByteArrayOutputStream responseBuffer =
                        new ByteArrayOutputStream();

                byte[] responseBytes = new byte[4096];

                int count;

                while ((count = responseStream.read(responseBytes)) != -1) {
                    responseBuffer.write(responseBytes, 0, count);
                }

                responseStream.close();

                String responseString =
                        responseBuffer.toString("UTF-8");

                JSONObject response =
                        new JSONObject(responseString);

                boolean status =
                        response.optBoolean("status", false);

                mainHandler.post(() -> {

                    if (!isAdded()) return;

                    btnAddResume.setEnabled(true);

                    if (status) {

                        resumePath =
                                response.optString("resume_path");

                        updateResumeUId();

                        Toast.makeText(
                                requireContext(),
                                "Resume uploaded successfully!",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {

                        btnAddResume.setText(
                                isFilled(resumePath)
                                        ? "Replace Resume"
                                        : "Add / Upload Resume"
                        );

                        Toast.makeText(
                                requireContext(),
                                response.optString(
                                        "message",
                                        "Resume upload failed"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();

                mainHandler.post(() -> {

                    if (!isAdded()) return;

                    btnAddResume.setEnabled(true);

                    btnAddResume.setText(
                            isFilled(resumePath)
                                    ? "Replace Resume"
                                    : "Add / Upload Resume"
                    );

                    Toast.makeText(
                            requireContext(),
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Upload failed",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
    private String getFileName(Uri uri) {

        String fileName = null;

        Cursor cursor = requireContext()
                .getContentResolver()
                .query(uri, null, null, null, null);

        if (cursor != null) {

            try {

                if (cursor.moveToFirst()) {

                    int index = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                    );

                    if (index != -1) {
                        fileName = cursor.getString(index);
                    }
                }

            } finally {

                cursor.close();
            }
        }

        return fileName;
    }
    private void updateResumeUId() {

        if (isFilled(resumePath)) {

            layoutResumeAvailable.setVisibility(View.VISIBLE);

            tvResumeMissing.setVisibility(View.GONE);

            String fileName = resumePath.substring(
                    resumePath.lastIndexOf("/") + 1
            );

            tvResumeName.setText(fileName);

            btnAddResume.setText("Replace Resume");

        } else {

            layoutResumeAvailable.setVisibility(View.GONE);

            tvResumeMissing.setVisibility(View.VISIBLE);

            btnAddResume.setText("Add / Upload Resume");
        }
    }
    private void writeFormField(
            DataOutputStream output,
            String boundary,
            String fieldName,
            String value) throws Exception {

        output.writeBytes("--" + boundary + "\r\n");

        output.writeBytes(
                "Content-Disposition: form-data; name=\"" +
                        fieldName + "\"\r\n\r\n"
        );

        output.write(value.getBytes("UTF-8"));

        output.writeBytes("\r\n");
    }
    // Button actions
    private void setClickListeners() {

        btnUpdateProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireActivity(),
                    UpdateProfileActivity.class
            );

            intent.putExtra("email", userEmail);

            startActivity(intent);
        });

        btnAddResume.setOnClickListener(v -> {

            resumePicker.launch("application/pdf");

        });
        ivProfilePhoto.setOnClickListener(v -> {

            if (imagePicker != null) {
                imagePicker.launch("image/*");
            }

        });

// Click resume filename to view PDF
        tvResumeName.setOnClickListener(v -> {

            viewResume();

        });

// Entire resume card can also be clickable
        layoutResumeAvailable.setOnClickListener(v -> {

            viewResume();

        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }
    private void viewResume() {

        if (resumePath == null || resumePath.trim().isEmpty()) {
            Toast.makeText(requireContext(),
                    "Resume not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String cleanPath = resumePath.trim();

        // Remove leading slash if present
        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        String resumeUrl = urls + cleanPath;

        Log.d("RESUME_URL", resumeUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse(resumeUrl),
                "application/pdf"
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(Intent.createChooser(intent, "Open Resume"));
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    "Unable to open resume",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void uploadProfileImage(Uri uri) {

        if (userEmail == null || userEmail.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please login again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Capture values before starting background thread
        ContentResolver resolver =
                requireContext().getContentResolver();

        String fileName = getFileName(uri);

        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "profile_image.jpg";
        }

        String finalFileName = fileName;

        btnUpdateProfile.setEnabled(false);

        uploadExecutor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String boundary = "SmartHireImage"
                        + System.currentTimeMillis();

                URL url = new URL(Urls.add_image);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);

                connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary=" + boundary
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                DataOutputStream output =
                        new DataOutputStream(connection.getOutputStream());

                // Send email
                writeFormField(
                        output,
                        boundary,
                        "email",
                        userEmail
                );

                // Send image
                output.writeBytes("--" + boundary + "\r\n");

                output.writeBytes(
                        "Content-Disposition: form-data; " +
                                "name=\"image\"; filename=\"" +
                                finalFileName.replace("\"", "") +
                                "\"\r\n"
                );

                String mimeType = resolver.getType(uri);

                if (mimeType == null ||
                        !mimeType.startsWith("image/")) {
                    mimeType = "application/octet-stream";
                }

                output.writeBytes(
                        "Content-Type: " + mimeType + "\r\n\r\n"
                );

                InputStream input = resolver.openInputStream(uri);

                if (input == null) {
                    throw new Exception("Unable to read selected image");
                }

                byte[] buffer = new byte[8192];

                int bytesRead;

                long totalBytes = 0;

                long maxSize = 5L * 1024 * 1024;

                while ((bytesRead = input.read(buffer)) != -1) {

                    totalBytes += bytesRead;

                    if (totalBytes > maxSize) {
                        throw new Exception(
                                "Image size must be below 5 MB"
                        );
                    }

                    output.write(buffer, 0, bytesRead);
                }

                input.close();

                output.writeBytes("\r\n");

                output.writeBytes("--" + boundary + "--\r\n");

                output.flush();
                output.close();

                // Get API response
                int responseCode = connection.getResponseCode();

                InputStream responseStream =
                        (responseCode >= 200 && responseCode < 300)
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                if (responseStream == null) {
                    throw new Exception("Empty server response");
                }

                ByteArrayOutputStream responseBuffer =
                        new ByteArrayOutputStream();

                byte[] responseBytes = new byte[4096];

                while ((bytesRead = responseStream.read(responseBytes)) != -1) {

                    responseBuffer.write(responseBytes, 0, bytesRead);
                }

                responseStream.close();

                String responseString =
                        responseBuffer.toString("UTF-8");

                Log.d("PROFILE_IMAGE", responseString);

                JSONObject response = new JSONObject(responseString);

                boolean status = response.optBoolean("status", false);

                String imagePath = response.optString("image_path", "");

                mainHandler.post(() -> {

                    if (!isAdded()) return;

                    btnUpdateProfile.setEnabled(true);

                    if (status && !imagePath.isEmpty()) {

                        // Save latest image path
                        // Display image from server
                        String imageUrl = getProfileImageUrl(imagePath);

                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.baseline_person_24)
                                .error(R.drawable.baseline_person_24)
                                .into(ivProfilePhoto);

                        Toast.makeText(
                                requireContext(),
                                "Profile image updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                requireContext(),
                                response.optString(
                                        "message",
                                        "Image upload failed"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();

                mainHandler.post(() -> {

                    if (!isAdded()) return;

                    btnUpdateProfile.setEnabled(true);

                    Toast.makeText(
                            requireContext(),
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Image upload failed",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String getProfileImageUrl(String imagePath) {

        if (imagePath == null || imagePath.trim().isEmpty()
                || imagePath.equalsIgnoreCase("null")) {
            return "";
        }

        imagePath = imagePath.trim();

        // Already a complete URL
        if (imagePath.startsWith("http://") ||
                imagePath.startsWith("https://")) {
            return imagePath;
        }

        // Remove starting slash
        while (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        return Urls.urls + imagePath;
    }

    // Logout confirmation
    private void showLogoutDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {

                    SharedPreferences preferences =
                            requireActivity().getSharedPreferences(
                                    "SmartHirePrefs",
                                    requireActivity().MODE_PRIVATE
                            );

                    preferences.edit().clear().apply();

                    Intent intent = new Intent(
                            requireActivity(),
                            WelcomeActivity.class
                    );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isAdded() && getView() != null &&
                userEmail != null && !userEmail.isEmpty()) {

            getProfileData();
        }
    }
}