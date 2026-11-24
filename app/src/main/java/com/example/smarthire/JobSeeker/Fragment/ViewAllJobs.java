
package com.example.smarthire.JobSeeker.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smarthire.Coman.Urls;

import com.example.smarthire.JobSeeker.JobModel2;
import com.example.smarthire.R;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.example.smarthire.JobSeeker.AllJobsAdapter;

public class ViewAllJobs extends AppCompatActivity {

    private RecyclerView recyclerAllJobs;

    private TextView tvJobCount;
    private TextView tvEmptyMessage;

    private View layoutLoading;
    private View layoutEmpty;

    private EditText etSearchJob;
    private ImageView btnClearSearch;

    private Spinner spinnerSort;
    private ChipGroup chipGroupFilters;

    private RequestQueue requestQueue;

    private AllJobsAdapter adapter;

    private final ArrayList<JobModel2> allJobs = new ArrayList<>();
    private final ArrayList<JobModel2> filteredJobs = new ArrayList<>();

    private String selectedSort = "Latest First";
    private String selectedFilter = "All";

    private static final String TAG_JOBS = "VIEW_ALL_JOBS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_jobs2);

        initializeViews();
        setupRecyclerView();
        setupSorting();
        setupSearch();
        setupFilters();
        setupResetButton();

        requestQueue = Volley.newRequestQueue(this);

