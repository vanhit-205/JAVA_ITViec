package com.example.timviecapp.ui.jobs;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityManageJobsBinding;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.viewmodels.JobViewModel;

public class ManageJobsActivity extends AppCompatActivity {
    private ActivityManageJobsBinding binding;
    private JobViewModel viewModel;
    private JobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageJobsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        loadJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new JobAdapter();
        adapter.setManageMode(true); // Bật chế độ quản lý: hiện nút Sửa / Xóa
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJobs.setAdapter(adapter);

        // Click vào card -> Xem chi tiết
        adapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });

        // Listener nút Sửa / Xóa
        adapter.setOnJobActionListener(new JobAdapter.OnJobActionListener() {
            @Override
            public void onEdit(JobResponse job) {
                Intent intent = new Intent(ManageJobsActivity.this, AddJobActivity.class);
                intent.putExtra(AddJobActivity.EXTRA_JOB_ID, (int) job.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(JobResponse job) {
                showDeleteConfirmDialog(job);
            }
        });
    }

    private void setupListeners() {
        binding.fabAddJob.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJobActivity.class));
        });
    }

    private void loadJobs() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getJobs(1, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
            } else {
                Toast.makeText(this, "Không thể tải danh sách công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmDialog(JobResponse job) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa công việc\n\"" + job.getName() + "\" không?\n\n"
                        + "Tất cả hồ sơ ứng tuyển liên quan sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> performDeleteJob(job))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performDeleteJob(JobResponse job) {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.deleteJob((int) job.getId()).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "✓ Đã xóa công việc \"" + job.getName() + "\"", Toast.LENGTH_SHORT).show();
                loadJobs(); // reload danh sách
            } else {
                Toast.makeText(this, "Xóa thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}
