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
import com.example.timviecapp.ui.adapters.ResumeAdapter;
import com.example.timviecapp.viewmodels.ResumeViewModel;

public class ManageResumesActivity extends AppCompatActivity {
    private ActivityManageResumesBinding binding;
    private ResumeViewModel viewModel;
    private ResumeAdapter adapter;
    private CompanyGroupAdapter companyGroupAdapter;
    private java.util.List<CompanyGroup> companyGroups = new java.util.ArrayList<>();
    private CompanyGroup selectedCompanyGroup = null;

    public static class CompanyGroup {
        private final long id;
        private final String name;
        private final java.util.List<ResumeResponse> resumes = new java.util.ArrayList<>();
        private final java.util.Set<Long> uniqueJobIds = new java.util.HashSet<>();

        public CompanyGroup(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() { return id; }
        public String getCompanyName() { return name; }
        public java.util.List<ResumeResponse> getResumes() { return resumes; }

        public void addResume(ResumeResponse resume) {
            resumes.add(resume);
            if (resume.getJobId() > 0) {
                uniqueJobIds.add(resume.getJobId());
            }
        }

        public int getJobCount() { return uniqueJobIds.size(); }
        public int getResumeCount() { return resumes.size(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageResumesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        setupRecyclerView();
        loadResumes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResumes();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbar.setTitle("Kiểm duyệt CV");
    }

    private void setupRecyclerView() {
        // Setup ResumeAdapter for showing CVs of selected company
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
                Toast.makeText(ManageResumesActivity.this, "Sửa trạng thái ứng viên (Đang phát triển)", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(ResumeResponse resume) {
                Toast.makeText(ManageResumesActivity.this, "Xoá hồ sơ ứng viên (Đang phát triển)", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup CompanyGroupAdapter for showing grouped companies list
        companyGroupAdapter = new CompanyGroupAdapter();
        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCompanies.setAdapter(companyGroupAdapter);

        companyGroupAdapter.setOnCompanyGroupClickListener(group -> {
            selectedCompanyGroup = group;
            binding.rvCompanies.setVisibility(View.GONE);
            binding.rvResumes.setVisibility(View.VISIBLE);
            adapter.setResumes(group.getResumes());
            binding.toolbar.setTitle(group.getCompanyName());
        });
    }

    private void loadResumes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        viewModel.getResumes(0, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                java.util.List<ResumeResponse> allResumes = response.getData().getItems();
                if (allResumes == null) {
                    allResumes = new java.util.ArrayList<>();
                }

                if (allResumes.isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvCompanies.setVisibility(View.GONE);
                    binding.rvResumes.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    
                    // Group resumes by company
                    companyGroups = groupResumesByCompany(allResumes);
                    
                    if (selectedCompanyGroup != null) {
                        // Find updated group in the new list to keep viewing details if refreshed
                        CompanyGroup updatedGroup = null;
                        for (CompanyGroup g : companyGroups) {
                            if (g.getId() == selectedCompanyGroup.getId()) {
                                updatedGroup = g;
                                break;
                            }
                        }
                        if (updatedGroup != null) {
                            selectedCompanyGroup = updatedGroup;
                            adapter.setResumes(selectedCompanyGroup.getResumes());
                            binding.rvCompanies.setVisibility(View.GONE);
                            binding.rvResumes.setVisibility(View.VISIBLE);
                            binding.toolbar.setTitle(selectedCompanyGroup.getCompanyName());
                        } else {
                            selectedCompanyGroup = null;
                            binding.rvResumes.setVisibility(View.GONE);
                            binding.rvCompanies.setVisibility(View.VISIBLE);
                            binding.toolbar.setTitle("Kiểm duyệt CV");
                            companyGroupAdapter.setGroups(companyGroups);
                        }
                    } else {
                        binding.rvResumes.setVisibility(View.GONE);
                        binding.rvCompanies.setVisibility(View.VISIBLE);
                        binding.toolbar.setTitle("Kiểm duyệt CV");
                        companyGroupAdapter.setGroups(companyGroups);
                    }
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách ứng viên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private java.util.List<CompanyGroup> groupResumesByCompany(java.util.List<ResumeResponse> resumes) {
        java.util.Map<Long, CompanyGroup> map = new java.util.HashMap<>();
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

        java.util.List<CompanyGroup> list = new java.util.ArrayList<>(map.values());
        if (otherGroup.getResumeCount() > 0) {
            list.add(otherGroup);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        if (selectedCompanyGroup != null) {
            selectedCompanyGroup = null;
            binding.rvResumes.setVisibility(View.GONE);
            binding.rvCompanies.setVisibility(View.VISIBLE);
            binding.toolbar.setTitle("Kiểm duyệt CV");
        } else {
            super.onBackPressed();
        }
    }
}
