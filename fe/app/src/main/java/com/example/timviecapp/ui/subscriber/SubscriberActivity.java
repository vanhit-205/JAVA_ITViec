package com.example.timviecapp.ui.subscriber;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivitySubscriberBinding;
import com.example.timviecapp.models.common.ApiResponse;
import com.example.timviecapp.models.common.PaginationResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.models.subscriber.SubscriberRequest;
import com.example.timviecapp.repository.SkillRepository;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.SubscriberViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * SubscriberActivity - Đăng ký nhận thông báo việc làm
 * UC30: Tạo mới Subscriber
 * UC31: Cập nhật Subscriber
 * - Nhập tên, email
 * - Chọn kỹ năng quan tâm (ChipGroup)
 * - Đăng ký / Cập nhật / Hủy đăng ký
 */
public class SubscriberActivity extends AppCompatActivity {
    private ActivitySubscriberBinding binding;
    private SubscriberViewModel viewModel;
    private SkillRepository skillRepository;
    private List<SkillResponse> allSkills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SubscriberViewModel.class);
        skillRepository = new SkillRepository();

        setupToolbar();
        prefillUserInfo();
        loadSkills();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Tự động điền thông tin từ user đã đăng nhập
     */
    private void prefillUserInfo() {
        String name = TokenManager.getUserName();
        String email = TokenManager.getUserEmail();

        if (name != null) binding.etName.setText(name);
        if (email != null) binding.etEmail.setText(email);
    }

    /**
     * Tải danh sách skills để hiển thị dưới dạng Chip
     */
    private void loadSkills() {
        binding.progressBarSkills.setVisibility(View.VISIBLE);

        skillRepository.getSkills(0, 100).observe(this, response -> {
            binding.progressBarSkills.setVisibility(View.GONE);

            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null) {
                allSkills = response.getData().getItems();
                displaySkillChips();
            } else {
                Toast.makeText(this, "Không thể tải danh sách kỹ năng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị kỹ năng dưới dạng Chip có thể chọn (multi-select)
     */
    private void displaySkillChips() {
        binding.cgSkills.removeAllViews();
        for (SkillResponse skill : allSkills) {
            Chip chip = new Chip(this);
            chip.setText(skill.getName());
            chip.setCheckable(true);
            chip.setTag(skill.getId());
            binding.cgSkills.addView(chip);
        }
    }

    /**
     * Lấy danh sách ID của các skill đã chọn
     */
    private List<Integer> getSelectedSkillIds() {
        List<Integer> selectedIds = new ArrayList<>();
        for (int i = 0; i < binding.cgSkills.getChildCount(); i++) {
            Chip chip = (Chip) binding.cgSkills.getChildAt(i);
            if (chip.isChecked()) {
                selectedIds.add((Integer) chip.getTag());
            }
        }
        return selectedIds;
    }

    private void setupListeners() {
        // UC30: Đăng ký nhận thông báo
        binding.btnSubscribe.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            List<Integer> skillIds = getSelectedSkillIds();

            // Validate
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (skillIds.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 kỹ năng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo request và gọi API
            SubscriberRequest request = new SubscriberRequest(email, name, skillIds);
            viewModel.createSubscriber(request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Đăng ký nhận thông báo thành công! 🎉",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Đăng ký thất bại. Email có thể đã được đăng ký.",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        // UC31: Hủy đăng ký
        binding.btnUnsubscribe.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng hủy đăng ký đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSubscribe.setEnabled(!isLoading);
        });
    }
}
