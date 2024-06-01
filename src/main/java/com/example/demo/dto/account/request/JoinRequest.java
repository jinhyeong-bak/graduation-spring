package com.example.demo.dto.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JoinRequest {

    private String name;
    @Email
    private String email;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()-+]).{9,22}$")
    private String password;
}
