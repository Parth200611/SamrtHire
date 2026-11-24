
package com.example.smarthire.JobSeeker.Fragment.Application;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApplicatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoApplications;

    private MyApplicationAdapter adapter;
    private final List<MyApplication> applicationList = new ArrayList<>();

    private RequestQueue requestQueue;
    private String userEmail = "";

    public ApplicatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_applicat,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.rv_my_applications);
        progressBar = view.findViewById(R.id.progress_applications);
        tvNoApplications = view.findViewById(R.id.tv_no_applications);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new MyApplicationAdapter(requireContext(), applicationList);
        recyclerView.setAdapter(adapter);

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        "SmartHirePrefs",
                        requireActivity().MODE_PRIVATE
                );

        userEmail = preferences.getString("email", "");

        requestQueue = Volley.newRequestQueue(requireContext());

        if (userEmail == null || userEmail.trim().isEmpty()) {
            tvNoApplications.setText("Please login to view your applications.");
            tvNoApplications.setVisibility(View.VISIBLE);
        } else {
            getMyApplications();
        }

        return view;
    }

    private void getMyApplications() {

        progressBar.setVisibility(View.VISIBLE);
        tvNoApplications.setVisibility(View.GONE);

        JSONObject params = new JSONObject();

        try {
            params.put("email", userEmail);
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(),
                    "Unable to prepare request",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Urls.get_app,
                params,

                response -> {

                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);

                    boolean status = response.optBoolean("status", false);

                    if (status) {

                        JSONArray applications =
                                response.optJSONArray("applications");

                        applicationList.clear();

                        if (applications != null) {

                            for (int i = 0; i < applications.length(); i++) {
                                JSONObject object =
                                        applications.optJSONObject(i);

                                if (object != null) {
                                    applicationList.add(
                                            new MyApplication(object));
                                }
                            }
                        }

                        adapter.updateList(applicationList);

                        if (applicationList.isEmpty()) {
                            tvNoApplications.setText(
                                    "You haven't applied for any jobs yet.");
                            tvNoApplications.setVisibility(View.VISIBLE);
                        } else {
                            tvNoApplications.setVisibility(View.GONE);
                        }

                    } else {

                        tvNoApplications.setText(
                                response.optString(
                                        "message",
                                        "Unable to fetch applications."
                                )
                        );

                        tvNoApplications.setVisibility(View.VISIBLE);
                    }
                },

                error -> {

                    if (!isAdded()) return;

                    progressBar.setVisibility(View.GONE);

                    tvNoApplications.setText(
                            "Unable to load applications. Please try again."
                    );

                    tvNoApplications.setVisibility(View.VISIBLE);

                    Toast.makeText(requireContext(),
                            "Network error. Check your API URL and connection.",
                            Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (requestQueue != null) {
            requestQueue.cancelAll(request -> true);
        }
    }
}
