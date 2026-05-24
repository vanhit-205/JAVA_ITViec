package com.example.timviecapp.ui.resume;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityManageResumesBinding;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.ui.adapters.CompanyGroupAdapter;
import com.example.timviecapp.ui.adapters.JobGroupAdapter;
import com.example.timviecapp.ui.adapters.ResumeAdapter;
import com.example.timviecapp.viewmodels.ResumeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ManageResumesActivity extends AppCompatActivity {
    private ActivityManageResumesBinding binding;
    private ResumeViewModel viewModel;
    
    private ResumeAdapter adapter;
    private CompanyGroupAdapter companyGroupAdapter;
    private JobGroupAdapter jobGroupAdapter;
    
    private List<CompanyGroup> companyGroups = new ArrayList<>();
    private List<JobGroup> jobGroups = new ArrayList<>();
    
    private CompanyGroup selectedCompanyGroup = null;
    private JobGroup selectedJobGroup = null;

    // Navigation States
    private static final int STATE_COMPANY_LIST = 1;
    private static final int STATE_JOB_LIST = 2;
    private static final int STATE_RESUME_LIST = 3;
    private int currentNavigationState = STATE_COMPANY_LIST;

    public static class CompanyGroup {
        private final long id;
        private final String name;
        private final List<ResumeResponse> resumes = new ArrayList<>();
        private final Set<Long> uniqueJobIds = new HashSet<>();

        public CompanyGroup(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() { return id; }
        public String getCompanyName() { return name; }
        public List<ResumeResponse> getResumes() { return resumes; }

        public void addResume(ResumeResponse resume) {
            resumes.add(resume);
            if (resume.getJobId() > 0) {
                uniqueJobIds.add(resume.getJobId());
            }
        }

        public int getJobCount() { return uniqueJobIds.size(); }
        public int getResumeCount() { return resumes.size(); }
    }

    public static class JobGroup {
        private final long id;
        private final String name;
        private final List<ResumeResponse> resumes = new ArrayList<>();

        public JobGroup(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() { return id; }
        public String getJobName() { return name; }
        public List<ResumeResponse> getResumes() { return resumes; }

        public void addResume(ResumeResponse resume) {
            resumes.add(resume);
        }

        public int getResumeCount() { return resumes.size(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageResumesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        setupRecyclerViews();
        loadAllResumesData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentNavigationState != STATE_RESUME_LIST) {
            loadAllResumesData();
        } else if (selectedJobGroup != null) {
            loadMatchingCandidates(selectedJobGroup);
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        if (currentNavigationState == STATE_COMPANY_LIST) {
            binding.toolbar.setTitle("Kiểm duyệt CV");
        } else if (currentNavigationState == STATE_JOB_LIST && selectedCompanyGroup != null) {
            binding.toolbar.setTitle(selectedCompanyGroup.getCompanyName());
        } else if (currentNavigationState == STATE_RESUME_LIST && selectedJobGroup != null) {
            binding.toolbar.setTitle(selectedJobGroup.getJobName());
        }
    }

    private void setupRecyclerViews() {
        // 1. ResumeAdapter for displaying candidates with matching scores
        adapter = new ResumeAdapter();
        binding.rvResumes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResumes.setAdapter(adapter);

        adapter.setOnResumeActionListener(new ResumeAdapter.OnResumeActionListener() {
            @Override
            public void onView(ResumeResponse resume) {
                Intent intent = new Intent(ManageResumesActivity.this, ResumeDetailActivity.class);
                intent.putExtra(ResumeDetailActivity.EXTRA_RESUME_ID, resume.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(ResumeResponse resume) {
                // Thao tác nhanh duyệt / từ chối hồ sơ
                Toast.makeText(ManageResumesActivity.this, "Đang mở chi tiết hồ sơ để duyệt trạng thái", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ManageResumesActivity.this, ResumeDetailActivity.class);
                intent.putExtra(ResumeDetailActivity.EXTRA_RESUME_ID, resume.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(ResumeResponse resume) {
                Toast.makeText(ManageResumesActivity.this, "Xoá hồ sơ ứng viên (Đang phát triển)", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. CompanyGroupAdapter for showing grouped companies list
        companyGroupAdapter = new CompanyGroupAdapter();
        jobGroupAdapter = new JobGroupAdapter();

        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCompanies.setAdapter(companyGroupAdapter);

        // Click Company -> Go to Job List of that Company
        companyGroupAdapter.setOnCompanyGroupClickListener(group -> {
            selectedCompanyGroup = group;
            jobGroups = groupResumesByJob(group.getResumes());
            
            // Switch state to JOB LIST
            currentNavigationState = STATE_JOB_LIST;
            binding.rvCompanies.setAdapter(jobGroupAdapter);
            jobGroupAdapter.setGroups(jobGroups);
            
            updateToolbarTitle();
        });

        // Click Job -> Go to Candidate List with matching algorithm fetched from Backend
        jobGroupAdapter.setOnJobGroupClickListener(group -> {
            selectedJobGroup = group;
            
            // Switch state to RESUME LIST
            currentNavigationState = STATE_RESUME_LIST;
            binding.rvCompanies.setVisibility(View.GONE);
            binding.rvResumes.setVisibility(View.VISIBLE);
            
            updateToolbarTitle();
            
            // Fetch candidates with backend skill matching scores!
            loadMatchingCandidates(group);
        });
    }

    private void loadAllResumesData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        // Load all resumes initially to group them hierarchically
        viewModel.getResumes(0, 100).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                List<ResumeResponse> allResumes = response.getData().getItems();
                if (allResumes == null) {
                    allResumes = new ArrayList<>();
                }

                if (allResumes.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvCompanies.setVisibility(View.GONE);
                    binding.rvResumes.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    
                    // Group resumes by company
                    companyGroups = groupResumesByCompany(allResumes);
                    
                    // Render current state
                    renderCurrentState();
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách ứng viên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Call the backend matching algorithm to fetch resumes for a specific job,
     * which automatically scores and sorts them with the highest matching CVs on top.
     */
    private void loadMatchingCandidates(JobGroup jobGroup) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvResumes.setVisibility(View.GONE);
        
        viewModel.getMatchingCandidates((int) jobGroup.getId()).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            
            if (response != null && response.isSuccess() && response.getData() != null) {
                List<ResumeResponse> matchedResumes = response.getData();
                binding.rvResumes.setVisibility(View.VISIBLE);
                
                if (matchedResumes == null || matchedResumes.isEmpty()) {
                    // Fallback to static resumes if no backend calculation returned
                    adapter.setResumes(jobGroup.getResumes());
                } else {
                    adapter.setResumes(matchedResumes);
                }
            } else {
                // Fallback to static resumes if matching api fails
                binding.rvResumes.setVisibility(View.VISIBLE);
                adapter.setResumes(jobGroup.getResumes());
            }
        });
    }

    private void renderCurrentState() {
        if (currentNavigationState == STATE_COMPANY_LIST) {
            binding.rvResumes.setVisibility(View.GONE);
            binding.rvCompanies.setVisibility(View.VISIBLE);
            binding.rvCompanies.setAdapter(companyGroupAdapter);
            companyGroupAdapter.setGroups(companyGroups);
        } else if (currentNavigationState == STATE_JOB_LIST && selectedCompanyGroup != null) {
            // Find updated company group
            CompanyGroup updated = null;
            for (CompanyGroup g : companyGroups) {
                if (g.getId() == selectedCompanyGroup.getId()) {
                    updated = g;
                    break;
                }
            }
            if (updated != null) {
                selectedCompanyGroup = updated;
                jobGroups = groupResumesByJob(selectedCompanyGroup.getResumes());
            }
            binding.rvResumes.setVisibility(View.GONE);
            binding.rvCompanies.setVisibility(View.VISIBLE);
            binding.rvCompanies.setAdapter(jobGroupAdapter);
            jobGroupAdapter.setGroups(jobGroups);
        }
        updateToolbarTitle();
    }

    private List<CompanyGroup> groupResumesByCompany(List<ResumeResponse> resumes) {
        Map<Long, CompanyGroup> map = new HashMap<>();
        CompanyGroup otherGroup = new CompanyGroup(-1L, "Công ty khác / Không xác định");

        for (ResumeResponse r : resumes) {
            long cId = r.getCompanyId();
            String cName = r.getCompanyName();

            if (cId > 0 && cName != null && !cName.trim().isEmpty()) {
                CompanyGroup group = map.get(cId);
                if (group == null) {
                    group = new CompanyGroup(cId, cName);
                    map.put(cId, group);
                }
                group.addResume(r);
            } else {
                otherGroup.addResume(r);
            }
        }

        List<CompanyGroup> list = new ArrayList<>(map.values());
        if (otherGroup.getResumeCount() > 0) {
            list.add(otherGroup);
        }
        return list;
    }

    private List<JobGroup> groupResumesByJob(List<ResumeResponse> resumes) {
        Map<Long, JobGroup> map = new HashMap<>();
        JobGroup otherGroup = new JobGroup(-1L, "Công việc khác / Không xác định");

        for (ResumeResponse r : resumes) {
            long jobId = r.getJobId();
            String jobName = r.getJobName();

            if (jobId > 0 && jobName != null && !jobName.trim().isEmpty()) {
                JobGroup group = map.get(jobId);
                if (group == null) {
                    group = new JobGroup(jobId, jobName);
                    map.put(jobId, group);
                }
                group.addResume(r);
            } else {
                otherGroup.addResume(r);
            }
        }

        List<JobGroup> list = new ArrayList<>(map.values());
        if (otherGroup.getResumeCount() > 0) {
            list.add(otherGroup);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        if (currentNavigationState == STATE_RESUME_LIST) {
            currentNavigationState = STATE_JOB_LIST;
            binding.rvResumes.setVisibility(View.GONE);
            binding.rvCompanies.setVisibility(View.VISIBLE);
            binding.rvCompanies.setAdapter(jobGroupAdapter);
            jobGroupAdapter.setGroups(jobGroups);
            updateToolbarTitle();
        } else if (currentNavigationState == STATE_JOB_LIST) {
            currentNavigationState = STATE_COMPANY_LIST;
            selectedCompanyGroup = null;
            binding.rvResumes.setVisibility(View.GONE);
            binding.rvCompanies.setVisibility(View.VISIBLE);
            binding.rvCompanies.setAdapter(companyGroupAdapter);
            companyGroupAdapter.setGroups(companyGroups);
            updateToolbarTitle();
        } else {
            super.onBackPressed();
        }
    }
}
