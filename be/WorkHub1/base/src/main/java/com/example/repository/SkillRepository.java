package com.example.repository;

import com.example.domain.entity.Skill;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SkillRepository implements PanacheRepository<Skill> {

    public Optional<Skill> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    public List<Skill> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    public long countActive() {
        return count("deleted = false");
    }

    public boolean existsByName(String name) {
        return count("name = ?1 AND deleted = false", name) > 0;
    }

    public boolean existsByNameAndNotId(String name, Long id) {
        return count("name = ?1 AND deleted = false AND id != ?2", name, id) > 0;
    }

    public List<Skill> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
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
}