package com.example.timviecapp.ui.companies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityCompanyDetailBinding;
import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.ui.jobs.JobDetailActivity;
import com.example.timviecapp.viewmodels.CompanyViewModel;
import com.example.timviecapp.viewmodels.JobViewModel;

/**
 * CompanyDetailActivity - Màn hình chi tiết công ty
 * UC10: Xem chi tiết công ty
 * - Hiển thị thông tin chi tiết: tên, địa chỉ, mô tả
 * - Hiển thị danh sách job đang tuyển của công ty
 */
public class CompanyDetailActivity extends AppCompatActivity {
    public static final String EXTRA_COMPANY_ID = "extra_company_id";
    private ActivityCompanyDetailBinding binding;
    private CompanyViewModel companyViewModel;
    private JobViewModel jobViewModel;
    private JobAdapter jobAdapter;
    private int companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        companyId = getIntent().getIntExtra(EXTRA_COMPANY_ID, -1);
        if (companyId == -1) {
            Toast.makeText(this, "Công ty không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupToolbar();
        setupJobsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompanyDetail();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Thêm nút Sửa và Xóa công ty cho Admin/Recruiter/Employer
        String role = com.example.timviecapp.utils.TokenManager.getUserRole();
        if (role != null && (role.toUpperCase().contains("ADMIN") || role.toUpperCase().contains("RECRUITER") || role.toUpperCase().contains("EMPLOYER"))) {
            binding.toolbar.getMenu().add(0, 101, 0, "Sửa")
                .setIcon(android.R.drawable.ic_menu_edit)
                .setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_ALWAYS);
            
            binding.toolbar.getMenu().add(0, 102, 1, "Xóa")
                .setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_NEVER);
            
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 101) {
                    Intent intent = new Intent(this, CreateCompanyActivity.class);
                    intent.putExtra("companyId", companyId);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == 102) {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Xóa công ty")
                        .setMessage("Bạn có chắc chắn muốn xóa công ty này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            deleteCompany();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                    return true;
                }
                return false;
            });
        }
    }

    private void deleteCompany() {
        binding.progressBar.setVisibility(View.VISIBLE);
        companyViewModel.deleteCompany(companyId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            companyViewModel.setLoading(false);
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Xóa công ty thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Xóa công ty thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupJobsList() {
        jobAdapter = new JobAdapter();
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJobs.setAdapter(jobAdapter);
        binding.rvJobs.setNestedScrollingEnabled(false);

        // Click vào job để xem chi tiết
        jobAdapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });
    }

    private void loadCompanyDetail() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // Lấy thông tin chi tiết công ty
        companyViewModel.getCompanyById(companyId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            companyViewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                displayCompanyInfo(response.getData());
                loadCompanyJobs();
            } else {
                Toast.makeText(this, "Không thể tải thông tin công ty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị thông tin công ty
     */
    private void displayCompanyInfo(CompanyResponse company) {
        binding.tvCompanyName.setText(company.getName() + " (ID: " + company.getId() + ")");
        binding.tvAddress.setText(company.getAddress() != null ? company.getAddress() : "Chưa cập nhật");
        binding.tvDescription.setText(company.getDescription() != null ? company.getDescription() : "Chưa có mô tả");
        binding.toolbar.setTitle(company.getName());
    }

    /**
     * Tải danh sách job của công ty (mở rộng từ UC10)
     */
    private void loadCompanyJobs() {
        jobViewModel.getJobsByCompany(companyId, 1, 100).observe(this, response -> {
            jobViewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                java.util.List<com.example.timviecapp.models.job.JobResponse> jobs = response.getData().getItems();
                if (jobs != null && !jobs.isEmpty()) {
                    binding.tvNoJobs.setVisibility(View.GONE);
                    binding.rvJobs.setVisibility(View.VISIBLE);
                    jobAdapter.setJobs(jobs);
                } else {
                    binding.tvNoJobs.setVisibility(View.VISIBLE);
                    binding.rvJobs.setVisibility(View.GONE);
                }
            } else {
                binding.tvNoJobs.setVisibility(View.VISIBLE);
                binding.rvJobs.setVisibility(View.GONE);
            }
        });
    }
}
