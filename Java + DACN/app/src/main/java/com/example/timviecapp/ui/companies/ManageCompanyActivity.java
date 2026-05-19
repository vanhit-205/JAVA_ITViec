package com.example.timviecapp.ui.companies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityManageCompanyBinding;
import com.example.timviecapp.ui.adapters.CompanyAdapter;
import com.example.timviecapp.viewmodels.CompanyViewModel;

public class ManageCompanyActivity extends AppCompatActivity {
    private ActivityManageCompanyBinding binding;
    private CompanyViewModel viewModel;
    private CompanyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompanies();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new CompanyAdapter();
        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCompanies.setAdapter(adapter);

        adapter.setOnCompanyClickListener(company -> {
            Intent intent = new Intent(this, CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailActivity.EXTRA_COMPANY_ID, company.getId());
            startActivity(intent);
        });
    }

    private void setupListeners() {
        binding.fabAddCompany.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateCompanyActivity.class));
        });
    }

    private void loadCompanies() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        viewModel.getCompanies(1, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null) {
                if (response.getData().getItems().isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    adapter.setCompanies(response.getData().getItems());
                }
            } else {
                Toast.makeText(this, "Lỗi kết nối, không thể tải danh sách công ty",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
