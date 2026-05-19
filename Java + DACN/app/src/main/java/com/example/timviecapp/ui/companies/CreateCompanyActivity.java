package com.example.timviecapp.ui.companies;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityCreateCompanyBinding;
import com.example.timviecapp.models.company.CompanyRequest;
import com.example.timviecapp.viewmodels.CompanyViewModel;

public class CreateCompanyActivity extends AppCompatActivity {
    private ActivityCreateCompanyBinding binding;
    private CompanyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        setupToolbar();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String logo = binding.etLogo.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và địa chỉ công ty", Toast.LENGTH_SHORT).show();
                return;
            }

            CompanyRequest request = new CompanyRequest(name, description, address, logo);

            viewModel.createCompany(request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    int companyId = response.getData().getId();
                    Toast.makeText(this, "Thêm công ty thành công! Mã công ty: " + companyId, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm công ty", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }
}
