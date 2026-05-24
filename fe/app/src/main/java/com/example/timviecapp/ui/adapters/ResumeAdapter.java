package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timviecapp.databinding.ItemResumeBinding;
import com.example.timviecapp.models.resume.ResumeResponse;
import java.util.ArrayList;
import java.util.List;

public class ResumeAdapter extends RecyclerView.Adapter<ResumeAdapter.ResumeViewHolder> {
    private List<ResumeResponse> resumes = new ArrayList<>();
    private OnResumeActionListener listener;

    public interface OnResumeActionListener {
        void onView(ResumeResponse resume);
        void onEdit(ResumeResponse resume);
        void onDelete(ResumeResponse resume);
    }

    public void setOnResumeActionListener(OnResumeActionListener listener) {
        this.listener = listener;
    }

    public void setResumes(List<ResumeResponse> resumes) {
        this.resumes = resumes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResumeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemResumeBinding binding = ItemResumeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ResumeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumeViewHolder holder, int position) {
        holder.bind(resumes.get(position));
    }

    @Override
    public int getItemCount() {
        return resumes.size();
    }

    class ResumeViewHolder extends RecyclerView.ViewHolder {
        private final ItemResumeBinding binding;

        public ResumeViewHolder(ItemResumeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ResumeResponse resume) {
            // Dùng trường phẳng từ backend (không phải lồng nhau)
            String jobTitle = resume.getJobName() != null && !resume.getJobName().isEmpty()
                    ? resume.getJobName() : "Công việc ẩn danh";
            binding.tvResumeTitle.setText(jobTitle);

            String companyName = resume.getCompanyName() != null && !resume.getCompanyName().isEmpty()
                    ? resume.getCompanyName() : "Công ty ẩn danh";
            binding.tvCompanyName.setText(companyName);

            String candidateName = resume.getUsername() != null && !resume.getUsername().isEmpty()
                    ? resume.getUsername() : "Chưa cập nhật tên";
            String candidateEmail = resume.getEmail() != null ? resume.getEmail() : "N/A";
            binding.tvCandidateInfo.setText("Ứng viên: " + candidateName + " (" + candidateEmail + ")");

            binding.chipStatus.setText(resume.getStatus());
            binding.tvResumeDate.setText("Mã hồ sơ: #" + resume.getId());

            // Bind Skill Matching data if present (Giai đoạn 3 & 4)
            if (resume.getMatchScore() != null) {
                binding.layoutMatchScore.setVisibility(android.view.View.VISIBLE);
                double score = resume.getMatchScore();
                binding.tvMatchScoreValue.setText(String.format(java.util.Locale.getDefault(), "%.1f%%", score));
                
                // Color-code based on score range
                int pillColor;
                if (score >= 75.0) {
                    pillColor = android.graphics.Color.parseColor("#388E3C"); // Dark Green
                } else if (score >= 50.0) {
                    pillColor = android.graphics.Color.parseColor("#F57C00"); // Orange
                } else {
                    pillColor = android.graphics.Color.parseColor("#D32F2F"); // Red
                }
                
                android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
                drawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(16);
                drawable.setColor(pillColor);
                binding.tvMatchScoreValue.setBackground(drawable);

                // Skills comparison details
                if ((resume.getMatchedSkills() != null && !resume.getMatchedSkills().isEmpty()) || 
                    (resume.getMissingSkills() != null && !resume.getMissingSkills().isEmpty())) {
                    
                    binding.layoutSkillsComparison.setVisibility(android.view.View.VISIBLE);
                    
                    if (resume.getMatchedSkills() != null && !resume.getMatchedSkills().isEmpty()) {
                        StringBuilder msText = new StringBuilder("✔ Kỹ năng đáp ứng: ");
                        for (int i = 0; i < resume.getMatchedSkills().size(); i++) {
                            msText.append(resume.getMatchedSkills().get(i).getName());
                            if (i < resume.getMatchedSkills().size() - 1) msText.append(", ");
                        }
                        binding.tvMatchedSkills.setText(msText.toString());
                        binding.tvMatchedSkills.setVisibility(android.view.View.VISIBLE);
                    } else {
                        binding.tvMatchedSkills.setVisibility(android.view.View.GONE);
                    }

                    if (resume.getMissingSkills() != null && !resume.getMissingSkills().isEmpty()) {
                        StringBuilder msText = new StringBuilder("✘ Kỹ năng thiếu: ");
                        for (int i = 0; i < resume.getMissingSkills().size(); i++) {
                            msText.append(resume.getMissingSkills().get(i).getName());
                            if (i < resume.getMissingSkills().size() - 1) msText.append(", ");
                        }
                        binding.tvMissingSkills.setText(msText.toString());
                        binding.tvMissingSkills.setVisibility(android.view.View.VISIBLE);
                    } else {
                        binding.tvMissingSkills.setVisibility(android.view.View.GONE);
                    }
                } else {
                    binding.layoutSkillsComparison.setVisibility(android.view.View.GONE);
                }
            } else {
                binding.layoutMatchScore.setVisibility(android.view.View.GONE);
                binding.layoutSkillsComparison.setVisibility(android.view.View.GONE);
            }

            // Beautiful status-based coloring for Chip
            if ("PENDING".equalsIgnoreCase(resume.getStatus())) {
                binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_orange_light);
                binding.chipStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(android.R.color.black));
            } else if ("APPROVED".equalsIgnoreCase(resume.getStatus()) || "ACCEPTED".equalsIgnoreCase(resume.getStatus())) {
                binding.chipStatus.setChipBackgroundColorResource(android.R.color.holo_green_light);
                binding.chipStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(android.R.color.black));
            } else {
                binding.chipStatus.setChipBackgroundColorResource(android.R.color.darker_gray);
                binding.chipStatus.setTextColor(binding.getRoot().getContext().getResources().getColor(android.R.color.white));
            }

            binding.btnView.setOnClickListener(v -> {
                if (listener != null) listener.onView(resume);
            });
            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(resume);
            });
            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(resume);
            });
        }
    }
}
