package com.example.timviecapp.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.R;
import com.example.timviecapp.models.user.RoleUpgradeRequest;
import com.example.timviecapp.viewmodels.ProfileViewModel;

import java.util.ArrayList;

public class ManageRoleUpgradesActivity extends AppCompatActivity implements RoleUpgradeAdapter.OnItemActionListener {

    private ProfileViewModel viewModel;
    private RoleUpgradeAdapter adapter;
    private RecyclerView rvRoleUpgrades;
    private View progressBar;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_role_upgrades);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvRoleUpgrades = findViewById(R.id.rvRoleUpgrades);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        rvRoleUpgrades.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoleUpgradeAdapter(new ArrayList<>(), this);
        rvRoleUpgrades.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        observeViewModel();
        loadRequests();
    }

    private void loadRequests() {
        progressBar.setVisibility(View.VISIBLE);
        viewModel.getAllRequests().observe(this, response -> {
            progressBar.setVisibility(View.GONE);
            viewModel.setLoading(false);
            if (response != null && response.isSuccess() && response.getData() != null) {
                if (response.getData().isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    rvRoleUpgrades.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvRoleUpgrades.setVisibility(View.VISIBLE);
                    adapter.updateData(response.getData());
                }
            } else {
                Toast.makeText(this, "Không thể tải danh sách yêu cầu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onApprove(RoleUpgradeRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("Phê duyệt nâng cấp")
                .setMessage("Bạn có chắc chắn muốn phê duyệt cho tài khoản này lên Nhà tuyển dụng?")
                .setPositiveButton("Phê duyệt", (dialog, which) -> {
                    progressBar.setVisibility(View.VISIBLE);
                    viewModel.approveRequest(request.getId()).observe(this, response -> {
                        progressBar.setVisibility(View.GONE);
                        viewModel.setLoading(false);
                        if (response != null && response.isSuccess()) {
                            Toast.makeText(this, "Đã phê duyệt thành công!", Toast.LENGTH_SHORT).show();
                            loadRequests();
                        } else {
                            Toast.makeText(this, "Phê duyệt thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onReject(RoleUpgradeRequest request) {
        EditText etReason = new EditText(this);
        etReason.setHint("Nhập lý do từ chối...");
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Từ chối yêu cầu")
                .setView(etReason)
                .setPositiveButton("Từ chối", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(d -> {
            etReason.setPadding(padding, padding, padding, padding);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String notes = etReason.getText().toString().trim();
                if (notes.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập lý do từ chối", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                
                progressBar.setVisibility(View.VISIBLE);
                viewModel.rejectRequest(request.getId(), notes).observe(this, response -> {
                    progressBar.setVisibility(View.GONE);
                    viewModel.setLoading(false);
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(this, "Đã từ chối yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                        loadRequests();
                    } else {
                        Toast.makeText(this, "Từ chối thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
        dialog.show();
    }
}
