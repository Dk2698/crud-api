package com.kumar.crudapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Permission {

    @Id
    @GeneratedValue
    private Long id;

    private String name; // READ_ACCOUNT, TRANSFER_MONEY
}