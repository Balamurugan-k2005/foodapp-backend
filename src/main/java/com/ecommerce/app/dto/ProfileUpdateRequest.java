package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    private String phone;
}
