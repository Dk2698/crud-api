package com.kumar.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name; // ROLE_USER, ROLE_ADMIN

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Permission> permissions;
}