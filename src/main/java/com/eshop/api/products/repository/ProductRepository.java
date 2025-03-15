package  com.eshop.api.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eshop.api.products.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
}