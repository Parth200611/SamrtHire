# SmartHire – AI-Powered Job Portal with Resume Parsing and Smart Matching

## 📌 Project Overview

SmartHire is an AI-powered job portal application designed to connect job seekers with recruiters through an intelligent and efficient recruitment platform.

The application allows job seekers to create professional profiles, upload resumes, search for job opportunities, and apply for suitable positions. Recruiters can register, post job openings, manage applications, and identify suitable candidates.

SmartHire aims to simplify the recruitment process by integrating resume parsing and skill-based matching to improve the accuracy and efficiency of candidate selection.

## 🎯 Objectives

- Develop a user-friendly Android-based job portal.
- Connect job seekers and recruiters on a single platform.
- Provide efficient job searching and application management.
- Implement resume parsing to extract candidate information.
- Introduce AI-based skill matching and candidate ranking.
- Simplify recruitment and candidate shortlisting.

## 🚀 Key Features

### 👨‍💻 Job Seeker Module
- User registration and secure login.
- Create and manage professional profiles.
- Add educational qualifications and technical skills.
- Upload and manage resumes.
- Search and explore available jobs.
- Apply for suitable job opportunities.
- Track application status.
- Receive job recommendations.

### 🏢 Recruiter Module
- Recruiter registration and login.
- Manage recruiter profiles.
- Create and publish job vacancies.
- Manage posted job listings.
- View candidate profiles and resumes.
- Review and manage job applications.
- Shortlist suitable candidates.
- Schedule interviews.
- AI-based candidate matching and ranking.

### ⚙️ Admin Module
- Manage job seekers and recruiters.
- Monitor job postings.
- Manage platform activities.
- Verify recruiter accounts.
- Maintain platform data and reports.

## 🤖 AI-Powered Features

- **Resume Parsing:** Extracts important information such as skills, education, and experience from uploaded resumes.
- **Smart Job Matching:** Matches candidate skills and qualifications with job requirements.
- **Candidate Ranking:** Helps recruiters identify candidates based on matching scores.
- **Intelligent Recommendations:** Suggests relevant job opportunities to job seekers.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Android application development |
| XML | User interface design |
| Android Studio | Development environment |
| PHP | Backend API development |
| MySQL | Database management |
| XAMPP | Local server environment |
| Volley | API integration |
| Python | AI/ML and resume processing |
| Git & GitHub | Version control and project management |

## 🏗️ Project Architecture

The project follows a client-server architecture.

1. **Frontend:** Android application developed using Java and XML.
2. **Backend:** PHP REST APIs handle requests and business logic.
3. **Database:** MySQL stores user details, recruiter information, job postings, and applications.
4. **AI/ML Layer:** Python-based processing supports resume parsing and intelligent matching.

## 📂 Project Structure

```text
SmartHire/
│
├── app/
│   └── src/main/
│       ├── java/com/example/smarthire/
│       │   ├── JobSeeker/
│       │   ├── Recruiter/
│       │   ├── Admin/
│       │   ├── Adapters/
│       │   ├── Models/
│       │   └── Utils/
│       │
│       └── res/
│           ├── layout/
│           ├── drawable/
│           ├── menu/
│           └── values/
│
├── SmartHireAPI/
│   ├── data.php
│   ├── register.php
│   ├── login.php
│   ├── get_jobs.php
│   └── apply_job.php
│
├── AI_Module/
│   ├── resume_parser.py
│   └── job_matching.py
│
└── README.md
```

## 🗄️ Database

SmartHire uses MySQL for storing and managing application data.

Major database entities include:

- Job Seekers
- Recruiters
- Admin
- Jobs
- Applications
- Saved Jobs
- Interviews
- Resume Details

## ⚙️ Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/SmartHire.git
```

### 2. Open Android Project

- Open Android Studio.
- Select Open Project.
- Choose the cloned SmartHire folder.
- Allow Gradle synchronization to complete.

### 3. Configure XAMPP

- Install and start Apache and MySQL.
- Open phpMyAdmin.
- Create a database named `smartiredb`.
- Import the provided SQL database file.
- Place PHP API files inside the XAMPP `htdocs` directory.

### 4. Configure API URL

Update the API base URL in your Android project.

For Android Emulator:

```java
http://10.0.2.2/SmartHireAPI/
```

For a physical Android device, use your computer's local network IP address.

### 5. Run the Application

- Connect an Android device or start an emulator.
- Build and run the application from Android Studio.

## 🔐 Security

- Password hashing for secure password storage.
- Server-side input validation.
- Prepared SQL statements to prevent SQL injection.
- Role-based access for different users.
- Secure API request handling.

## 🔮 Future Enhancements

- Advanced AI-based resume analysis.
- Improved semantic job matching.
- Automated interview scheduling.
- Real-time notifications.
- Online interview integration.
- Advanced recruiter analytics.
- Cloud deployment and scalability.

## 👨‍💻 Developer

**Project Name:** SmartHire

**Project Type:** Android Application / AI-Powered Job Portal

**Domain:** Android Development, Web APIs, Database Management, Artificial Intelligence and Machine Learning.

## 📄 License

This project is developed for academic and educational purposes. All rights reserved to the respective project developers.

---

⭐ If you find this project interesting, consider giving the repository a star!
