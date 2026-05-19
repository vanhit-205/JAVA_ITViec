package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.databinding.ItemUserBinding;
import com.example.timviecapp.models.auth.UserResponse;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<UserResponse> users = new ArrayList<>();
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onLockUnlock(UserResponse user);
        void onEnableDisable(UserResponse user);
        void onChangeRole(UserResponse user);
    }

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<UserResponse> newList) {
        users.clear();
        if (newList != null) {
            users.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        ViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserResponse user) {
            binding.tvUserName.setText(user.getName() != null ? user.getName() : "N/A");
            binding.tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            
            // Set Role Badge
            String roleName = "USER";
            if (user.getRole() != null) {
                roleName = user.getRole();
            }
            binding.tvUserRole.setText(roleName);
            if (roleName.contains("ADMIN")) {
                binding.tvUserRole.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
                binding.tvUserRole.setTextColor(android.graphics.Color.parseColor("#C62828"));
            } else if (roleName.contains("RECRUITER")) {
                binding.tvUserRole.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
                binding.tvUserRole.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            } else {
                binding.tvUserRole.setBackgroundColor(android.graphics.Color.parseColor("#E3F2FD"));
                binding.tvUserRole.setTextColor(android.graphics.Color.parseColor("#1565C0"));
            }

            // Set Status Badge
            boolean isLocked = user.isLocked() != null ? user.isLocked() : false;
            boolean isEnabled = user.isEnabled() != null ? user.isEnabled() : true;

            if (isLocked) {
                binding.tvUserStatus.setText("LOCKED");
                binding.tvUserStatus.setBackgroundColor(android.graphics.Color.parseColor("#FFF3E0"));
                binding.tvUserStatus.setTextColor(android.graphics.Color.parseColor("#E65100"));
            } else if (!isEnabled) {
                binding.tvUserStatus.setText("DISABLED");
                binding.tvUserStatus.setBackgroundColor(android.graphics.Color.parseColor("#ECEFF1"));
                binding.tvUserStatus.setTextColor(android.graphics.Color.parseColor("#37474F"));
            } else {
                binding.tvUserStatus.setText("ACTIVE");
                binding.tvUserStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"));
                binding.tvUserStatus.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
            }

            binding.btnOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                
                popup.getMenu().add(isLocked ? "Mở khóa tài khoản" : "Khóa tài khoản");
                popup.getMenu().add(isEnabled ? "Vô hiệu hóa tài khoản" : "Kích hoạt tài khoản");
                popup.getMenu().add("Thay đổi Vai trò (Role)");

                popup.setOnMenuItemClickListener(item -> {
                    if (listener == null) return false;
                    String title = item.getTitle().toString();
                    if (title.contains("khóa") || title.contains("Khóa")) {
                        listener.onLockUnlock(user);
                        return true;
                    } else if (title.contains("hiệu hóa") || title.contains("Kích hoạt")) {
                        listener.onEnableDisable(user);
                        return true;
                    } else if (title.contains("Vai trò")) {
                        listener.onChangeRole(user);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
