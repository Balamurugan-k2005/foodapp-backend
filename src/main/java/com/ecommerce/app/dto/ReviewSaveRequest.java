package com.ecommerce.app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewSaveRequest {

    @NotNull(message = "Rating score is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer rating;

    @NotBlank(message = "Review content comment cannot be empty")
    private String comment;
}
