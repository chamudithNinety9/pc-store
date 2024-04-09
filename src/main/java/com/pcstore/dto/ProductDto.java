package com.pcstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;

import javax.validation.constraints.*;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class ProductDto {

    @NotEmpty(message = "Field is required")
    public String name;

    @NotEmpty(message = "Field is required")
    public String brand;

    @NotEmpty(message = "Field is required")
    public String category;

    @Min(0)
    public double price;

    @Size(min = 10, message = "should contain at least 10 characters")
    @Size(max = 2000, message = "cannot exceed 200 characters")
    public String description;

    public MultipartFile imageFile;
}
