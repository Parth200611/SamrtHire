
package com.example.smarthire.Recruiter.Application;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.smarthire.R;

public class RecruiterApplicationAdapter
        extends RecyclerView.Adapter<RecruiterApplicationAdapter.ApplicationViewHolder> {

    // This is the same list received from ApplicationsFragment.
    private final List<RecruiterApplication> applicationList;

    // This list contains only the items currently visible after filtering.
    private final List<RecruiterApplication> filteredList;

    public RecruiterApplicationAdapter(List<RecruiterApplication> list) {
        applicationList = list;
        filteredList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recruiter_application, parent, false);

        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ApplicationViewHolder holder, int position) {

        RecruiterApplication application = filteredList.get(position);

        String name = safe(application.getFull_name());
        String email = safe(application.getEmail());
        String jobTitle = safe(application.getJob_title());
        String mobile = safe(application.getMobile());

        String city = safe(application.getCity());
        String state = safe(application.getState());
        String location = "";

        if (!city.isEmpty() && !state.isEmpty()) {
            location = city + ", " + state;
        } else if (!city.isEmpty()) {
            location = city;
        } else {
            location = state;
        }

        String appliedDate = safe(application.getApplied_at());
        String status = safe(application.getApplication_status());

        holder.tvCandidateName.setText(
                name.isEmpty() ? "Candidate" : name
        );

        holder.tvCandidateEmail.setText(email);
        holder.tvAppliedJobTitle.setText(jobTitle);
        holder.tvCandidateMobile.setText(mobile);
        holder.tvCandidateLocation.setText(
                location.isEmpty() ? "Location not provided" : location
        );
        holder.tvAppliedDate.setText(appliedDate);

        holder.tvCandidateInitial.setText(
                name.isEmpty() ? "C" :
                        name.substring(0, 1).toUpperCase(Locale.ROOT)
        );

        holder.tvApplicationStatus.setText(
                status.isEmpty() ? "Applied" : status
        );

        setStatusColor(holder.tvApplicationStatus, status);

        holder.btnViewApplication.setOnClickListener(v -> {

            Context context = v.getContext();

            Intent i = new Intent(context, ViewCandidate.class);

            i.putExtra("Can_email", application.getEmail());

            i.putExtra("Can_name", application.getFull_name());
            i.putExtra("Can_mobile", application.getMobile());
            i.putExtra("job_title", application.getJob_title());
            i.putExtra("application_id", application.getId());

            context.startActivity(i);
        });
    }

    private void setStatusColor(TextView textView, String status) {

        String value = status == null ? "" :
                status.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "shortlisted":
                textView.setTextColor(Color.parseColor("#15803D"));
                textView.setBackgroundColor(Color.parseColor("#DCFCE7"));
                break;

            case "rejected":
                textView.setTextColor(Color.parseColor("#B91C1C"));
                textView.setBackgroundColor(Color.parseColor("#FEE2E2"));
                break;

            case "under review":
                textView.setTextColor(Color.parseColor("#B45309"));
                textView.setBackgroundColor(Color.parseColor("#FEF3C7"));
                break;

            default:
                textView.setTextColor(Color.parseColor("#1D4ED8"));
                textView.setBackgroundColor(Color.parseColor("#DBEAFE"));
                break;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    public void filter(String query, String status) {

        filteredList.clear();

        String searchText = query == null ? "" :
                query.trim().toLowerCase(Locale.ROOT);

        String selectedStatus = status == null ? "All" :
                status.trim();

        for (RecruiterApplication application : applicationList) {

            if (application == null) {
                continue;
            }

            String name = safe(application.getFull_name())
                    .toLowerCase(Locale.ROOT);

            String email = safe(application.getEmail())
                    .toLowerCase(Locale.ROOT);

            String jobTitle = safe(application.getJob_title())
                    .toLowerCase(Locale.ROOT);

            String mobile = safe(application.getMobile())
                    .toLowerCase(Locale.ROOT);

            String applicationStatus = safe(
                    application.getApplication_status()
            );

            boolean matchesSearch =
                    searchText.isEmpty()
                            || name.contains(searchText)
                            || email.contains(searchText)
                            || jobTitle.contains(searchText)
                            || mobile.contains(searchText);

            boolean matchesStatus =
                    selectedStatus.equalsIgnoreCase("All")
                            || applicationStatus.equalsIgnoreCase(selectedStatus);

            if (matchesSearch && matchesStatus) {
                filteredList.add(application);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public static class ApplicationViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvCandidateInitial;
        TextView tvCandidateName;
        TextView tvCandidateEmail;
        TextView tvApplicationStatus;
        TextView tvAppliedJobTitle;
        TextView tvCandidateMobile;
        TextView tvCandidateLocation;
        TextView tvAppliedDate;
        Button btnViewApplication;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCandidateInitial =
                    itemView.findViewById(R.id.tv_candidate_initial);

            tvCandidateName =
                    itemView.findViewById(R.id.tv_candidate_name);

            tvCandidateEmail =
                    itemView.findViewById(R.id.tv_candidate_email);

            tvApplicationStatus =
                    itemView.findViewById(R.id.tv_application_status);

            tvAppliedJobTitle =
                    itemView.findViewById(R.id.tv_applied_job_title);

            tvCandidateMobile =
                    itemView.findViewById(R.id.tv_candidate_mobile);

            tvCandidateLocation =
                    itemView.findViewById(R.id.tv_candidate_location);

            tvAppliedDate =
                    itemView.findViewById(R.id.tv_applied_date);

            btnViewApplication =
                    itemView.findViewById(R.id.btn_view_application);
        }
    }
}
