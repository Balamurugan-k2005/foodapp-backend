package com.ecommerce.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String slug;

    private String description;

    private Long parentCategoryId;

    private List<CategoryDto> subCategories;
}
