package com.example.timviecapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivitySkillManagementBinding;
import com.example.timviecapp.models.skill.SkillRequest;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.ui.adapters.SkillAdapter;
import com.example.timviecapp.viewmodels.SkillViewModel;

public class SkillManagementActivity extends AppCompatActivity {
    private ActivitySkillManagementBinding binding;
    private SkillViewModel viewModel;
    private SkillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySkillManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SkillViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
        loadSkills();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new SkillAdapter();
        binding.rvSkills.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSkills.setAdapter(adapter);

        adapter.setOnSkillActionListener(new SkillAdapter.OnSkillActionListener() {
            @Override
            public void onEdit(SkillResponse skill) {
                showSkillDialog(skill);
            }

            @Override
            public void onDelete(SkillResponse skill) {
                showDeleteConfirmDialog(skill);
            }
        });
    }

    private void setupListeners() {
        binding.fabAddSkill.setOnClickListener(v -> showSkillDialog(null));
    }

    private void loadSkills() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        viewModel.getSkills(0, 50).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                if (response.getData().getItems().isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    adapter.setSkills(null);
                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                    adapter.setSkills(response.getData().getItems());
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách kỹ năng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSkillDialog(SkillResponse skill) {
        boolean isEdit = skill != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Cập nhật kỹ năng" : "Thêm kỹ năng mới");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        final EditText etName = new EditText(this);
        etName.setHint("Tên kỹ năng (VD: Java, Python)");
        if (isEdit) etName.setText(skill.getName());
        layout.addView(etName);

        final EditText etDesc = new EditText(this);
        etDesc.setHint("Mô tả kỹ năng");
        if (isEdit) etDesc.setText(skill.getDescription());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        layout.addView(etDesc, params);

        builder.setView(layout);

        builder.setPositiveButton(isEdit ? "Cập nhật" : "Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Tên kỹ năng không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            SkillRequest request = new SkillRequest(name, desc);
            binding.progressBar.setVisibility(View.VISIBLE);

            if (isEdit) {
                viewModel.updateSkill(skill.getId(), request).observe(this, response -> {
                    binding.progressBar.setVisibility(View.GONE);
                    viewModel.setLoading(false);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        loadSkills();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewModel.createSkill(request).observe(this, response -> {
                    binding.progressBar.setVisibility(View.GONE);
                    viewModel.setLoading(false);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Thêm kỹ năng thành công", Toast.LENGTH_SHORT).show();
                        loadSkills();
                    } else {
                        Toast.makeText(this, "Thêm kỹ năng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDeleteConfirmDialog(SkillResponse skill) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa kỹ năng '" + skill.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    viewModel.deleteSkill(skill.getId()).observe(this, response -> {
                        binding.progressBar.setVisibility(View.GONE);
                        viewModel.setLoading(false);
                        if (response != null) {
                            Toast.makeText(this, "Xóa kỹ năng thành công", Toast.LENGTH_SHORT).show();
                            loadSkills();
                        } else {
                            Toast.makeText(this, "Xóa kỹ năng thất bại", Toast.LENGTH_SHORT).show();
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
