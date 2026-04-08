package com.kumar.crudapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;


import java.util.Set;

@Entity
@Getter
@Setter
//@SQLDelete(sql = "UPDATE app_user SET deleted = true WHERE id = ?")
//@Where(clause = "deleted = false")
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;

    private String userName;
    private String password;
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

//    @Where(clause = "deleted = false")
    private boolean deleted = false;
}