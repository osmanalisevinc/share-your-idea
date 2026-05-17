package com.share.controller.sharingcontroller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateSharingRequest {
    @NotBlank
    public String share;
}
