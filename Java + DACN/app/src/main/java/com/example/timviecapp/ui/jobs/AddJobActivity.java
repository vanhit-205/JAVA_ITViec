package com.example.timviecapp.ui.jobs;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityAddJobBinding;
import com.example.timviecapp.models.job.JobRequest;
import com.example.timviecapp.viewmodels.JobViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddJobActivity extends AppCompatActivity {
    private ActivityAddJobBinding binding;
    private JobViewModel viewModel;

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

        setupToolbar();
        setupDefaultDates();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDefaultDates() {
        // Mặc định hạn nộp là sau 30 ngày
        endCalendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
        binding.etStartDate.setText(displayDateFormat.format(startCalendar.getTime()));
        binding.etEndDate.setText(displayDateFormat.format(endCalendar.getTime()));
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

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String location = binding.etLocation.getText().toString().trim();
            String salaryStr = binding.etSalary.getText().toString().trim();
            String quantityStr = binding.etQuantity.getText().toString().trim();
            String level = binding.etLevel.getText().toString().trim().toUpperCase();
            String companyIdStr = binding.etCompanyId.getText().toString().trim();
            String description = binding.etDescription.getText().toString().trim();

            if (name.isEmpty() || location.isEmpty() || salaryStr.isEmpty() || quantityStr.isEmpty() || level.isEmpty() || companyIdStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double salary = Double.parseDouble(salaryStr);
            int quantity = Integer.parseInt(quantityStr);
            int companyId = Integer.parseInt(companyIdStr);

            apiDateFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String startDate = apiDateFormat.format(startCalendar.getTime());
            String endDate = apiDateFormat.format(endCalendar.getTime());

            JobRequest request = new JobRequest(
                    name, location, salary, quantity, level, description,
                    startDate, endDate, true, companyId, new ArrayList<>()
            );

            viewModel.createJob(request).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Thêm công việc thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm công việc", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
        });
    }
}
