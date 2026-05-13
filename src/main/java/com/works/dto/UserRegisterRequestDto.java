package com.works.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.works.entity.User}
 */
@Data
public class UserRegisterRequestDto implements Serializable {
    @NotNull
    @Size(min = 2, max = 100)
    @NotEmpty
    String name;
    @NotNull
    @Size(min = 2, max = 100)
    @NotEmpty
    String surname;
    @NotNull
    @Email
    @NotEmpty
    String email;
    @NotNull
    @Size(min = 9, max = 15)
    @NotEmpty
    String phone;
    boolean enabled;
    @NotNull
    @Size(min = 6, max = 15)
    @NotEmpty
    String password;
}