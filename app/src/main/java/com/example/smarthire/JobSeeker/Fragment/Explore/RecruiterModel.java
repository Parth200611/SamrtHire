
package com.example.smarthire.JobSeeker.Fragment.Explore;

public class RecruiterModel {

    private String id;
    private String fullName;
    private String email;
    private String mobile;
    private String profilePhoto;
    private String designation;
    private String department;
    private String experienceYears;
    private String companyName;
    private String industry;
    private String companyWebsite;
    private String companyDescription;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String verificationStatus;
    private String accountStatus;
    private String createdAt;
    private String updatedAt;

    public RecruiterModel() {
    }

    public RecruiterModel(
            String id,
            String fullName,
            String email,
            String mobile,
            String profilePhoto,
            String designation,
            String department,
            String experienceYears,
            String companyName,
            String industry,
            String companyWebsite,
            String companyDescription,
            String address,
            String city,
            String state,
            String country,
            String pincode,
            String verificationStatus,
            String accountStatus,
            String createdAt,
            String updatedAt
    ) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.profilePhoto = profilePhoto;
        this.designation = designation;
        this.department = department;
        this.experienceYears = experienceYears;
        this.companyName = companyName;
        this.industry = industry;
        this.companyWebsite = companyWebsite;
        this.companyDescription = companyDescription;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.verificationStatus = verificationStatus;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDepartment() {
        return department;
    }

    public String getExperienceYears() {
        return experienceYears;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPincode() {
        return pincode;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
