package com.example.timviecapp.ui.resume;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityMyResumesBinding;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.ui.adapters.ResumeAdapter;
import com.example.timviecapp.viewmodels.ResumeViewModel;

/**
 * MyResumesActivity - Lịch sử ứng tuyển
 * UC29: Lấy danh sách resume theo user
 * UC26: Xóa resume
 * UC27: Xem chi tiết resume (navigate)
 */
public class MyResumesActivity extends AppCompatActivity {
    private ActivityMyResumesBinding binding;
    private ResumeViewModel viewModel;
    private ResumeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyResumesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadResumes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResumes(); // Reload khi quay lại
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new ResumeAdapter();
        binding.rvResumes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResumes.setAdapter(adapter);

        adapter.setOnResumeActionListener(new ResumeAdapter.OnResumeActionListener() {
            @Override
            public void onView(ResumeResponse resume) {
                // UC27: Xem chi tiết resume
                Intent intent = new Intent(MyResumesActivity.this, ResumeDetailActivity.class);
                intent.putExtra(ResumeDetailActivity.EXTRA_RESUME_ID, resume.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(ResumeResponse resume) {
                // UC25: Chỉ cho phép sửa nếu status là PENDING
                if ("PENDING".equals(resume.getStatus())) {
                    Toast.makeText(MyResumesActivity.this,
                            "Chức năng cập nhật CV đang phát triển", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyResumesActivity.this,
                            "Hồ sơ đã được tiếp nhận, không thể thay đổi nội dung",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDelete(ResumeResponse resume) {
                // UC26: Xóa resume
                deleteResume(resume);
            }
        });
    }

    private void setupListeners() {
        // Nút "Tìm việc ngay" khi danh sách trống
        binding.btnFindJobs.setOnClickListener(v -> {
            finish(); // Quay về trang chủ tìm việc
        });
    }

    /**
     * UC29: Tải danh sách resume của user hiện tại
     */
    private void loadResumes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        viewModel.getMyResumes(0, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null) {
                if (response.getData().getItems().isEmpty()) {
                    // Hiển thị thông báo danh sách trống + nút gợi ý
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvResumes.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvResumes.setVisibility(View.VISIBLE);
                    adapter.setResumes(response.getData().getItems());
                }
            } else {
                Toast.makeText(this, "Không thể tải lịch sử ứng tuyển", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * UC26: Xóa resume với xác nhận
     * Chỉ cho phép xóa nếu status = PENDING
     */
    private void deleteResume(ResumeResponse resume) {
        // Kiểm tra trạng thái
        if (!"PENDING".equals(resume.getStatus())) {
            Toast.makeText(this, "Không thể rút hồ sơ đã được xử lý", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị dialog xác nhận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn rút hồ sơ này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    viewModel.deleteResume(resume.getId()).observe(this, response -> {
                        viewModel.setLoading(false);
                        if (response != null) {
                            Toast.makeText(this, "Xóa hồ sơ thành công", Toast.LENGTH_SHORT).show();
                            loadResumes(); // Reload danh sách
                        } else {
                            Toast.makeText(this, "Xóa thất bại, vui lòng thử lại",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
