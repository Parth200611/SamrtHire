
package com.example.smarthire.JobSeeker.Fragment.Explore;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ExploreFragment extends Fragment {

    private static final String TAG = "RECRUITER_API";
    private static final String REQUEST_TAG = "ALL_RECRUITERS_REQUEST";

    private EditText etSearchRecruiter;
    private ImageButton btnClearSearch;
    private RecyclerView recyclerRecruiters;
    private TextView tvRecruiterCount;
    private MaterialCardView layoutEmpty;
    private LinearLayout layoutLoading;

    private RecruiterAdapter recruiterAdapter;
    private ArrayList<RecruiterModel> recruiterList;
    private RequestQueue requestQueue;

    public ExploreFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_explore,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        recruiterList = new ArrayList<>();

        setupRecyclerView();

        setupSearch();

        requestQueue = Volley.newRequestQueue(requireContext());

        fetchAllRecruiters();
    }

    private void initializeViews(View view) {

        etSearchRecruiter = view.findViewById(R.id.et_search_recruiter);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);
        recyclerRecruiters = view.findViewById(R.id.recycler_recruiters);
        tvRecruiterCount = view.findViewById(R.id.tv_recruiter_count);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutLoading = view.findViewById(R.id.layout_loading);
    }

    private void setupRecyclerView() {

        recyclerRecruiters.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerRecruiters.setHasFixedSize(false);
        recyclerRecruiters.setNestedScrollingEnabled(false);

        recruiterAdapter = new RecruiterAdapter(
                recruiterList,
                recruiter -> {

                    Toast.makeText(
                            requireContext(),
                            "Selected: " + recruiter.getFullName()
                                    + " | ID: " + recruiter.getId(),
                            Toast.LENGTH_SHORT
                    ).show();

                    // Add RecruiterDetailsFragment navigation here later.
                }
        );

        recyclerRecruiters.setAdapter(recruiterAdapter);
    }

    private void setupSearch() {

        etSearchRecruiter.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (recruiterAdapter == null) return;

                String query = s.toString();

                recruiterAdapter.filter(query);

                updateRecruiterCount();
                updateEmptyState();

                btnClearSearch.setVisibility(
                        query.trim().isEmpty()
                                ? View.GONE
                                : View.VISIBLE
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnClearSearch.setOnClickListener(v ->
                etSearchRecruiter.setText("")
        );
    }

    private void fetchAllRecruiters() {

        if (!isAdded() || requestQueue == null) {
            return;
        }

        showLoading(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Urls.all_rectu,
                null,

                response -> {

                    if (!isAdded()) return;

                    Log.d(TAG, "API RESPONSE: " + response);

                    try {

                        boolean status = response.optBoolean("status", false);

                        if (!status) {

                            String message = response.optString(
                                    "message",
                                    "Unable to fetch recruiters."
                            );

                            recruiterList.clear();
                            recruiterAdapter.updateData(recruiterList);

                            showLoading(false);
                            updateRecruiterCount();
                            updateEmptyState();

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        JSONArray array = response.optJSONArray("recruiters");

                        recruiterList.clear();

                        if (array != null) {

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject object = array.optJSONObject(i);

                                if (object == null) continue;

                                RecruiterModel recruiter = new RecruiterModel(

                                        object.optString("id", ""),
                                        object.optString("full_name", ""),
                                        object.optString("email", ""),
                                        object.optString("mobile", ""),
                                        object.optString("profile_photo", ""),
                                        object.optString("designation", ""),
                                        object.optString("department", ""),
                                        object.optString("experience_years", ""),
                                        object.optString("company_name", ""),
                                        object.optString("industry", ""),
                                        object.optString("company_website", ""),
                                        object.optString("company_description", ""),
                                        object.optString("address", ""),
                                        object.optString("city", ""),
                                        object.optString("state", ""),
                                        object.optString("country", ""),
                                        object.optString("pincode", ""),
                                        object.optString("verification_status", ""),
                                        object.optString("account_status", ""),
                                        object.optString("created_at", ""),
                                        object.optString("updated_at", "")
                                );

                                recruiterList.add(recruiter);
                            }
                        }

                        Log.d(TAG, "TOTAL RECRUITERS: " + recruiterList.size());

                        recruiterAdapter.updateData(recruiterList);

                        showLoading(false);
                        updateRecruiterCount();
                        updateEmptyState();

                    } catch (Exception e) {

                        Log.e(TAG, "JSON PARSING ERROR", e);

                        showLoading(false);

                        recruiterList.clear();
                        recruiterAdapter.updateData(recruiterList);

                        updateRecruiterCount();
                        updateEmptyState();

                        Toast.makeText(
                                requireContext(),
                                "Error parsing recruiter data.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    if (!isAdded()) return;

                    showLoading(false);

                    String message = "Unable to connect to server.";

                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;

                        String responseBody = "";

                        if (error.networkResponse.data != null) {
                            responseBody = new String(
                                    error.networkResponse.data,
                                    StandardCharsets.UTF_8
                            );
                        }

                        Log.e(TAG, "HTTP CODE: " + statusCode);
                        Log.e(TAG, "ERROR RESPONSE: " + responseBody);

                        message = "Server error: " + statusCode;

                    } else if (error.getMessage() != null) {

                        message = error.getMessage();

                        Log.e(TAG, "VOLLEY ERROR: " + message);
                    }

                    recruiterList.clear();
                    recruiterAdapter.updateData(recruiterList);

                    updateRecruiterCount();
                    updateEmptyState();

                    Toast.makeText(
                            requireContext(),
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        request.setTag(REQUEST_TAG);

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        15000,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requestQueue.add(request);
    }

    private void showLoading(boolean show) {

        if (layoutLoading == null) return;

        layoutLoading.setVisibility(
                show ? View.VISIBLE : View.GONE
        );

        if (show) {
            layoutEmpty.setVisibility(View.GONE);
            recyclerRecruiters.setVisibility(View.GONE);
        }
    }

    private void updateRecruiterCount() {

        if (recruiterAdapter != null && tvRecruiterCount != null) {

            int count = recruiterAdapter.getItemCount();

            tvRecruiterCount.setText(count + " recruiters");
        }
    }

    private void updateEmptyState() {

        if (recruiterAdapter == null) return;

        if (recruiterAdapter.getItemCount() == 0) {

            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerRecruiters.setVisibility(View.GONE);

        } else {

            layoutEmpty.setVisibility(View.GONE);
            recyclerRecruiters.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {

        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }

        etSearchRecruiter = null;
        btnClearSearch = null;
        recyclerRecruiters = null;
        tvRecruiterCount = null;
        layoutEmpty = null;
        layoutLoading = null;

        recruiterAdapter = null;

        super.onDestroyView();
    }
}
