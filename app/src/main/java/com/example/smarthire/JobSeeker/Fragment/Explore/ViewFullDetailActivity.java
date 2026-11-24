
package com.example.smarthire.JobSeeker.Fragment.Explore;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewFullDetailActivity extends AppCompatActivity {

    private static final String TAG = "RecruiterDetails";

    // Profile views
    private CircleImageView ivProfilePhoto;

    private TextView tvProfileName;
    private TextView tvProfileDesignation;
    private TextView tvProfileCompany;
    private TextView tvProfileEmail;
    private TextView tvProfileMobile;
    private TextView tvProfileLocation;
    private TextView tvVerificationStatus;

    // Professional information
    private TextView tvDepartment;
    private TextView tvExperience;

    // Company information
    private TextView tvCompanyDescription;
    private TextView tvCompanyIndustry;
    private TextView tvCompanyWebsite;
    private TextView tvCompanyName;
    private TextView tvCompanyAddress;
    private TextView tvCompanyPincode;

    // Contact information
    private TextView tvContactEmail;
    private TextView tvContactMobile;

    // Account information
    private TextView tvAccountStatus;
    private TextView tvMemberSince;

    // Cards
    private MaterialCardView cardProfessional;
    private MaterialCardView cardAboutCompany;
    private MaterialCardView cardCompanyDetails;
    private MaterialCardView cardContact;
    private MaterialCardView cardAccountStatus;

    private RequestQueue requestQueue;

    private String recruiterId = "";
    private String recruiterEmail = "";
    private String recruiterMobile = "";
    private String recruiterCountry = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_detail);

        initializeViews();

        requestQueue = Volley.newRequestQueue(this);

        // Receive recruiter ID from RecyclerView adapter
        recruiterId = getIntent().getStringExtra("recruiter_id");

        if (recruiterId == null || recruiterId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Recruiter ID not found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        fetchRecruiterDetails();
    }

    private void initializeViews() {

        ivProfilePhoto = findViewById(R.id.iv_profile_photo);

        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileDesignation = findViewById(R.id.tv_profile_designation);
        tvProfileCompany = findViewById(R.id.tv_profile_company);
        tvProfileEmail = findViewById(R.id.tv_profile_email);
        tvProfileMobile = findViewById(R.id.tv_profile_mobile);
        tvProfileLocation = findViewById(R.id.tv_profile_location);
        tvVerificationStatus = findViewById(R.id.tv_verification_status);

        tvDepartment = findViewById(R.id.tv_department);
        tvExperience = findViewById(R.id.tv_experience);

        tvCompanyDescription = findViewById(R.id.tv_company_description);
        tvCompanyIndustry = findViewById(R.id.tv_company_industry);
        tvCompanyWebsite = findViewById(R.id.tv_company_website);

        tvCompanyName = findViewById(R.id.tv_company_name);
        tvCompanyAddress = findViewById(R.id.tv_company_address);
        tvCompanyPincode = findViewById(R.id.tv_company_pincode);

        tvContactEmail = findViewById(R.id.tv_contact_email);
        tvContactMobile = findViewById(R.id.tv_contact_mobile);

        tvAccountStatus = findViewById(R.id.tv_account_status);
        tvMemberSince = findViewById(R.id.tv_member_since);

        cardProfessional = findViewById(R.id.card_professional);
        cardAboutCompany = findViewById(R.id.card_about_company);
        cardCompanyDetails = findViewById(R.id.card_company_details);
        cardContact = findViewById(R.id.card_contact);
        cardAccountStatus = findViewById(R.id.card_account_status);
    }

    // -------------------------------------------------
    // FETCH RECRUITER DETAILS
    // -------------------------------------------------

    private void fetchRecruiterDetails() {

        JSONObject params = new JSONObject();

        try {
            params.put("recruiter_id", recruiterId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.rect_get_by_id,
                params,

                response -> {

                    Log.d(TAG, "API Response: " + response);

                    try {

                        boolean status = response.optBoolean("status", false);

                        if (status) {

                            JSONObject recruiter =
                                    response.getJSONObject("recruiter");

                            displayRecruiterDetails(recruiter);

                        } else {

                            String message = response.optString(
                                    "message",
                                    "Unable to fetch recruiter details."
                            );

                            Toast.makeText(
                                    this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {

                        Log.e(TAG, "JSON Error: ", e);

                        Toast.makeText(
                                this,
                                "Invalid server response.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },

                error -> {

                    Log.e(TAG, "API Error: ", error);

                    String message = "Unable to connect to server.";

                    if (error.networkResponse != null) {
                        message = "Server error: "
                                + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setTag(TAG);
        requestQueue.add(request);
    }

    // -------------------------------------------------
    // DISPLAY RECRUITER DETAILS
    // -------------------------------------------------

    private void displayRecruiterDetails(JSONObject recruiter) {

        // Basic details
        String fullName = getValue(recruiter, "full_name");
        String designation = getValue(recruiter, "designation");
        String companyName = getValue(recruiter, "company_name");

        recruiterEmail = getValue(recruiter, "email");
        recruiterMobile = getValue(recruiter, "mobile");

        String city = getValue(recruiter, "city");
        String state = getValue(recruiter, "state");
        recruiterCountry = getValue(recruiter, "country");

        if (recruiterCountry.isEmpty()) {
            recruiterCountry = "India";
        }

        String location = buildLocation(city, state, recruiterCountry);

        tvProfileName.setText(
                valueOrDefault(fullName, "Recruiter")
        );

        tvProfileDesignation.setText(
                valueOrDefault(designation, "HR Professional")
        );

        tvProfileCompany.setText(
                valueOrDefault(companyName, "Company not available")
        );

        tvProfileEmail.setText(
                valueOrDefault(recruiterEmail, "Email not available")
        );

        tvProfileMobile.setText(
                valueOrDefault(recruiterMobile, "Mobile not available")
        );

        tvProfileLocation.setText(location);

        setOptionalVisibility(tvProfileDesignation, designation);
        setOptionalVisibility(tvProfileMobile, recruiterMobile);
        setOptionalVisibility(tvProfileLocation, location);

        // Verification status
        String verificationStatus =
                getValue(recruiter, "verification_status");

        tvVerificationStatus.setText(
                verificationStatus.equalsIgnoreCase("Verified")
                        ? "✓ Verified Recruiter"
                        : "Not Verified"
        );

        if (verificationStatus.equalsIgnoreCase("Verified")) {
            tvVerificationStatus.setTextColor(0xFF047857);
            tvVerificationStatus.setBackgroundColor(0xFFECFDF5);
        } else {
            tvVerificationStatus.setTextColor(0xFFB45309);
            tvVerificationStatus.setBackgroundColor(0xFFFFF7ED);
        }

        // Profile photo
        loadProfileImage(getValue(recruiter, "profile_photo"));

        // Professional information
        String department = getValue(recruiter, "department");
        String experience = getValue(recruiter, "experience_years");

        tvDepartment.setText(
                "Department: " +
                        valueOrDefault(department, "Not specified")
        );

        tvExperience.setText(
                "Experience: " +
                        formatExperience(experience)
        );

        // About company
        String description = getValue(recruiter, "company_description");
        String industry = getValue(recruiter, "industry");
        String website = getValue(recruiter, "company_website");

        tvCompanyDescription.setText(
                valueOrDefault(description, "Company description not available.")
        );

        tvCompanyIndustry.setText(
                "Industry: " +
                        valueOrDefault(industry, "Not specified")
        );

        tvCompanyWebsite.setText(
                valueOrDefault(website, "Website not available")
        );

        // Company details
        tvCompanyName.setText(
                valueOrDefault(companyName, "Company not available")
        );

        String address = getValue(recruiter, "address");

        String fullAddress = buildLocation(
                address,
                city,
                state,
                recruiterCountry
        );

        tvCompanyAddress.setText(
                valueOrDefault(fullAddress, "Address not available")
        );

        String pincode = getValue(recruiter, "pincode");

        tvCompanyPincode.setText(
                "Pincode: " + valueOrDefault(pincode, "Not available")
        );

        // Contact information
        tvContactEmail.setText(
                valueOrDefault(recruiterEmail, "Email not available")
        );

        tvContactMobile.setText(
                valueOrDefault(recruiterMobile, "Mobile not available")
        );

        // Account information
        String accountStatus = getValue(recruiter, "account_status");

        tvAccountStatus.setText(
                "Account Status: " +
                        valueOrDefault(accountStatus, "Not available")
        );

        String createdAt = getValue(recruiter, "created_at");

        tvMemberSince.setText(
                "Member Since: " +
                        valueOrDefault(createdAt, "Not available")
        );

        // Hide sections when all their information is missing
        cardProfessional.setVisibility(
                department.isEmpty() && experience.isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        cardAboutCompany.setVisibility(
                description.isEmpty() && industry.isEmpty() && website.isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        cardCompanyDetails.setVisibility(
                companyName.isEmpty() && address.isEmpty()
                        && city.isEmpty() && state.isEmpty()
                        && pincode.isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        cardContact.setVisibility(
                recruiterEmail.isEmpty() && recruiterMobile.isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        cardAccountStatus.setVisibility(
                accountStatus.isEmpty() && createdAt.isEmpty()
                        ? View.GONE : View.VISIBLE
        );

        // Email and WhatsApp actions
        setupContactActions();
    }

    // -------------------------------------------------
    // PROFILE IMAGE
    // -------------------------------------------------

    private void loadProfileImage(String photo) {

        if (photo == null || photo.trim().isEmpty()) {

            ivProfilePhoto.setImageResource(
                    R.drawable.ic_smarthire_logo
            );

            return;
        }

        String imageUrl;

        if (photo.startsWith("http://") ||
                photo.startsWith("https://")) {

            imageUrl = photo;

        } else {

            imageUrl = Urls.urls +
                    (photo.startsWith("/")
                            ? photo.substring(1)
                            : photo);
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_smarthire_logo)
                .error(R.drawable.ic_smarthire_logo)
                .into(ivProfilePhoto);
    }

    // -------------------------------------------------
    // EMAIL AND WHATSAPP ACTIONS
    // -------------------------------------------------

    private void setupContactActions() {

        // Header email click
        tvProfileEmail.setOnClickListener(v -> openGmail());

        // Contact section email click
        tvContactEmail.setOnClickListener(v -> openGmail());

        // Header mobile click
        tvProfileMobile.setOnClickListener(v -> openWhatsApp());

        // Contact section mobile click
        tvContactMobile.setOnClickListener(v -> openWhatsApp());
    }

    private void openGmail() {

        if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Recruiter email is not available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent emailIntent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + Uri.encode(recruiterEmail.trim()))
        );

        emailIntent.putExtra(
                Intent.EXTRA_EMAIL,
                new String[]{recruiterEmail.trim()}
        );

        // Try Gmail first
        emailIntent.setPackage("com.google.android.gm");

        try {

            startActivity(emailIntent);

        } catch (ActivityNotFoundException e) {

            // If Gmail is unavailable, show other email applications
            emailIntent.setPackage(null);

            try {

                startActivity(
                        Intent.createChooser(
                                emailIntent,
                                "Send email using"
                        )
                );

            } catch (ActivityNotFoundException ex) {

                Toast.makeText(
                        this,
                        "No email application found.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void openWhatsApp() {

        if (recruiterMobile == null || recruiterMobile.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Recruiter mobile number is not available.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Remove spaces, +, brackets and other symbols
        String phone = recruiterMobile.replaceAll("[^0-9]", "");

        // Add India's country code if a 10-digit number is stored
        if (phone.length() == 10 &&
                recruiterCountry.equalsIgnoreCase("India")) {

            phone = "91" + phone;
        }

        if (phone.length() < 10) {

            Toast.makeText(
                    this,
                    "Invalid recruiter mobile number.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Uri whatsappUri = Uri.parse(
                "https://wa.me/" + phone
        );

        Intent whatsappIntent = new Intent(
                Intent.ACTION_VIEW,
                whatsappUri
        );

        // Open WhatsApp directly when installed
        whatsappIntent.setPackage("com.whatsapp");

        try {

            startActivity(whatsappIntent);

        } catch (ActivityNotFoundException e) {

            // Otherwise open the WhatsApp link using an available app/browser
            whatsappIntent.setPackage(null);

            try {

                startActivity(whatsappIntent);

            } catch (ActivityNotFoundException ex) {

                Toast.makeText(
                        this,
                        "Unable to open WhatsApp.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // -------------------------------------------------
    // HELPER METHODS
    // -------------------------------------------------

    private String getValue(JSONObject object, String key) {

        String value = object.optString(key, "");

        if (value == null ||
                value.equalsIgnoreCase("null") ||
                value.trim().isEmpty()) {

            return "";
        }

        return value.trim();
    }

    private String valueOrDefault(String value, String defaultValue) {

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    private void setOptionalVisibility(TextView textView, String value) {

        textView.setVisibility(
                value == null || value.trim().isEmpty()
                        ? View.GONE : View.VISIBLE
        );
    }

    private String buildLocation(String... values) {

        StringBuilder builder = new StringBuilder();

        for (String value : values) {

            if (value != null && !value.trim().isEmpty()) {

                if (builder.length() > 0) {
                    builder.append(", ");
                }

                builder.append(value.trim());
            }
        }

        return builder.toString();
    }

    private String formatExperience(String experience) {

        if (experience == null || experience.trim().isEmpty()) {
            return "Not specified";
        }

        try {

            double years = Double.parseDouble(experience);

            String formatted = years == (long) years
                    ? String.valueOf((long) years)
                    : String.valueOf(years);

            return formatted + " years";

        } catch (NumberFormatException e) {

            return experience + " years";
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
