package com.example.smarthire.JobSeeker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthire.JobSeeker.Fragment.ViewCompleteJobActivity;
import com.example.smarthire.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AllJobsAdapter extends RecyclerView.Adapter<AllJobsAdapter.JobViewHolder>{

    private final Context context;
    private final ArrayList<JobModel2> jobList;

    public AllJobsAdapter(Context context, ArrayList<JobModel2> jobList) {
        this.context = context;
        this.jobList = new ArrayList<>(jobList);
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_new_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {

        JobModel2 job = jobList.get(position);

        holder.tvCategory.setText(valueOrFallback(job.getJobCategory(), "General"));
        holder.tvOpenings.setText(valueOrFallback(job.getOpenings(), "1") + " Openings");
        holder.tvTitle.setText(valueOrFallback(job.getJobTitle(), "Job Title"));
        holder.tvDepartment.setText(valueOrFallback(job.getDepartment(), "Department not specified"));

        String location = joinLocation(
                job.getJobLocation(), job.getState(), job.getCountry()
        );
        holder.tvLocation.setText(location);

        holder.tvExperience.setText(formatExperience(
                job.getMinExperience(), job.getMaxExperience()
        ));

        holder.tvWorkMode.setText(valueOrFallback(job.getWorkMode(), "Not specified"));

        String employment = valueOrFallback(job.getEmploymentType(), "Not specified");
        String qualification = valueOrFallback(
                job.getMinimumQualification(), "Qualification not specified"
        );
        holder.tvEmployment.setText(employment + "  •  " + qualification);

        holder.tvSalary.setText(formatSalary(job.getMinSalary(),
                job.getMaxSalary(), job.getSalaryPeriod()));

        holder.tvSkills.setText("Skills: " +
                valueOrFallback(job.getRequiredSkills(), "Not specified"));

        holder.tvDeadline.setText("Apply before: " +
                formatDate(job.getApplicationDeadline()));

        holder.tvStatus.setText(valueOrFallback(job.getJobStatus(), "Published"));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewCompleteJobActivity.class);
            intent.putExtra("job_id", job.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobs(ArrayList<JobModel2> newJobs) {
        jobList.clear();
        jobList.addAll(newJobs);
        notifyDataSetChanged();
    }

    private String valueOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return fallback;
        }
        return value.trim();
    }

    private String joinLocation(String location, String state, String country) {
        ArrayList<String> parts = new ArrayList<>();

        if (hasValue(location)) parts.add(location.trim());
        if (hasValue(state)) parts.add(state.trim());
        if (hasValue(country)) parts.add(country.trim());

        if (parts.isEmpty()) return "Location not specified";

        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (result.length() > 0) result.append(", ");
            result.append(part);
        }
        return result.toString();
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty()
                && !value.equalsIgnoreCase("null");
    }

    private String formatExperience(String min, String max) {
        min = valueOrFallback(min, "Not specified");
        max = valueOrFallback(max, "");

        if (min.equalsIgnoreCase("Fresher")) {
            return "Experience: Fresher";
        }

        if (max.isEmpty()) {
            return "Experience: " + min;
        }

        return "Experience: " + min + " - " + max + " years";
    }

    private String formatSalary(String min, String max, String period) {
        boolean hasMin = hasValue(min);
        boolean hasMax = hasValue(max);

        if (!hasMin && !hasMax) return "Salary: Not disclosed";

        String salary;
        if (hasMin && hasMax) {
            salary = formatAmount(min) + " - " + formatAmount(max);
        } else if (hasMin) {
            salary = "From " + formatAmount(min);
        } else {
            salary = "Up to " + formatAmount(max);
        }

        if (hasValue(period)) {
            salary += " " + period;
        }

        return salary;
    }

    private String formatAmount(String amount) {
        try {
            double number = Double.parseDouble(amount);
            DecimalFormat formatter = new DecimalFormat("#,##,##0.##");
            return "₹" + formatter.format(number);
        } catch (Exception e) {
            return amount;
        }
    }

    private String formatDate(String inputDate) {
        if (!hasValue(inputDate)) return "Not specified";

        try {
            SimpleDateFormat input = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());
            Date date = input.parse(inputDate);

            SimpleDateFormat output = new SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault());

            return date != null ? output.format(date) : inputDate;
        } catch (Exception e) {
            return inputDate;
        }
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvOpenings, tvTitle, tvDepartment;
        TextView tvLocation, tvExperience, tvWorkMode, tvEmployment;
        TextView tvSalary, tvSkills, tvDeadline, tvStatus;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tv_job_category);
            tvOpenings = itemView.findViewById(R.id.tv_job_openings);
            tvTitle = itemView.findViewById(R.id.tv_job_title);
            tvDepartment = itemView.findViewById(R.id.tv_job_department);
            tvLocation = itemView.findViewById(R.id.tv_job_location);
            tvExperience = itemView.findViewById(R.id.tv_job_experience);
            tvWorkMode = itemView.findViewById(R.id.tv_job_work_mode);
            tvEmployment = itemView.findViewById(R.id.tv_job_employment);
            tvSalary = itemView.findViewById(R.id.tv_job_salary);
            tvSkills = itemView.findViewById(R.id.tv_job_skills);
            tvDeadline = itemView.findViewById(R.id.tv_job_deadline);
            tvStatus = itemView.findViewById(R.id.tv_job_status);
        }
    }
}
