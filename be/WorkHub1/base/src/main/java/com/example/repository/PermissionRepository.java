package com.example.repository;

import com.example.domain.entity.Permission;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PermissionRepository implements PanacheRepository<Permission> {

    public Optional<Permission> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
