package com.example.timviecapp.ui.jobs;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityAddJobBinding;
import com.example.timviecapp.models.company.CompanyResponse;
import com.example.timviecapp.models.job.JobRequest;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.ui.admin.SkillPickerDialog;
import com.example.timviecapp.viewmodels.CompanyViewModel;
import com.example.timviecapp.viewmodels.JobViewModel;
import com.example.timviecapp.viewmodels.SkillViewModel;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddJobActivity extends AppCompatActivity {
    private ActivityAddJobBinding binding;
    private JobViewModel viewModel;
    private SkillViewModel skillViewModel;
    private CompanyViewModel companyViewModel;
    private int jobId = -1;
    private boolean isEditMode = false;

    private List<SkillResponse> allSkills = new ArrayList<>();
    private List<Integer> selectedSkillIds = new ArrayList<>();
    private List<SkillResponse> selectedSkills = new ArrayList<>();

    // Danh sách cấp bậc có sẵn
    private static final String[] LEVELS = {"INTERN", "FRESHER", "JUNIOR", "MIDDLE", "SENIOR"};
    private static final String[] LEVEL_DISPLAY_NAMES = {"Intern", "Fresher", "Junior", "Middle", "Senior"};

    // Danh sách công ty
    private List<CompanyResponse> allCompanies = new ArrayList<>();
    private int selectedCompanyId = -1;

    private final java.util.Calendar startCalendar = java.util.Calendar.getInstance();
    private final java.util.Calendar endCalendar = java.util.Calendar.getInstance();
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(JobViewModel.class);
        skillViewModel = new ViewModelProvider(this).get(SkillViewModel.class);
        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        jobId = getIntent().getIntExtra("jobId", -1);
        isEditMode = jobId != -1;

        setupToolbar();
        setupLevelDropdown();
        setupListeners();
        observeViewModel();
        loadAllSkills();
        loadAllCompanies();

        if (isEditMode) {
            binding.toolbar.setTitle("Sửa công việc");
            binding.btnSave.setText("Cập nhật");
            loadJobDetails();
        } else {
            binding.toolbar.setTitle("Thêm công việc");
            binding.btnSave.setText("Lưu");
            setupDefaultDates();
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /**
     * Thiết lập dropdown cấp bậc với các giá trị có sẵn
     */
    private void setupLevelDropdown() {
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                LEVEL_DISPLAY_NAMES
        );
        binding.etLevel.setAdapter(levelAdapter);
        binding.etLevel.setOnItemClickListener((parent, view, position, id) -> {
            // Lưu giá trị enum thực tế (INTERN, FRESHER, ...)
            binding.etLevel.setTag(LEVELS[position]);
        });
    }

    /**
     * Tải danh sách công ty từ API và thiết lập dropdown
     */
    private void loadAllCompanies() {
        companyViewModel.getCompanies(0, 200).observe(this, response -> {
            companyViewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null) {
                allCompanies = response.getData().getItems();
                setupCompanyDropdown();
            }
        });
    }

    /**
     * Thiết lập dropdown công ty với danh sách tải từ API
     */
    private void setupCompanyDropdown() {
        List<String> companyNames = new ArrayList<>();
        for (CompanyResponse company : allCompanies) {
            companyNames.add(company.getName());
        }

        ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                companyNames
        );
        binding.etCompanyId.setAdapter(companyAdapter);
        binding.etCompanyId.setOnItemClickListener((parent, view, position, id) -> {
            selectedCompanyId = allCompanies.get(position).getId();
        });

        // Nếu đang ở chế độ sửa, set lại công ty đã chọn
        if (isEditMode && selectedCompanyId != -1) {
            for (int i = 0; i < allCompanies.size(); i++) {
                if (allCompanies.get(i).getId() == selectedCompanyId) {
                    binding.etCompanyId.setText(allCompanies.get(i).getName(), false);
                    break;
                }
            }
        }
    }

    private void setupDefaultDates() {
        endCalendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
        binding.etStartDate.setText(displayDateFormat.format(startCalendar.getTime()));
        binding.etEndDate.setText(displayDateFormat.format(endCalendar.getTime()));
    }

    /**
     * Tải toàn bộ danh sách kỹ năng từ API để hiển thị trong SkillPickerDialog
     */
    private void loadAllSkills() {
        skillViewModel.getSkills(0, 200).observe(this, response -> {
            if (response != null && response.isSuccess() && response.getData() != null
                    && response.getData().getItems() != null) {
                allSkills = response.getData().getItems();
            }
        });
    }

    private void loadJobDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getJobById(jobId).observe(this, response -> {
            binding.progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                JobResponse job = response.getData();
                binding.etName.setText(job.getName());
                binding.etLocation.setText(job.getLocation());
                binding.etSalary.setText(String.valueOf(job.getSalary()));
                binding.etQuantity.setText(String.valueOf(job.getQuantity()));

                // Set level dropdown
                String level = job.getLevel();
                if (level != null) {
                    String levelUpper = level.toUpperCase();
                    for (int i = 0; i < LEVELS.length; i++) {
                        if (LEVELS[i].equals(levelUpper)) {
                            binding.etLevel.setText(LEVEL_DISPLAY_NAMES[i], false);
                            binding.etLevel.setTag(LEVELS[i]);
                            break;
                        }
                    }
                }

                // Set company dropdown
                if (job.getCompany() != null) {
                    selectedCompanyId = job.getCompany().getId();
                    binding.etCompanyId.setText(job.getCompany().getName(), false);
                }

                binding.etDescription.setText(job.getDescription());

                // Pre-populate selected skills
                if (job.getSkills() != null) {
                    selectedSkills = new ArrayList<>(job.getSkills());
                    selectedSkillIds = new ArrayList<>();
                    for (SkillResponse s : selectedSkills) {
                        selectedSkillIds.add(s.getId());
                    }
                    renderSelectedSkillChips();
                }

                try {
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    parser.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    if (job.getStartDate() != null) {
                        Date start = parser.parse(job.getStartDate());
                        startCalendar.setTime(start);
                        binding.etStartDate.setText(displayDateFormat.format(start));
                    }
                    if (job.getEndDate() != null) {
                        Date end = parser.parse(job.getEndDate());
                        endCalendar.setTime(end);
                        binding.etEndDate.setText(displayDateFormat.format(end));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Không thể tải chi tiết công việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Render lại ChipGroup kỹ năng đã chọn với nút X để bỏ chọn
     */
    private void renderSelectedSkillChips() {
        binding.cgSelectedSkills.removeAllViews();
        for (SkillResponse skill : selectedSkills) {
            Chip chip = new Chip(this);
            chip.setText(skill.getName());
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColorResource(com.example.timviecapp.R.color.colorPrimary);
            chip.setTextColor(getResources().getColor(android.R.color.white));
            chip.setCloseIconTint(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(android.R.color.white)));
            chip.setOnCloseIconClickListener(v -> {
                selectedSkillIds.remove((Integer) skill.getId());
                selectedSkills.remove(skill);
                renderSelectedSkillChips();
            });
            binding.cgSelectedSkills.addView(chip);
        }
    }

    private void showDatePicker(java.util.Calendar calendar, android.widget.EditText editText) {
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            editText.setText(displayDateFormat.format(calendar.getTime()));
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void setupListeners() {
        binding.etStartDate.setOnClickListener(v -> showDatePicker(startCalendar, binding.etStartDate));
        binding.etEndDate.setOnClickListener(v -> showDatePicker(endCalendar, binding.etEndDate));

        // Skill picker button
        binding.btnPickSkills.setOnClickListener(v -> {
            SkillPickerDialog dialog = new SkillPickerDialog(
                    this,
                    allSkills,
                    selectedSkillIds,
                    (ids, skills) -> {
                        selectedSkillIds = ids;
                        selectedSkills = skills;
                        renderSelectedSkillChips();
                    }
            );
            dialog.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String location = binding.etLocation.getText().toString().trim();
            String salaryStr = binding.etSalary.getText().toString().trim();
            String quantityStr = binding.etQuantity.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();

            // Lấy level từ tag (giá trị enum thực tế)
            String level = binding.etLevel.getTag() != null
                    ? binding.etLevel.getTag().toString()
                    : binding.etLevel.getText().toString().trim().toUpperCase();

            if (name.isEmpty() || location.isEmpty() || salaryStr.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (level.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn cấp bậc", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCompanyId == -1) {
                Toast.makeText(this, "Vui lòng chọn công ty", Toast.LENGTH_SHORT).show();
                return;
            }

            double salary = Double.parseDouble(salaryStr);
            int quantity = Integer.parseInt(quantityStr);

            apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String startDate = apiDateFormat.format(startCalendar.getTime());
            String endDate = apiDateFormat.format(endCalendar.getTime());

            JobRequest request = new JobRequest(
                    name, location, salary, quantity, level, description,
                    startDate, endDate, true, selectedCompanyId, selectedSkillIds
            );

            binding.progressBar.setVisibility(View.VISIBLE);
            if (isEditMode) {
                viewModel.updateJob(jobId, request).observe(this, response -> {
                    viewModel.setLoading(false);
                    binding.progressBar.setVisibility(View.GONE);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Cập nhật công việc thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi cập nhật công việc", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewModel.createJob(request).observe(this, response -> {
                    viewModel.setLoading(false);
                    binding.progressBar.setVisibility(View.GONE);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi khi thêm công việc", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }
}
