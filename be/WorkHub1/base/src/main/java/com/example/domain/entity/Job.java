package com.example.domain.entity;

import com.example.constant.LevelEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "tbl_jobs", indexes = {
    @Index(name = "idx_job_company", columnList = "company_id"),
    @Index(name = "idx_job_deleted", columnList = "deleted"),
    @Index(name = "idx_job_name", columnList = "name")
})
public class Job extends PanacheEntity {

    public String name;

    public String location;

    public double salary;

    public int quantity;

    @Enumerated(EnumType.STRING)
    public LevelEnum level;

    @Column(columnDefinition = "TEXT")
    public String description;

    public Instant startDate;
    public Instant endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    public Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("jobs")
    @JoinTable(
            name = "tbl_job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    public List<Skill> skills;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Resume> resumes;

    // Soft delete
    public boolean deleted = false;

    // Auditing
    public Instant createdAt;
    public Instant updatedAt;
    public Long createdBy;
    public Long updatedBy;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (!deleted) {
            deleted = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    // Business methods
    public void softDelete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }

    public void close() {
        this.endDate = Instant.now();
    }

    public void reopen() {
        this.endDate = null;
    }
}