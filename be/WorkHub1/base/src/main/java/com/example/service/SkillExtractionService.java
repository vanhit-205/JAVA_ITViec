package com.example.service;

import com.example.domain.entity.Skill;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class SkillExtractionService {

    private static final Logger log = Logger.getLogger(SkillExtractionService.class);

    public List<Skill> extractSkills(String cvText) {
        if (cvText == null || cvText.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Skill> allActiveSkills = Skill.findAllActive();
        List<Skill> matchedSkills = new ArrayList<>();
        String lowerText = cvText.toLowerCase();

        log.info("Bắt đầu đối khớp kỹ năng với " + allActiveSkills.size() + " kỹ năng đang hoạt động trong hệ thống.");

        for (Skill skill : allActiveSkills) {
            String skillName = skill.name.trim().toLowerCase();
            if (skillName.isEmpty()) {
                continue;
            }

            boolean isMatch = false;

            // Xử lý các từ khóa kỹ năng đặc biệt thông dụng trong ngành IT
            if (skillName.equals("c++")) {
                isMatch = lowerText.contains("c++") || lowerText.contains("cpp");
            } else if (skillName.equals("c#")) {
                isMatch = lowerText.contains("c#") || lowerText.contains("csharp");
            } else if (skillName.equals(".net")) {
                isMatch = lowerText.contains(".net") || lowerText.contains("dotnet");
            } else if (skillName.equals("react") || skillName.equals("reactjs")) {
                isMatch = lowerText.contains("react") || lowerText.contains("reactjs") || lowerText.contains("react.js");
            } else if (skillName.equals("node") || skillName.equals("nodejs")) {
                isMatch = lowerText.contains("node") || lowerText.contains("nodejs") || lowerText.contains("node.js");
            } else if (skillName.equals("vue") || skillName.equals("vuejs")) {
                isMatch = lowerText.contains("vue") || lowerText.contains("vuejs") || lowerText.contains("vue.js");
            } else {
                // Sử dụng regex word boundaries \b để tránh khớp nhầm ký tự trong từ khác (Ví dụ: "c" trong "company")
                try {
                    Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skillName) + "\\b");
                    isMatch = pattern.matcher(lowerText).find();
                } catch (Exception e) {
                    isMatch = lowerText.contains(skillName);
                }
            }

            if (isMatch) {
                matchedSkills.add(skill);
                log.debug("Khớp thành công kỹ năng: " + skill.name);
            }
        }

        log.info("Đối khớp hoàn tất. Tìm thấy " + matchedSkills.size() + " kỹ năng từ CV.");
        return matchedSkills;
    }
}
