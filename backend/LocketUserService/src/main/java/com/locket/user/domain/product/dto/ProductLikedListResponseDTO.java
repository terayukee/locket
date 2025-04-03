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
public class ProductLikedListResponseDTO {
    private Long userId;
    private Integer page;
    private Integer totalPages;
    private Integer likedProductCount; // 현재 페이지에 포함된 찜 상품 수
    private long totalLikedProducts;   // 전체 찜한 상품 수
    private List<ProductSummaryDTO> likedProducts;
}