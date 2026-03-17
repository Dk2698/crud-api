package com.kumar.crudapi.entity.vo;

import jakarta.persistence.Embeddable;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Embeddable
public class AddressDetails {
    private String fullAddress;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
}
