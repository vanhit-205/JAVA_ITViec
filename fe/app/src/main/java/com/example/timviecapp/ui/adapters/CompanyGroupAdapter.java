package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timviecapp.databinding.ItemCompanyGroupBinding;
import com.example.timviecapp.ui.resume.ManageResumesActivity.CompanyGroup;
import java.util.ArrayList;
import java.util.List;

public class CompanyGroupAdapter extends RecyclerView.Adapter<CompanyGroupAdapter.ViewHolder> {
    private List<CompanyGroup> groups = new ArrayList<>();
    private OnCompanyGroupClickListener listener;

    public interface OnCompanyGroupClickListener {
        void onCompanyClick(CompanyGroup group);
    }

    public void setOnCompanyGroupClickListener(OnCompanyGroupClickListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<CompanyGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCompanyGroupBinding binding = ItemCompanyGroupBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCompanyGroupBinding binding;

        public ViewHolder(ItemCompanyGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CompanyGroup group) {
            binding.tvCompanyName.setText(group.getCompanyName());

            int jobsCount = group.getJobCount();
            binding.tvCompanyJobsCount.setText(jobsCount + " công việc đang tuyển");

            int resumesCount = group.getResumeCount();
            binding.tvCompanyResumesCount.setText(resumesCount + " hồ sơ ứng tuyển");

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompanyClick(group);
                }
            });
        }
    }
}
