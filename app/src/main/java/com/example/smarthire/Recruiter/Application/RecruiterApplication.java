
package com.example.smarthire.Recruiter.Application;

public class RecruiterApplication {

    private String id, job_id, job_title, hr_mail;
    private String user_id, full_name, email, mobile;
    private String state, city, cover_letter;
    private String application_status, recruiter_remarks;
    private String interview_date, interview_time, interview_mode;
    private String interview_location, interview_link;
    private String applied_at, updated_at;

    public RecruiterApplication(
            String id,
            String job_id,
            String job_title,
            String hr_mail,
            String user_id,
            String full_name,
            String email,
            String mobile,
            String state,
            String city,
            String cover_letter,
            String application_status,
            String recruiter_remarks,
            String interview_date,
            String interview_time,
            String interview_mode,
            String interview_location,
            String interview_link,
            String applied_at,
            String updated_at) {

        this.id = id;
        this.job_id = job_id;
        this.job_title = job_title;
        this.hr_mail = hr_mail;
        this.user_id = user_id;
        this.full_name = full_name;
        this.email = email;
        this.mobile = mobile;
        this.state = state;
        this.city = city;
        this.cover_letter = cover_letter;
        this.application_status = application_status;
        this.recruiter_remarks = recruiter_remarks;
        this.interview_date = interview_date;
        this.interview_time = interview_time;
        this.interview_mode = interview_mode;
        this.interview_location = interview_location;
        this.interview_link = interview_link;
        this.applied_at = applied_at;
        this.updated_at = updated_at;
    }

    public String getId() { return id; }
    public String getJob_id() { return job_id; }
    public String getJob_title() { return job_title; }
    public String getHr_mail() { return hr_mail; }
    public String getUser_id() { return user_id; }
    public String getFull_name() { return full_name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getCover_letter() { return cover_letter; }
    public String getApplication_status() { return application_status; }
    public String getRecruiter_remarks() { return recruiter_remarks; }
    public String getInterview_date() { return interview_date; }
    public String getInterview_time() { return interview_time; }
    public String getInterview_mode() { return interview_mode; }
    public String getInterview_location() { return interview_location; }
    public String getInterview_link() { return interview_link; }
    public String getApplied_at() { return applied_at; }
    public String getUpdated_at() { return updated_at; }
}
