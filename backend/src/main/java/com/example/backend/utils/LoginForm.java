package com.example.backend.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@AllArgsConstructor
@Data
public class LoginForm {

    @NotEmpty(message = "Please provide a username")
    @Schema(example = "rocketman")
    private String username;

    @NotEmpty(message = "Please provide a password")
    @Schema(example = "rakieta")
    private String password;

}
