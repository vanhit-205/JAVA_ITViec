package com.example.timviecapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.R;
import com.example.timviecapp.databinding.ActivityUserManagementBinding;
import com.example.timviecapp.models.auth.UserResponse;
import com.example.timviecapp.models.user.CreateUserRequest;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.ui.adapters.UserAdapter;
import com.example.timviecapp.viewmodels.UserViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class UserManagementActivity extends AppCompatActivity {
    private ActivityUserManagementBinding binding;
    private UserViewModel viewModel;
    private UserAdapter adapter;
    private String currentSearchKeyword = "";
    private Timer searchTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSearchBox();
        observeViewModel();
        loadUsers();

        binding.fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter();
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(adapter);

        adapter.setOnUserActionListener(new UserAdapter.OnUserActionListener() {
            @Override
            public void onLockUnlock(UserResponse user) {
                performLockUnlock(user);
            }

            @Override
            public void onChangeRole(UserResponse user) {
                showChangeRoleDialog(user);
            }
        });
    }

    private void setupSearchBox() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            currentSearchKeyword = s.toString().trim();
                            loadUsers();
                        });
                    }
                }, 500); // Debounce of 500ms to optimize server calls
            }
        });
    }

    private void loadUsers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        viewModel.getUsers(0, 50, currentSearchKeyword).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                if (response.getData().getItems().isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    adapter.setUsers(null);
                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                    adapter.setUsers(response.getData().getItems());
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLockUnlock(UserResponse user) {
        boolean isLocked = user.isLocked() != null ? user.isLocked() : false;
        binding.progressBar.setVisibility(View.VISIBLE);

        if (isLocked) {
            viewModel.unlockUser(user.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Mở khóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(this, "Mở khóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewModel.lockUser(user.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Khóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(this, "Khóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void showChangeRoleDialog(UserResponse user) {
        String[] roles = {"ROLE_ADMIN", "ROLE_RECRUITER", "ROLE_CANDIDATE"};
        int checkedItem = 2; // Default to CANDIDATE
        
        String currentRole = user.getRole() != null ? user.getRole() : "";
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equalsIgnoreCase(currentRole)) {
                checkedItem = i;
                break;
            }
        }

        final int[] selectedItemHolder = {checkedItem};

        new AlertDialog.Builder(this)
                .setTitle("Thay đổi Vai trò (Role)")
                .setSingleChoiceItems(roles, checkedItem, (dialog, which) -> {
                    selectedItemHolder[0] = which;
                })
                .setPositiveButton("Thay đổi", (dialog, which) -> {
                    String newRoleStr = roles[selectedItemHolder[0]];
                    
                    UpdateUserRequest request = new UpdateUserRequest();
                    request.setName(user.getName());
                    request.setAge(user.getAge());
                    request.setGender(user.getGender());
                    request.setAddress(user.getAddress());
                    // backend handles role conversion or nested role update
                    request.setRole(newRoleStr);

                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.updateUser(user.getId(), request).observe(this, response -> {
                        binding.progressBar.setVisibility(View.GONE);
                        viewModel.setLoading(false);
                        if (response != null && response.isSuccess()) {
                            Toast.makeText(this, "Thay đổi Vai trò thành công", Toast.LENGTH_SHORT).show();
                            loadUsers();
                        } else {
                            Toast.makeText(this, "Thay đổi Vai trò thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);

        EditText etUsername = view.findViewById(R.id.etAddUsername);
        EditText etEmail = view.findViewById(R.id.etAddEmail);
        EditText etPassword = view.findViewById(R.id.etAddPassword);
        EditText etAge = view.findViewById(R.id.etAddAge);
        EditText etPhone = view.findViewById(R.id.etAddPhone);
        Spinner spinnerGender = view.findViewById(R.id.spinnerAddGender);
        Spinner spinnerRole = view.findViewById(R.id.spinnerAddRole);
        EditText etAddress = view.findViewById(R.id.etAddAddress);

        // Setup Gender Spinner
        String[] genders = {"MALE", "FEMALE", "OTHER"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Setup Role Spinner
        String[] roles = {"ROLE_CANDIDATE", "ROLE_RECRUITER", "ROLE_ADMIN"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        builder.setView(view)
                .setPositiveButton("Thêm", null) // Set null to manually override close behavior
                .setNegativeButton("Hủy", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String ageStr = etAge.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String gender = spinnerGender.getSelectedItem().toString();
            String role = spinnerRole.getSelectedItem().toString();
            String address = etAddress.getText().toString().trim();

            if (username.length() < 3 || username.length() > 50) {
                etUsername.setError("Tên đăng nhập phải từ 3 đến 50 ký tự");
                return;
            }
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Email không đúng định dạng");
                return;
            }
            if (password.length() < 8 || password.length() > 100) {
                etPassword.setError("Mật khẩu phải từ 8 đến 100 ký tự");
                return;
            }

            Integer age = null;
            if (!ageStr.isEmpty()) {
                try {
                    age = Integer.parseInt(ageStr);
                    if (age < 18 || age > 100) {
                        etAge.setError("Tuổi phải từ 18 đến 100");
                        return;
                    }
                } catch (NumberFormatException e) {
                    etAge.setError("Tuổi không hợp lệ");
                    return;
                }
            } else {
                etAge.setError("Vui lòng nhập tuổi");
                return;
            }

            if (!phone.isEmpty() && !phone.matches("^[0-9]{10,15}$")) {
                etPhone.setError("Số điện thoại phải gồm 10-15 chữ số");
                return;
            }

            CreateUserRequest request = new CreateUserRequest(email, password, username, role);
            request.setAge(String.valueOf(age));
            request.setGender(gender);
            if (!phone.isEmpty()) {
                request.setPhone(phone);
            }
            request.setAddress(address);

            binding.progressBar.setVisibility(View.VISIBLE);
            dialog.dismiss();

            viewModel.createUser(request).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Thêm người dùng mới thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    String errorMsg = "Thêm người dùng mới thất bại";
                    if (response != null && response.getError() != null && response.getError().getMessage() != null) {
                        errorMsg += ": " + response.getError().getMessage();
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
