package com.pcstore.controller;

import com.pcstore.dto.ProductDto;
import com.pcstore.entity.Product;
import com.pcstore.repo.ProductsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/products")

public class ProductsController {

    @Autowired
    private ProductsRepo productsRepo;

    @GetMapping({ "", "/" })
    public String showProductList(Model model){
        System.out.println("1");
        List<Product> products = productsRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "/products/list";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        System.out.println("2");
        ProductDto productDTO = new ProductDto();
        model.addAttribute("productDto", productDTO);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
        public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {

        if (productDto.getImageFile().isEmpty()){
            System.out.println("image file is empty");
            result.addError(new FieldError("productDto", "getImageFile", "this file is required"));
        }

        if (result.hasErrors()){
            System.out.println("result has errors");
            return "products/CreateProduct";

        }

        //save image file
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "__" + image.getOriginalFilename();

        try{
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImage_file_name(storageFileName);

        productsRepo.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(@RequestParam int id, Model model){

        try {
            Product product = productsRepo.findById(id).get();
            model.addAttribute("product",product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(@RequestParam int id, Model model, @Valid @ModelAttribute ProductDto productDto, BindingResult result){

        try {
            Product product = productsRepo.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()){
                return "products/EditProduct";
            }

            if (!productDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImage_file_name());

                try {
                    Files.delete(oldImagePath);
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                }

                //save new image file
                MultipartFile image = productDto.getImageFile();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "__" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()){
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                    StandardCopyOption.REPLACE_EXISTING);
                }

                product.setImage_file_name(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            productsRepo.save(product);

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id){
        try {

            Product product = productsRepo.findById(id).get();
            //delete product image
            Path imagePath = Paths.get("public/images/" + product.getImage_file_name());

            try {
                Files.delete(imagePath);

            }catch (Exception e){
                System.out.println("Exception: " + e.getMessage());
            }

            productsRepo.delete(product);

        }catch (Exception e){
            System.out.println("Exception: " + e.getMessage());
        }

        return "redirect:/products";
    }

}

