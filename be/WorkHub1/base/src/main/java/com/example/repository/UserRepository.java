package com.example.repository;

import com.example.domain.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findActiveById(Long id) {
        return find("id = ?1 AND deleted = false", id).firstResultOptional();
    }

    public List<User> findAllActive(Sort sort) {
        return find("deleted = false", sort).list();
    }

    public long countActive() {
        return count("deleted = false");
    }

    public Optional<User> findByEmail(String email) {
        return find("email = ?1 AND deleted = false", email).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email = ?1 AND deleted = false", email) > 0;
    }

    public boolean existsByEmailAndNotId(String email, Long id) {
        return count("email = ?1 AND deleted = false AND id != ?2", email, id) > 0;
    }

    public boolean existsByUsername(String username) {
        return count("username = ?1 AND deleted = false", username) > 0;
    }

    public boolean existsByUsernameAndNotId(String username, Long id) {
        return count("username = ?1 AND deleted = false AND id != ?2", username, id) > 0;
    }

    public long countAdmins() {
        return count("deleted = false AND role.name = 'ROLE_ADMIN'");
    }

    public List<User> findWithFilter(String query, Parameters params, Sort sort, int offset, int limit) {
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
