package com.example.timviecapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivityUserManagementBinding;
import com.example.timviecapp.models.auth.UserResponse;
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
            public void onEnableDisable(UserResponse user) {
                performEnableDisable(user);
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

    private void performEnableDisable(UserResponse user) {
        boolean isEnabled = user.isEnabled() != null ? user.isEnabled() : true;
        binding.progressBar.setVisibility(View.VISIBLE);

        if (isEnabled) {
            viewModel.disableUser(user.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Vô hiệu hóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(this, "Vô hiệu hóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewModel.enableUser(user.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Kích hoạt tài khoản thành công", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(this, "Kích hoạt tài khoản thất bại", Toast.LENGTH_SHORT).show();
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
}
