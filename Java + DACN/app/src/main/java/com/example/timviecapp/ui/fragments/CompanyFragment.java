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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.timviecapp.databinding.FragmentCompanyBinding;
import com.example.timviecapp.ui.adapters.CompanyAdapter;
import com.example.timviecapp.ui.companies.CompanyDetailActivity;
import com.example.timviecapp.viewmodels.CompanyViewModel;

public class CompanyFragment extends Fragment {
    private FragmentCompanyBinding binding;
    private CompanyViewModel viewModel;
    private CompanyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompanyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        setupRecyclerView();
        loadCompanies();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new CompanyAdapter();
        // Grid layout with 2 columns
        binding.rvCompanies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvCompanies.setAdapter(adapter);

        adapter.setOnCompanyClickListener(company -> {
            Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailActivity.EXTRA_COMPANY_ID, company.getId());
            startActivity(intent);
        });
    }

    private void loadCompanies() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getCompanies(1, 30).observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                adapter.setCompanies(response.getData().getItems());
            } else {
                Toast.makeText(getContext(), "Không thể tải danh sách công ty", Toast.LENGTH_SHORT).show();
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