        fetchAllJobs();
    }

    // ---------------- INITIALIZE VIEWS ----------------

    private void initializeViews() {

        recyclerAllJobs = findViewById(R.id.recycler_all_jobs);

        tvJobCount = findViewById(R.id.tv_job_count);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);

        layoutLoading = findViewById(R.id.layout_loading);
        layoutEmpty = findViewById(R.id.layout_empty);

        etSearchJob = findViewById(R.id.et_search_job);
        btnClearSearch = findViewById(R.id.btn_clear_search);

        spinnerSort = findViewById(R.id.spinner_sort);

        chipGroupFilters = findViewById(R.id.chip_group_filters);
    }

    // ---------------- RECYCLER VIEW ----------------

    private void setupRecyclerView() {

        recyclerAllJobs.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AllJobsAdapter(
                this,
                new ArrayList<>()
        );

        recyclerAllJobs.setAdapter(adapter);

        recyclerAllJobs.setHasFixedSize(true);
    }

    // ---------------- SORTING ----------------

    private void setupSorting() {

        String[] sortOptions = {
                "Latest First",
                "Oldest First",
                "Title A-Z",
                "Title Z-A"
        };

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sortOptions
        );

        sortAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSort.setAdapter(sortAdapter);

        spinnerSort.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        selectedSort = sortOptions[position];

                        applyFiltersAndSorting();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    // ---------------- SEARCH ----------------

    private void setupSearch() {

        etSearchJob.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                applyFiltersAndSorting();

                btnClearSearch.setVisibility(
                        s.length() > 0 ? View.VISIBLE : View.GONE
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnClearSearch.setOnClickListener(v -> {
            etSearchJob.setText("");
        });
    }

    // ---------------- FILTERS ----------------

    private void setupFilters() {

        chipGroupFilters.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {

                    if (checkedIds == null || checkedIds.isEmpty()) {

                        selectedFilter = "All";

                    } else {

                        int checkedId = checkedIds.get(0);

                        if (checkedId == R.id.chip_full_time) {

                            selectedFilter = "Full Time";

                        } else if (checkedId == R.id.chip_part_time) {

                            selectedFilter = "Part Time";

                        } else if (checkedId == R.id.chip_internship) {

                            selectedFilter = "Internship";

                        } else if (checkedId == R.id.chip_remote) {

                            selectedFilter = "Remote";

                        } else {

                            selectedFilter = "All";
                        }
                    }

                    applyFiltersAndSorting();
                }
        );
    }

    // ---------------- RESET FILTERS ----------------

    private void setupResetButton() {

        findViewById(R.id.btn_reset_filters).setOnClickListener(v -> {

            etSearchJob.setText("");

            selectedFilter = "All";
            selectedSort = "Latest First";

            chipGroupFilters.check(R.id.chip_all);

            spinnerSort.setSelection(0);

            applyFiltersAndSorting();
        });
    }

    // ---------------- FETCH JOBS API ----------------

    private void fetchAllJobs() {

        showLoading(true);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                Urls.get__all__jobs,
                null,

                response -> {

                    showLoading(false);

                    try {

                        boolean status = response.optBoolean(
                                "status", false
                        );

                        if (!status) {

                            String message = response.optString(
                                    "message",
                                    "Unable to fetch jobs."
                            );

                            Toast.makeText(
                                    this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            showEmpty("Unable to load jobs.");
                            return;
                        }

                        JSONArray jobsArray = response.optJSONArray("jobs");

                        allJobs.clear();

                        if (jobsArray != null) {

                            for (int i = 0; i < jobsArray.length(); i++) {

                                JSONObject object =
                                        jobsArray.optJSONObject(i);

                                if (object == null) continue;

                                JobModel2 job = new JobModel2(

                                        object.optString("id", ""),
                                        object.optString("recruiter_id", ""),
                                        object.optString("recruiter_email", ""),

                                        object.optString("job_title", ""),
                                        object.optString("department", ""),
                                        object.optString("job_category", ""),
                                        object.optString("openings", "1"),

                                        object.optString("job_description", ""),
                                        object.optString("responsibilities", ""),
                                        object.optString("required_skills", ""),
                                        object.optString("soft_skills", ""),

                                        object.optString("min_experience", ""),
                                        object.optString("max_experience", ""),

                                        object.optString("minimum_qualification", ""),
                                        object.optString("preferred_course", ""),

                                        object.optString("employment_type", ""),
                                        object.optString("work_mode", ""),

                                        object.optString("job_location", ""),
                                        object.optString("state", ""),
                                        object.optString("country", ""),

                                        object.optString("min_salary", ""),
                                        object.optString("max_salary", ""),
                                        object.optString("salary_period", ""),

                                        object.optString("benefits", ""),
                                        object.optString("application_deadline", ""),

                                        object.optString("hr_email", ""),
                                        object.optString("company_email", ""),

                                        object.optString("visibility", ""),
                                        object.optString("job_status", ""),

                                        object.optString("created_at", ""),
                                        object.optString("updated_at", "")
                                );
                                Log.d("VIEW_ALL_JOBS", "Job ID: " + job.getId());
                                Log.d("VIEW_ALL_JOBS", "Job Title: " + job.getJobTitle());
                                Log.d("VIEW_ALL_JOBS", "Job Status: " + job.getJobStatus());
                                Log.d("VIEW_ALL_JOBS", "Visibility: " + job.getVisibility());
                                Log.d("VIEW_ALL_JOBS", "Deadline: " + job.getApplicationDeadline());

                                if (isPublicActiveJob(job)) {
                                    allJobs.add(job);
                                    Log.d("VIEW_ALL_JOBS", "JOB ADDED: " + job.getJobTitle());
                                } else {
                                    Log.d("VIEW_ALL_JOBS", "JOB FILTERED OUT: " + job.getJobTitle());
                                }


                                // Only display public, published,
                                // non-expired jobs to JobSeekers.


                            }
                        }

                        applyFiltersAndSorting();

                        if (allJobs.isEmpty()) {
                            showEmpty("No active job opportunities are available right now.");
                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                        showEmpty("Error while processing job data.");
                    }
                },

                error -> {

                    showLoading(false);

                    String message = "Network error. Please try again.";

                    if (error.networkResponse != null) {
                        message += " HTTP " +
                                error.networkResponse.statusCode;
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    showEmpty("Unable to connect to server.");
                }
        );

        request.setTag(TAG_JOBS);
        request.setShouldCache(false);

        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        20000,
                        1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );

        requestQueue.add(request);
    }

    // ---------------- CHECK JOB STATUS ----------------

    private boolean isPublicActiveJob(JobModel2 job) {

        if (!"Published".equalsIgnoreCase(job.getJobStatus())
                || !"Public".equalsIgnoreCase(job.getVisibility())) {

            return false;
        }

        String deadline = job.getApplicationDeadline();

        if (deadline == null || deadline.trim().isEmpty()) {
            return false;
        }

        try {

            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
            );

            format.setLenient(false);

            Date deadlineDate = format.parse(deadline);

            if (deadlineDate == null) return false;

            String todayString = format.format(new Date());

            Date today = format.parse(todayString);

            return today != null && !deadlineDate.before(today);

        } catch (Exception e) {

            return false;
        }
    }

    // ---------------- SEARCH + FILTER + SORT ----------------

    private void applyFiltersAndSorting() {

        if (adapter == null) return;

        filteredJobs.clear();

        String searchText = etSearchJob.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        for (JobModel2 job : allJobs) {

            boolean matchesSearch = searchText.isEmpty()

                    || contains(job.getJobTitle(), searchText)
                    || contains(job.getDepartment(), searchText)
                    || contains(job.getJobCategory(), searchText)
                    || contains(job.getJobLocation(), searchText)
                    || contains(job.getState(), searchText)
                    || contains(job.getRequiredSkills(), searchText)
                    || contains(job.getEmploymentType(), searchText)
                    || contains(job.getWorkMode(), searchText)
                    || contains(job.getMinimumQualification(), searchText);

            boolean matchesFilter = true;

            if ("Full Time".equals(selectedFilter)) {

                matchesFilter = equalsIgnoreCase(
                        job.getEmploymentType(), "Full Time"
                );

            } else if ("Part Time".equals(selectedFilter)) {

                matchesFilter = equalsIgnoreCase(
                        job.getEmploymentType(), "Part Time"
                );

            } else if ("Internship".equals(selectedFilter)) {

                matchesFilter = equalsIgnoreCase(
                        job.getEmploymentType(), "Internship"
                );

            } else if ("Remote".equals(selectedFilter)) {

                matchesFilter = equalsIgnoreCase(
                        job.getWorkMode(), "Remote"
                );
            }

            if (matchesSearch && matchesFilter) {
                filteredJobs.add(job);
            }
        }

        sortJobs();

        adapter.updateJobs(new ArrayList<>(filteredJobs));

        tvJobCount.setText(
                filteredJobs.size() + " Jobs Found"
        );

        if (filteredJobs.isEmpty()) {

            showEmpty("No jobs found. Try changing your search or filters.");

        } else {

            layoutEmpty.setVisibility(View.GONE);
            recyclerAllJobs.setVisibility(View.VISIBLE);
        }
    }

    private boolean contains(String value, String search) {

        return value != null
                && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private boolean equalsIgnoreCase(String value, String expected) {

        return value != null && value.equalsIgnoreCase(expected);
    }

    // ---------------- SORT JOBS ----------------

    private void sortJobs() {

        if ("Latest First".equals(selectedSort)) {

            Collections.sort(filteredJobs, (job1, job2) -> {

                int result = compareDates(
                        job2.getCreatedAt(),
                        job1.getCreatedAt()
                );

                if (result == 0) {
                    return compareNumericIds(
                            job2.getId(), job1.getId()
                    );
                }

                return result;
            });

        } else if ("Oldest First".equals(selectedSort)) {

            Collections.sort(filteredJobs, (job1, job2) -> {

                int result = compareDates(
                        job1.getCreatedAt(),
                        job2.getCreatedAt()
                );

                if (result == 0) {
                    return compareNumericIds(
                            job1.getId(), job2.getId()
                    );
                }

                return result;
            });

        } else if ("Title A-Z".equals(selectedSort)) {

            Collections.sort(
                    filteredJobs,
                    Comparator.comparing(
                            job -> safe(job.getJobTitle()),
                            String.CASE_INSENSITIVE_ORDER
                    )
            );

        } else if ("Title Z-A".equals(selectedSort)) {

            Collections.sort(filteredJobs, (job1, job2) ->
                    safe(job2.getJobTitle()).compareToIgnoreCase(
                            safe(job1.getJobTitle())
                    )
            );
        }
    }

    private int compareDates(String date1, String date2) {

        return Long.compare(
                parseDateTime(date1),
                parseDateTime(date2)
        );
    }

    private long parseDateTime(String value) {

        if (value == null || value.trim().isEmpty()) return 0;

        String[] patterns = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
        };

        for (String pattern : patterns) {

            try {

                SimpleDateFormat format = new SimpleDateFormat(
                        pattern,
                        Locale.US
                );

                format.setLenient(false);

                Date date = format.parse(value);

                if (date != null) return date.getTime();

            } catch (Exception ignored) {
            }
        }

        return 0;
    }

    private int compareNumericIds(String id1, String id2) {

        try {

            return Long.compare(
                    Long.parseLong(id1),
                    Long.parseLong(id2)
            );

        } catch (Exception e) {

            return safe(id1).compareTo(safe(id2));
        }
    }

    private String safe(String value) {

        return value == null ? "" : value.trim();
    }

    // ---------------- LOADING / EMPTY STATE ----------------

    private void showLoading(boolean show) {

        layoutLoading.setVisibility(
                show ? View.VISIBLE : View.GONE
        );

        if (show) {
            recyclerAllJobs.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmpty(String message) {

        recyclerAllJobs.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);

        tvEmptyMessage.setText(message);

        tvJobCount.setText("0 Jobs Found");
    }

    // ---------------- DESTROY ----------------

    @Override
    protected void onDestroy() {

        if (requestQueue != null) {
            requestQueue.cancelAll(TAG_JOBS);
        }

        super.onDestroy();
    }
}
