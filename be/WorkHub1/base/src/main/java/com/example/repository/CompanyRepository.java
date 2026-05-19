package com.example.repository;

import com.example.domain.entity.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

    /**
     * Find active company by ID (not deleted)
     */
    public Optional<Company> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    /**
     * Find all active companies
     */
    public List<Company> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    /**
     * Count all active companies
     */
    public long countActive() {
        return count("deleted = false");
    }

    /**
     * Check if company name exists (for validation)
     */
    public boolean existsByName(String name) {
        return count("name = ?1 AND deleted = false", name) > 0;
    }

    /**
     * Check if company name exists but exclude given ID (for update)
     */
    public boolean existsByNameAndNotId(String name, Long id) {
        return count("name = ?1 AND deleted = false AND id != ?2", name, id) > 0;
    }

    /**
     * Dynamic query with filter and keyword
     */
    public List<Company> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
        if (query == null || query.isEmpty()) {
            return find("deleted = false", sort).range(offset, offset + limit - 1).list();
        }
        return find(query, sort, params).range(offset, offset + limit - 1).list();
    }

    /**
     * Count with filter
     */
    public long countWithFilter(String query, Parameters params) {
        if (query == null || query.isEmpty()) {
            return count("deleted = false");
        }
        return count(query, params);
    }
}
