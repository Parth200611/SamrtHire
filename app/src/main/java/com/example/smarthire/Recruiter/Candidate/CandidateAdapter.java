
package com.example.smarthire.Recruiter.Candidate;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smarthire.Coman.Urls;
import com.example.smarthire.R;
import com.example.smarthire.Recruiter.Application.ViewCandidate;
import com.example.smarthire.Recruiter.Application.viewcan;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private final Context context;
    private final List<Candidate> candidateList;
    private final List<Candidate> filteredList;

    public CandidateAdapter(Context context, List<Candidate> candidateList) {
        this.context = context;
        this.candidateList = new ArrayList<>(candidateList);
        this.filteredList = new ArrayList<>(candidateList);
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_candidate, parent, false);

        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {

        Candidate candidate = filteredList.get(position);

        holder.tvName.setText(valueOrDefault(candidate.getFull_name(), "Unknown Candidate"));
        holder.tvRole.setText(valueOrDefault(candidate.getDesired_job_role(), "Role not specified"));

        String location = joinValues(candidate.getCity(), candidate.getState());
        holder.tvLocation.setText(location.isEmpty() ? "Location not specified" : location);

        String experience;

        if ("Fresher".equalsIgnoreCase(candidate.getExperience_type())) {
            experience = "Fresher";
        } else if (!TextUtils.isEmpty(candidate.getExperience_years())) {
            experience = candidate.getExperience_years() + " years";
        } else {
            experience = valueOrDefault(candidate.getExperience_type(), "Not specified");
        }

        holder.tvExperience.setText(experience);

        holder.tvQualification.setText(
                valueOrDefault(candidate.getHighest_qualification(), "Not specified")
        );

        String skills = candidate.getTechnical_skills();

        if (TextUtils.isEmpty(skills)) {
            skills = candidate.getSoft_skills();
        } else if (!TextUtils.isEmpty(candidate.getSoft_skills())) {
            skills = skills + " • " + candidate.getSoft_skills();
        }

        holder.tvSkills.setText(
                valueOrDefault(skills, "Skills not added")
        );

        holder.tvCompletion.setText(
                "Profile: " + valueOrDefault(candidate.getProfile_completion(), "0") + "% complete"
        );

        // Profile image
        String imagePath = candidate.getProfile_photo();

        if (!TextUtils.isEmpty(imagePath)) {

            String imageUrl = imagePath;

            if (!imagePath.startsWith("http://") &&
                    !imagePath.startsWith("https://")) {

                imageUrl = Urls.urls + imagePath;
            }

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_smarthire_logo)
                    .error(R.drawable.ic_smarthire_logo)
                    .into(holder.ivProfile);

        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_smarthire_logo);
        }

        // View candidate profile
        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, viewcan.class);

            intent.putExtra("Can_email", candidate.getEmail());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Search candidates
    public void filter(String query) {

        filteredList.clear();

        if (TextUtils.isEmpty(query)) {

            filteredList.addAll(candidateList);

        } else {

            String searchText = query.toLowerCase().trim();

            for (Candidate candidate : candidateList) {

                String name = safe(candidate.getFull_name());
                String role = safe(candidate.getDesired_job_role());
                String email = safe(candidate.getEmail());
                String city = safe(candidate.getCity());
                String state = safe(candidate.getState());
                String technicalSkills = safe(candidate.getTechnical_skills());
                String softSkills = safe(candidate.getSoft_skills());
                String qualification = safe(candidate.getHighest_qualification());

                if (name.contains(searchText) ||
                        role.contains(searchText) ||
                        email.contains(searchText) ||
                        city.contains(searchText) ||
                        state.contains(searchText) ||
                        technicalSkills.contains(searchText) ||
                        softSkills.contains(searchText) ||
                        qualification.contains(searchText)) {

                    filteredList.add(candidate);
                }
            }
        }

        notifyDataSetChanged();
    }

    // Update list after API response
    public void updateList(List<Candidate> newList) {

        candidateList.clear();
        filteredList.clear();

        candidateList.addAll(newList);
        filteredList.addAll(newList);

        notifyDataSetChanged();
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    private String valueOrDefault(String value, String defaultValue) {
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    private String joinValues(String first, String second) {

        String a = first == null ? "" : first.trim();
        String b = second == null ? "" : second.trim();

        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;

        return a + ", " + b;
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivProfile;

        TextView tvName, tvRole, tvLocation;
        TextView tvExperience, tvQualification;
        TextView tvSkills, tvCompletion;

        MaterialButton btnView;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.iv_candidate_profile);

            tvName = itemView.findViewById(R.id.tv_candidate_name);
            tvRole = itemView.findViewById(R.id.tv_candidate_role);
            tvLocation = itemView.findViewById(R.id.tv_candidate_location);

            tvExperience = itemView.findViewById(R.id.tv_candidate_experience);
            tvQualification = itemView.findViewById(R.id.tv_candidate_qualification);

            tvSkills = itemView.findViewById(R.id.tv_candidate_skills);
            tvCompletion = itemView.findViewById(R.id.tv_candidate_completion);

            btnView = itemView.findViewById(R.id.btn_view_candidate);
        }
    }
}
