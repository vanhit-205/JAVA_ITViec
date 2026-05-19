package com.example.timviecapp.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.MainActivity;
import com.example.timviecapp.databinding.ActivityLoginBinding;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.LoginViewModel;

/**
 * LoginActivity - Màn hình đăng nhập
 * UC1: Đăng nhập
 * - Nhập email + mật khẩu
 * - Validate input
 * - Gọi API đăng nhập
 * - Lưu token + user info
 * - Auto-login nếu đã có token
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-login: Nếu đã có token, chuyển thẳng đến MainActivity
        if (TokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        // Nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Validate đầu vào
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.login(email, password).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    // Lưu token
                    TokenManager.saveToken(response.getData().getAccessToken());
                    if (response.getData().getRefreshToken() != null) {
                        TokenManager.saveRefreshToken(response.getData().getRefreshToken());
                    }

                    // Lưu thông tin user
                    if (response.getData().getUser() != null) {
                        TokenManager.saveUserInfo(
                                response.getData().getUser().getId(),
                                response.getData().getUser().getEmail(),
                                response.getData().getUser().getName(),
                                response.getData().getUser().getRole()
                        );
                    }

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    Toast.makeText(this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Nút Đăng ký
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Nút Quên mật khẩu - UC5: Navigate đến ForgotPasswordActivity
        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
        });
    }

    /**
     * Chuyển đến MainActivity và clear back stack
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
