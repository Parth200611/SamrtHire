
package com.example.smarthire.Recruiter.Candidate;

import org.json.JSONObject;

public class Candidate {

    private String id, full_name, email, mobile, profile_photo;
    private String city, state, date_of_birth;

    private String highest_qualification, course_name, college_name;
    private String graduation_year, percentage, education_status;

    private String technical_skills, soft_skills;

    private String experience_type, experience_years;
    private String previous_company, previous_job_title;

    private String projects, certifications, resume_path;

    private String linkedin_url, github_url, portfolio_url;

    private String desired_job_role, preferred_industry, preferred_location;
    private String expected_salary_min, expected_salary_max;
    private String work_mode, employment_type;

    private String profile_completion, created_at, updated_at;

    public Candidate(JSONObject json) {

        id = json.optString("id", "");
        full_name = json.optString("full_name", "");
        email = json.optString("email", "");
        mobile = json.optString("mobile", "");
        profile_photo = json.optString("profile_photo", "");

        city = json.optString("city", "");
        state = json.optString("state", "");
        date_of_birth = json.optString("date_of_birth", "");

        highest_qualification = json.optString("highest_qualification", "");
        course_name = json.optString("course_name", "");
        college_name = json.optString("college_name", "");
        graduation_year = json.optString("graduation_year", "");
        percentage = json.optString("percentage", "");
        education_status = json.optString("education_status", "");

        technical_skills = json.optString("technical_skills", "");
        soft_skills = json.optString("soft_skills", "");

        experience_type = json.optString("experience_type", "");
        experience_years = json.optString("experience_years", "");
        previous_company = json.optString("previous_company", "");
        previous_job_title = json.optString("previous_job_title", "");

        projects = json.optString("projects", "");
        certifications = json.optString("certifications", "");
        resume_path = json.optString("resume_path", "");

        linkedin_url = json.optString("linkedin_url", "");
        github_url = json.optString("github_url", "");
        portfolio_url = json.optString("portfolio_url", "");

        desired_job_role = json.optString("desired_job_role", "");
        preferred_industry = json.optString("preferred_industry", "");
        preferred_location = json.optString("preferred_location", "");

        expected_salary_min = json.optString("expected_salary_min", "");
        expected_salary_max = json.optString("expected_salary_max", "");

        work_mode = json.optString("work_mode", "");
        employment_type = json.optString("employment_type", "");

        profile_completion = json.optString("profile_completion", "");
        created_at = json.optString("created_at", "");
        updated_at = json.optString("updated_at", "");
    }

    public String getId() { return id; }
    public String getFull_name() { return full_name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getProfile_photo() { return profile_photo; }

    public String getCity() { return city; }
    public String getState() { return state; }
    public String getDate_of_birth() { return date_of_birth; }

    public String getHighest_qualification() { return highest_qualification; }
    public String getCourse_name() { return course_name; }
    public String getCollege_name() { return college_name; }
    public String getGraduation_year() { return graduation_year; }
    public String getPercentage() { return percentage; }
    public String getEducation_status() { return education_status; }

    public String getTechnical_skills() { return technical_skills; }
    public String getSoft_skills() { return soft_skills; }

    public String getExperience_type() { return experience_type; }
    public String getExperience_years() { return experience_years; }
    public String getPrevious_company() { return previous_company; }
    public String getPrevious_job_title() { return previous_job_title; }

    public String getProjects() { return projects; }
    public String getCertifications() { return certifications; }
    public String getResume_path() { return resume_path; }

    public String getLinkedin_url() { return linkedin_url; }
    public String getGithub_url() { return github_url; }
    public String getPortfolio_url() { return portfolio_url; }

    public String getDesired_job_role() { return desired_job_role; }
    public String getPreferred_industry() { return preferred_industry; }
    public String getPreferred_location() { return preferred_location; }

    public String getExpected_salary_min() { return expected_salary_min; }
    public String getExpected_salary_max() { return expected_salary_max; }

    public String getWork_mode() { return work_mode; }
    public String getEmployment_type() { return employment_type; }

    public String getProfile_completion() { return profile_completion; }
    public String getCreated_at() { return created_at; }
    public String getUpdated_at() { return updated_at; }
}
