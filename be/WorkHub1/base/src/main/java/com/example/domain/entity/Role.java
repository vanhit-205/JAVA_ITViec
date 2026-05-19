package com.example.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tbl_roles")
public class Role extends PanacheEntity {

    public String name;

    public String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("roles")
    @JoinTable(
            name = "tbl_permission_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    public List<Permission> permissions;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<User> users;
}
