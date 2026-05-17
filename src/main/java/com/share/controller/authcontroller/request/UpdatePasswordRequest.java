package com.share.controller.authcontroller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @NotBlank(message = "Lütfen eski şifrenizi giriniz")
    private String oldPassword;

    @NotBlank(message = "Lütfen yeni şifrenizi giriniz")
    private String newPassword;
}
