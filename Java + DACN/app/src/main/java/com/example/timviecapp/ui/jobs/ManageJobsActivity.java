package com.example.timviecapp.ui.jobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityManageJobsBinding;
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
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvJobs.setAdapter(adapter);

        adapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        binding.fabAddJob.setOnClickListener(v -> {
            startActivity(new Intent(this, AddJobActivity.class));
        });
    }

    private void loadJobs() {
        viewModel.getJobs(1, 50).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
            } else {
                Toast.makeText(this, "Không thể tải danh sách công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}
