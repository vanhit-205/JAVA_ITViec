package com.example.timviecapp.ui.resume;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityResumeDetailBinding;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.models.resume.ResumeResponse;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.ResumeViewModel;

/**
 * ResumeDetailActivity - Xem chi tiết hồ sơ ứng tuyển
 * UC27: Lấy thông tin chi tiết resume
 * - Hiển thị email, trạng thái, tên công việc, link CV
 */
public class ResumeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RESUME_ID = "extra_resume_id";
    private ActivityResumeDetailBinding binding;
    private ResumeViewModel viewModel;
    private int resumeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        resumeId = getIntent().getIntExtra(EXTRA_RESUME_ID, -1);
        if (resumeId == -1) {
            Toast.makeText(this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        loadResumeDetail();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * UC27: Tải và hiển thị chi tiết resume
     */
    private void loadResumeDetail() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.layoutContent.setVisibility(View.GONE);
        binding.cardStatusNotification.setVisibility(View.GONE);

        viewModel.getResumeById(resumeId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                binding.layoutContent.setVisibility(View.VISIBLE);
                displayResumeDetail(response.getData());
            } else {
                Toast.makeText(this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * Hiển thị thông tin chi tiết resume
     */
    private void displayResumeDetail(ResumeResponse resume) {
        // Trạng thái
        binding.chipStatus.setText(resume.getStatus());

        // Email ứng viên
        binding.tvEmail.setText("Email: " + (resume.getEmail() != null ? resume.getEmail() : "N/A"));

        // Thông tin công việc
        if (resume.getJob() != null) {
            binding.tvJobName.setText(resume.getJob().getName());
            if (resume.getJob().getCompany() != null) {
                binding.tvJobCompany.setText("Công ty: " + resume.getJob().getCompany().getName());
            } else {
                binding.tvJobCompany.setText("Công ty: N/A");
            }
        } else {
            binding.tvJobName.setText("N/A");
            binding.tvJobCompany.setText("");
        }

        // Link CV
        binding.tvCvUrl.setText(resume.getUrl() != null ? resume.getUrl() : "Không có link CV");

        // Nhận diện vai trò người dùng hiện tại
        String role = TokenManager.getUserRole();
        if (role == null) {
            role = "";
        }

        // 1. Phân quyền và hiển thị các nút thao tác tương ứng
        if (role.toUpperCase().contains("ADMIN") || role.toUpperCase().contains("RECRUITER") || role.toUpperCase().contains("EMPLOYER")) {
            binding.layoutCandidateActions.setVisibility(View.GONE);

            // Nếu nhà tuyển dụng mở CV ở trạng thái PENDING, tự động cập nhật lên REVIEWING
            if ("PENDING".equalsIgnoreCase(resume.getStatus())) {
                viewModel.updateResumeStatus(resumeId, "REVIEWING").observe(this, updateResponse -> {
                    if (updateResponse != null && updateResponse.isSuccess()) {
                        resume.setStatus("REVIEWING");
                        binding.chipStatus.setText("REVIEWING");
                        showStatusNotification(resume.getStatus());
                    }
                });
            }

            // Nếu đã được duyệt (APPROVED) hoặc từ chối (REJECTED), ẩn các nút duyệt hồ sơ
            if ("APPROVED".equalsIgnoreCase(resume.getStatus()) || "REJECTED".equalsIgnoreCase(resume.getStatus())) {
                binding.layoutRecruiterActions.setVisibility(View.GONE);
            } else {
                binding.layoutRecruiterActions.setVisibility(View.VISIBLE);

                binding.btnApprove.setOnClickListener(v -> {
                    performStatusTransition(resume.getStatus(), "APPROVED");
                });

                binding.btnReject.setOnClickListener(v -> {
                    performStatusTransition(resume.getStatus(), "REJECTED");
                });
            }
        } else {
            // Vai trò Ứng viên / USER
            binding.layoutRecruiterActions.setVisibility(View.GONE);

            if ("APPROVED".equalsIgnoreCase(resume.getStatus())) {
                binding.layoutCandidateActions.setVisibility(View.VISIBLE);
                binding.btnEditCv.setEnabled(false);
                binding.btnEditCv.setText("Đã được duyệt - Không thể sửa");
                binding.btnEditCv.setOnClickListener(null);
            } else if ("REJECTED".equalsIgnoreCase(resume.getStatus())) {
                binding.layoutCandidateActions.setVisibility(View.VISIBLE);
                binding.btnEditCv.setEnabled(false);
                binding.btnEditCv.setText("Đã bị từ chối - Không thể sửa");
                binding.btnEditCv.setOnClickListener(null);
            } else {
                binding.layoutCandidateActions.setVisibility(View.VISIBLE);
                binding.btnEditCv.setEnabled(true);
                binding.btnEditCv.setText("Chỉnh sửa CV");
                binding.btnEditCv.setOnClickListener(v -> showEditCvDialog(resume));
            }
        }

        // 2. Hiển thị thông báo trạng thái trực quan cho ứng viên
        showStatusNotification(resume.getStatus());
    }

    /**
     * Hiển thị bảng thông báo trạng thái duyệt CV trực quan cho ứng viên
     */
    private void showStatusNotification(String status) {
        binding.cardStatusNotification.setVisibility(View.VISIBLE);
        if ("APPROVED".equalsIgnoreCase(status)) {
            binding.cardStatusNotification.setCardBackgroundColor(android.graphics.Color.parseColor("#E8F5E9")); // Light green
            binding.ivNotificationIcon.setColorFilter(android.graphics.Color.parseColor("#2E7D32")); // Green
            binding.tvNotificationMessage.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            binding.tvNotificationMessage.setText("Chúc mừng! Hồ sơ của bạn đã được Nhà tuyển dụng duyệt. Vui lòng chờ liên hệ phỏng vấn.");
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            binding.cardStatusNotification.setCardBackgroundColor(android.graphics.Color.parseColor("#FFEBEE")); // Light red
            binding.ivNotificationIcon.setColorFilter(android.graphics.Color.parseColor("#C62828")); // Red
            binding.tvNotificationMessage.setTextColor(android.graphics.Color.parseColor("#C62828"));
            binding.tvNotificationMessage.setText("Rất tiếc! Hồ sơ của bạn chưa phù hợp với vị trí này. Hãy tiếp tục ứng tuyển các công việc khác nhé!");
        } else if ("REVIEWING".equalsIgnoreCase(status)) {
            binding.cardStatusNotification.setCardBackgroundColor(android.graphics.Color.parseColor("#E3F2FD")); // Light blue
            binding.ivNotificationIcon.setColorFilter(android.graphics.Color.parseColor("#1565C0")); // Blue
            binding.tvNotificationMessage.setTextColor(android.graphics.Color.parseColor("#1565C0"));
            binding.tvNotificationMessage.setText("Hồ sơ của bạn đang được Nhà tuyển dụng xem xét.");
        } else {
            binding.cardStatusNotification.setCardBackgroundColor(android.graphics.Color.parseColor("#F5F5F5")); // Light gray
            binding.ivNotificationIcon.setColorFilter(android.graphics.Color.parseColor("#616161")); // Gray
            binding.tvNotificationMessage.setTextColor(android.graphics.Color.parseColor("#616161"));
            binding.tvNotificationMessage.setText("Hồ sơ đã nộp thành công và đang chờ xử lý.");
        }
    }

    /**
     * Dialog hỗ trợ ứng viên cập nhật/sửa link CV của mình
     */
    private void showEditCvDialog(ResumeResponse resume) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa link CV");

        final EditText input = new EditText(this);
        input.setText(resume.getUrl());
        input.setHint("Nhập link CV mới của bạn (ví dụ: Google Drive, Dropbox,...)");
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        
        // Thêm padding cho EditText
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        lp.setMargins(padding, padding, padding, padding);
        container.addView(input, lp);
        
        builder.setView(container);

        builder.setPositiveButton("Lưu lại", (dialog, which) -> {
            String newUrl = input.getText().toString().trim();
            if (newUrl.isEmpty()) {
                Toast.makeText(this, "Link CV không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = (resume.getUser() != null) ? resume.getUser().getId() : TokenManager.getUserId();
            ResumeRequest request = new ResumeRequest(
                    resume.getEmail(),
                    newUrl,
                    userId,
                    resume.getJob() != null ? resume.getJob().getId() : 0
            );

            binding.progressBar.setVisibility(View.VISIBLE);
            viewModel.updateResume(resumeId, request).observe(this, updateResponse -> {
                binding.progressBar.setVisibility(View.GONE);
                if (updateResponse != null && updateResponse.isSuccess()) {
                    Toast.makeText(this, "Cập nhật CV thành công", Toast.LENGTH_SHORT).show();
                    loadResumeDetail();
                } else {
                    Toast.makeText(this, "Không thể cập nhật CV (Hồ sơ đã được duyệt hoặc lỗi hệ thống)", Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Quản lý chuyển đổi trạng thái duyệt CV an toàn theo luật Backend (State Machine)
     */
    private void performStatusTransition(String currentStatus, String targetStatus) {
        binding.progressBar.setVisibility(View.VISIBLE);

        if ("PENDING".equalsIgnoreCase(currentStatus) && "APPROVED".equalsIgnoreCase(targetStatus)) {
            // Luật backend: PENDING -> REVIEWING -> APPROVED (cần 2 bước)
            viewModel.updateResumeStatus(resumeId, "REVIEWING").observe(this, response1 -> {
                if (response1 != null && response1.isSuccess()) {
                    viewModel.updateResumeStatus(resumeId, "APPROVED").observe(this, response2 -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response2 != null && response2.isSuccess()) {
                            Toast.makeText(this, "Duyệt hồ sơ thành công", Toast.LENGTH_SHORT).show();
                            loadResumeDetail();
                        } else {
                            Toast.makeText(this, "Duyệt hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Không thể chuyển đổi trạng thái sang REVIEWING", Toast.LENGTH_SHORT).show();
                }
            });
        } else if ("REJECTED".equalsIgnoreCase(currentStatus) && "APPROVED".equalsIgnoreCase(targetStatus)) {
            // Luật backend: REJECTED -> REVIEWING -> APPROVED (cần 2 bước)
            viewModel.updateResumeStatus(resumeId, "REVIEWING").observe(this, response1 -> {
                if (response1 != null && response1.isSuccess()) {
                    viewModel.updateResumeStatus(resumeId, "APPROVED").observe(this, response2 -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response2 != null && response2.isSuccess()) {
                            Toast.makeText(this, "Duyệt hồ sơ thành công", Toast.LENGTH_SHORT).show();
                            loadResumeDetail();
                        } else {
                            Toast.makeText(this, "Duyệt hồ sơ thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Không thể chuyển đổi trạng thái sang REVIEWING", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Chuyển đổi trực tiếp được hỗ trợ: REVIEWING -> APPROVED, PENDING -> REJECTED, v.v.
            viewModel.updateResumeStatus(resumeId, targetStatus).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    loadResumeDetail();
                } else {
                    Toast.makeText(this, "Không thể cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
