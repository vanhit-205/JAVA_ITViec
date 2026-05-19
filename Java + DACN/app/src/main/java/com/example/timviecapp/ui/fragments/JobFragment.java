package com.example.timviecapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.FragmentJobBinding;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.ui.jobs.JobDetailActivity;
import com.example.timviecapp.viewmodels.JobViewModel;

public class JobFragment extends Fragment {
    private FragmentJobBinding binding;
    private JobViewModel viewModel;
    private JobAdapter adapter;
    private String initialQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentJobBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();

        if (initialQuery != null && !initialQuery.isEmpty()) {
            binding.etSearchKeyword.setText(initialQuery);
            initialQuery = ""; // reset after setting
            searchJobs();
        } else {
            loadJobs();
        }
    }

    public void setQuery(String query) {
        this.initialQuery = query;
        if (binding != null) {
            binding.etSearchKeyword.setText(query);
            searchJobs();
        }
    }

    private void setupRecyclerView() {
        adapter = new JobAdapter();
        binding.rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvJobs.setAdapter(adapter);

        adapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(getContext(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        binding.btnSearch.setOnClickListener(v -> searchJobs());
    }

    private void loadJobs() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getJobs(1, 20).observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
                binding.tvJobCount.setText(response.getData().getItems().size() + " công việc được tìm thấy");
            } else {
                Toast.makeText(getContext(), "Không thể tải danh sách công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchJobs() {
        String keyword = binding.etSearchKeyword.getText().toString().trim();
        String location = binding.etSearchLocation.getText().toString().trim();

        if (keyword.isEmpty() && location.isEmpty()) {
            loadJobs();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.searchJobs(
                keyword.isEmpty() ? null : keyword,
                location.isEmpty() ? null : location,
                null, 1, 30
        ).observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setJobs(response.getData().getItems());
                int count = response.getData().getItems().size();
                String searchText = keyword.isEmpty() ? location : keyword;
                binding.tvJobCount.setText(count + " công việc phù hợp với \"" + searchText + "\"");

                if (count == 0) {
                    Toast.makeText(getContext(), "Không tìm thấy công việc phù hợp", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
