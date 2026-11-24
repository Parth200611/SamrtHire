
package com.example.smarthire.JobSeeker.Fragment.Explore;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecruiterAdapter extends RecyclerView.Adapter<RecruiterAdapter.RecruiterViewHolder> {

    private final List<RecruiterModel> recruiterList;
    private final List<RecruiterModel> filteredList;

    private final OnRecruiterClickListener listener;

    public interface OnRecruiterClickListener {
        void onViewProfile(RecruiterModel recruiter);
    }

    public RecruiterAdapter(
            List<RecruiterModel> recruiterList,
            OnRecruiterClickListener listener
    ) {
        this.recruiterList = new ArrayList<>(recruiterList);
        this.filteredList = new ArrayList<>(recruiterList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecruiterViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_explore_recruiter,
                        parent,
                        false
                );

        return new RecruiterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecruiterViewHolder holder,
            int position
    ) {

        RecruiterModel recruiter = filteredList.get(position);

        holder.tvName.setText(
                valueOrDefault(recruiter.getFullName(), "Recruiter")
        );

        holder.tvDesignation.setText(
                valueOrDefault(recruiter.getDesignation(), "HR Professional")
        );

        holder.tvCompany.setText(
                valueOrDefault(recruiter.getCompanyName(), "Company not available")
        );

        holder.tvIndustry.setText(
                valueOrDefault(recruiter.getIndustry(), "Industry not specified")
        );

        String experience = recruiter.getExperienceYears();

        if (experience == null || experience.trim().isEmpty()) {

            holder.tvExperience.setText("Experience not specified");

        } else {

            try {

                double years = Double.parseDouble(experience);

                String formatted = years == (long) years
                        ? String.valueOf((long) years)
                        : String.valueOf(years);

                holder.tvExperience.setText(formatted + " years experience");

            } catch (NumberFormatException e) {

                holder.tvExperience.setText(experience + " experience");
            }
        }

        holder.tvLocation.setText(buildLocation(recruiter));

        if ("Verified".equalsIgnoreCase(recruiter.getVerificationStatus())) {

            holder.tvVerification.setVisibility(View.VISIBLE);
            holder.tvVerification.setText("✓ Verified");
            holder.tvVerification.setTextColor(0xFF047857);
            holder.tvVerification.setBackgroundColor(0xFFECFDF5);

        } else {

            holder.tvVerification.setVisibility(View.VISIBLE);
            holder.tvVerification.setText("Not verified");
            holder.tvVerification.setTextColor(0xFFB45309);
            holder.tvVerification.setBackgroundColor(0xFFFFF7ED);
        }

        String photo = recruiter.getProfilePhoto();

        if (photo != null && !photo.trim().isEmpty()) {

            String imageUrl;

            if (photo.startsWith("http://") || photo.startsWith("https://")) {
                imageUrl = photo;
            } else {
                imageUrl = Urls.urls +
                        (photo.startsWith("/") ? photo.substring(1) : photo);
            }

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_smarthire_logo)
                    .error(R.drawable.ic_smarthire_logo)
                    .into(holder.ivPhoto);

        } else {

            holder.ivPhoto.setImageResource(R.drawable.ic_smarthire_logo);
        }

        holder.btnViewProfile.setOnClickListener(v -> {

            String recruiterId = recruiter.getId();

            Intent intent = new Intent(
                    holder.itemView.getContext(),
                    ViewFullDetailActivity.class
            );

            intent.putExtra("recruiter_id", recruiterId);

            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onViewProfile(recruiter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String query) {

        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {

            filteredList.addAll(recruiterList);

        } else {

            String search = query.toLowerCase(Locale.ROOT).trim();

            for (RecruiterModel recruiter : recruiterList) {

                String name = safe(recruiter.getFullName());
                String designation = safe(recruiter.getDesignation());
                String company = safe(recruiter.getCompanyName());
                String industry = safe(recruiter.getIndustry());
                String city = safe(recruiter.getCity());
                String state = safe(recruiter.getState());

                if (name.contains(search)
                        || designation.contains(search)
                        || company.contains(search)
                        || industry.contains(search)
                        || city.contains(search)
                        || state.contains(search)) {

                    filteredList.add(recruiter);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void updateData(List<RecruiterModel> newList) {

        // The adapter maintains its own copy of the original data.
        recruiterList.clear();
        recruiterList.addAll(newList);

        filteredList.clear();
        filteredList.addAll(recruiterList);

        notifyDataSetChanged();
    }

    private String buildLocation(RecruiterModel recruiter) {

        StringBuilder location = new StringBuilder();

        addLocationPart(location, recruiter.getCity());
        addLocationPart(location, recruiter.getState());
        addLocationPart(location, recruiter.getCountry());

        if (location.length() == 0) {
            return "Location not available";
        }

        return location.toString();
    }

    private void addLocationPart(StringBuilder builder, String value) {

        if (value != null && !value.trim().isEmpty()) {

            if (builder.length() > 0) {
                builder.append(", ");
            }

            builder.append(value.trim());
        }
    }

    private String valueOrDefault(String value, String defaultValue) {

        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value.toLowerCase(Locale.ROOT);
    }

    static class RecruiterViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;

        TextView tvName;
        TextView tvDesignation;
        TextView tvCompany;
        TextView tvIndustry;
        TextView tvExperience;
        TextView tvLocation;
        TextView tvVerification;

        MaterialButton btnViewProfile;

        public RecruiterViewHolder(@NonNull View itemView) {

            super(itemView);

            ivPhoto = itemView.findViewById(R.id.iv_recruiter_photo);

            tvName = itemView.findViewById(R.id.tv_recruiter_name);

            tvDesignation = itemView.findViewById(R.id.tv_recruiter_designation);

            tvCompany = itemView.findViewById(R.id.tv_recruiter_company);

            tvIndustry = itemView.findViewById(R.id.tv_recruiter_industry);

            tvExperience = itemView.findViewById(R.id.tv_recruiter_experience);

            tvLocation = itemView.findViewById(R.id.tv_recruiter_location);

            tvVerification = itemView.findViewById(R.id.tv_recruiter_verification);

            btnViewProfile = itemView.findViewById(R.id.btn_view_recruiter);
        }
    }
}
