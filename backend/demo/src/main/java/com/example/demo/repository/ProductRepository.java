package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    
    List<Product> findByCategory(Category category);
    
    @Query("SELECT AVG(p.demandVelocity) FROM Product p WHERE p.category = ?1 AND p.demandVelocity > 0")
    Double getAverageDemandVelocityByCategory(Category category);
}