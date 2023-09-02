package com.github.messageconsumer.repository;

import com.github.messageconsumer.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
