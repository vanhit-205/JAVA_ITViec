package com.example.timviecapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.R;
import com.example.timviecapp.databinding.ActivitySubscriberManagementBinding;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.models.subscriber.SubscriberRequest;
import com.example.timviecapp.models.subscriber.SubscriberResponse;
import com.example.timviecapp.repository.SkillRepository;
import com.example.timviecapp.ui.adapters.SubscriberAdapter;
import com.example.timviecapp.viewmodels.SubscriberViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ManageSubscribersActivity extends AppCompatActivity {

    private ActivitySubscriberManagementBinding binding;
    private SubscriberViewModel viewModel;
    private SkillRepository skillRepository;
    private SubscriberAdapter adapter;
    private List<SubscriberResponse> allSubscribers = new ArrayList<>();
    private List<SkillResponse> availableSkills = new ArrayList<>();
    
    private String searchKeyword = "";
    private Timer searchTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscriberManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SubscriberViewModel.class);
        skillRepository = new SkillRepository();

        setupToolbar();
        setupRecyclerView();
        setupSearch();
        observeViewModel();
        
        loadSubscribers();
        loadAllAvailableSkills();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new SubscriberAdapter();
        binding.rvSubscribers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSubscribers.setAdapter(adapter);

        adapter.setOnSubscriberActionListener(new SubscriberAdapter.OnSubscriberActionListener() {
            @Override
            public void onToggleStatus(SubscriberResponse subscriber, boolean isEnabled) {
                performToggleStatus(subscriber, isEnabled);
            }

            @Override
            public void onEdit(SubscriberResponse subscriber) {
                showEditSkillsBottomSheet(subscriber);
            }

            @Override
            public void onDelete(SubscriberResponse subscriber) {
                showDeleteConfirmDialog(subscriber);
            }

            @Override
            public void onViewDetail(SubscriberResponse subscriber) {
                showDetailDialog(subscriber);
            }
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchTimer != null) searchTimer.cancel();
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            searchKeyword = s.toString().trim();
                            filterSubscribers(searchKeyword);
                        });
                    }
                }, 500); // Debounce 500ms
            }
        });
    }

    private void loadSubscribers() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        // Lấy danh sách subscribers từ API
        viewModel.getSubscribers(1, 100).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                allSubscribers = response.getData().getItems();
                if (allSubscribers == null) allSubscribers = new ArrayList<>();
                filterSubscribers(searchKeyword);
            } else {
                Toast.makeText(this, "Không thể tải danh sách người đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllAvailableSkills() {
        skillRepository.getSkills(0, 200).observe(this, response -> {
            if (response != null && response.isSuccess() && response.getData() != null) {
                availableSkills = response.getData().getItems();
            }
        });
    }

    private void filterSubscribers(String keyword) {
        if (keyword.isEmpty()) {
            adapter.setList(allSubscribers);
            binding.tvEmpty.setVisibility(allSubscribers.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            String query = keyword.toLowerCase();
            List<SubscriberResponse> filtered = new ArrayList<>();
            for (SubscriberResponse sub : allSubscribers) {
                if ((sub.getEmail() != null && sub.getEmail().toLowerCase().contains(query)) ||
                    (sub.getName() != null && sub.getName().toLowerCase().contains(query))) {
                    filtered.add(sub);
                }
            }
            adapter.setList(filtered);
            binding.tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void performToggleStatus(SubscriberResponse subscriber, boolean isEnabled) {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (isEnabled) {
            viewModel.enableSubscriber(subscriber.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    subscriber.setEnabled(true);
                    Toast.makeText(this, "Đã kích hoạt người đăng ký: " + subscriber.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged(); // Reset trạng thái UI nếu lỗi
                    Toast.makeText(this, "Lỗi khi kích hoạt trạng thái", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewModel.disableSubscriber(subscriber.getId()).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    subscriber.setEnabled(false);
                    Toast.makeText(this, "Đã tạm khóa người đăng ký: " + subscriber.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged(); // Reset trạng thái UI
                    Toast.makeText(this, "Lỗi khi vô hiệu hóa trạng thái", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showEditSkillsBottomSheet(SubscriberResponse subscriber) {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_subscriber_skills, null);
        
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        ChipGroup cgSkills = dialogView.findViewById(R.id.cgSkills);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        tvTitle.setText("Kỹ năng của: " + subscriber.getEmail());

        // Đổ danh sách kỹ năng hiện có dưới dạng Checkable Chips
        cgSkills.removeAllViews();
        List<Integer> currentSkillIds = new ArrayList<>();
        if (subscriber.getSkills() != null) {
            for (SkillResponse s : subscriber.getSkills()) {
                currentSkillIds.add(s.getId());
            }
        }

        for (SkillResponse skill : availableSkills) {
            Chip chip = new Chip(this);
            chip.setText(skill.getName());
            chip.setCheckable(true);
            chip.setTag(skill.getId());
            if (currentSkillIds.contains(skill.getId())) {
                chip.setChecked(true);
            }
            cgSkills.addView(chip);
        }

        btnSave.setOnClickListener(v -> {
            List<Integer> selectedIds = new ArrayList<>();
            for (int i = 0; i < cgSkills.getChildCount(); i++) {
                Chip chip = (Chip) cgSkills.getChildAt(i);
                if (chip.isChecked()) {
                    selectedIds.add((Integer) chip.getTag());
                }
            }

            if (selectedIds.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 kỹ năng", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            sheet.dismiss();

            SubscriberRequest request = new SubscriberRequest(subscriber.getEmail(), subscriber.getName(), selectedIds);
            viewModel.updateSubscriber(subscriber.getId(), request).observe(this, response -> {
                binding.progressBar.setVisibility(View.GONE);
                viewModel.setLoading(false);
                if (response != null && response.isSuccess() && response.getData() != null) {
                    Toast.makeText(this, "Cập nhật kỹ năng thành công", Toast.LENGTH_SHORT).show();
                    loadSubscribers(); // Tải lại danh sách
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        });

        sheet.setContentView(dialogView);
        sheet.show();
    }

    private void showDetailDialog(SubscriberResponse subscriber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_subscriber_detail, null);

        TextView tvId = view.findViewById(R.id.tvDetailId);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvStatus = view.findViewById(R.id.tvDetailStatus);
        TextView tvDate = view.findViewById(R.id.tvDetailDate);
        ChipGroup cgSkills = view.findViewById(R.id.cgDetailSkills);

        tvId.setText("#" + subscriber.getId());
        tvName.setText(subscriber.getName());
        tvEmail.setText(subscriber.getEmail());
        tvStatus.setText(subscriber.isEnabled() ? "Đang hoạt động" : "Tạm khóa");
        tvDate.setText(subscriber.getCreatedAt());

        cgSkills.removeAllViews();
        if (subscriber.getSkills() != null) {
            for (SkillResponse s : subscriber.getSkills()) {
                Chip chip = new Chip(this);
                chip.setText(s.getName());
                chip.setCheckable(false);
                chip.setClickable(false);
                cgSkills.addView(chip);
            }
        }

        builder.setView(view)
               .setPositiveButton("Đóng", null)
               .show();
    }

    private void showDeleteConfirmDialog(SubscriberResponse subscriber) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa người đăng ký '" + subscriber.getEmail() + "' khỏi danh sách không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.deleteSubscriber(subscriber.getId()).observe(this, response -> {
                        binding.progressBar.setVisibility(View.GONE);
                        viewModel.setLoading(false);
                        if (response != null && response.isSuccess()) {
                            Toast.makeText(this, "Xóa người đăng ký thành công", Toast.LENGTH_SHORT).show();
                            loadSubscribers(); // Tải lại danh sách
                        } else {
                            Toast.makeText(this, "Xóa người đăng ký thất bại", Toast.LENGTH_SHORT).show();
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
