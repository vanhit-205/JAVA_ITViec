package com.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Skill Entity with Soft Delete
 */
@Entity
@Table(name = "tbl_skills")
public class Skill extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column
    public String level;

    @Column(nullable = false)
    public boolean deleted = false;

    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(nullable = false)
    public LocalDateTime updatedAt;

    @Column
    public Long createdBy;

    @Column
    public Long updatedBy;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Job> jobs;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Subscriber> subscribers;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<Resume> resumes = new ArrayList<>();

    public Skill() {
        this.deleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public static Skill findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResult();
    }

    public static List<Skill> findAllActive() {
        return list("deleted = false ORDER BY createdAt DESC");
    }

    public static boolean existsByName(String name) {
        return count("name = ?1 AND deleted = false", name) > 0;
    }

    public static boolean existsByNameAndNotId(String name, Long id) {
        return count("name = ?1 AND deleted = false AND id != ?2", name, id) > 0;
    }
}
