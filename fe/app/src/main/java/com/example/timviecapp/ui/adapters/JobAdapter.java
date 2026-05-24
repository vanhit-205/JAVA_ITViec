package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timviecapp.databinding.ItemJobBinding;
import com.example.timviecapp.models.job.JobResponse;
import com.example.timviecapp.models.skill.SkillResponse;
import com.google.android.material.chip.Chip;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<JobResponse> jobs = new ArrayList<>();
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(JobResponse job);
    }

    public void setOnJobClickListener(OnJobClickListener listener) {
        this.listener = listener;
    }

    public void setJobs(List<JobResponse> jobs) {
        this.jobs = jobs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJobBinding binding = ItemJobBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new JobViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        holder.bind(jobs.get(position));
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    class JobViewHolder extends RecyclerView.ViewHolder {
        private final ItemJobBinding binding;

        public JobViewHolder(ItemJobBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onJobClick(jobs.get(pos));
                }
            });
        }

        public void bind(JobResponse job) {
            binding.tvJobTitle.setText(job.getName());
            binding.tvCompanyName.setText(job.getCompany() != null ? job.getCompany().getName() : "N/A");
            binding.tvLocation.setText(job.getLocation());
            binding.chipLevel.setText(job.getLevel());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            binding.tvSalary.setText(currencyFormat.format(job.getSalary()));
            
            binding.tvDescription.setText(job.getDescription());
            binding.tvDate.setText("Ngày bắt đầu: " + com.example.timviecapp.utils.DateUtils.formatIsoDate(job.getStartDate()));

            // Render skill chips (tối đa 3, phần còn lại hiện "+N nữa")
            binding.cgSkills.removeAllViews();
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                int max = Math.min(3, job.getSkills().size());
                for (int i = 0; i < max; i++) {
                    SkillResponse skill = job.getSkills().get(i);
                    Chip chip = new Chip(itemView.getContext());
                    chip.setText(skill.getName());
                    chip.setChipMinHeight(0f);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chip.setChipBackgroundColorResource(com.example.timviecapp.R.color.colorPrimary);
                    chip.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));
                    chip.setTextSize(11f);
                    binding.cgSkills.addView(chip);
                }
                if (job.getSkills().size() > 3) {
                    Chip more = new Chip(itemView.getContext());
                    more.setText("+" + (job.getSkills().size() - 3) + " nữa");
                    more.setChipMinHeight(0f);
                    more.setClickable(false);
                    more.setCheckable(false);
                    more.setTextSize(11f);
                    binding.cgSkills.addView(more);
                }
            }
        }
    }
}
