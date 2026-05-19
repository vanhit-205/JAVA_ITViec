package com.example.repository;

import com.example.domain.entity.Resume;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ResumeRepository implements PanacheRepository<Resume> {

    public Optional<Resume> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    public List<Resume> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    public long countActive() {
        return count("deleted = false");
    }

    public Optional<Resume> findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 AND user.id = ?2 AND deleted = false", id, userId).firstResultOptional();
    }

    public Optional<Resume> findByUserIdAndJobId(Long userId, Long jobId) {
        return find("user.id = ?1 AND job.id = ?2 AND deleted = false", userId, jobId).firstResultOptional();
    }

    public boolean existsByUserIdAndJobId(Long userId, Long jobId) {
        return count("user.id = ?1 AND job.id = ?2 AND deleted = false", userId, jobId) > 0;
    }

    public List<Resume> findByUserId(Long userId, Sort sort) {
        return find("user.id = ?1 AND deleted = false", sort, userId).list();
    }

    public List<Resume> findByJobId(Long jobId, Sort sort) {
        return find("job.id = ?1 AND deleted = false", sort, jobId).list();
    }

    public List<Resume> findByCompanyId(Long companyId, Sort sort) {
        return find("job.company.id = ?1 AND deleted = false", sort, companyId).list();
    }

    public List<Resume> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
        if (query == null || query.isEmpty()) {
            return find("deleted = false", sort).range(offset, offset + limit - 1).list();
        }
        return find(query, sort, params).range(offset, offset + limit - 1).list();
    }

    public long countWithFilter(String query, Parameters params) {
        if (query == null || query.isEmpty()) {
            return count("deleted = false");
        }
        return count(query, params);
    }

    public List<Resume> findByStatus(String status, Sort sort) {
        return find("status = ?1 AND deleted = false", sort, status).list();
    }
}
