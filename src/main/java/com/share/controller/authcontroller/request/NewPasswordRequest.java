package com.share.controller.authcontroller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 40)
  //  @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",message = "Lütfen Geçerli bir şifre giriniz")
    private String password;

    @NotBlank
    @Size(min = 8, max = 40)
  //  @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",message = "Lütfen Geçerli bir şifre giriniz")
    private String passwordAgain;
}
