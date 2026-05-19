package com.example.service;

import com.example.constant.ErrorCode;
import com.example.domain.dto.request.UserCreateRequest;
import com.example.domain.dto.request.UserUpdateRequest;
import com.example.domain.dto.response.UserResponse;
import com.example.domain.entity.Company;
import com.example.domain.entity.Role;
import com.example.domain.entity.User;
import com.example.exception.AppException;
import com.example.filter.FilterParser;
import com.example.filter.UserSpecification;
import com.example.mapper.UserMapper;
import com.example.pagination.PageRequest;
import com.example.pagination.PageResponse;
import com.example.pagination.PagingMeta;
import com.example.repository.CompanyRepository;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.repository.UserSessionRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@ApplicationScoped
public class UserService {

    private static final Logger log = Logger.getLogger(UserService.class);

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    UserSessionRepository userSessionRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    FilterParser filterParser;

    @Transactional
    public UserResponse create(UserCreateRequest request, Long currentUserId) {
        log.info("Creating user: " + request.email);

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTS.code, ErrorCode.EMAIL_EXISTS.message);
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(request.username)) {
            throw new AppException(ErrorCode.USERNAME_EXISTS.code, ErrorCode.USERNAME_EXISTS.message);
        }

        // Validate role exists
        Role role = roleRepository.findByName(request.role)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND.code, ErrorCode.ROLE_NOT_FOUND.message));

        // Validate company for RECRUITER role
        Company company = null;
        if ("ROLE_RECRUITER".equals(request.role)) {
            if (request.companyId == null) {
                throw new AppException(ErrorCode.INVALID_INPUT.code, "Company is required for RECRUITER role");
            }
            company = companyRepository.findById(request.companyId);
            if (company == null || company.deleted) {
                throw new AppException(ErrorCode.INVALID_INPUT.code, "Company not found");
            }
        }

        // Create user
        User user = userMapper.toEntity(request);
        user.password = BCrypt.hashpw(request.password, BCrypt.gensalt());
        user.role = role;
        user.company = company;
        user.createdBy = currentUserId;
        user.updatedBy = currentUserId;

        userRepository.persist(user);

        log.info("User created with ID: " + user.id);
        return userMapper.toDto(user);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request, Long currentUserId) {
        log.info("Updating user: " + id);

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        // Validate email uniqueness
        if (request.email != null && !request.email.equals(user.email)) {
            if (userRepository.existsByEmailAndNotId(request.email, id)) {
                throw new AppException(ErrorCode.EMAIL_EXISTS.code, ErrorCode.EMAIL_EXISTS.message);
            }
        }

        // Validate username uniqueness
        if (request.username != null && !request.username.equals(user.username)) {
            if (userRepository.existsByUsernameAndNotId(request.username, id)) {
                throw new AppException(ErrorCode.USERNAME_EXISTS.code, ErrorCode.USERNAME_EXISTS.message);
            }
        }

        // Update company if role is RECRUITER
        if ("ROLE_RECRUITER".equals(user.role.name) && request.companyId != null) {
            Company company = companyRepository.findById(request.companyId);
            if (company == null || company.deleted) {
                throw new AppException(ErrorCode.INVALID_INPUT.code, "Company not found");
            }
            user.company = company;
        }

        // Update role if provided and valid (Only Admin can change role)
        if (request.role != null && !request.role.isEmpty()) {
            User currentUser = userRepository.findById(currentUserId);
            if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.role.name)) {
                throw new AppException(403, "Only administrators can change user roles");
            }

            Role newRole = roleRepository.findByName(request.role)
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND.code, ErrorCode.ROLE_NOT_FOUND.message));
            user.role = newRole;
        }

        // Change password if provided
        if (request.newPassword != null) {
            user.password = BCrypt.hashpw(request.newPassword, BCrypt.gensalt());
        }

        userMapper.updateEntity(user, request);
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        log.info("User updated: " + id);
        return userMapper.toDto(user);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        log.info("Deleting user: " + id);

        // Cannot delete yourself
        if (id.equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN.code, "Cannot delete your own account");
        }

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        // Cannot delete last admin
        if ("ROLE_ADMIN".equals(user.role.name)) {
            long adminCount = userRepository.countAdmins();
            if (adminCount <= 1) {
                throw new AppException(ErrorCode.FORBIDDEN.code, "Cannot delete the last admin account");
            }
        }

        // Soft delete
        user.softDelete();
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        // Invalidate all sessions
        userSessionRepository.deactivateAllByUserId(id);

        log.info("User soft deleted: " + id);
    }

    @Transactional
    public void lock(Long id, Long currentUserId) {
        log.info("Locking user: " + id);

        if (id.equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN.code, "Cannot lock your own account");
        }

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        user.lock();
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        // Invalidate all sessions
        userSessionRepository.deactivateAllByUserId(id);

        log.info("User locked: " + id);
    }

    @Transactional
    public void unlock(Long id, Long currentUserId) {
        log.info("Unlocking user: " + id);

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        user.unlock();
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        log.info("User unlocked: " + id);
    }

    @Transactional
    public void disable(Long id, Long currentUserId) {
        log.info("Disabling user: " + id);

        if (id.equals(currentUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN.code, "Cannot disable your own account");
        }

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        user.disable();
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        // Invalidate all sessions
        userSessionRepository.deactivateAllByUserId(id);

        log.info("User disabled: " + id);
    }

    @Transactional
    public void enable(Long id, Long currentUserId) {
        log.info("Enabling user: " + id);

        User user = userRepository.findActiveById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND.code, ErrorCode.USER_NOT_FOUND.message));

        user.enable();
        user.updatedBy = currentUserId;
        userRepository.persist(user);

        log.info("User enabled: " + id);
    }

    public PageResponse<UserResponse> getAll(PageRequest pageRequest) {
        log.info("Getting users - page: " + pageRequest.getPage() + ", size: " + pageRequest.getSize());

        var filter = filterParser.parse(pageRequest.getFilter());
        var queryResult = UserSpecification.buildQuery(filter, pageRequest.getKeyword());
        Sort sort = UserSpecification.buildSort(pageRequest.getSortBy(), pageRequest.isAscending());

        int offset = pageRequest.getOffset();
        int limit = pageRequest.getSize();

        List<User> users = userRepository.findWithFilter(
                queryResult.query, queryResult.params, sort, offset, limit);
        long total = userRepository.countWithFilter(queryResult.query, queryResult.params);

        PagingMeta meta = new PagingMeta(
                total,
                (int) Math.ceil((double) total / pageRequest.getSize()),
                pageRequest.getPage(),
                pageRequest.getSize(),
                pageRequest.getSortBy(),
                pageRequest.getDirection()
        );

        List<UserResponse> items = userMapper.toDtoList(users);
        return PageResponse.of(meta, items);
    }
}
