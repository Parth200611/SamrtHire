
package com.example.smarthire.Recruiter.Application;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApplicationsFragment extends Fragment {

    private RecyclerView recyclerApplications;
    private EditText etSearch;

    private TextView tvTotal;
    private TextView tvPending;
    private TextView tvShortlisted;

    private LinearLayout layoutNoApplications;
    private ChipGroup chipGroup;

    private RecruiterApplicationAdapter adapter;

    private final ArrayList<RecruiterApplication> applicationList =
            new ArrayList<>();

    private String recruiterEmail = "";
    private String currentStatus = "All";
    private String currentSearch = "";

    private static final String PREF_NAME = "SmartHirePrefs";

    public ApplicationsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_applications,
                container,
                false
        );

        initializeViews(view);

        getRecruiterEmail();

        setupRecyclerView();

        setupSearch();

        setupFilters();

        if (recruiterEmail.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Recruiter email not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            getApplications();
        }

        return view;
    }

    private void initializeViews(View view) {

        recyclerApplications = view.findViewById(R.id.recycler_applications);

        etSearch = view.findViewById(R.id.et_search_application);

        tvTotal = view.findViewById(R.id.tv_total_applications);
        tvPending = view.findViewById(R.id.tv_pending_applications);
        tvShortlisted = view.findViewById(R.id.tv_shortlisted_applications);

        layoutNoApplications = view.findViewById(R.id.layout_no_applications);

        chipGroup = view.findViewById(R.id.chip_application_filters);
    }

    // Get email saved during recruiter login
    private void getRecruiterEmail() {

        SharedPreferences preferences = requireActivity()
                .getSharedPreferences(PREF_NAME, requireActivity().MODE_PRIVATE);

        recruiterEmail = preferences.getString("email", "");

        if (recruiterEmail == null) {
            recruiterEmail = "";
        }

        recruiterEmail = recruiterEmail.trim();
    }

    private void setupRecyclerView() {

        recyclerApplications.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter = new RecruiterApplicationAdapter(applicationList);

        recyclerApplications.setAdapter(adapter);
    }

    // Fetch applications from PHP API
    private void getApplications() {

        JSONObject params = new JSONObject();

        try {
            params.put("hr_mail", recruiterEmail);

        } catch (Exception e) {
            Log.e("APPLICATION_API", "JSON creation error", e);
            return;
        }

        // Request logs
        Log.d("APPLICATION_API", "========== API REQUEST ==========");
        Log.d("APPLICATION_API", "URL: " + Urls.get_application);
        Log.d("APPLICATION_API", "Recruiter Email: " + recruiterEmail);
        Log.d("APPLICATION_API", "Request JSON: " + params);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.get_application,
                params,

                response -> {

                    Log.d("APPLICATION_API", "========== API SUCCESS ==========");
                    Log.d("APPLICATION_API", "Full Response: " + response);

                    try {

                        boolean status = response.optBoolean("status", false);

                        Log.d("APPLICATION_API", "API Status: " + status);
                        Log.d("APPLICATION_API", "API Message: " +
                                response.optString("message"));

                        if (!status) {

                            Log.e("APPLICATION_API", "API returned false status");

                            Toast.makeText(
                                    requireContext(),
                                    response.optString("message"),
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        // Read statistics
                        JSONObject statistics =
                                response.optJSONObject("statistics");

                        Log.d("APPLICATION_API", "Statistics: " + statistics);

                        if (statistics != null) {

                            int total = response.optInt("total", 0);
                            int applied = statistics.optInt("applied", 0);
                            int underReview = statistics.optInt("under_review", 0);
                            int shortlisted = statistics.optInt("shortlisted", 0);

                            int pending = applied + underReview;

                            tvTotal.setText(String.valueOf(total));
                            tvPending.setText(String.valueOf(pending));
                            tvShortlisted.setText(String.valueOf(shortlisted));

                            Log.d("APPLICATION_API", "Total: " + total);
                            Log.d("APPLICATION_API", "Applied: " + applied);
                            Log.d("APPLICATION_API", "Under Review: " + underReview);
                            Log.d("APPLICATION_API", "Shortlisted: " + shortlisted);
                        }

                        // Read application records
                        JSONArray applications =
                                response.optJSONArray("applications");

                        applicationList.clear();

                        Log.d("APPLICATION_API", "Applications Array: " + applications);

                        if (applications != null) {

                            Log.d("APPLICATION_API", "Array Length: " +
                                    applications.length());

                            for (int i = 0; i < applications.length(); i++) {

                                JSONObject obj = applications.getJSONObject(i);

                                Log.d("APPLICATION_API",
                                        "Application " + i + ": " + obj);

                                RecruiterApplication application =
                                        new RecruiterApplication(

                                                obj.optString("id"),
                                                obj.optString("job_id"),
                                                obj.optString("job_title"),
                                                obj.optString("hr_mail"),

                                                obj.optString("user_id"),
                                                obj.optString("full_name"),
                                                obj.optString("email"),
                                                obj.optString("mobile"),

                                                obj.optString("state"),
                                                obj.optString("city"),
                                                obj.optString("cover_letter"),

                                                obj.optString("application_status"),
                                                obj.optString("recruiter_remarks"),

                                                obj.optString("interview_date"),
                                                obj.optString("interview_time"),
                                                obj.optString("interview_mode"),
                                                obj.optString("interview_location"),
                                                obj.optString("interview_link"),

                                                obj.optString("applied_at"),
                                                obj.optString("updated_at")
                                        );

                                applicationList.add(application);

                                Log.d("APPLICATION_API",
                                        "Candidate Added: " +
                                                application.getFull_name());

                                Log.d("APPLICATION_API",
                                        "Candidate Email: " +
                                                application.getEmail());
                            }

                        } else {

                            Log.e("APPLICATION_API",
                                    "Applications array is NULL");
                        }

                        Log.d("APPLICATION_API",
                                "Final List Size: " + applicationList.size());

                        // Refresh adapter
                        adapter.filter(currentSearch, currentStatus);

                        Log.d("APPLICATION_API",
                                "Adapter Item Count: " + adapter.getItemCount());

                        updateEmptyView();

                        Log.d("APPLICATION_API",
                                "========== DATA LOADING FINISHED ==========");

                    } catch (Exception e) {

                        Log.e("APPLICATION_API",
                                "JSON parsing error", e);

                        Toast.makeText(
                                requireContext(),
                                "Error reading applications.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                },

                error -> {

                    Log.e("APPLICATION_API", "========== API ERROR ==========");
                    Log.e("APPLICATION_API", "Volley Error: " + error.toString());

                    if (error.networkResponse != null) {

                        Log.e("APPLICATION_API", "HTTP Code: " +
                                error.networkResponse.statusCode);

                        if (error.networkResponse.data != null) {

                            Log.e("APPLICATION_API", "Server Response: " +
                                    new String(error.networkResponse.data));
                        }
                    }

                    Toast.makeText(
                            requireContext(),
                            "Unable to fetch applications. Check your connection.",
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setShouldCache(false);

        Volley.newRequestQueue(requireContext()).add(request);
    }
    private void setupSearch() {

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                currentSearch = s.toString();

                adapter.filter(currentSearch, currentStatus);

                updateEmptyView();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Application status filters
    private void setupFilters() {

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {

            if (checkedIds.isEmpty()) {
                currentStatus = "All";
            } else {

                int selectedId = checkedIds.get(0);

                if (selectedId == R.id.chip_applied) {
                    currentStatus = "Applied";

                } else if (selectedId == R.id.chip_review) {
                    currentStatus = "Under Review";

                } else if (selectedId == R.id.chip_shortlisted) {
                    currentStatus = "Shortlisted";

                } else if (selectedId == R.id.chip_rejected) {
                    currentStatus = "Rejected";

                } else {
                    currentStatus = "All";
                }
            }

            adapter.filter(currentSearch, currentStatus);

            updateEmptyView();
        });
    }

    private void updateEmptyView() {

        if (adapter.getItemCount() == 0) {

            recyclerApplications.setVisibility(View.GONE);
            layoutNoApplications.setVisibility(View.VISIBLE);

        } else {

            recyclerApplications.setVisibility(View.VISIBLE);
            layoutNoApplications.setVisibility(View.GONE);
        }
    }
}
