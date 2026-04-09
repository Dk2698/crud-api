package com.kumar.crudapi.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kumar.crudapi.config.YesNoToBooleanDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import tools.jackson.databind.annotation.JsonDeserialize;

@Getter
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

//    @JsonDeserialize(using = YesNoToBooleanDeserializer.class)
    @JsonProperty("is_admin")
    private boolean isAdmin;
}
