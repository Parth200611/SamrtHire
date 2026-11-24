
package com.example.smarthire.JobSeeker.Fragment.Application;

import org.json.JSONObject;

public class MyApplication {

    private String id;
    private String jobId;
    private String jobTitle;
    private String recruiterEmail;
    private String applicationStatus;
    private String appliedAt;

    private String interviewDate;
    private String interviewTime;
    private String interviewMode;
    private String interviewLocation;
    private String interviewLink;

    public MyApplication(JSONObject object) {

        id = object.optString("id", "");
        jobId = object.optString("job_id", "");
        jobTitle = object.optString("job_title", "");
        recruiterEmail = object.optString("hr_mail", "");
        applicationStatus = object.optString("application_status", "Applied");
        appliedAt = object.optString("applied_at", "");

        interviewDate = object.optString("interview_date", "");
        interviewTime = object.optString("interview_time", "");
        interviewMode = object.optString("interview_mode", "");
        interviewLocation = object.optString("interview_location", "");
        interviewLink = object.optString("interview_link", "");
    }

    public String getId() {
        return id;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public String getAppliedAt() {
        return appliedAt;
    }

    public String getInterviewDate() {
        return interviewDate;
    }

    public String getInterviewTime() {
        return interviewTime;
    }

    public String getInterviewMode() {
        return interviewMode;
    }

    public String getInterviewLocation() {
        return interviewLocation;
    }

    public String getInterviewLink() {
        return interviewLink;
    }
}
