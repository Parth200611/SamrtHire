package com.example.smarthire.JobSeeker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthire.R;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewJobsAdapter extends RecyclerView.Adapter<NewJobsAdapter.JobViewHolder> {

    private final ArrayList<JobModel> jobList;

    public NewJobsAdapter(List<JobModel> jobList) {
        this.jobList = new ArrayList<>(jobList);
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_job, parent, false);

        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {

        JobModel job = jobList.get(position);

        holder.tvJobTitle.setText(value(job.getJobTitle(), "Untitled Job"));
        holder.tvJobCategory.setText(value(job.getJobCategory(), "General"));
        holder.tvJobOpenings.setText(value(job.getOpenings(), "1") + " Openings");

        holder.tvJobDepartment.setText(
                value(job.getDepartment(), "Department not specified")
        );

        // Location
        String location = joinLocation(
                job.getJobLocation(),
                job.getState(),
                job.getCountry()
        );

        holder.tvJobLocation.setText(location);

        // Experience
        String experience = "Experience: " +
                value(job.getMinExperience(), "Not specified");

        if (job.getMaxExperience() != null &&
                !job.getMaxExperience().trim().isEmpty() &&
                !job.getMaxExperience().equalsIgnoreCase("null")) {

            experience += " - " + job.getMaxExperience() + " years";
        }

        holder.tvJobExperience.setText(experience);

        // Work mode
        holder.tvJobWorkMode.setText(
                value(job.getWorkMode(), "Not specified")
        );

        // Employment type and qualification
        String employment = value(job.getEmploymentType(), "Not specified");

        String qualification = value(
                job.getMinimumQualification(),
                "Qualification not specified"
        );

        holder.tvJobEmployment.setText(
                employment + "  •  " + qualification
        );

        // Salary
        holder.tvJobSalary.setText(formatSalary(job));

        // Skills
        String skills = value(
                job.getRequiredSkills(),
                "Skills not specified"
        );

        holder.tvJobSkills.setText("Skills: " + skills);

        // Deadline
        holder.tvJobDeadline.setText(
                "Apply before: " + formatDate(job.getApplicationDeadline())
        );

        // Status
        String status = value(job.getJobStatus(), "Unknown");
        holder.tvJobStatus.setText(status);

        if (status.equalsIgnoreCase("Published")) {
            holder.tvJobStatus.setTextColor(0xFF15803D);
            holder.tvJobStatus.setBackgroundColor(0xFFDCFCE7);
        } else {
            holder.tvJobStatus.setTextColor(0xFF475569);
            holder.tvJobStatus.setBackgroundColor(0xFFF1F5F9);
        }

        holder.Job_card.setOnClickListener(v -> {

            Context context = holder.itemView.getContext();

            Intent intent = new Intent(context, ViewCompleteJobActivity.class);

            intent.putExtra("job_id", job.getId());

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobs(List<JobModel> newJobs) {
        jobList.clear();
        jobList.addAll(newJobs);
        notifyDataSetChanged();
    }

    private String value(String text, String defaultValue) {

        if (text == null ||
                text.trim().isEmpty() ||
                text.equalsIgnoreCase("null")) {
            return defaultValue;
        }

        return text.trim();
    }

    private String joinLocation(String city, String state, String country) {

        StringBuilder location = new StringBuilder();

        String[] parts = {
                city, state, country
        };

        for (String part : parts) {

            if (part != null &&
                    !part.trim().isEmpty() &&
                    !part.equalsIgnoreCase("null")) {

                if (location.length() > 0) {
                    location.append(", ");
                }

                location.append(part.trim());
            }
        }

        return location.length() == 0
                ? "Location not specified"
                : location.toString();
    }

    private String formatSalary(JobModel job) {

        String min = job.getMinSalary();
        String max = job.getMaxSalary();

        boolean hasMin = isValidNumber(min);
        boolean hasMax = isValidNumber(max);

        if (!hasMin && !hasMax) {
            return "Salary: Not disclosed";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(
                Locale.forLanguageTag("en-IN")
        );

        String salary;

        if (hasMin && hasMax) {
            salary = "₹" + formatter.format(Double.parseDouble(min))
                    + " - ₹" + formatter.format(Double.parseDouble(max));
        } else if (hasMin) {
            salary = "From ₹" + formatter.format(Double.parseDouble(min));
        } else {
            salary = "Up to ₹" + formatter.format(Double.parseDouble(max));
        }

        String period = job.getSalaryPeriod();

        if (period != null &&
                !period.trim().isEmpty() &&
                !period.equalsIgnoreCase("null")) {

            salary += " " + period;
        }

        return salary;
    }

    private boolean isValidNumber(String value) {

        if (value == null ||
                value.trim().isEmpty() ||
                value.equalsIgnoreCase("null")) {
            return false;
        }

        try {
            return Double.parseDouble(value) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String formatDate(String dateString) {

        if (dateString == null ||
                dateString.trim().isEmpty() ||
                dateString.equalsIgnoreCase("null")) {
            return "Not specified";
        }

        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateString);

            if (date != null) {
                return outputFormat.format(date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView tvJobTitle, tvJobCategory, tvJobOpenings;
        TextView tvJobDepartment, tvJobLocation;
        TextView tvJobExperience, tvJobWorkMode;
        TextView tvJobEmployment, tvJobSalary;
        TextView tvJobSkills, tvJobDeadline, tvJobStatus;
        MaterialCardView Job_card;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvJobCategory = itemView.findViewById(R.id.tv_job_category);
            tvJobOpenings = itemView.findViewById(R.id.tv_job_openings);
            tvJobDepartment = itemView.findViewById(R.id.tv_job_department);
            tvJobLocation = itemView.findViewById(R.id.tv_job_location);
            tvJobExperience = itemView.findViewById(R.id.tv_job_experience);
            tvJobWorkMode = itemView.findViewById(R.id.tv_job_work_mode);
            tvJobEmployment = itemView.findViewById(R.id.tv_job_employment);
            tvJobSalary = itemView.findViewById(R.id.tv_job_salary);
            tvJobSkills = itemView.findViewById(R.id.tv_job_skills);
            tvJobDeadline = itemView.findViewById(R.id.tv_job_deadline);
            tvJobStatus = itemView.findViewById(R.id.tv_job_status);
            Job_card = itemView.findViewById(R.id.Job_card);
        }
    }
}
