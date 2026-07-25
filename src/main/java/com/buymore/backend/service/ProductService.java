package com.buymore.backend.service;

import com.buymore.backend.dto.CategoryResponse;
import com.buymore.backend.dto.ProductRequest;
import com.buymore.backend.dto.ProductResponse;
import com.buymore.backend.entity.Category;
import com.buymore.backend.entity.Product;
import com.buymore.backend.entity.User;
import com.buymore.backend.exception.ResourceNotFoundException;
import com.buymore.backend.repository.CategoryRepository;
import com.buymore.backend.repository.ProductRepository;
import com.buymore.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<ProductResponse> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .image(request.image())
                .price(request.price())
                .discount(request.discount())
                .specialPrice(request.specialPrice())
                .quantity(request.quantity())
                .status(request.status() != null ? request.status() : Product.Status.ACTIVE)
                .category(resolveCategory(request.categoryId()))
                .seller(resolveSeller(request.sellerId()))
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setImage(request.image());
        product.setPrice(request.price());
        product.setDiscount(request.discount());
        product.setSpecialPrice(request.specialPrice());
        product.setQuantity(request.quantity());
        product.setStatus(request.status() != null ? request.status() : product.getStatus());
        product.setCategory(resolveCategory(request.categoryId()));
        product.setSeller(resolveSeller(request.sellerId()));

        return toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findOrThrow(id));
    }

    private Category resolveCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private User resolveSeller(Long sellerId) {
        if (sellerId == null) {
            return null;
        }

        return userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found: " + sellerId));
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product product) {
        CategoryResponse categoryResponse = product.getCategory() != null
                ? new CategoryResponse(product.getCategory().getId(), product.getCategory().getName())
                : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getImage(),
                product.getPrice(),
                product.getDiscount(),
                product.getSpecialPrice(),
                product.getQuantity(),
                product.getSold(),
                product.getStatus(),
                categoryResponse,
                product.getSeller() != null ? product.getSeller().getId() : null,
                product.getCreatedAt()
        );
    }
}
