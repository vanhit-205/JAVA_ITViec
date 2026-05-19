package com.example.timviecapp.ui.companies;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityCreateCompanyBinding;
import com.example.timviecapp.models.company.CompanyRequest;
import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.viewmodels.CompanyViewModel;

public class CreateCompanyActivity extends AppCompatActivity {
    private ActivityCreateCompanyBinding binding;
    private CompanyViewModel viewModel;
    private int companyId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        // Check if we are in Edit Mode
        companyId = getIntent().getIntExtra("companyId", -1);
        isEditMode = companyId != -1;

        setupToolbar();
        setupListeners();
        observeViewModel();

        if (isEditMode) {
            binding.toolbar.setTitle("Sửa thông tin công ty");
            binding.btnSave.setText("Cập nhật");
            loadCompanyDetails();
        } else {
            binding.toolbar.setTitle("Thêm công ty mới");
            binding.btnSave.setText("Lưu");
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadCompanyDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getCompanyById(companyId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                CompanyResponse company = response.getData();
                binding.etName.setText(company.getName());
                binding.etAddress.setText(company.getAddress());
                binding.etLogo.setText(company.getLogo());
                binding.etDescription.setText(company.getDescription());
            } else {
                Toast.makeText(this, "Không thể tải chi tiết công ty", Toast.LENGTH_SHORT).show();
            }
        });
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

            binding.progressBar.setVisibility(View.VISIBLE);
            if (isEditMode) {
                viewModel.updateCompany(companyId, request).observe(this, response -> {
                    viewModel.setLoading(false);
                    binding.progressBar.setVisibility(View.GONE);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Cập nhật công ty thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi cập nhật công ty", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewModel.createCompany(request).observe(this, response -> {
                    viewModel.setLoading(false);
                    binding.progressBar.setVisibility(View.GONE);
                    if (response != null && response.isSuccess() && response.getData() != null) {
                        int newId = response.getData().getId();
                        Toast.makeText(this, "Thêm công ty thành công! Mã công ty: " + newId, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm công ty", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }
}
