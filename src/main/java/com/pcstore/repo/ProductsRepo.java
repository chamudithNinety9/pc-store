package com.pcstore.repo;

import com.pcstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories

public interface ProductsRepo extends JpaRepository<Product, Integer> {
}
