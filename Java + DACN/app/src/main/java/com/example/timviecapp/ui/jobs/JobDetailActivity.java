package com.example.timviecapp.ui.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityJobDetailBinding;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.ui.resume.ApplyJobActivity;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.JobViewModel;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * JobDetailActivity - Xem chi tiết công việc
 * UC15: Xem chi tiết công việc
 * UC24: Ứng tuyển (Navigate đến ApplyJobActivity)
 */
public class JobDetailActivity extends AppCompatActivity {
    public static final String EXTRA_JOB_ID = "extra_job_id";
    private ActivityJobDetailBinding binding;
    private JobViewModel viewModel;
    private int jobId;
    private JobResponse currentJob; // Lưu job hiện tại để truyền sang Apply

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupListeners();
        loadJobDetails();
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadJobDetails() {
        viewModel.getJobById(jobId).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                currentJob = response.getData();
                displayJobDetails(currentJob);
            } else {
                Toast.makeText(this, "Không thể tải chi tiết công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayJobDetails(JobResponse job) {
        binding.tvJobTitle.setText(job.getName());
        binding.tvCompanyName.setText(job.getCompany() != null ? job.getCompany().getName() : "N/A");
        binding.tvLocation.setText(job.getLocation());
        binding.tvLevel.setText(job.getLevel());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.tvSalary.setText(currencyFormat.format(job.getSalary()));

        binding.tvDescription.setText(job.getDescription());
        binding.tvPostedDate.setText("Ngày bắt đầu: " + com.example.timviecapp.utils.DateUtils.formatIsoDate(job.getStartDate()));
        binding.tvDeadline.setText("Hạn nộp: " + com.example.timviecapp.utils.DateUtils.formatIsoDate(job.getEndDate()));

        if (job.getCompany() != null) {
            binding.tvAboutCompany.setText(job.getCompany().getDescription());
        }

        // Hiển thị skills dưới dạng Chip
        binding.cgSkills.removeAllViews();
        if (job.getSkills() != null) {
            for (SkillResponse skill : job.getSkills()) {
                Chip chip = new Chip(this);
                chip.setText(skill.getName());
                binding.cgSkills.addView(chip);
            }
        }

        // Phân quyền hiển thị nút Ứng tuyển/Sửa theo Role
        String role = TokenManager.getUserRole();
        if (role != null && (role.toUpperCase().contains("ADMIN") || role.toUpperCase().contains("RECRUITER") || role.toUpperCase().contains("EMPLOYER"))) {
            binding.btnApply.setVisibility(View.VISIBLE);
            binding.btnApply.setEnabled(true);
            binding.btnApply.setText("Sửa công việc");
            binding.btnApply.setOnClickListener(v -> {
                if (currentJob != null) {
                    Intent intent = new Intent(JobDetailActivity.this, AddJobActivity.class);
                    intent.putExtra("jobId", currentJob.getId());
                    startActivity(intent);
                }
            });

            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xóa công việc")
                    .setMessage("Bạn có chắc chắn muốn xóa công việc này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        deleteJob(jobId);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            });
        } else {
            binding.btnApply.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.GONE);
            if (!job.isActive()) {
                binding.btnApply.setEnabled(false);
                binding.btnApply.setText("Đã đóng tuyển dụng");
                binding.btnApply.setOnClickListener(null);
            } else {
                binding.btnApply.setEnabled(true);
                binding.btnApply.setText("Ứng tuyển ngay");
                binding.btnApply.setOnClickListener(v -> {
                    if (!TokenManager.isLoggedIn()) {
                        Toast.makeText(this, "Vui lòng đăng nhập để ứng tuyển", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (currentJob != null) {
                        Intent intent = new Intent(this, ApplyJobActivity.class);
                        intent.putExtra(ApplyJobActivity.EXTRA_JOB_ID, currentJob.getId());
                        intent.putExtra(ApplyJobActivity.EXTRA_JOB_TITLE, currentJob.getName());
                        if (currentJob.getCompany() != null) {
                            intent.putExtra(ApplyJobActivity.EXTRA_COMPANY_NAME, currentJob.getCompany().getName());
                        }
                        startActivity(intent);
                    }
                });
            }
        }
    }

    private void deleteJob(int id) {
        viewModel.deleteJob(id).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Xóa công việc thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Xóa công việc thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
