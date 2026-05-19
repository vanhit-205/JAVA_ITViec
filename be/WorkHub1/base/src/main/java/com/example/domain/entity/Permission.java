package com.example.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tbl_permissions")
public class Permission extends PanacheEntity {

    @Column(nullable = false, unique = true)
    public String name;

    public String description;

    @ManyToMany(mappedBy = "permissions")
    public List<Role> roles;
}
