
package com.example.smarthire.Recruiter.Profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.example.smarthire.Recruiter.RecruiterLoginActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RecruiterProfileFragment extends Fragment {

    // Change this IP according to your computer's current LAN IP.
    // Android emulator: http://10.0.2.2/
    // Physical phone: use your computer's LAN IP.


    private static final String API_URL =
            Urls.get_rect;

    private static final String PREF_NAME = "SmartHirePrefs";

    private RequestQueue requestQueue;

    private ImageView ivRecruiterPhoto;

    private TextView tvRecruiterName, tvRecruiterDesignation,
            tvRecruiterCompany, tvRecruiterEmail,
            tvRecruiterMobile, tvRecruiterLocation;

    private TextView tvCompletionPercentage, tvCompletionMessage;

    private ProgressBar profileProgress;
    private ActivityResultLauncher<String> imagePicker;

    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private TextView tvInfoFullName, tvInfoEmail, tvInfoMobile,
            tvInfoDesignation, tvInfoDepartment, tvInfoExperience;

    private TextView tvCompanyName, tvCompanyIndustry,
            tvCompanyWebsite, tvCompanyDescription;

    private TextView tvCompanyAddress, tvCompanyCity,
            tvCompanyState, tvCompanyCountry, tvCompanyPincode;

    private TextView tvVerificationStatus, tvVerificationBadge;
    private TextView tvAccountStatus, tvAccountBadge;
    private TextView tvCreatedAt, tvUpdatedAt;
    MaterialButton btn_update_profile;
    MaterialButton btn_logout;

    private String userEmail = "";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_recruiter_profile,
                container,
                false
        );

        initializeViews(view);

        requestQueue = Volley.newRequestQueue(requireContext());
        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        uploadRecruiterImage(uri);
                    }
                }
        );
        getRecruiterEmail();

        return view;
    }

    // -----------------------------------------
    // INITIALIZE XML VIEWS
    // -----------------------------------------

    private void initializeViews(View view) {

        ivRecruiterPhoto = view.findViewById(R.id.iv_recruiter_photo);
        ivRecruiterPhoto.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });

        tvRecruiterName = view.findViewById(R.id.tv_recruiter_name);
        tvRecruiterDesignation = view.findViewById(R.id.tv_recruiter_designation);
        tvRecruiterCompany = view.findViewById(R.id.tv_recruiter_company);
        tvRecruiterEmail = view.findViewById(R.id.tv_recruiter_email);
        tvRecruiterMobile = view.findViewById(R.id.tv_recruiter_mobile);
        tvRecruiterLocation = view.findViewById(R.id.tv_recruiter_location);

        tvCompletionPercentage = view.findViewById(R.id.tv_completion_percentage);
        profileProgress = view.findViewById(R.id.profile_progress);
        tvCompletionMessage = view.findViewById(R.id.tv_completion_message);
        btn_update_profile = view.findViewById(R.id.btn_update_profile);

        tvInfoFullName = view.findViewById(R.id.tv_info_full_name);
        tvInfoEmail = view.findViewById(R.id.tv_info_email);
        tvInfoMobile = view.findViewById(R.id.tv_info_mobile);
        tvInfoDesignation = view.findViewById(R.id.tv_info_designation);
        tvInfoDepartment = view.findViewById(R.id.tv_info_department);
        tvInfoExperience = view.findViewById(R.id.tv_info_experience);

        tvCompanyName = view.findViewById(R.id.tv_company_name);
        tvCompanyIndustry = view.findViewById(R.id.tv_company_industry);
        tvCompanyWebsite = view.findViewById(R.id.tv_company_website);
        tvCompanyDescription = view.findViewById(R.id.tv_company_description);

        tvCompanyAddress = view.findViewById(R.id.tv_company_address);
        tvCompanyCity = view.findViewById(R.id.tv_company_city);
        tvCompanyState = view.findViewById(R.id.tv_company_state);
        tvCompanyCountry = view.findViewById(R.id.tv_company_country);
        tvCompanyPincode = view.findViewById(R.id.tv_company_pincode);

        tvVerificationStatus = view.findViewById(R.id.tv_verification_status);
        tvVerificationBadge = view.findViewById(R.id.tv_verification_badge);

        tvAccountStatus = view.findViewById(R.id.tv_account_status);
        tvAccountBadge = view.findViewById(R.id.tv_account_badge);

        tvCreatedAt = view.findViewById(R.id.tv_created_at);
        tvUpdatedAt = view.findViewById(R.id.tv_updated_at);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> showLogoutDialog());

        btn_update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),UpdateRect.class);
                startActivity(i);
            }
        });
    }

    // -----------------------------------------
    // GET EMAIL FROM LOGIN SESSION
    // -----------------------------------------

