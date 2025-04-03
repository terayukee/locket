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

public class ProductDetailResponseDTO {
    //상품 상세 응답

    private Long userId;
    private Integer productId;
    private String productName;
    private String imageUrl;
    private String highestPrice;
    private String currentPrice;
    private String discountAmount;
    private String unitPrice;
    private String shippingType;
    private String discountRate;
    private String reviewCount;
    private String reviewRating;
    private boolean isLiked;
    private boolean isAlert;
    private Integer alertPrice;
    private String coupangUrl;
    private Integer averagePrice;
    private List<ProductPriceHistoryResponseDTO> priceHistory;
}
