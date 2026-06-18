package com.sport_pro_be.modules.product.repository;

import com.sport_pro_be.modules.product.enums.Gender;
import com.sport_pro_be.modules.product.enums.ProductStatus;
import com.sport_pro_be.modules.product.domain.Product;
import com.sport_pro_be.modules.product.domain.ProductVariant;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterProducts(
            String keyword, Long categoryId, Long brandId, Gender gender, String size, String color,
            BigDecimal minPrice, BigDecimal maxPrice, Boolean isFeatured, ProductStatus status,
            Boolean includeDeleted) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
                Predicate descPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
                predicates.add(criteriaBuilder.or(namePredicate, descPredicate));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            } else if (!Boolean.TRUE.equals(includeDeleted)) {
                predicates.add(criteriaBuilder.notEqual(root.get("status"), ProductStatus.DELETED));
            }

            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }

            if (isFeatured != null) {
                predicates.add(criteriaBuilder.equal(root.get("isFeatured"), isFeatured));
            }

            boolean hasSize = size != null && !size.isBlank();
            boolean hasColor = color != null && !color.isBlank();

            if (hasSize || hasColor || minPrice != null || maxPrice != null) {
                Subquery<Long> variantSubquery = query.subquery(Long.class);
                Root<ProductVariant> variantRoot = variantSubquery.from(ProductVariant.class);
                Root<Product> correlatedRoot = variantSubquery.correlate(root);
                variantSubquery.select(variantRoot.get("id"));

                List<Predicate> variantPredicates = new ArrayList<>();
                variantPredicates.add(criteriaBuilder.equal(variantRoot.get("product"), correlatedRoot));
                variantPredicates.add(criteriaBuilder.equal(variantRoot.get("status"), ProductStatus.ACTIVE));

                if (hasSize) {
                    variantPredicates.add(criteriaBuilder.equal(variantRoot.get("size"), size));
                }

                if (hasColor) {
                    variantPredicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(variantRoot.get("color").get("name"), color),
                            criteriaBuilder.equal(variantRoot.get("colorOld"), color)
                    ));
                }

                if (minPrice != null) {
                    Expression<BigDecimal> effectivePrice = criteriaBuilder.coalesce(variantRoot.get("salePrice"), variantRoot.get("originalPrice"));
                    variantPredicates.add(criteriaBuilder.greaterThanOrEqualTo(effectivePrice, minPrice));
                }

                if (maxPrice != null) {
                    Expression<BigDecimal> effectivePrice = criteriaBuilder.coalesce(variantRoot.get("salePrice"), variantRoot.get("originalPrice"));
                    variantPredicates.add(criteriaBuilder.lessThanOrEqualTo(effectivePrice, maxPrice));
                }

                variantSubquery.where(criteriaBuilder.and(variantPredicates.toArray(new Predicate[0])));
                predicates.add(criteriaBuilder.exists(variantSubquery));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


