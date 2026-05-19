package com.example.timviecapp.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityProfileBinding;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.ProfileViewModel;

/**
 * ProfileActivity - Trang thông tin cá nhân
 * UC4: Cập nhật thông tin cá nhân
 */
public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProfileViewModel viewModel;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        currentUserId = TokenManager.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupListeners();
        observeViewModel();
        loadUserProfile();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getUserById(currentUserId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                binding.etEmail.setText(response.getData().getEmail());
                binding.etName.setText(response.getData().getName());
                binding.etAge.setText(response.getData().getAge() != null ? response.getData().getAge() : "");
                binding.etGender.setText(response.getData().getGender() != null ? response.getData().getGender() : "");
                binding.etAddress.setText(response.getData().getAddress() != null ? response.getData().getAddress() : "");
            } else {
                Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String age = binding.etAge.getText().toString().trim();
            String gender = binding.etGender.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.length() < 3 || name.length() > 50) {
                Toast.makeText(this, "Họ tên phải từ 3 đến 50 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!age.isEmpty()) {
                try {
                    int ageVal = Integer.parseInt(age);
                    if (ageVal < 18 || ageVal > 100) {
                        Toast.makeText(this, "Tuổi phải từ 18 đến 100", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Tuổi phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!gender.isEmpty()) {
                String upper = gender.toUpperCase();
                if (!upper.equals("NAM") && !upper.equals("MALE") &&
                    !upper.equals("NỮ") && !upper.equals("NU") && !upper.equals("FEMALE") &&
                    !upper.equals("KHÁC") && !upper.equals("KHAC") && !upper.equals("OTHER")) {
                    Toast.makeText(this, "Giới tính phải là 'Nam', 'Nữ' hoặc 'Khác'", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (address.length() > 500) {
                Toast.makeText(this, "Địa chỉ không được vượt quá 500 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateUserRequest request = new UpdateUserRequest();
            request.setName(name);
            request.setAge(age);
            request.setGender(gender);
            request.setAddress(address);
            request.setRole(TokenManager.getUserRole());

            viewModel.updateUser(currentUserId, request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    
                    // Cập nhật TokenManager
                    TokenManager.saveUserInfo(
                            currentUserId,
                            TokenManager.getUserEmail(),
                            name,
                            TokenManager.getUserRole()
                    );
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
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
