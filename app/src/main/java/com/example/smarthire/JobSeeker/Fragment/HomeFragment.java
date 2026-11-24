package com.example.smarthire.JobSeeker.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

// Import your ChatAIActivity here
// import com.example.smarthire.JobSeeker.ChatAIActivity;

public class HomeFragment extends Fragment {

    // Welcome Section
    private TextView tvWelcomeTitle, tvWelcomeSubtitle;

    // AI Career Assistant
    private MaterialCardView cardChatAI;
    private MaterialButton btnChatAI;

    // About App
    private MaterialCardView cardAboutApp;
    private TextView tvAboutApp;

    // New Jobs
    private TextView tvNewJobsHeading, tvViewAllJobs;
    private RecyclerView rvNewJobs;

    // Contact & Support
    private MaterialCardView cardContactSupport;
    private TextView tvSupportTitle;
    private TextView tvSupportDescription;
    private TextView tvSupportPhone;
    private TextView tvSupportEmail;

    // Support Details
    private static final String SUPPORT_PHONE = "+97291198859";
    private static final String SUPPORT_EMAIL = "admin@gmail.com";

    private NewJobsAdapter newJobsAdapter;
    private ArrayList<JobModel> jobList;

    private RequestQueue requestQueue;

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        // Initialize all views
        initializeViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup Click Listeners
        setupClickListeners();

        // Setup Welcome Message
        setWelcomeMessage();

