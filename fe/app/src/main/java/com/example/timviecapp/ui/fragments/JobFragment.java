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
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.ui.jobs.JobDetailActivity;
import com.example.timviecapp.viewmodels.JobViewModel;
import com.example.timviecapp.viewmodels.SkillViewModel;
import com.google.android.material.chip.Chip;

import java.util.List;

public class JobFragment extends Fragment {
    private FragmentJobBinding binding;
    private JobViewModel viewModel;
    private SkillViewModel skillViewModel;
    private JobAdapter adapter;
    private String initialQuery = "";
    // Skill filter state
    private String selectedSkillName = null;
    private List<JobResponse> allLoadedJobs = new java.util.ArrayList<>();

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
        skillViewModel = new ViewModelProvider(this).get(SkillViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();
        loadSkillFilterChips();

        if (initialQuery != null && !initialQuery.isEmpty()) {
            binding.etSearchKeyword.setText(initialQuery);
            initialQuery = "";
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

    /**
     * Nhóm 2: Tải và hiển thị thanh chip lọc theo kỹ năng
     */
    private void loadSkillFilterChips() {
        skillViewModel.getSkills(0, 50).observe(getViewLifecycleOwner(), response -> {
            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null
                    && !response.getData().getItems().isEmpty()) {

                List<SkillResponse> skills = response.getData().getItems();
                binding.skillFilterScrollView.setVisibility(View.VISIBLE);
                binding.chipGroupSkillFilter.removeAllViews();

                // Chip "Tất cả"
                Chip allChip = createFilterChip("Tất cả");
                allChip.setChecked(true);
                allChip.setOnCheckedChangeListener((btn, isChecked) -> {
                    if (isChecked) {
                        selectedSkillName = null;
                        applySkillFilter();
                    }
                });
                binding.chipGroupSkillFilter.addView(allChip);

                // Chip từng kỹ năng
                for (SkillResponse skill : skills) {
                    Chip chip = createFilterChip(skill.getName());
                    chip.setOnCheckedChangeListener((btn, isChecked) -> {
                        if (isChecked) {
                            selectedSkillName = skill.getName();
                            applySkillFilter();
                        }
                    });
                    binding.chipGroupSkillFilter.addView(chip);
                }
            }
        });
    }

    private Chip createFilterChip(String label) {
        Chip chip = new Chip(requireContext());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setChipBackgroundColorResource(android.R.color.transparent);
        chip.setChipStrokeColorResource(com.example.timviecapp.R.color.colorPrimary);
        chip.setChipStrokeWidth(2f);
        chip.setCheckedIconVisible(false);
        chip.setTextColor(requireContext().getResources().getColor(com.example.timviecapp.R.color.black));
        chip.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                chip.setChipBackgroundColorResource(com.example.timviecapp.R.color.colorPrimary);
                chip.setTextColor(requireContext().getResources().getColor(android.R.color.white));
            } else {
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setTextColor(requireContext().getResources().getColor(com.example.timviecapp.R.color.black));
            }
        });
        return chip;
    }

    /**
     * Lọc danh sách job đã tải sẵn theo kỹ năng được chọn (client-side)
     */
    private void applySkillFilter() {
        if (selectedSkillName == null) {
            adapter.setJobs(allLoadedJobs);
            binding.tvJobCount.setText(allLoadedJobs.size() + " công việc được tìm thấy");
        } else {
            List<JobResponse> filtered = new java.util.ArrayList<>();
            for (JobResponse job : allLoadedJobs) {
                if (job.getSkills() != null) {
                    for (SkillResponse s : job.getSkills()) {
                        if (selectedSkillName.equals(s.getName())) {
                            filtered.add(job);
                            break;
                        }
                    }
                }
            }
            adapter.setJobs(filtered);
            binding.tvJobCount.setText(filtered.size() + " công việc yêu cầu \"" + selectedSkillName + "\"");
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
                allLoadedJobs = response.getData().getItems() != null
                        ? response.getData().getItems() : new java.util.ArrayList<>();
                applySkillFilter(); // Áp dụng filter hiện tại (nếu có)
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
                allLoadedJobs = response.getData().getItems() != null
                        ? response.getData().getItems() : new java.util.ArrayList<>();
                applySkillFilter();

                if (allLoadedJobs.isEmpty()) {
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
