
package com.example.smarthire.JobSeeker.Fragment;

public class JobModel {

    private String id;
    private String recruiterId;
    private String recruiterEmail;
    private String jobTitle;
    private String department;
    private String jobCategory;
    private String openings;
    private String jobDescription;
    private String responsibilities;
    private String requiredSkills;
    private String softSkills;
    private String minExperience;
    private String maxExperience;
    private String minimumQualification;
    private String preferredCourse;
    private String employmentType;
    private String workMode;
    private String jobLocation;
    private String state;
    private String country;
    private String minSalary;
    private String maxSalary;
    private String salaryPeriod;
    private String benefits;
    private String applicationDeadline;
    private String hrEmail;
    private String companyEmail;
    private String visibility;
    private String jobStatus;
    private String createdAt;
    private String updatedAt;

    public JobModel(
            String id,
            String recruiterId,
            String recruiterEmail,
            String jobTitle,
            String department,
            String jobCategory,
            String openings,
            String jobDescription,
            String responsibilities,
            String requiredSkills,
            String softSkills,
            String minExperience,
            String maxExperience,
            String minimumQualification,
            String preferredCourse,
            String employmentType,
            String workMode,
            String jobLocation,
            String state,
            String country,
            String minSalary,
            String maxSalary,
            String salaryPeriod,
            String benefits,
            String applicationDeadline,
            String hrEmail,
            String companyEmail,
            String visibility,
            String jobStatus,
            String createdAt,
            String updatedAt
    ) {
        this.id = id;
        this.recruiterId = recruiterId;
        this.recruiterEmail = recruiterEmail;
        this.jobTitle = jobTitle;
        this.department = department;
        this.jobCategory = jobCategory;
        this.openings = openings;
        this.jobDescription = jobDescription;
        this.responsibilities = responsibilities;
        this.requiredSkills = requiredSkills;
        this.softSkills = softSkills;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.minimumQualification = minimumQualification;
        this.preferredCourse = preferredCourse;
        this.employmentType = employmentType;
        this.workMode = workMode;
        this.jobLocation = jobLocation;
        this.state = state;
        this.country = country;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.salaryPeriod = salaryPeriod;
        this.benefits = benefits;
        this.applicationDeadline = applicationDeadline;
        this.hrEmail = hrEmail;
        this.companyEmail = companyEmail;
        this.visibility = visibility;
        this.jobStatus = jobStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getRecruiterId() { return recruiterId; }
    public String getRecruiterEmail() { return recruiterEmail; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public String getJobCategory() { return jobCategory; }
    public String getOpenings() { return openings; }
    public String getJobDescription() { return jobDescription; }
    public String getResponsibilities() { return responsibilities; }
    public String getRequiredSkills() { return requiredSkills; }
    public String getSoftSkills() { return softSkills; }
    public String getMinExperience() { return minExperience; }
    public String getMaxExperience() { return maxExperience; }
    public String getMinimumQualification() { return minimumQualification; }
    public String getPreferredCourse() { return preferredCourse; }
    public String getEmploymentType() { return employmentType; }
    public String getWorkMode() { return workMode; }
    public String getJobLocation() { return jobLocation; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getMinSalary() { return minSalary; }
    public String getMaxSalary() { return maxSalary; }
    public String getSalaryPeriod() { return salaryPeriod; }
    public String getBenefits() { return benefits; }
    public String getApplicationDeadline() { return applicationDeadline; }
    public String getHrEmail() { return hrEmail; }
    public String getCompanyEmail() { return companyEmail; }
    public String getVisibility() { return visibility; }
    public String getJobStatus() { return jobStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
