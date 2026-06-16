package com.example.timviecapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timviecapp.databinding.ItemSubscriberBinding;
import com.example.timviecapp.models.skill.SkillResponse;
import com.example.timviecapp.models.subscriber.SubscriberResponse;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SubscriberAdapter extends RecyclerView.Adapter<SubscriberAdapter.ViewHolder> {

    private List<SubscriberResponse> list = new ArrayList<>();
    private OnSubscriberActionListener listener;

    public interface OnSubscriberActionListener {
        void onToggleStatus(SubscriberResponse subscriber, boolean isEnabled);
        void onEdit(SubscriberResponse subscriber);
        void onDelete(SubscriberResponse subscriber);
        void onViewDetail(SubscriberResponse subscriber);
    }

    public void setList(List<SubscriberResponse> list) {
        this.list = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnSubscriberActionListener(OnSubscriberActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSubscriberBinding binding = ItemSubscriberBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSubscriberBinding binding;

        public ViewHolder(ItemSubscriberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SubscriberResponse item) {
            Context context = itemView.getContext();
            binding.tvId.setText("#" + item.getId());
            binding.tvEmail.setText(item.getEmail());
            binding.tvName.setText(item.getName());

            // Click vào email để mở ứng dụng gửi thư
            binding.tvEmail.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{item.getEmail()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ từ WorkHub Admin");
                    context.startActivity(Intent.createChooser(intent, "Gửi Mail qua..."));
                } catch (Exception e) {
                    Toast.makeText(context, "Không thể mở ứng dụng gửi thư", Toast.LENGTH_SHORT).show();
                }
            });

            // Set trạng thái Switch
            binding.switchStatus.setOnCheckedChangeListener(null); // Tránh trigger listener ngoài ý muốn
            binding.switchStatus.setChecked(item.isEnabled());
            binding.switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleStatus(item, isChecked);
                }
            });

            // Định dạng ngày đăng ký (DD/MM/YYYY)
            binding.tvJoinedDate.setText(formatDate(item.getCreatedAt()));

            // Hiển thị Chip kỹ năng giới hạn tối đa 3 Chip
            binding.cgSkills.removeAllViews();
            List<SkillResponse> skills = item.getSkills();
            if (skills != null && !skills.isEmpty()) {
                int maxDisplay = 3;
                int displayCount = Math.min(skills.size(), maxDisplay);
                
                for (int i = 0; i < displayCount; i++) {
                    Chip chip = new Chip(context);
                    chip.setText(skills.get(i).getName());
                    chip.setChipMinHeight(0f);
                    chip.setClickable(false);
                    chip.setCheckable(false);
                    chip.setChipBackgroundColorResource(com.example.timviecapp.R.color.colorSurfaceVariant);
                    chip.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.timviecapp.R.color.textPrimary));
                    chip.setTextSize(11f);
                    chip.setChipCornerRadius(8f);
                    chip.setChipStrokeColorResource(com.example.timviecapp.R.color.colorOutline);
                    chip.setChipStrokeWidth(1f);
                    chip.setEnsureMinTouchTargetSize(false);
                    binding.cgSkills.addView(chip);
                }

                // Nếu vượt quá 3 kỹ năng, thêm tag hiển thị phần dư
                if (skills.size() > maxDisplay) {
                    Chip more = new Chip(context);
                    more.setText("+" + (skills.size() - maxDisplay));
                    more.setChipMinHeight(0f);
                    more.setClickable(false);
                    more.setCheckable(false);
                    more.setTextSize(11f);
                    more.setChipBackgroundColorResource(com.example.timviecapp.R.color.colorSurfaceVariant);
                    more.setTextColor(androidx.core.content.ContextCompat.getColor(context, com.example.timviecapp.R.color.textSecondary));
                    more.setChipCornerRadius(8f);
                    more.setEnsureMinTouchTargetSize(false);
                    binding.cgSkills.addView(more);
                }
            }

            // Xử lý sự kiện 3 nút chức năng
            binding.btnView.setOnClickListener(v -> {
                if (listener != null) listener.onViewDetail(item);
            });

            binding.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        }

        private String formatDate(String isoDateStr) {
            if (isoDateStr == null || isoDateStr.isEmpty()) return "N/A";
            try {
                // Định dạng thời gian ISO trả về từ backend (ví dụ: "2026-06-16T12:00:00Z")
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(isoDateStr);
                
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            } catch (ParseException e) {
                return isoDateStr.split("T")[0]; // Fallback lấy phần ngày
            }
        }
    }
}
