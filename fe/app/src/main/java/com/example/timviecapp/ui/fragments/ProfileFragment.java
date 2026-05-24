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

import com.example.timviecapp.databinding.FragmentProfileBinding;
import com.example.timviecapp.models.user.UpdateUserRequest;
import com.example.timviecapp.ui.admin.SkillManagementActivity;
import com.example.timviecapp.ui.admin.UserManagementActivity;
import com.example.timviecapp.ui.admin.ManageRoleUpgradesActivity;
import com.example.timviecapp.ui.auth.LoginActivity;
import com.example.timviecapp.ui.companies.ManageCompanyActivity;
import com.example.timviecapp.ui.jobs.ManageJobsActivity;
import com.example.timviecapp.ui.resume.ManageResumesActivity;
import com.example.timviecapp.ui.resume.MyResumesActivity;
import com.example.timviecapp.ui.subscriber.SubscriberActivity;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.ProfileViewModel;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        currentUserId = TokenManager.getUserId();

        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        setupRolePanels();
        setupListeners();
        observeViewModel();
        loadUserProfile();
    }

    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getUserById(currentUserId).observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                binding.etEmail.setText(response.getData().getEmail());
                binding.etName.setText(response.getData().getName());
                binding.etAge.setText(response.getData().getAge() != null ? response.getData().getAge() : "");
                binding.etGender.setText(response.getData().getGender() != null ? response.getData().getGender() : "");
                binding.etAddress.setText(response.getData().getAddress() != null ? response.getData().getAddress() : "");
            } else {
                Toast.makeText(getContext(), "Không thể tải thông tin cá nhân", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRolePanels() {
        String role = TokenManager.getUserRole();
        if (role == null) {
            role = "";
        }

        if (role.toUpperCase().contains("ADMIN") || role.toUpperCase().contains("RECRUITER") || role.toUpperCase().contains("EMPLOYER")) {
            binding.layoutAdminPanel.setVisibility(View.VISIBLE);
            binding.layoutCandidatePanel.setVisibility(View.GONE);

            if (role.toUpperCase().contains("ADMIN")) {
                binding.btnManageSkills.setVisibility(View.VISIBLE);
                binding.btnManageUsers.setVisibility(View.VISIBLE);
                binding.btnManageRoleUpgrades.setVisibility(View.VISIBLE);
            } else {
                binding.btnManageSkills.setVisibility(View.GONE);
                binding.btnManageUsers.setVisibility(View.GONE);
                binding.btnManageRoleUpgrades.setVisibility(View.GONE);
            }
        } else {
            binding.layoutAdminPanel.setVisibility(View.GONE);
            binding.layoutCandidatePanel.setVisibility(View.VISIBLE);
            checkUpgradeRequestStatus();
        }
    }

    private void setupListeners() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String age = binding.etAge.getText().toString().trim();
            String gender = binding.etGender.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.length() < 3 || name.length() > 50) {
                Toast.makeText(getContext(), "Họ tên phải từ 3 đến 50 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!age.isEmpty()) {
                try {
                    int ageVal = Integer.parseInt(age);
                    if (ageVal < 18 || ageVal > 100) {
                        Toast.makeText(getContext(), "Tuổi phải từ 18 đến 100", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Tuổi phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!gender.isEmpty()) {
                String upper = gender.toUpperCase();
                if (!upper.equals("NAM") && !upper.equals("MALE") &&
                    !upper.equals("NỮ") && !upper.equals("NU") && !upper.equals("FEMALE") &&
                    !upper.equals("KHÁC") && !upper.equals("KHAC") && !upper.equals("OTHER")) {
                    Toast.makeText(getContext(), "Giới tính phải là 'Nam', 'Nữ' hoặc 'Khác'", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (address.length() > 500) {
                Toast.makeText(getContext(), "Địa chỉ không được vượt quá 500 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateUserRequest request = new UpdateUserRequest();
            request.setName(name);
            request.setAge(age);
            request.setGender(gender);
            request.setAddress(address);
            request.setRole(TokenManager.getUserRole());

            viewModel.updateUser(currentUserId, request).observe(getViewLifecycleOwner(), response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    // Sync backward-compatible TokenManager
                    TokenManager.saveUserInfo(
                            currentUserId,
                            TokenManager.getUserEmail(),
                            name,
                            TokenManager.getUserRole()
                    );
                } else {
                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Navigation listeners for Candidate Panel
        binding.btnMyResumes.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), MyResumesActivity.class));
        });

        binding.btnSubscriber.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SubscriberActivity.class));
        });

        binding.btnRequestUpgrade.setOnClickListener(v -> {
            showUpgradeRequestDialog();
        });

        // Navigation listeners for Admin Panel
        binding.btnManageJobs.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ManageJobsActivity.class));
        });

        binding.btnManageCompanies.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ManageCompanyActivity.class));
        });

        binding.btnManageResumes.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ManageResumesActivity.class));
        });

        binding.btnManageSkills.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SkillManagementActivity.class));
        });

        binding.btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), UserManagementActivity.class));
        });

        binding.btnManageRoleUpgrades.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ManageRoleUpgradesActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        TokenManager.clear();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void checkUpgradeRequestStatus() {
        viewModel.getMyRequest().observe(getViewLifecycleOwner(), response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                String status = response.getData().getStatus();
                if ("PENDING".equals(status)) {
                    binding.btnRequestUpgrade.setText("⏳ Đang chờ Admin xét duyệt...");
                    binding.btnRequestUpgrade.setEnabled(false);
                } else if ("REJECTED".equals(status)) {
                    String reason = response.getData().getAdminNotes();
                    if (reason == null || reason.isEmpty()) {
                        reason = "Không rõ lý do";
                    }
                    binding.btnRequestUpgrade.setText("❌ Từ chối (Gửi lại yêu cầu nâng cấp)");
                    binding.btnRequestUpgrade.setEnabled(true);
                    Toast.makeText(getContext(), "Yêu cầu nâng cấp bị từ chối: " + reason, Toast.LENGTH_LONG).show();
                } else {
                    binding.btnRequestUpgrade.setText("🚀 Nâng cấp lên tài khoản tuyển dụng");
                    binding.btnRequestUpgrade.setEnabled(true);
                }
            } else {
                binding.btnRequestUpgrade.setText("🚀 Nâng cấp lên tài khoản tuyển dụng");
                binding.btnRequestUpgrade.setEnabled(true);
            }
        });
    }

    private void showUpgradeRequestDialog() {
        android.view.View dialogView = LayoutInflater.from(getContext()).inflate(com.example.timviecapp.R.layout.dialog_request_upgrade, null);
        com.google.android.material.textfield.TextInputEditText etCompany = dialogView.findViewById(com.example.timviecapp.R.id.etCompanyName);
        com.google.android.material.textfield.TextInputEditText etReason = dialogView.findViewById(com.example.timviecapp.R.id.etReason);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                .setTitle("Nâng cấp tài khoản tuyển dụng")
                .setView(dialogView)
                .setPositiveButton("Gửi yêu cầu", (dialog, which) -> {
                    String companyName = etCompany.getText().toString().trim();
                    String reason = etReason.getText().toString().trim();

                    if (companyName.isEmpty() || reason.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    viewModel.requestUpgrade(companyName, reason).observe(getViewLifecycleOwner(), response -> {
                        viewModel.setLoading(false);
                        if (response != null && response.isSuccess()) {
                            Toast.makeText(getContext(), "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                            checkUpgradeRequestStatus();
                        } else {
                            Toast.makeText(getContext(), "Gửi yêu cầu thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.btnSave.setEnabled(!isLoading);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
