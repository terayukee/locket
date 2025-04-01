package com.locket.user.service.product;

import com.locket.user.domain.product.dto.PriceHistoryDTO;
import com.locket.user.domain.product.dto.ProductDetailResponseDTO;
import com.locket.user.domain.product.dto.ProductListResponseDTO;
import com.locket.user.domain.product.dto.ProductSummaryDTO;
import com.locket.user.domain.product.entity.*;
import com.locket.user.domain.product.repository.*;
import com.locket.user.exception.CategoryNotFoundException;
import com.locket.user.exception.InvalidRequestException;
import com.locket.user.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductDetailRepository productDetailRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductUserPreferenceRepository productUserPreferenceRepository;

    @Transactional(readOnly = true)
    public ProductListResponseDTO getProductsByCategory(Integer categoryId, Integer page) {
        // 카테고리 유효성 검사 (1~11 범위 체크)
        if (categoryId < 1 || categoryId > 11) {
            throw new InvalidRequestException("category 파라미터는 1에서 11 사이의 값이어야 합니다.");
        }

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = 30;

        // 페이징 처리된 상품 목록 조회
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ProductSummaryDTO> productDTOs = productPage.getContent().stream()
                .map(ProductSummaryDTO::fromEntity)
                .collect(Collectors.toList());

        return ProductListResponseDTO.builder()
                .category(categoryId)
                .categoryName(category.getName())
                .page(pageNumber + 1)
                .pageProductCount(productDTOs.size())
                .totalPages(productPage.getTotalPages())
                .categoryProductCount(productRepository.countByCategoryId(categoryId))
                .products(productDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDTO getProductDetail(Integer productId, Long userId) {
        // 상품 존재 여부 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 상품 상세 정보 조회
        ProductDetail productDetail = productDetailRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 사용자 상품 선호도 정보 조회 (찜, 알림 등)
        ProductUserPreference userPreference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        // 가격 히스토리 조회
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductIdOrderByPriceDateAsc(productId);
        List<PriceHistoryDTO> priceHistoryDTOs = priceHistories.stream()
                .map(PriceHistoryDTO::fromEntity)
                .collect(Collectors.toList());

        // 응답 DTO 생성 및 반환
        return ProductDetailResponseDTO.builder()
                .userId(userId)
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .currentPrice(product.getCurrentPrice())
                .discountRate(product.getDiscountRate())
                .highestPrice(productDetail.getHighestPrice())
                .discountAmount(productDetail.getDiscountAmount())
                .unitPrice(productDetail.getUnitPrice())
                .shippingType(productDetail.getShippingType())
                .reviewCount(productDetail.getReviewCount())
                .reviewRating(productDetail.getReviewRating())
                .isLiked(userPreference.isLiked())
                .isAlert(userPreference.isAlert())
                .alertPrice(userPreference.getAlertPrice())
                .coupangUrl(productDetail.getCoupangUrl())
                .averagePrice(productDetail.getAveragePrice())
                .priceHistory(priceHistoryDTOs)
                .build();
    }
}