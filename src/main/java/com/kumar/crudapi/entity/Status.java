package com.kumar.crudapi.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive");

    final String description;
}
