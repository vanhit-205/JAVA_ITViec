package com.example.timviecapp.ui.resume;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.timviecapp.databinding.ActivityApplyJobBinding;
import com.example.timviecapp.models.resume.ResumeRequest;
import com.example.timviecapp.utils.TokenManager;
import com.example.timviecapp.viewmodels.ResumeViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ApplyJobActivity extends AppCompatActivity {
    public static final String EXTRA_JOB_ID = "extra_job_id";
    public static final String EXTRA_JOB_TITLE = "extra_job_title";
    public static final String EXTRA_COMPANY_NAME = "extra_company_name";

    private static final int FILE_PICKER_REQUEST_CODE = 456;

    private ActivityApplyJobBinding binding;
    private ResumeViewModel viewModel;
    private int jobId;
    private Uri selectedFileUri;
    private String selectedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplyJobBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        jobId = getIntent().getIntExtra(EXTRA_JOB_ID, -1);
        if (jobId == -1) {
            Toast.makeText(this, "Không tìm thấy công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ResumeViewModel.class);

        setupToolbar();
        setupJobInfo();
        setupListeners();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupJobInfo() {
        String jobTitle = getIntent().getStringExtra(EXTRA_JOB_TITLE);
        String companyName = getIntent().getStringExtra(EXTRA_COMPANY_NAME);

        binding.tvJobTitle.setText(jobTitle != null ? jobTitle : "N/A");
        binding.tvCompanyName.setText(companyName != null ? companyName : "N/A");

        // Auto-fill email
        String userEmail = TokenManager.getUserEmail();
        if (userEmail != null) {
            binding.etEmail.setText(userEmail);
        }
    }

    private void setupListeners() {
        binding.btnSelectFile.setOnClickListener(v -> openFilePicker());

        binding.btnSubmit.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String cvUrl = binding.etCvUrl.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không đúng định dạng", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = TokenManager.getUserId();

            // Nếu người dùng chọn file -> Thực hiện upload Multipart
            if (selectedFileUri != null) {
                uploadMultipartCV(email, userId);
            } else {
                // FALLBACK: Thực hiện submit bằng link URL truyền thống
                if (cvUrl.isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn tệp tin CV hoặc nhập link CV thay thế", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cvUrl.startsWith("http://") && !cvUrl.startsWith("https://")) {
                    Toast.makeText(this, "Link CV phải bắt đầu bằng http:// hoặc https://", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitUrlCV(email, cvUrl, userId);
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Chọn file CV"), FILE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                selectedFileName = getFileName(selectedFileUri);
                binding.tvSelectedFileName.setText(selectedFileName);
                binding.tvSelectedFileName.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                // Clear fallback input to clarify intention
                binding.etCvUrl.setText("");
            }
        }
    }

    private void uploadMultipartCV(String email, int userId) {
        try {
            File file = getFileFromUri(selectedFileUri);
            if (file == null || !file.exists()) {
                Toast.makeText(this, "Lỗi đọc tệp tin", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody jobIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(jobId));

            String mimeType = getContentResolver().getType(selectedFileUri);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            RequestBody fileBody = RequestBody.create(MediaType.parse(mimeType), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

            viewModel.uploadResume(emailPart, jobIdPart, filePart).observe(this, response -> {
                viewModel.setLoading(false);
                if (response != null && response.isSuccess()) {
                    Toast.makeText(this, "Nộp CV thành công! 🎉", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(this, "Nộp CV thất bại hoặc bạn đã ứng tuyển công việc này rồi.", Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi xử lý tệp tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void submitUrlCV(String email, String cvUrl, int userId) {
        ResumeRequest request = new ResumeRequest(email, cvUrl, userId, jobId);
        viewModel.createResume(request).observe(this, response -> {
            viewModel.setLoading(false);
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Ứng tuyển thành công! 🎉", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Ứng tuyển thất bại. Bạn có thể đã ứng tuyển công việc này rồi.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) throws IOException {
        File tempFile = new File(getCacheDir(), getFileName(uri));
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffers = new byte[1024];
            int read;
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSubmit.setEnabled(!isLoading);
        });
    }
}
