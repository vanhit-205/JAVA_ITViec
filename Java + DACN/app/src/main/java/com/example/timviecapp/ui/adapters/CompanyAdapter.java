package com.example.timviecapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.databinding.ItemCompanyBinding;
import com.example.timviecapp.models.company.CompanyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * CompanyAdapter - Hiển thị danh sách công ty dạng card
 * UC9: Xem danh sách công ty (dành cho Ứng viên)
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {
    private List<CompanyResponse> companies = new ArrayList<>();
    private OnCompanyClickListener listener;

    public interface OnCompanyClickListener {
        void onCompanyClick(CompanyResponse company);
    }

    public void setOnCompanyClickListener(OnCompanyClickListener listener) {
        this.listener = listener;
    }

    public void setCompanies(List<CompanyResponse> companies) {
        this.companies = companies != null ? companies : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCompanyBinding binding = ItemCompanyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CompanyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        holder.bind(companies.get(position));
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder {
        private final ItemCompanyBinding binding;

        public CompanyViewHolder(ItemCompanyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onCompanyClick(companies.get(pos));
                }
            });
        }

        public void bind(CompanyResponse company) {
            binding.tvCompanyName.setText(company.getName());
            binding.tvCompanyId.setText("ID: " + company.getId());
            binding.tvCompanyAddress.setText(company.getAddress() != null ? company.getAddress() : "Chưa cập nhật");
            
            // Mô tả ngắn gọn
            String desc = company.getDescription();
            if (desc != null && desc.length() > 100) {
                desc = desc.substring(0, 100) + "...";
            }
            binding.tvCompanyDescription.setText(desc != null ? desc : "");
        }
    }
}
