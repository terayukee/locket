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
    private Integer likedProductCount;
    private List<ProductSummaryDTO> likedProducts;
}