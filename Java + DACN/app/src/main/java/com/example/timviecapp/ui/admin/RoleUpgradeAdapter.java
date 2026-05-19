package com.example.timviecapp.ui.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.R;
import com.example.timviecapp.models.user.RoleUpgradeRequest;

import java.util.List;

public class RoleUpgradeAdapter extends RecyclerView.Adapter<RoleUpgradeAdapter.ViewHolder> {
    private List<RoleUpgradeRequest> requestList;
    private final OnItemActionListener listener;

    public interface OnItemActionListener {
        void onApprove(RoleUpgradeRequest request);
        void onReject(RoleUpgradeRequest request);
    }

    public RoleUpgradeAdapter(List<RoleUpgradeRequest> requestList, OnItemActionListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    public void updateData(List<RoleUpgradeRequest> newList) {
        this.requestList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_role_upgrade_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoleUpgradeRequest request = requestList.get(position);
        
        holder.tvCompanyName.setText(request.getNewCompanyName() != null ? request.getNewCompanyName() : "Tên doanh nghiệp chưa cập nhật");
        
        String applicantName = request.getApplicantName() != null ? request.getApplicantName() : "Ứng viên";
        String applicantEmail = request.getApplicantEmail() != null ? request.getApplicantEmail() : "Chưa cập nhật";
        holder.tvApplicantInfo.setText("Người gửi: " + applicantName + " (" + applicantEmail + ")");
        
        holder.tvReason.setText(request.getReason() != null ? "Lý do: " + request.getReason() : "Lý do: Không có lý do cụ thể");
        holder.tvDate.setText("Ngày yêu cầu: " + (request.getCreatedAt() != null ? request.getCreatedAt().substring(0, 10) : "--"));

        String status = request.getStatus();
        holder.tvStatus.setText(status);

        if ("PENDING".equals(status)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFE0B2"));
            holder.tvStatus.setTextColor(Color.parseColor("#FB8C00"));
            holder.layoutActions.setVisibility(View.VISIBLE);
        } else if ("APPROVED".equals(status)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.layoutActions.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            holder.layoutActions.setVisibility(View.GONE);
        }

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(request));
        holder.btnReject.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompanyName, tvApplicantInfo, tvReason, tvDate, tvStatus;
        View layoutActions, btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvApplicantInfo = itemView.findViewById(R.id.tvApplicantInfo);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
