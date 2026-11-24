
package com.example.smarthire.JobSeeker.Fragment.Application;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthire.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyApplicationAdapter extends RecyclerView.Adapter<MyApplicationAdapter.ViewHolder> {

    private final Context context;
    private final List<MyApplication> applicationList;

    public MyApplicationAdapter(Context context, List<MyApplication> applicationList) {
        this.context = context;
        this.applicationList = new ArrayList<>(applicationList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_application, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyApplication application = applicationList.get(position);

        holder.tvJobTitle.setText(
                valueOrDefault(application.getJobTitle(), "Job Title Not Available")
        );

        holder.tvRecruiter.setText(
                "Recruiter: " +
                        valueOrDefault(application.getRecruiterEmail(), "Not available")
        );

        holder.tvStatus.setText(
                valueOrDefault(application.getApplicationStatus(), "Applied")
        );

        holder.tvAppliedDate.setText(
                "Applied on: " + formatDate(application.getAppliedAt())
        );

        // Status color
        setStatusColor(holder.tvStatus, application.getApplicationStatus());

        // Interview details
        String interviewDate = application.getInterviewDate();
        String interviewTime = application.getInterviewTime();
        String interviewMode = application.getInterviewMode();

        boolean hasInterviewDate =
                !TextUtils.isEmpty(interviewDate) &&
                        !interviewDate.equals("null") &&
                        !interviewDate.equals("0000-00-00");

        boolean hasInterview =
                hasInterviewDate ||
                        (!TextUtils.isEmpty(interviewTime) &&
                                !interviewTime.equals("null")) ||
                        ("Online".equalsIgnoreCase(interviewMode) ||
                                "Offline".equalsIgnoreCase(interviewMode) ||
                                "Phone".equalsIgnoreCase(interviewMode));

        if (hasInterview) {

            holder.layoutInterview.setVisibility(View.VISIBLE);

            holder.tvInterviewDate.setText(
                    "Date: " + (hasInterviewDate
                            ? formatDate(interviewDate)
                            : "Not specified")
            );

            holder.tvInterviewTime.setText(
                    "Time: " + formatTime(interviewTime)
            );

            holder.tvInterviewMode.setText(
                    "Mode: " + valueOrDefault(interviewMode, "Not specified")
            );

            // Interview location
            String location = application.getInterviewLocation();

            if (!TextUtils.isEmpty(location) && !location.equals("null")) {

                holder.tvInterviewLocation.setVisibility(View.VISIBLE);
                holder.tvInterviewLocation.setText("Location: " + location);

            } else {

                holder.tvInterviewLocation.setVisibility(View.GONE);
            }

            // Interview link
            String meetingLink = application.getInterviewLink();

            if (!TextUtils.isEmpty(meetingLink) &&
                    !meetingLink.equals("null")) {

                holder.tvInterviewLink.setVisibility(View.VISIBLE);
                holder.tvInterviewLink.setText("Open Meeting Link");

                holder.tvInterviewLink.setOnClickListener(v -> {

                    try {

                        String url = meetingLink.trim();

                        if (!url.startsWith("https://") &&
                                !url.startsWith("http://")) {
                            url = "https://" + url;
                        }

                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(url)
                        );

                        context.startActivity(intent);

                    } catch (Exception e) {

                        Toast.makeText(
                                context,
                                "Unable to open meeting link.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });

            } else {

                holder.tvInterviewLink.setVisibility(View.GONE);
                holder.tvInterviewLink.setOnClickListener(null);
            }

        } else {

            holder.layoutInterview.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    // Update list after API response
    public void updateList(List<MyApplication> newList) {

        applicationList.clear();
        applicationList.addAll(newList);

        notifyDataSetChanged();
    }

    // Set status background and text color
    private void setStatusColor(TextView textView, String status) {

        String value = status == null ? "" : status.trim();

        int backgroundColor;
        int textColor;

        switch (value.toLowerCase(Locale.ROOT)) {

            case "shortlisted":
                backgroundColor = Color.parseColor("#DCFCE7");
                textColor = Color.parseColor("#15803D");
                break;

            case "selected":
                backgroundColor = Color.parseColor("#D1FAE5");
                textColor = Color.parseColor("#047857");
                break;

            case "rejected":
                backgroundColor = Color.parseColor("#FEE2E2");
                textColor = Color.parseColor("#DC2626");
                break;

            case "under review":
                backgroundColor = Color.parseColor("#FEF3C7");
                textColor = Color.parseColor("#B45309");
                break;

            case "interview scheduled":
                backgroundColor = Color.parseColor("#EDE9FE");
                textColor = Color.parseColor("#6D28D9");
                break;

            default:
                backgroundColor = Color.parseColor("#DBEAFE");
                textColor = Color.parseColor("#1D4ED8");
                break;
        }

        textView.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(backgroundColor)
        );

        textView.setTextColor(textColor);
    }

    private String valueOrDefault(String value, String defaultValue) {

        if (TextUtils.isEmpty(value) || value.equals("null")) {
            return defaultValue;
        }

        return value;
    }

    // Format database date: yyyy-MM-dd HH:mm:ss
    private String formatDate(String dateString) {

        if (TextUtils.isEmpty(dateString) || dateString.equals("null")) {
            return "Not available";
        }

        try {

            SimpleDateFormat inputFormat;

            if (dateString.contains(" ")) {
                inputFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                );
            } else {
                inputFormat = new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );
            }

            Date date = inputFormat.parse(dateString);

            if (date != null) {

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                return outputFormat.format(date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;
    }

    // Format interview time
    private String formatTime(String timeString) {

        if (TextUtils.isEmpty(timeString) ||
                timeString.equals("null")) {
            return "Not specified";
        }

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Date date = inputFormat.parse(timeString);

            if (date != null) {

                SimpleDateFormat outputFormat =
                        new SimpleDateFormat("hh:mm a", Locale.getDefault());

                return outputFormat.format(date);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeString;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvJobTitle;
        TextView tvRecruiter;
        TextView tvStatus;
        TextView tvAppliedDate;

        LinearLayout layoutInterview;

        TextView tvInterviewDate;
        TextView tvInterviewTime;
        TextView tvInterviewMode;
        TextView tvInterviewLocation;
        TextView tvInterviewLink;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvJobTitle = itemView.findViewById(R.id.tv_application_job_title);
            tvRecruiter = itemView.findViewById(R.id.tv_application_recruiter);
            tvStatus = itemView.findViewById(R.id.tv_application_status);
            tvAppliedDate = itemView.findViewById(R.id.tv_application_applied_date);

            layoutInterview = itemView.findViewById(R.id.layout_interview_details);

            tvInterviewDate = itemView.findViewById(R.id.tv_interview_date);
            tvInterviewTime = itemView.findViewById(R.id.tv_interview_time);
            tvInterviewMode = itemView.findViewById(R.id.tv_interview_mode);
            tvInterviewLocation = itemView.findViewById(R.id.tv_interview_location);
            tvInterviewLink = itemView.findViewById(R.id.tv_interview_link);
        }
    }
}
