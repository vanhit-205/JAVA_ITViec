package com.example.repository;

import com.example.domain.entity.Subscriber;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SubscriberRepository implements PanacheRepository<Subscriber> {

    public Optional<Subscriber> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    public List<Subscriber> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    public long countActive() {
        return count("deleted = false");
    }

    public Optional<Subscriber> findByEmail(String email) {
        return find("email = ?1 AND deleted = false", email).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email = ?1 AND deleted = false", email) > 0;
    }

    public boolean existsByEmailAndNotId(String email, Long id) {
        return count("email = ?1 AND deleted = false AND id != ?2", email, id) > 0;
    }

    public List<Subscriber> findAllEnabled(Sort sort) {
        return find("enabled = true AND deleted = false", sort).list();
    }

    public List<Subscriber> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
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

    public List<Subscriber> findBySkillId(Long skillId) {
        return find("SELECT DISTINCT s FROM Subscriber s JOIN s.skills sk WHERE sk.id = ?1 AND s.enabled = true AND s.deleted = false", skillId).list();
    }

    public List<Subscriber> findBySkillIds(List<Long> skillIds) {
        return find("SELECT DISTINCT s FROM Subscriber s JOIN s.skills sk WHERE sk.id IN ?1 AND s.enabled = true AND s.deleted = false", skillIds).list();
    }
}