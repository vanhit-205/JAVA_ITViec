package com.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Company Entity with Soft Delete
 *
 * WHY SOFT DELETE?
 * 1. Data Recovery - Deleted data can be recovered if deleted by mistake
 * 2. Audit Trail - Maintain history for compliance and debugging
 * 3. Data Integrity - Foreign key relationships remain intact
 * 4. Business Reporting - Historical data preserved for reports
 *
 * HARD DELETE RISKS IN PRODUCTION:
 * - Permanent data loss
 * - Broken foreign key relationships
 * - No audit trail
 * - Compliance issues (GDPR, SOX)
 * - Difficult debugging
 */
@Entity
@Table(name = "tbl_companies")
@NamedQueries({
    @NamedQuery(name = "Company.findActiveById",
                query = "SELECT c FROM Company c WHERE c.id = :id AND c.deleted = false"),
    @NamedQuery(name = "Company.findAllActive",
                query = "SELECT c FROM Company c WHERE c.deleted = false ORDER BY c.createdAt DESC")
})
public class Company extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column
    public String address;

    @Column
    public String logo;

    /**
     * Soft delete flag
     * false = active, true = deleted
     */
    @Column(nullable = false)
    public boolean deleted = false;

    /**
     * Auditing fields
     */
    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(nullable = false)
    public LocalDateTime updatedAt;

    @Column
    public Long createdBy;

    @Column
    public Long updatedBy;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Job> jobs;

    public Company() {
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.deleted == false) {
            this.deleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Soft delete method
     */
    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if company is active
     */
    public boolean isActive() {
        return !this.deleted;
    }

    // Static finder methods
    public static Company findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResult();
    }

    public static List<Company> findAllActive() {
        return list("deleted = false ORDER BY createdAt DESC");
    }

    public static boolean existsByName(String name) {
        return count("name = ?1 AND deleted = false", name) > 0;
    }

    public static boolean existsByNameAndNotId(String name, Long id) {
        return count("name = ?1 AND deleted = false AND id != ?2", name, id) > 0;
    }
}