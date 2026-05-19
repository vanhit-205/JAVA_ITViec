package com.example.service;

import com.example.constant.ErrorCode;
import com.example.constant.RoleConstant;
import com.example.domain.entity.Company;
import com.example.domain.entity.RoleUpgradeRequest;
import com.example.domain.entity.User;
import com.example.domain.entity.Role;
import com.example.exception.AppException;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class RoleUpgradeService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    // 1. Tạo yêu cầu nâng cấp mới (Dành cho Ứng viên)
    @Transactional
    public RoleUpgradeRequest createRequest(Long userId, Long companyId, String newCompanyName, String reason) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message);
        }

        // Nếu đã là nhà tuyển dụng hoặc admin thì không được yêu cầu nữa
        if (!RoleConstant.ROLE_CANDIDATE.name().equals(user.role.name)) {
            throw new AppException(400, "Tài khoản của bạn đã được phân quyền đặc biệt.");
        }

        // Kiểm tra xem đã có yêu cầu PENDING nào chưa
        long pendingCount = RoleUpgradeRequest.count("user.id = ?1 and status = 'PENDING'", userId);
        if (pendingCount > 0) {
            throw new AppException(400, "Bạn đã gửi một yêu cầu nâng cấp đang chờ xử lý.");
        }

        RoleUpgradeRequest request = new RoleUpgradeRequest();
        request.user = user;
        request.applicantName = user.username;
        request.applicantEmail = user.email;
        request.reason = reason;
        
        if (companyId != null) {
            Company company = Company.findById(companyId);
            if (company != null) {
                request.company = company;
            }
        } else {
            request.newCompanyName = newCompanyName;
        }

        RoleUpgradeRequest.persist(request);
        return request;
    }

    // 2. Lấy yêu cầu hiện tại của User đăng nhập
    public RoleUpgradeRequest getMyRequest(Long userId) {
        return RoleUpgradeRequest.find("user.id = ?1 order by createdAt desc", userId).firstResult();
    }

    // 3. Danh sách yêu cầu dành cho Admin
    public List<RoleUpgradeRequest> getAllRequests() {
        return RoleUpgradeRequest.list("order by createdAt desc");
    }

    // 4. Admin phê duyệt yêu cầu nâng cấp
    @Transactional
    public void approveRequest(Long requestId, Long adminId) {
        RoleUpgradeRequest request = RoleUpgradeRequest.findById(requestId);
        if (request == null) {
            throw new AppException(404, "Không tìm thấy yêu cầu nâng cấp");
        }

        if (!"PENDING".equals(request.status)) {
            throw new AppException(400, "Yêu cầu này đã được xử lý trước đó");
        }

        User user = request.user;
        Role recruiterRole = roleRepository.findByName(RoleConstant.ROLE_RECRUITER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND.code, ErrorCode.ROLE_NOT_FOUND.message));

        // Nâng cấp Role
        user.role = recruiterRole;

        // Nếu yêu cầu tạo công ty mới, Admin có thể tạo trước hoặc liên kết thủ công
        if (request.company != null) {
            user.company = request.company;
        } else if (request.newCompanyName != null && !request.newCompanyName.isEmpty()) {
            // Tự động tạo Công ty mới nếu có tên công ty đăng ký mới
            Company newCompany = new Company();
            newCompany.name = request.newCompanyName;
            Company.persist(newCompany);
            user.company = newCompany;
        }

        userRepository.persist(user);

        // Cập nhật trạng thái yêu cầu
        request.status = "APPROVED";
        request.approvedBy = adminId;
        RoleUpgradeRequest.persist(request);
    }

    // 5. Admin từ chối yêu cầu nâng cấp
    @Transactional
    public void rejectRequest(Long requestId, Long adminId, String adminNotes) {
        RoleUpgradeRequest request = RoleUpgradeRequest.findById(requestId);
        if (request == null) {
            throw new AppException(404, "Không tìm thấy yêu cầu nâng cấp");
        }

        if (!"PENDING".equals(request.status)) {
            throw new AppException(400, "Yêu cầu này đã được xử lý trước đó");
        }

        request.status = "REJECTED";
        request.approvedBy = adminId;
        request.adminNotes = adminNotes;
        RoleUpgradeRequest.persist(request);
    }
}
