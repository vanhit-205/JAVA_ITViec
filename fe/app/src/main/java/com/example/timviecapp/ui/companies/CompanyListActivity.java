package com.example.timviecapp.ui.companies;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityCompanyListBinding;
import com.example.timviecapp.ui.adapters.CompanyAdapter;
import com.example.timviecapp.viewmodels.CompanyViewModel;

/**
 * CompanyListActivity - Màn hình danh sách công ty
 * UC9: Xem danh sách các công ty
 * - Hiển thị danh sách công ty dạng card
 * - Click để xem chi tiết
 */
public class CompanyListActivity extends AppCompatActivity {
    private ActivityCompanyListBinding binding;
    private CompanyViewModel viewModel;
    private CompanyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        setupToolbar();
        setupRecyclerView();
        loadCompanies();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new CompanyAdapter();
        binding.rvCompanies.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCompanies.setAdapter(adapter);

        // UC10: Click vào công ty để xem chi tiết
        adapter.setOnCompanyClickListener(company -> {
            Intent intent = new Intent(this, CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailActivity.EXTRA_COMPANY_ID, company.getId());
            startActivity(intent);
        });
    }

    private void loadCompanies() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        viewModel.getCompanies(1, 20).observe(this, response -> {
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
