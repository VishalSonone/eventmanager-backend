package com.kbcnmu.eventmanager.payload;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String department;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{10,12}$")
    private String prn;

    @NotBlank
    @Pattern(regexp = "^\\d{10}$")
    private String phone;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String studentClass;
}
