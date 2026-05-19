package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.R;
import com.example.timviecapp.databinding.ItemSkillBinding;
import com.example.timviecapp.models.skill.SkillResponse;

import java.util.ArrayList;
import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
    private final List<SkillResponse> skills = new ArrayList<>();
    private OnSkillActionListener listener;

    public interface OnSkillActionListener {
        void onEdit(SkillResponse skill);
        void onDelete(SkillResponse skill);
    }

    public void setOnSkillActionListener(OnSkillActionListener listener) {
        this.listener = listener;
    }

    public void setSkills(List<SkillResponse> newList) {
        skills.clear();
        if (newList != null) {
            skills.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSkillBinding binding = ItemSkillBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(skills.get(position));
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSkillBinding binding;

        ViewHolder(ItemSkillBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SkillResponse skill) {
            binding.tvSkillName.setText(skill.getName());
            binding.tvSkillDesc.setText(skill.getDescription() != null ? skill.getDescription() : "Không có mô tả");

            binding.btnOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenu().add("Chỉnh sửa");
                popup.getMenu().add("Xóa");
                popup.setOnMenuItemClickListener(item -> {
                    if (listener == null) return false;
                    if (item.getTitle().equals("Chỉnh sửa")) {
                        listener.onEdit(skill);
                        return true;
                    } else if (item.getTitle().equals("Xóa")) {
                        listener.onDelete(skill);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