        return view;
    }

    // ------------------------------------------------
    // INITIALIZE ALL XML VIEWS
    // ------------------------------------------------

    private void initializeViews(View view) {

        // Welcome Section
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = view.findViewById(R.id.tv_welcome_subtitle);

        // AI Career Assistant
        cardChatAI = view.findViewById(R.id.card_chat_ai);
        btnChatAI = view.findViewById(R.id.btn_chat_ai);

        // About App
        cardAboutApp = view.findViewById(R.id.card_about_app);
        tvAboutApp = view.findViewById(R.id.tv_about_app);

        // New Jobs
        tvNewJobsHeading = view.findViewById(R.id.tv_new_jobs_heading);
        tvViewAllJobs = view.findViewById(R.id.tv_view_all_jobs);
        rvNewJobs = view.findViewById(R.id.rv_new_jobs);

        // Contact & Support
        cardContactSupport = view.findViewById(R.id.card_contact_support);
        tvSupportTitle = view.findViewById(R.id.tv_support_title);
        tvSupportDescription = view.findViewById(R.id.tv_support_description);
        tvSupportPhone = view.findViewById(R.id.tv_support_phone);
        tvSupportEmail = view.findViewById(R.id.tv_support_email);


    }

    // ------------------------------------------------
    // RECYCLER VIEW INITIALIZATION
    // ------------------------------------------------

    private void setupRecyclerView() {

        jobList = new ArrayList<>();

        rvNewJobs.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        rvNewJobs.setNestedScrollingEnabled(false);

        newJobsAdapter = new NewJobsAdapter(jobList);

        rvNewJobs.setAdapter(newJobsAdapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        // Fetch jobs from database
        loadNewJobs();
    }
    private void loadNewJobs() {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Urls.get__all__jobs,
                null,

                response -> {

                    try {

                        boolean status = response.optBoolean("status", false);

                        if (!status) {

                            Toast.makeText(
                                    requireContext(),
                                    response.optString("message", "Unable to load jobs"),
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        JSONArray jobsArray = response.optJSONArray("jobs");

                        ArrayList<JobModel> latestJobs = new ArrayList<>();

                        if (jobsArray != null) {

                            for (int i = 0; i < jobsArray.length(); i++) {

                                JSONObject object = jobsArray.getJSONObject(i);

                                JobModel job = new JobModel(

                                        object.optString("id", ""),
                                        object.optString("recruiter_id", ""),
                                        object.optString("recruiter_email", ""),
                                        object.optString("job_title", ""),
                                        object.optString("department", ""),
                                        object.optString("job_category", ""),
                                        object.optString("openings", ""),
                                        object.optString("job_description", ""),
                                        object.optString("responsibilities", ""),
                                        object.optString("required_skills", ""),
                                        object.optString("soft_skills", ""),
                                        object.optString("min_experience", ""),
                                        object.optString("max_experience", ""),
                                        object.optString("minimum_qualification", ""),
                                        object.optString("preferred_course", ""),
                                        object.optString("employment_type", ""),
                                        object.optString("work_mode", ""),
                                        object.optString("job_location", ""),
                                        object.optString("state", ""),
                                        object.optString("country", ""),
                                        object.optString("min_salary", ""),
                                        object.optString("max_salary", ""),
                                        object.optString("salary_period", ""),
                                        object.optString("benefits", ""),
                                        object.optString("application_deadline", ""),
                                        object.optString("hr_email", ""),
                                        object.optString("company_email", ""),
                                        object.optString("visibility", ""),
                                        object.optString("job_status", ""),
                                        object.optString("created_at", ""),
                                        object.optString("updated_at", "")
                                );

                                latestJobs.add(job);
                            }
                        }

                        // Replace old list with fresh database records
                        jobList.clear();
                        jobList.addAll(latestJobs);

                        newJobsAdapter.updateJobs(latestJobs);

                        Log.d(TAG, "Total jobs loaded: " + latestJobs.size());

                        if (latestJobs.isEmpty()) {

                            Toast.makeText(
                                    requireContext(),
                                    "No jobs available right now.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {

                        Log.e(TAG, "Job parsing error: ", e);

                        Toast.makeText(
                                requireContext(),
                                "Error while loading jobs.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },

                error -> {

                    Log.e(TAG, "Job API error: ", error);

                    String message = "Unable to connect to server.";

                    if (error.networkResponse != null) {
                        message = "Server error: " + error.networkResponse.statusCode;
                    }

                    if (isAdded()) {
                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // Always fetch fresh data
        request.setShouldCache(false);

        request.setTag(TAG);

        requestQueue.add(request);
    }

    // ------------------------------------------------
    // CLICK LISTENERS
    // ------------------------------------------------

    private void setupClickListeners() {

        // Chat with AI
        btnChatAI.setOnClickListener(v -> openChatAI());

        // Also allow clicking the complete AI card
        cardChatAI.setOnClickListener(v -> openChatAI());

        // View All Jobs
        tvViewAllJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ViewAllJobs.class);
                startActivity(i);
            }
        });

        // Phone Number - Open Dialer
        tvSupportPhone.setOnClickListener(v -> makePhoneCall());

        // Email Address - Open Email Application
        tvSupportEmail.setOnClickListener(v -> openEmail());

    }

    // ------------------------------------------------
    // OPEN CHAT AI ACTIVITY
    // ------------------------------------------------

    private void openChatAI() {

        try {

            Intent intent = new Intent(getContext(),ChatActivity.class);
            startActivity(intent);


        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Chat AI Activity not found. Check its package name.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ------------------------------------------------
    // PHONE CALL / DIALER
    // ------------------------------------------------

    private void makePhoneCall() {

        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + SUPPORT_PHONE)
        );

        startActivity(intent);
    }

    // ------------------------------------------------
    // OPEN EMAIL APPLICATION
    // ------------------------------------------------

    private void openEmail() {

        Intent intent = new Intent(
                Intent.ACTION_SENDTO
        );

        intent.setData(Uri.parse("mailto:" + SUPPORT_EMAIL));

        intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "SmartHire Support Request"
        );

        intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hello SmartHire Support Team,\n\n"
        );

        try {

            startActivity(
                    Intent.createChooser(
                            intent,
                            "Send Email"
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "No email application found.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ------------------------------------------------
    // OPEN ALL JOBS
    // ------------------------------------------------

    private void openAllJobs() {

        // Replace this with your actual All Jobs Fragment navigation.
        // Example:
        // ((JobSeekerDashboardActivity) requireActivity())
        //        .loadFragment(new JobsFragment());

        Toast.makeText(
                requireContext(),
                "Connect your All Jobs screen here.",
                Toast.LENGTH_SHORT
        ).show();
    }

    // ------------------------------------------------
    // WELCOME MESSAGE
    // ------------------------------------------------

    private void setWelcomeMessage() {

        android.content.SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        "SmartHirePrefs",
                        android.content.Context.MODE_PRIVATE
                );

        String fullName = preferences.getString(
                "full_name",
                "Job Seeker"
        );

        tvWelcomeTitle.setText("Welcome, " + fullName + "!");

        tvWelcomeSubtitle.setText(
                "Find the right role tailored to your skills."
        );
    }

}
