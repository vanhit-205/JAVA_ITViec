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
import com.example.timviecapp.ui.adapters.ResumeAdapter;
import com.example.timviecapp.viewmodels.ResumeViewModel;

public class ManageResumesActivity extends AppCompatActivity {
    private ActivityManageResumesBinding binding;
    private ResumeViewModel viewModel;
    private ResumeAdapter adapter;

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
    }

    private void setupRecyclerView() {
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
    }

    private void loadResumes() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);

        viewModel.getResumes(0, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                if (response.getData().getItems().isEmpty()) {
                    binding.layoutEmpty.setVisibility(View.VISIBLE);
                    binding.rvResumes.setVisibility(View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.rvResumes.setVisibility(View.VISIBLE);
                    adapter.setResumes(response.getData().getItems());
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách ứng viên", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