// -----------------------------------------
// UPLOAD RECRUITER PROFILE IMAGE
// -----------------------------------------
private void showLogoutDialog() {

    new AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> logoutRecruiter())
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .show();
}

    private void logoutRecruiter() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        requireActivity().MODE_PRIVATE
                );

        // Clear the recruiter login session
        preferences.edit()
                .clear()
                .apply();

        Toast.makeText(
                requireContext(),
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        // Navigate to recruiter login
        Intent intent = new Intent(
                requireActivity(),
                RecruiterLoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        requireActivity().finish();
    }
    private void uploadRecruiterImage(Uri imageUri) {

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(
                    requireContext(),
                    "Recruiter email not found. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        try {

            InputStream inputStream =
                    requireContext()
                            .getContentResolver()
                            .openInputStream(imageUri);

            if (inputStream == null) {
                Toast.makeText(
                        requireContext(),
                        "Unable to read selected image",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            byte[] imageBytes = readImageBytes(inputStream);
            inputStream.close();

            if (imageBytes.length == 0) {
                Toast.makeText(
                        requireContext(),
                        "Selected image is empty",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (imageBytes.length > MAX_IMAGE_SIZE) {
                Toast.makeText(
                        requireContext(),
                        "Image size must be below 5 MB",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            String mimeType = requireContext()
                    .getContentResolver()
                    .getType(imageUri);

            if (mimeType == null ||
                    !(mimeType.equals("image/jpeg")
                            || mimeType.equals("image/png")
                            || mimeType.equals("image/webp"))) {

                Toast.makeText(
                        requireContext(),
                        "Only JPG, PNG and WEBP images are allowed",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            sendRecruiterImageToServer(
                    imageBytes,
                    mimeType
            );

        } catch (Exception e) {

            Log.e("RecruiterImage", "Image reading error", e);

            Toast.makeText(
                    requireContext(),
                    "Unable to select image",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


// -----------------------------------------
// READ IMAGE BYTES
// -----------------------------------------

    private byte[] readImageBytes(InputStream inputStream)
            throws IOException {

        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        int length;

        while ((length = inputStream.read(buffer)) != -1) {

            byteArrayOutputStream.write(buffer, 0, length);

            if (byteArrayOutputStream.size() > MAX_IMAGE_SIZE) {
                throw new IOException("Image size exceeds 5 MB");
            }
        }

        return byteArrayOutputStream.toByteArray();
    }


// -----------------------------------------
// SEND IMAGE USING VOLLEY
// -----------------------------------------

    private void sendRecruiterImageToServer(
            byte[] imageBytes,
            String mimeType) {

        final String boundary =
                "SmartHireBoundary" + System.currentTimeMillis();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.add_rec_img,
                null,

                response -> {

                    if (!isAdded()) return;

                    boolean status =
                            response.optBoolean("status", false);

                    if (status) {

                        String imagePath =
                                response.optString("image_path", "");

                        if (!TextUtils.isEmpty(imagePath)) {

                            String imageUrl = getRecruiterImageUrl(imagePath);

                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_smarthire_logo)
                                    .error(R.drawable.ic_smarthire_logo)
                                    .into(ivRecruiterPhoto);

                            Toast.makeText(
                                    requireContext(),
                                    "Profile image updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            // Fetch latest profile from database
                            fetchRecruiterProfile(userEmail);

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Image uploaded but image path is missing",
                                    Toast.LENGTH_LONG
                            ).show();
                        }

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
                },

                error -> {

                    if (!isAdded()) return;

                    String message = "Image upload failed";

                    if (error.networkResponse != null) {
                        message += " (" +
                                error.networkResponse.statusCode + ")";
                    }

                    Log.e("RecruiterImage", "Upload error", error);

                    Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        ) {

            @Override
            public String getBodyContentType() {
                return "multipart/form-data; boundary=" + boundary;
            }

            @Override
            public byte[] getBody() {

                try {

                    ByteArrayOutputStream output =
                            new ByteArrayOutputStream();

                    // EMAIL FIELD
                    output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));

                    output.write((
                            "Content-Disposition: form-data; name=\"email\"\r\n\r\n"
                    ).getBytes(StandardCharsets.UTF_8));

                    output.write(userEmail.getBytes(StandardCharsets.UTF_8));
                    output.write("\r\n".getBytes(StandardCharsets.UTF_8));

                    // IMAGE FIELD
                    output.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));

                    output.write((
                            "Content-Disposition: form-data; name=\"image\"; filename=\"recruiter_profile\"\r\n"
                    ).getBytes(StandardCharsets.UTF_8));

                    output.write((
                            "Content-Type: " + mimeType + "\r\n\r\n"
                    ).getBytes(StandardCharsets.UTF_8));

                    output.write(imageBytes);

                    output.write("\r\n".getBytes(StandardCharsets.UTF_8));

                    // END MULTIPART REQUEST
                    output.write((
                            "--" + boundary + "--\r\n"
                    ).getBytes(StandardCharsets.UTF_8));

                    return output.toByteArray();

                } catch (IOException e) {

                    Log.e("RecruiterImage", "Multipart body error", e);
                    return null;
                }
            }
        };

        request.setShouldCache(false);

        requestQueue.add(request);
    }


// -----------------------------------------
// BUILD COMPLETE IMAGE URL
// -----------------------------------------

    private String getRecruiterImageUrl(String imagePath) {

        if (imagePath.startsWith("http://")
                || imagePath.startsWith("https://")) {
            return imagePath;
        }

        String baseUrl = com.example.smarthire.Coman.Urls.urls;

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }

        return baseUrl + imagePath;
    }

    private void getRecruiterEmail() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        requireActivity().MODE_PRIVATE
                );

        userEmail = preferences.getString("email", "");

        if (TextUtils.isEmpty(userEmail)) {

            Toast.makeText(
                    requireContext(),
                    "Recruiter email not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        fetchRecruiterProfile(userEmail);
    }

    // -----------------------------------------
    // FETCH PROFILE USING VOLLEY
    // -----------------------------------------

    private void fetchRecruiterProfile(String email) {

        JSONObject requestData = new JSONObject();

        try {
            requestData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                API_URL,
                requestData,

                response -> {

                    try {

                        boolean status = response.optBoolean("status", false);

                        if (status) {

                            JSONObject recruiter =
                                    response.getJSONObject("recruiter");

                            displayRecruiterProfile(recruiter);

                        } else {

                            String message = response.optString(
                                    "message",
                                    "Unable to fetch profile"
                            );

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                        Toast.makeText(
                                requireContext(),
                                "Invalid profile response",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },

                error -> {

                    String errorMessage = "Network error. Please try again.";

                    if (error.networkResponse != null) {
                        errorMessage = "Server error: "
                                + error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();

                    error.printStackTrace();
                }
        );

        requestQueue.add(request);
    }

    // -----------------------------------------
    // DISPLAY RECRUITER PROFILE
    // -----------------------------------------

    private void displayRecruiterProfile(JSONObject data) {

        String id = getValue(data, "id");
        String fullName = getValue(data, "full_name");
        String email = getValue(data, "email");
        String mobile = getValue(data, "mobile");

        String photo = getValue(data, "profile_photo");

        String designation = getValue(data, "designation");
        String department = getValue(data, "department");
        String experience = getValue(data, "experience_years");

        String companyName = getValue(data, "company_name");
        String industry = getValue(data, "industry");
        String website = getValue(data, "company_website");
        String description = getValue(data, "company_description");

        String address = getValue(data, "address");
        String city = getValue(data, "city");
        String state = getValue(data, "state");
        String country = getValue(data, "country");
        String pincode = getValue(data, "pincode");

        String verification = getValue(data, "verification_status");
        String accountStatus = getValue(data, "account_status");

        String createdAt = getValue(data, "created_at");
        String updatedAt = getValue(data, "updated_at");

        // PROFILE HEADER

        setText(tvRecruiterName, fullName);
        setText(tvRecruiterDesignation, designation);
        setText(tvRecruiterCompany, companyName);
        setText(tvRecruiterEmail, email);
        setText(tvRecruiterMobile, mobile);

        String location = joinValues(city, state, country);
        setText(tvRecruiterLocation, location);

        // PROFILE PHOTO

        if (!TextUtils.isEmpty(photo)) {

            String imageUrl = photo;

            if (!photo.startsWith("http://")
                    && !photo.startsWith("https://")) {

                imageUrl = Urls.urls + photo;
            }

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_smarthire_logo)
                    .error(R.drawable.ic_smarthire_logo)
                    .into(ivRecruiterPhoto);

        } else {

            ivRecruiterPhoto.setImageResource(R.drawable.ic_smarthire_logo);
        }

        // RECRUITER INFORMATION

        setText(tvInfoFullName, fullName);
        setText(tvInfoEmail, email);
        setText(tvInfoMobile, mobile);
        setText(tvInfoDesignation, designation);
        setText(tvInfoDepartment, department);

        if (!TextUtils.isEmpty(experience)) {
            setText(tvInfoExperience, experience + " Years");
        } else {
            setText(tvInfoExperience, "Not added");
        }

        // COMPANY INFORMATION

        setText(tvCompanyName, companyName);
        setText(tvCompanyIndustry, industry);
        setText(tvCompanyWebsite, website);
        setText(tvCompanyDescription, description);

        // COMPANY ADDRESS

        setText(tvCompanyAddress, address);
        setText(tvCompanyCity, city);
        setText(tvCompanyState, state);
        setText(tvCompanyCountry, country);
        setText(tvCompanyPincode, pincode);

        // ACCOUNT INFORMATION

        setText(tvVerificationStatus, verification);
        setText(tvVerificationBadge, verification);

        setText(tvAccountStatus, accountStatus);
        setText(tvAccountBadge, accountStatus);

        setText(tvCreatedAt, createdAt);
        setText(tvUpdatedAt, updatedAt);

        // PROFILE COMPLETION

        calculateProfileCompletion(data);
    }

    // -----------------------------------------
    // CALCULATE PROFILE COMPLETION
    // -----------------------------------------

    private void calculateProfileCompletion(JSONObject data) {

        String[] requiredFields = {
                "full_name",
                "email",
                "mobile",
                "profile_photo",
                "designation",
                "department",
                "experience_years",
                "company_name",
                "industry",
                "company_website",
                "company_description",
                "address",
                "city",
                "state",
                "country",
                "pincode"
        };

        int completed = 0;

        for (String field : requiredFields) {

            String value = getValue(data, field);

            if (!TextUtils.isEmpty(value)) {
                completed++;
            }
        }

        int total = requiredFields.length;

        int percentage = (completed * 100) / total;

        profileProgress.setProgress(percentage);

        tvCompletionPercentage.setText(percentage + "%");

        if (percentage == 100) {

            tvCompletionMessage.setText(
                    "Your recruiter profile is complete!"
            );

        } else {

            tvCompletionMessage.setText(
                    "Complete your profile to improve your recruiter presence."
            );
        }
    }

    // -----------------------------------------
    // JSON VALUE HELPER
    // -----------------------------------------

    private String getValue(JSONObject object, String key) {

        if (object == null || object.isNull(key)) {
            return "";
        }

        String value = object.optString(key, "");

        if (value.equals("null")) {
            return "";
        }

        return value.trim();
    }

    // -----------------------------------------
    // SET TEXT SAFELY
    // -----------------------------------------

    private void setText(TextView textView, String value) {

        if (textView == null) {
            return;
        }

        if (TextUtils.isEmpty(value)) {
            textView.setText("Not added");
        } else {
            textView.setText(value);
        }
    }

    // -----------------------------------------
    // JOIN LOCATION VALUES
    // -----------------------------------------

    private String joinValues(String... values) {

        StringBuilder result = new StringBuilder();

        for (String value : values) {

            if (!TextUtils.isEmpty(value)) {

                if (result.length() > 0) {
                    result.append(", ");
                }

                result.append(value);
            }
        }

        return result.length() == 0 ? "Location not added" : result.toString();
    }
}
