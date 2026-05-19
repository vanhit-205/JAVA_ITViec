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
            binding.tvResumeTitle.setText(resume.getEmail()); // Using email as title if title is missing in model
            binding.chipStatus.setText(resume.getStatus());
            binding.tvResumeDate.setText("ID: " + resume.getId());

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
