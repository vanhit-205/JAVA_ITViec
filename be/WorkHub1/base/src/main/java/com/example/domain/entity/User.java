package com.example.domain.entity;

import com.example.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_users")
public class User extends PanacheEntity {

    @Column(name = "username", nullable = false, unique = true, length = 100)
    public String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    public String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    public String password;

    @Column(name = "age")
    public Integer age;

    @Column(name = "phone", length = 20)
    public String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    public GenderEnum gender;

    @Column(name = "dob")
    public LocalDate dob;

    @Column(name = "address", columnDefinition = "TEXT")
    public String address;

    @Column(name = "enabled", nullable = false)
    public Boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false)
    public Boolean accountNonLocked = true;

    @Column(name = "deleted", nullable = false)
    public Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Column(name = "created_by")
    public Long createdBy;

    @Column(name = "updated_by")
    public Long updatedBy;

    @JsonIgnore
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    public String refreshToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    public Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    public Company company;

    @Column(name = "provider", length = 50)
    public String provider;

    @Column(name = "provider_id", length = 255)
    public String providerId;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Resume> resumes = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<ForgotPassword> forgotPasswords = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<UserSession> sessions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (enabled == null) {
            enabled = true;
        }

        if (accountNonLocked == null) {
            accountNonLocked = true;
        }

        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deleted = true;
    }

    public void lock() {
        this.accountNonLocked = false;
    }

    public void unlock() {
        this.accountNonLocked = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }

    public static User findActiveById(Long id) {
        return find("id = ?1 and deleted = false", id)
                .firstResult();
    }

    public static List<User> findAllActive() {
        return list("deleted = false order by createdAt desc");
    }

    public static boolean existsByEmail(String email) {
        return count("email = ?1 and deleted = false", email) > 0;
    }

    public static boolean existsByEmailAndNotId(String email, Long id) {
        return count(
                "email = ?1 and deleted = false and id != ?2",
                email,
                id
        ) > 0;
    }

    public static boolean existsByUsername(String username) {
        return count(
                "username = ?1 and deleted = false",
                username
        ) > 0;
    }

    public static boolean existsByUsernameAndNotId(String username, Long id) {
        return count(
                "username = ?1 and deleted = false and id != ?2",
                username,
                id
        ) > 0;
    }

    public static long countAdmins() {
        return count(
                "deleted = false and role.name = ?1",
                "ROLE_ADMIN"
        );
    }
}