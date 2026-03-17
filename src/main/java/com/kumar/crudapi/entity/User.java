package com.kumar.crudapi.entity;

import com.kumar.crudapi.base.AuditableBaseEntity;
import com.kumar.crudapi.entity.vo.AddressDetails;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@ToString(callSuper = true, exclude = "password")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends AuditableBaseEntity<Long> {

    @Column(name = "first_name")
    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;
    private String phone;

    @Embedded
    private AddressDetails address;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Status status;
}