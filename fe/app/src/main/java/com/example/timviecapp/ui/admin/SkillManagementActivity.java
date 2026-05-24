package com.example.timviecapp.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.timviecapp.databinding.ActivitySkillManagementBinding;
import com.example.timviecapp.models.skill.SkillRequest;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.ui.adapters.SkillAdapter;
import com.example.timviecapp.viewmodels.SkillViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SkillManagementActivity extends AppCompatActivity {
    private ActivitySkillManagementBinding binding;
    private SkillViewModel viewModel;
    private SkillAdapter adapter;
    private List<SkillResponse> allSkills = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySkillManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SkillViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSearch();
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
                showSkillBottomSheet(skill);
            }

            @Override
            public void onDelete(SkillResponse skill) {
                showDeleteConfirmDialog(skill);
            }
        });
    }

    /**
     * Nhóm 3: SearchView tìm kiếm kỹ năng theo tên (client-side)
     */
    private void setupSearch() {
        binding.searchViewSkill.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSkills(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSkills(newText);
                return true;
            }
        });
    }

    private void filterSkills(String query) {
        if (TextUtils.isEmpty(query)) {
            adapter.setSkills(allSkills);
            binding.tvEmpty.setVisibility(allSkills.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            String lower = query.toLowerCase();
            List<SkillResponse> filtered = new ArrayList<>();
            for (SkillResponse s : allSkills) {
                if (s.getName() != null && s.getName().toLowerCase().contains(lower)) {
                    filtered.add(s);
                }
            }
            adapter.setSkills(filtered);
            binding.tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void setupListeners() {
        binding.fabAddSkill.setOnClickListener(v -> showSkillBottomSheet(null));
    }

    private void loadSkills() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        viewModel.getSkills(0, 200).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);

            if (response != null && response.isSuccess() && response.getData() != null) {
                allSkills = response.getData().getItems() != null
                        ? response.getData().getItems() : new ArrayList<>();
                if (allSkills.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                    adapter.setSkills(null);
                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                    adapter.setSkills(allSkills);
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách kỹ năng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Nhóm 3: BottomSheetDialog thay thế AlertDialog cũ
     */
    private void showSkillBottomSheet(SkillResponse skill) {
        boolean isEdit = skill != null;
        BottomSheetDialog sheet = new BottomSheetDialog(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pd = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pd, pd, pd, pd);

        // Title
        android.widget.TextView tvTitle = new android.widget.TextView(this);
        tvTitle.setText(isEdit ? "✏️  Cập nhật kỹ năng" : "➕  Thêm kỹ năng mới");
        tvTitle.setTextSize(18f);
        tvTitle.setTextColor(0xFF1A1A1A);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvTitle);

        // Name field
        com.google.android.material.textfield.TextInputLayout tilName = new com.google.android.material.textfield.TextInputLayout(this);
        tilName.setHint("Tên kỹ năng (VD: Java, React)");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = (int) (16 * getResources().getDisplayMetrics().density);
        tilName.setLayoutParams(lp);

        com.google.android.material.textfield.TextInputEditText etName = new com.google.android.material.textfield.TextInputEditText(tilName.getContext());
        etName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (isEdit) etName.setText(skill.getName());
        tilName.addView(etName);
        layout.addView(tilName);

        // Description field
        com.google.android.material.textfield.TextInputLayout tilDesc = new com.google.android.material.textfield.TextInputLayout(this);
        tilDesc.setHint("Mô tả ngắn");
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp2.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        tilDesc.setLayoutParams(lp2);

        com.google.android.material.textfield.TextInputEditText etDesc = new com.google.android.material.textfield.TextInputEditText(tilDesc.getContext());
        etDesc.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        etDesc.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etDesc.setLines(2);
        if (isEdit) etDesc.setText(skill.getDescription());
        tilDesc.addView(etDesc);
        layout.addView(tilDesc);

        // Confirm button
        android.widget.Button btnConfirm = new android.widget.Button(this);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (52 * getResources().getDisplayMetrics().density));
        lp3.topMargin = (int) (20 * getResources().getDisplayMetrics().density);
        btnConfirm.setLayoutParams(lp3);
        btnConfirm.setText(isEdit ? "Cập nhật" : "Thêm kỹ năng");
        btnConfirm.setAllCaps(false);
        btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                getResources().getColor(com.example.timviecapp.R.color.colorPrimary)));
        btnConfirm.setTextColor(getResources().getColor(android.R.color.white));
        layout.addView(btnConfirm);

        sheet.setContentView(layout);
        sheet.show();

        btnConfirm.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String desc = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";
            if (name.isEmpty()) {
                Toast.makeText(this, "Tên kỹ năng không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            SkillRequest request = new SkillRequest(name, desc);
            binding.progressBar.setVisibility(View.VISIBLE);
            sheet.dismiss();

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
    }

    private void showDeleteConfirmDialog(SkillResponse skill) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa kỹ năng '" + skill.getName() + "' không?\n" +
                        "Các công việc đang yêu cầu kỹ năng này sẽ bị ảnh hưởng.")
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
