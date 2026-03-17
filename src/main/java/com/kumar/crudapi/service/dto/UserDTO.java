package com.kumar.crudapi.service.dto;

import com.kumar.crudapi.base.EntityDTO;
import com.kumar.crudapi.entity.vo.AddressDetails;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;


@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDTO implements EntityDTO<Long> {

    private Long id;
    private Integer version;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String phone;
    private AddressDetails address;
}