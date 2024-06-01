package com.example.demo.dto.account.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailDto {
   @Email
   private String email;
}
