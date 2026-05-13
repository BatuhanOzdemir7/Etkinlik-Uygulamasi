package com.works.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.works.entity.User}
 */
@Value
public class UserLoginRequestDto implements Serializable {
    @NotNull
    @NotEmpty
    @Size(min = 5, max = 100)
    String username;
    @NotNull
    @Size(min = 6, max = 15)
    @NotEmpty
    String password;
}