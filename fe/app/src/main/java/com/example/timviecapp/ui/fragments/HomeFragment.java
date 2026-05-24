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

import com.example.timviecapp.databinding.FragmentHomeBinding;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.repository.SkillRepository;
import com.example.timviecapp.ui.adapters.CompanyAdapter;
import com.example.timviecapp.ui.adapters.JobAdapter;
import com.example.timviecapp.ui.companies.CompanyDetailActivity;
import com.example.timviecapp.ui.jobs.JobDetailActivity;
import com.example.timviecapp.viewmodels.CompanyViewModel;
import com.example.timviecapp.viewmodels.JobViewModel;
import com.google.android.material.chip.Chip;

import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private JobViewModel jobViewModel;
    private CompanyViewModel companyViewModel;
    private SkillRepository skillRepository;

    private JobAdapter jobAdapter;
    private CompanyAdapter companyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        jobViewModel = new ViewModelProvider(this).get(JobViewModel.class);
        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        skillRepository = new SkillRepository();

        setupRecyclerViews();
        loadSkills();
        loadFeaturedJobs();
        loadFeaturedCompanies();
        setupListeners();
    }

    private void setupRecyclerViews() {
        // Jobs Recycler View
        jobAdapter = new JobAdapter();
        binding.rvFeaturedJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFeaturedJobs.setAdapter(jobAdapter);

        jobAdapter.setOnJobClickListener(job -> {
            Intent intent = new Intent(getContext(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB_ID, job.getId());
            startActivity(intent);
        });

        // Companies Recycler View
        companyAdapter = new CompanyAdapter();
        binding.rvFeaturedCompanies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFeaturedCompanies.setAdapter(companyAdapter);

        companyAdapter.setOnCompanyClickListener(company -> {
            Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailActivity.EXTRA_COMPANY_ID, company.getId());
            startActivity(intent);
        });
    }

    private void loadSkills() {
        binding.progressBarSkills.setVisibility(View.VISIBLE);
        skillRepository.getSkills(0, 15).observe(getViewLifecycleOwner(), response -> {
            binding.progressBarSkills.setVisibility(View.GONE);
            if (response != null && response.isSuccess() && response.getData() != null) {
                displaySkillChips(response.getData().getItems());
            }
        });
    }

    private void displaySkillChips(List<SkillResponse> skills) {
        binding.cgSkills.removeAllViews();
        if (skills == null) return;

        for (SkillResponse skill : skills) {
            Chip chip = new Chip(getContext());
            chip.setText(skill.getName());
            chip.setCheckable(false);
            chip.setClickable(true);
            chip.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Tìm kiếm công việc với kỹ năng: " + skill.getName(), Toast.LENGTH_SHORT).show();
                // We can notify MainActivity to switch tab to Jobs with search query
                if (getActivity() instanceof com.example.timviecapp.MainActivity) {
                    ((com.example.timviecapp.MainActivity) getActivity()).switchToJobsTabWithQuery(skill.getName());
                }
            });
            binding.cgSkills.addView(chip);
        }
    }

    private void loadFeaturedJobs() {
        binding.progressBarJobs.setVisibility(View.VISIBLE);
        jobViewModel.getJobs(1, 5).observe(getViewLifecycleOwner(), response -> {
            binding.progressBarJobs.setVisibility(View.GONE);
            jobViewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                jobAdapter.setJobs(response.getData().getItems());
            }
        });
    }

    private void loadFeaturedCompanies() {
        binding.progressBarCompanies.setVisibility(View.VISIBLE);
        companyViewModel.getCompanies(1, 5).observe(getViewLifecycleOwner(), response -> {
            binding.progressBarCompanies.setVisibility(View.GONE);
            companyViewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                companyAdapter.setCompanies(response.getData().getItems());
            }
        });
    }

    private void setupListeners() {
        binding.tvViewAllJobs.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.timviecapp.MainActivity) {
                ((com.example.timviecapp.MainActivity) getActivity()).switchToJobsTab();
            }
        });

        binding.tvViewAllCompanies.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.timviecapp.MainActivity) {
                ((com.example.timviecapp.MainActivity) getActivity()).switchToCompaniesTab();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
