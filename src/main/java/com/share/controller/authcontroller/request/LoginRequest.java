package com.share.controller.authcontroller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 40)
    private String password;

}
