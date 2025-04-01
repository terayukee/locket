package com.locket.user.domain.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDTO {
    // 상품 목록 응답
    private Integer category;
    private String categoryName;
    private Integer page;
    private Integer pageProductCount;
    private Integer totalPages;
    private long categoryProductCount;
    private List<ProductSummaryDTO> products;
}