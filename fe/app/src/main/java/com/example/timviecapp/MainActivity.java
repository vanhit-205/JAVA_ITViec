package com.example.timviecapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.timviecapp.databinding.ActivityMainBinding;
import com.example.timviecapp.ui.auth.LoginActivity;
import com.example.timviecapp.ui.companies.CompanyListActivity;
import com.example.timviecapp.ui.fragments.CompanyFragment;
import com.example.timviecapp.ui.fragments.HomeFragment;
import com.example.timviecapp.ui.fragments.JobFragment;
import com.example.timviecapp.ui.fragments.ProfileFragment;
import com.example.timviecapp.ui.profile.ProfileActivity;
import com.example.timviecapp.ui.resume.MyResumesActivity;
import com.example.timviecapp.ui.subscriber.SubscriberActivity;
import com.example.timviecapp.utils.TokenManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private HomeFragment homeFragment;
    private JobFragment jobFragment;
    private CompanyFragment companyFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        initFragments();
        setupBottomNavigation();
        setupMenuByRole();
        setupToolbarListeners();

        // Default tab is Home
        loadFragment(homeFragment, "home");
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        jobFragment = new JobFragment();
        companyFragment = new CompanyFragment();
        profileFragment = new ProfileFragment();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_bottom_home) {
                loadFragment(homeFragment, "home");
                binding.toolbar.setTitle("WorkHub - Trang chủ");
                return true;
            } else if (itemId == R.id.menu_bottom_jobs) {
                loadFragment(jobFragment, "jobs");
                binding.toolbar.setTitle("Khám phá Việc làm");
                return true;
            } else if (itemId == R.id.menu_bottom_companies) {
                loadFragment(companyFragment, "companies");
                binding.toolbar.setTitle("Danh sách Công ty");
                return true;
            } else if (itemId == R.id.menu_bottom_profile) {
                loadFragment(profileFragment, "profile");
                binding.toolbar.setTitle("Thông tin tài khoản");
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        Fragment active = fragmentManager.findFragmentByTag(tag);
        if (active == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, active, tag)
                    .commit();
        }
    }

    public void switchToJobsTab() {
        binding.bottomNavigation.setSelectedItemId(R.id.menu_bottom_jobs);
    }

    public void switchToJobsTabWithQuery(String query) {
        switchToJobsTab();
        jobFragment.setQuery(query);
    }

    public void switchToCompaniesTab() {
        binding.bottomNavigation.setSelectedItemId(R.id.menu_bottom_companies);
    }

    private void setupMenuByRole() {
        try {
            binding.toolbar.getMenu().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupToolbarListeners() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.menu_companies) {
                switchToCompaniesTab();
                return true;
            } else if (itemId == R.id.menu_my_resumes) {
                startActivity(new Intent(this, MyResumesActivity.class));
                return true;
            } else if (itemId == R.id.menu_subscriber) {
                startActivity(new Intent(this, SubscriberActivity.class));
                return true;
            } else if (itemId == R.id.menu_manage_jobs) {
                startActivity(new Intent(this, com.example.timviecapp.ui.jobs.ManageJobsActivity.class));
                return true;
            } else if (itemId == R.id.menu_manage_resumes) {
                startActivity(new Intent(this, com.example.timviecapp.ui.resume.ManageResumesActivity.class));
                return true;
            } else if (itemId == R.id.menu_company_info) {
                startActivity(new Intent(this, com.example.timviecapp.ui.companies.ManageCompanyActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                showLogoutDialog();
                return true;
            }
            return false;
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    TokenManager.clear();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
