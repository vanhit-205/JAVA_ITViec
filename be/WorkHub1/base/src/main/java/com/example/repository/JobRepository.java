package com.example.repository;

import com.example.domain.entity.Job;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JobRepository implements PanacheRepository<Job> {

    public Optional<Job> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    public List<Job> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    public long countActive() {
        return count("deleted = false");
    }

    public Optional<Job> findByIdAndCompanyId(Long id, Long companyId) {
        return find("id = ?1 AND company.id = ?2 AND deleted = false", id, companyId).firstResultOptional();
    }

    public List<Job> findByCompanyId(Long companyId, Sort sort) {
        return find("company.id = ?1 AND deleted = false", sort, companyId).list();
    }

    public List<Job> findByLocation(String location, Sort sort) {
        return find("location LIKE ?1 AND deleted = false", sort, "%" + location + "%").list();
    }

    public List<Job> findByLevel(String level, Sort sort) {
        return find("level = ?1 AND deleted = false", sort, level).list();
    }

    public List<Job> findOpenJobs(Sort sort) {
        Instant now = Instant.now();
        return find("(endDate IS NULL OR endDate > ?1) AND deleted = false", sort, now).list();
    }

    public List<Job> findBySalaryRange(Double minSalary, Double maxSalary, Sort sort) {
        return find("salary >= ?1 AND salary <= ?2 AND deleted = false", sort, minSalary, maxSalary).list();
    }

    public List<Job> findBySkillId(Long skillId, Sort sort) {
        return find("SELECT j FROM Job j JOIN j.skills s WHERE s.id = ?1 AND j.deleted = false", sort, skillId).list();
    }

    public List<Job> findBySkillIds(List<Long> skillIds, Sort sort) {
        return find("SELECT DISTINCT j FROM Job j JOIN j.skills s WHERE s.id IN ?1 AND j.deleted = false", sort, skillIds).list();
    }

    public List<Job> findByKeyword(String keyword, Sort sort) {
        String pattern = "%" + keyword + "%";
        return find("(name LIKE ?1 OR description LIKE ?2 OR location LIKE ?3) AND deleted = false",
                sort, pattern, pattern, pattern).list();
    }

    public List<Job> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
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

    public List<Job> findActiveByIds(List<Long> ids) {
        return find("id IN ?1 AND deleted = false", ids).list();
    }
}
