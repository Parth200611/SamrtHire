
package com.example.smarthire.Recruiter.Post;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PostJobFragment extends Fragment {

    // Basic information
    private TextInputEditText etJobTitle, etJobDepartment, etOpenings;

    // Description
    private TextInputEditText etJobDescription, etResponsibilities;

    // Skills and requirements
    private TextInputEditText etRequiredSkills, etSoftSkills;
    private TextInputEditText etMaxExperience, etPreferredCourse;

    // Employment and location
    private TextInputEditText etJobLocation, etJobState, etJobCountry;

    // Salary
    private TextInputEditText etMinSalary, etMaxSalary, etBenefits;

    // Application settings
    private TextInputEditText etApplicationDeadline;
    private TextInputEditText etHrEmail, etCompanyEmail;

    // Dropdowns
    private AutoCompleteTextView actvJobCategory;
    private AutoCompleteTextView actvMinExperience;
    private AutoCompleteTextView actvQualification;
    private AutoCompleteTextView actvEmploymentType;
    private AutoCompleteTextView actvWorkMode;
    private AutoCompleteTextView actvSalaryPeriod;

    private MaterialButton btnPostJob;

    private RequestQueue requestQueue;

    private String recruiterId = "";
    private String recruiterEmail = "";

    private boolean isPosting = false;

    public PostJobFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_post_job,
                container,
                false
        );

        requestQueue = Volley.newRequestQueue(requireContext());

        initializeViews(view);
        loadRecruiterDetails();
        setupDropdowns();
        setupDatePicker();

        btnPostJob.setOnClickListener(v -> publishJob());

        return view;
    }

    // ---------------------------------------------------------
    // INITIALIZE ALL XML VIEWS
    // ---------------------------------------------------------

    private void initializeViews(View view) {

        etJobTitle = view.findViewById(R.id.et_job_title);
        etJobDepartment = view.findViewById(R.id.et_job_department);
        etOpenings = view.findViewById(R.id.et_openings);

        etJobDescription = view.findViewById(R.id.et_job_description);
        etResponsibilities = view.findViewById(R.id.et_responsibilities);

        etRequiredSkills = view.findViewById(R.id.et_required_skills);
        etSoftSkills = view.findViewById(R.id.et_soft_skills);
        etMaxExperience = view.findViewById(R.id.et_max_experience);
        etPreferredCourse = view.findViewById(R.id.et_preferred_course);

        etJobLocation = view.findViewById(R.id.et_job_location);
        etJobState = view.findViewById(R.id.et_job_state);
        etJobCountry = view.findViewById(R.id.et_job_country);

        etMinSalary = view.findViewById(R.id.et_min_salary);
        etMaxSalary = view.findViewById(R.id.et_max_salary);
        etBenefits = view.findViewById(R.id.et_benefits);

        etApplicationDeadline = view.findViewById(
                R.id.et_application_deadline
        );

        etHrEmail = view.findViewById(R.id.et_hr_email);
        etCompanyEmail = view.findViewById(R.id.et_company_email);

        actvJobCategory = view.findViewById(R.id.actv_job_category);
        actvMinExperience = view.findViewById(R.id.actv_min_experience);
        actvQualification = view.findViewById(R.id.actv_qualification);
        actvEmploymentType = view.findViewById(R.id.actv_employment_type);
        actvWorkMode = view.findViewById(R.id.actv_work_mode);
        actvSalaryPeriod = view.findViewById(R.id.actv_salary_period);

        btnPostJob = view.findViewById(R.id.btn_post_job);
    }

    // ---------------------------------------------------------
    // LOAD RECRUITER EMAIL AND ID FROM SHAREDPREFERENCES
    // ---------------------------------------------------------

    private void loadRecruiterDetails() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        "SmartHirePrefs",
                        Context.MODE_PRIVATE
                );

        recruiterEmail = preferences.getString("email", "");

        // Read ID safely whether it was saved as String or Integer.
        Object savedId = preferences.getAll().get("userId");

        recruiterId = savedId == null ? "" : String.valueOf(savedId);

        // Automatically fill HR email from logged-in recruiter email.
        etHrEmail.setText(recruiterEmail);

        // HR email cannot be edited.
        etHrEmail.setFocusable(false);
        etHrEmail.setFocusableInTouchMode(false);
        etHrEmail.setClickable(false);
        etHrEmail.setLongClickable(false);
        etHrEmail.setCursorVisible(false);

        if (TextUtils.isEmpty(recruiterEmail) ||
                TextUtils.isEmpty(recruiterId)) {

            btnPostJob.setEnabled(false);

            Toast.makeText(
                    requireContext(),
                    "Recruiter login details not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // ---------------------------------------------------------
    // DROPDOWN DATA / SUGGESTIONS
    // ---------------------------------------------------------

    private void setupDropdowns() {

        setDropdown(
                actvJobCategory,
                new String[]{
                        "IT",
                        "Engineering",
                        "Finance",
                        "Marketing",
                        "Human Resources",
                        "Sales",
                        "Design",
                        "Healthcare",
                        "Education",
                        "Customer Support",
                        "Operations",
                        "Other"
                }
        );

        setDropdown(
                actvMinExperience,
                new String[]{
                        "Fresher",
                        "0-1 Years",
                        "1-2 Years",
                        "2-3 Years",
                        "3-5 Years",
                        "5-8 Years",
                        "8+ Years"
                }
        );

        setDropdown(
                actvQualification,
                new String[]{
                        "10th",
                        "12th",
                        "Diploma",
                        "Graduate",
                        "Postgraduate",
                        "PhD",
                        "Any Qualification"
                }
        );

        setDropdown(
                actvEmploymentType,
                new String[]{
                        "Full Time",
                        "Part Time",
                        "Internship",
                        "Contract",
                        "Freelance"
                }
        );

        setDropdown(
                actvWorkMode,
                new String[]{
                        "On-site",
                        "Remote",
                        "Hybrid"
                }
        );

        setDropdown(
                actvSalaryPeriod,
                new String[]{
                        "Per Hour",
                        "Per Month",
                        "Per Year"
                }
        );
    }

    private void setDropdown(AutoCompleteTextView textView,
                             String[] items) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                items
        );

        textView.setAdapter(adapter);

        // Show dropdown when the field is clicked.
        textView.setOnClickListener(v -> textView.showDropDown());

        // Also show suggestions when the dropdown icon is tapped.
        textView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                textView.post(textView::showDropDown);
            }
        });
    }

    // ---------------------------------------------------------
    // APPLICATION DEADLINE DATE PICKER
    // ---------------------------------------------------------

    private void setupDatePicker() {

        etApplicationDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            requireContext(),
                            (datePicker, year, month, day) -> {

                                Calendar selectedDate =
                                        Calendar.getInstance();

                                selectedDate.set(
                                        year,
                                        month,
                                        day
                                );

                                SimpleDateFormat dateFormat =
                                        new SimpleDateFormat(
                                                "yyyy-MM-dd",
                                                Locale.getDefault()
                                        );

                                String formattedDate =
                                        dateFormat.format(
                                                selectedDate.getTime()
                                        );

                                etApplicationDeadline.setText(
                                        formattedDate
                                );

                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );

            // Do not allow past dates.
            datePickerDialog.getDatePicker().setMinDate(
                    System.currentTimeMillis() - 1000
            );

            datePickerDialog.show();
        });
    }

    // ---------------------------------------------------------
    // GET TEXT FROM EDITTEXT
    // ---------------------------------------------------------

    private String getText(TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    private String getDropdownValue(AutoCompleteTextView textView) {

        return textView.getText().toString().trim();
    }

    // ---------------------------------------------------------
    // VALIDATE FORM
    // ---------------------------------------------------------

    private boolean validateForm() {

        if (TextUtils.isEmpty(recruiterId) ||
                TextUtils.isEmpty(recruiterEmail)) {

            showToast("Recruiter login details are missing.");
            return false;
        }

        if (getText(etJobTitle).isEmpty()) {
            etJobTitle.setError("Enter job title");
            etJobTitle.requestFocus();
            return false;
        }

        if (getDropdownValue(actvJobCategory).isEmpty()) {
            showToast("Please select job category.");
            return false;
        }

        if (getText(etOpenings).isEmpty()) {
            etOpenings.setError("Enter number of openings");
            etOpenings.requestFocus();
            return false;
        }

        try {
            int openings = Integer.parseInt(getText(etOpenings));

            if (openings <= 0 || openings > 9999) {
                etOpenings.setError("Enter openings between 1 and 9999");
                return false;
            }

        } catch (NumberFormatException e) {
            etOpenings.setError("Invalid number of openings");
            return false;
        }

        if (getText(etJobDescription).isEmpty()) {
            etJobDescription.setError("Enter job description");
            etJobDescription.requestFocus();
            return false;
        }

        if (getText(etResponsibilities).isEmpty()) {
            etResponsibilities.setError("Enter responsibilities");
            etResponsibilities.requestFocus();
            return false;
        }

        if (getText(etRequiredSkills).isEmpty()) {
            etRequiredSkills.setError("Enter required technical skills");
            etRequiredSkills.requestFocus();
            return false;
        }

        if (getDropdownValue(actvMinExperience).isEmpty()) {
            showToast("Please select minimum experience.");
            return false;
        }

        if (getDropdownValue(actvEmploymentType).isEmpty()) {
            showToast("Please select employment type.");
            return false;
        }

        if (getDropdownValue(actvWorkMode).isEmpty()) {
            showToast("Please select work mode.");
            return false;
        }

        if (getText(etJobLocation).isEmpty()) {
            etJobLocation.setError("Enter job location");
            etJobLocation.requestFocus();
            return false;
        }

        if (getText(etApplicationDeadline).isEmpty()) {
            etApplicationDeadline.setError("Select application deadline");
            showToast("Please select application deadline.");
            return false;
        }

        if (getText(etHrEmail).isEmpty() ||
                !Patterns.EMAIL_ADDRESS.matcher(
                        getText(etHrEmail)
                ).matches()) {

            showToast("Recruiter email is invalid. Please login again.");
            return false;
        }

        String companyEmail = getText(etCompanyEmail);

        if (companyEmail.isEmpty()) {
            etCompanyEmail.setError("Enter company email");
            etCompanyEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(companyEmail).matches()) {
            etCompanyEmail.setError("Enter a valid company email");
            etCompanyEmail.requestFocus();
            return false;
        }

        // Validate optional experience.
        String maxExperience = getText(etMaxExperience);

        if (!maxExperience.isEmpty()) {
            try {
                double value = Double.parseDouble(maxExperience);

                if (value < 0 || value > 99.9) {
                    etMaxExperience.setError(
                            "Experience must be between 0 and 99.9"
                    );
                    return false;
                }

            } catch (NumberFormatException e) {
                etMaxExperience.setError("Enter valid experience");
                return false;
            }
        }

        // Validate optional salary.
        String minSalary = getText(etMinSalary);
        String maxSalary = getText(etMaxSalary);

        if (!isValidOptionalNumber(minSalary, etMinSalary, "salary")) {
            return false;
        }

        if (!isValidOptionalNumber(maxSalary, etMaxSalary, "salary")) {
            return false;
        }

        if (!minSalary.isEmpty() && !maxSalary.isEmpty()) {

            if (Double.parseDouble(maxSalary) <
                    Double.parseDouble(minSalary)) {

                etMaxSalary.setError(
                        "Maximum salary cannot be less than minimum salary"
                );

                etMaxSalary.requestFocus();
                return false;
            }
        }

        return true;
    }

    private boolean isValidOptionalNumber(
            String value,
            TextInputEditText editText,
            String fieldName) {

        if (value.isEmpty()) {
            return true;
        }

        try {
            double number = Double.parseDouble(value);

            if (number < 0 || Double.isInfinite(number) ||
                    Double.isNaN(number) || number > 9999999999.99) {

                editText.setError("Enter valid " + fieldName);
                editText.requestFocus();
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            editText.setError("Enter valid " + fieldName);
            editText.requestFocus();
            return false;
        }
    }

    // ---------------------------------------------------------
    // PUBLISH JOB
    // ---------------------------------------------------------

    private void publishJob() {

        if (isPosting) {
            return;
        }

        if (!validateForm()) {
            return;
        }

        JSONObject params = new JSONObject();

        try {

            // Recruiter details from SharedPreferences.
            params.put("recruiter_id", recruiterId);
            params.put("recruiter_email", recruiterEmail);

            // Basic job information.
            params.put("job_title", getText(etJobTitle));
            params.put("department", getText(etJobDepartment));
            params.put("job_category", getDropdownValue(actvJobCategory));
            params.put("openings", getText(etOpenings));

            // Job description.
            params.put("job_description", getText(etJobDescription));
            params.put("responsibilities", getText(etResponsibilities));

            // Skills and requirements.
            params.put("required_skills", getText(etRequiredSkills));
            params.put("soft_skills", getText(etSoftSkills));
            params.put("min_experience", getDropdownValue(actvMinExperience));
            params.put("max_experience", getText(etMaxExperience));
            params.put("minimum_qualification",
                    getDropdownValue(actvQualification));
            params.put("preferred_course", getText(etPreferredCourse));

            // Employment and location.
            params.put("employment_type",
                    getDropdownValue(actvEmploymentType));
            params.put("work_mode", getDropdownValue(actvWorkMode));
            params.put("job_location", getText(etJobLocation));
            params.put("state", getText(etJobState));
            params.put("country", getText(etJobCountry));

            // Salary and benefits.
            params.put("min_salary", getText(etMinSalary));
            params.put("max_salary", getText(etMaxSalary));
            params.put("salary_period", getDropdownValue(actvSalaryPeriod));
            params.put("benefits", getText(etBenefits));

            // Application settings.
            params.put("application_deadline",
                    getText(etApplicationDeadline));

            // HR email is the logged-in recruiter's email.
            params.put("hr_email", recruiterEmail);

            // Company email is entered by recruiter.
            params.put("company_email", getText(etCompanyEmail));

            params.put("visibility", "Public");

        } catch (JSONException e) {

            showToast("Unable to prepare job information.");
            return;
        }

        sendJobToServer(params);
    }

    // ---------------------------------------------------------
    // SEND DATA TO PHP API USING VOLLEY
    // ---------------------------------------------------------

    private void sendJobToServer(JSONObject params) {

        isPosting = true;

        btnPostJob.setEnabled(false);
        btnPostJob.setText("Publishing...");

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.post_job,
                params,

                response -> {

                    isPosting = false;

                    btnPostJob.setEnabled(true);
                    btnPostJob.setText("Publish Job");

                    boolean status = response.optBoolean(
                            "status",
                            false
                    );

                    String message = response.optString(
                            "message",
                            "Unknown server response."
                    );

                    if (status) {

                        int jobId = response.optInt("job_id", 0);

                        showToast(
                                "Job published successfully. Job ID: " + jobId
                        );

                        clearForm();

                    } else {

                        showToast(message);
                    }
                },

                error -> {

                    isPosting = false;

                    btnPostJob.setEnabled(true);
                    btnPostJob.setText("Publish Job");

                    String errorMessage =
                            "Unable to connect to server.";

                    if (error.networkResponse != null &&
                            error.networkResponse.data != null) {

                        try {

                            String responseData = new String(
                                    error.networkResponse.data,
                                    "UTF-8"
                            );

                            JSONObject errorObject =
                                    new JSONObject(responseData);

                            errorMessage = errorObject.optString(
                                    "message",
                                    errorMessage
                            );

                        } catch (Exception e) {
                            errorMessage = "Server error. Please check PHP API.";
                        }

                    } else if (error.getMessage() != null) {

                        errorMessage = error.getMessage();
                    }

                    showToast(errorMessage);
                }
        );

        request.setShouldCache(false);

        // Add request to Volley queue.
        requestQueue.add(request);
    }

    // ---------------------------------------------------------
    // CLEAR FORM AFTER SUCCESSFUL INSERT
    // ---------------------------------------------------------

    private void clearForm() {

        etJobTitle.setText("");
        etJobDepartment.setText("");
        actvJobCategory.setText("", false);
        etOpenings.setText("");

        etJobDescription.setText("");
        etResponsibilities.setText("");

        etRequiredSkills.setText("");
        etSoftSkills.setText("");
        actvMinExperience.setText("", false);
        etMaxExperience.setText("");
        actvQualification.setText("", false);
        etPreferredCourse.setText("");

        actvEmploymentType.setText("", false);
        actvWorkMode.setText("", false);
        etJobLocation.setText("");
        etJobState.setText("");
        etJobCountry.setText("India");

        etMinSalary.setText("");
        etMaxSalary.setText("");
        actvSalaryPeriod.setText("", false);
        etBenefits.setText("");

        etApplicationDeadline.setText("");

        // Keep the logged-in recruiter's HR email.
        etHrEmail.setText(recruiterEmail);

        etCompanyEmail.setText("");
    }

    private void showToast(String message) {

        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
