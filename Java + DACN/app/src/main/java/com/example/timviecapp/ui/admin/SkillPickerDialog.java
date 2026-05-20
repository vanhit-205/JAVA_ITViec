package com.example.timviecapp.ui.admin;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.R;
import com.example.timviecapp.databinding.DialogSkillPickerBinding;
import com.example.timviecapp.models.skill.SkillResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BottomSheet cho phép Employer chọn nhiều kỹ năng khi tạo/sửa Job
 */
public class SkillPickerDialog extends BottomSheetDialog {

    public interface OnSkillSelectedListener {
        void onSkillsSelected(List<Integer> skillIds, List<SkillResponse> skills);
    }

    private final List<SkillResponse> allSkills;
    private final Set<Integer> selectedIds;
    private final OnSkillSelectedListener listener;
    private SkillPickerAdapter adapter;

    public SkillPickerDialog(@NonNull Context context,
                              List<SkillResponse> allSkills,
                              List<Integer> preSelectedIds,
                              OnSkillSelectedListener listener) {
        super(context);
        this.allSkills = allSkills != null ? allSkills : new ArrayList<>();
        this.selectedIds = new HashSet<>(preSelectedIds != null ? preSelectedIds : new ArrayList<>());
        this.listener = listener;
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogSkillPickerBinding binding = DialogSkillPickerBinding.inflate(LayoutInflater.from(getContext()));
        setContentView(binding.getRoot());

        // Setup RecyclerView
        adapter = new SkillPickerAdapter(allSkills, selectedIds);
        binding.rvSkillPicker.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSkillPicker.setAdapter(adapter);

        // Search filter
        binding.etSkillSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Confirm button
        binding.btnConfirm.setOnClickListener(v -> {
            List<Integer> ids = new ArrayList<>(adapter.getSelectedIds());
            List<SkillResponse> selected = new ArrayList<>();
            for (SkillResponse skill : allSkills) {
                if (ids.contains(skill.getId())) {
                    selected.add(skill);
                }
            }
            if (listener != null) {
                listener.onSkillsSelected(ids, selected);
            }
            dismiss();
        });

        // Cancel button
        binding.btnCancel.setOnClickListener(v -> dismiss());
    }

    // ─── Inner Adapter ──────────────────────────────────────────────────────────

    static class SkillPickerAdapter extends RecyclerView.Adapter<SkillPickerAdapter.VH> {
        private final List<SkillResponse> allItems;
        private List<SkillResponse> displayItems;
        private final Set<Integer> selectedIds;

        SkillPickerAdapter(List<SkillResponse> items, Set<Integer> selectedIds) {
            this.allItems = items;
            this.displayItems = new ArrayList<>(items);
            this.selectedIds = selectedIds;
        }

        void filter(String query) {
            if (query == null || query.trim().isEmpty()) {
                displayItems = new ArrayList<>(allItems);
            } else {
                String lower = query.toLowerCase();
                displayItems = new ArrayList<>();
                for (SkillResponse s : allItems) {
                    if (s.getName() != null && s.getName().toLowerCase().contains(lower)) {
                        displayItems.add(s);
                    }
                }
            }
            notifyDataSetChanged();
        }

        Set<Integer> getSelectedIds() {
            return selectedIds;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_skill_picker, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            SkillResponse skill = displayItems.get(position);
            holder.tvName.setText(skill.getName());
            holder.tvDesc.setText(skill.getDescription() != null ? skill.getDescription() : "");
            holder.cbSkill.setChecked(selectedIds.contains(skill.getId()));

            holder.cbSkill.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked) selectedIds.add(skill.getId());
                else selectedIds.remove(skill.getId());
            });

            holder.itemView.setOnClickListener(v -> {
                holder.cbSkill.toggle();
            });
        }

        @Override
        public int getItemCount() {
            return displayItems.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            CheckBox cbSkill;
            TextView tvName, tvDesc;

            VH(@NonNull View v) {
                super(v);
                cbSkill = v.findViewById(R.id.cbSkill);
                tvName = v.findViewById(R.id.tvSkillPickerName);
                tvDesc = v.findViewById(R.id.tvSkillPickerDesc);
            }
        }
    }
}
