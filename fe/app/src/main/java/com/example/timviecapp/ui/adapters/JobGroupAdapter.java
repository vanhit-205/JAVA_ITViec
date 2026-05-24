package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timviecapp.databinding.ItemJobGroupBinding;
import com.example.timviecapp.ui.resume.ManageResumesActivity.JobGroup;
import java.util.ArrayList;
import java.util.List;

public class JobGroupAdapter extends RecyclerView.Adapter<JobGroupAdapter.ViewHolder> {
    private List<JobGroup> groups = new ArrayList<>();
    private OnJobGroupClickListener listener;

    public interface OnJobGroupClickListener {
        void onJobClick(JobGroup group);
    }

    public void setOnJobGroupClickListener(OnJobGroupClickListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<JobGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJobGroupBinding binding = ItemJobGroupBinding.inflate(
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
        private final ItemJobGroupBinding binding;

        public ViewHolder(ItemJobGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JobGroup group) {
            binding.tvJobName.setText(group.getJobName());
            binding.tvJobResumesCount.setText(group.getResumeCount() + " hồ sơ ứng tuyển");

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onJobClick(group);
                }
            });
        }
    }
}
