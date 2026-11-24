
package com.example.smarthire.Recruiter.Candidate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidatesFragment extends Fragment {

    private RecyclerView rvCandidates;
    private SearchView searchCandidates;

    private TextView tvCandidateCount, tvNoCandidates;
    private ProgressBar progressCandidates;

    private CandidateAdapter adapter;

    private final List<Candidate> candidateList = new ArrayList<>();

    private RequestQueue requestQueue;

    public CandidatesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_candidates,
                container,
                false
        );

        initializeViews(view);
        setupRecyclerView();
        setupSearch();

        requestQueue = Volley.newRequestQueue(requireContext());

        getAllCandidates();

        return view;
    }

    private void initializeViews(View view) {

        rvCandidates = view.findViewById(R.id.rv_candidates);
        searchCandidates = view.findViewById(R.id.search_candidates);

        tvCandidateCount = view.findViewById(R.id.tv_candidate_count);
        tvNoCandidates = view.findViewById(R.id.tv_no_candidates);

        progressCandidates = view.findViewById(R.id.progress_candidates);
    }

    private void setupRecyclerView() {

        rvCandidates.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        rvCandidates.setHasFixedSize(true);

        adapter = new CandidateAdapter(
                requireContext(),
                candidateList
        );

        rvCandidates.setAdapter(adapter);
    }

    private void setupSearch() {

        searchCandidates.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        adapter.filter(query);
                        updateCandidateCount();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        adapter.filter(newText);
                        updateCandidateCount();

                        return true;
                    }
                }
        );
    }

    private void getAllCandidates() {

        progressCandidates.setVisibility(View.VISIBLE);
        tvNoCandidates.setVisibility(View.GONE);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                Urls.getAll_can,

                response -> {

                    if (!isAdded()) return;

                    progressCandidates.setVisibility(View.GONE);

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        boolean status = jsonObject.optBoolean("status", false);

                        if (status) {

                            JSONArray candidatesArray =
                                    jsonObject.optJSONArray("candidates");

                            candidateList.clear();

                            if (candidatesArray != null) {

                                for (int i = 0; i < candidatesArray.length(); i++) {

                                    JSONObject candidateObject =
                                            candidatesArray.getJSONObject(i);

                                    Candidate candidate =
                                            new Candidate(candidateObject);

                                    candidateList.add(candidate);
                                }
                            }

                            adapter.updateList(candidateList);

                            updateCandidateCount();

                            if (candidateList.isEmpty()) {

                                tvNoCandidates.setText(
                                        "No candidates are available yet."
                                );

                                tvNoCandidates.setVisibility(View.VISIBLE);

                            } else {

                                tvNoCandidates.setVisibility(View.GONE);
                            }

                        } else {

                            String message = jsonObject.optString(
                                    "message",
                                    "Unable to fetch candidates."
                            );

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                            candidateList.clear();
                            adapter.updateList(candidateList);

                            updateCandidateCount();
                            tvNoCandidates.setText(message);
                            tvNoCandidates.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(
                                requireContext(),
                                "Invalid server response.",
                                Toast.LENGTH_SHORT
                        ).show();

                        tvNoCandidates.setText("Unable to read candidate data.");
                        tvNoCandidates.setVisibility(View.VISIBLE);
                    }
                },

                error -> {

                    if (!isAdded()) return;

                    progressCandidates.setVisibility(View.GONE);

                    String errorMessage = "Failed to connect to server.";

                    if (error.networkResponse != null) {
                        errorMessage += " Code: " +
                                error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            requireContext(),
                            errorMessage,
                            Toast.LENGTH_LONG
                    ).show();

                    tvNoCandidates.setText(
                            "Unable to load candidates. Please try again."
                    );

                    tvNoCandidates.setVisibility(View.VISIBLE);
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                // The PHP API only requires the POST method.
                return new HashMap<>();
            }
        };

        request.setShouldCache(false);

        requestQueue.add(request);
    }

    private void updateCandidateCount() {

        if (adapter == null) return;

        int count = adapter.getItemCount();

        tvCandidateCount.setText(
                count + (count == 1 ? " Candidate" : " Candidates")
        );

        if (count == 0 && !candidateList.isEmpty()) {

            tvNoCandidates.setText("No matching candidates found.");
            tvNoCandidates.setVisibility(View.VISIBLE);

        } else if (count > 0) {

            tvNoCandidates.setVisibility(View.GONE);
        }
    }
}
