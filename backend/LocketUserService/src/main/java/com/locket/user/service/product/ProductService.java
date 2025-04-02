package com.locket.user.service.product;

import com.locket.user.domain.product.dto.*;
import com.locket.user.domain.product.entity.*;
import com.locket.user.domain.product.repository.*;
import com.locket.user.service.notification.ProductAlertNotificationService;
import com.locket.user.exception.CategoryNotFoundException;
import com.locket.user.exception.InvalidRequestException;
import com.locket.user.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductUserPreferenceRepository productUserPreferenceRepository;
    private final ProductAlertNotificationService productAlertNotificationService;

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

        // 사용자 상품 선호도 정보 조회 (찜, 알림 등)
        ProductUserPreference userPreference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        // 가격 히스토리 조회
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductIdOrderByPriceDateAsc(productId);
        List<ProductPriceHistoryDTO> priceHistoryDTOs = priceHistories.stream()
                .map(ProductPriceHistoryDTO::fromEntity)
                .collect(Collectors.toList());

        return ProductDetailResponseDTO.builder()
                .userId(userId)
                .productId(product.getId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .currentPrice(product.getCurrentPrice())
                .discountRate(product.getDiscountRate())
                // [수정] ProductDetail 대신 Product에서 필드 가져오기
                .highestPrice(product.getHighestPrice())
                .discountAmount(product.getDiscountAmount())
                .unitPrice(product.getUnitPrice())
                .shippingType(product.getShippingType())
                .reviewCount(product.getReviewCount())
                .reviewRating(product.getReviewRating())
                .isLiked(userPreference.isLiked())
                .isAlert(userPreference.isAlert())
                .alertPrice(userPreference.getAlertPrice())
                .coupangUrl(product.getCoupangUrl())
                .averagePrice(product.getAveragePrice())
                .priceHistory(priceHistoryDTOs)
                .build();
    }

    // 상품 찜하기
    @Transactional
    public ProductLikeResponseDTO toggleProductLike(Integer productId, Long userId, Boolean isLiked) {
        if (isLiked == null) {
            throw new InvalidRequestException("isLiked 값은 필수입니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        ProductUserPreference preference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        preference.setLiked(isLiked);

        ProductUserPreference savedPreference = productUserPreferenceRepository.save(preference);

        return ProductLikeResponseDTO.builder()
                .isLiked(savedPreference.isLiked())
                .build();
    }

    // 찜한 상품 리스트
    @Transactional(readOnly = true)
    public ProductLikedListResponseDTO getLikedProducts(Long userId) {
        log.info("찜한 상품 목록 조회 - 사용자 ID: {}", userId);

        // 조회
        List<ProductUserPreference> likedPreferences = productUserPreferenceRepository
                .findByUserIdAndIsLikedTrue(userId);

        log.info("조회된 찜 상품 수: {}", likedPreferences.size());

        // 찜한 상품이 없는 경우
        if (likedPreferences.isEmpty()) {
            log.info("찜한 상품이 없습니다.");
            return ProductLikedListResponseDTO.builder()
                    .userId(userId)
                    .likedProductCount(0)
                    .likedProducts(Collections.emptyList())
                    .build();
        }

        // [추가] 찜한 상품 정보 로깅
        for (ProductUserPreference pref : likedPreferences) {
            Product product = pref.getProduct();
            if (product == null) {
                log.warn("상품 정보가 없습니다. 선호도 ID: {}", pref.getId());
            } else {
                log.info("찜한 상품: ID={}, 이름={}", product.getId(), product.getProductName());
            }
        }

        // ProductSummaryDTO
        List<ProductSummaryDTO> likedProducts = likedPreferences.stream()
                .filter(preference -> preference.getProduct() != null) // [추가] null 상품 필터링
                .map(preference -> ProductSummaryDTO.fromEntity(preference.getProduct()))
                .collect(Collectors.toList());

        log.info("변환된 상품 DTO 수: {}", likedProducts.size());

        return ProductLikedListResponseDTO.builder()
                .userId(userId)
                .likedProductCount(likedProducts.size())
                .likedProducts(likedProducts)
                .build();
    }

    // 상품 가격 알림
    @Transactional
    public ProductAlertResponseDTO setProductPriceAlert(Integer productId, Long userId, Boolean isAlert, Integer alertPrice) {
        if (isAlert == null) {
            throw new InvalidRequestException("isAlert 값은 필수입니다.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // isAlert가 true일 때, alertPrice가 null이면 예외 발생
        if (Boolean.TRUE.equals(isAlert) && alertPrice == null) {
            throw new InvalidRequestException("알림 설정 시 알림 가격(alertPrice)은 필수입니다.");
        }

        ProductUserPreference preference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        preference.setAlert(isAlert);
        preference.setAlertPrice(isAlert ? alertPrice : null);  // 알림 해제 시 알림 가격도 null로 설정

        ProductUserPreference savedPreference = productUserPreferenceRepository.save(preference);

        return ProductAlertResponseDTO.builder()
                .isAlert(savedPreference.isAlert())
                .alertPrice(savedPreference.getAlertPrice())
                .build();
    }

    // 상품 현재가 업데이트
    @Transactional
    public void updateProductPrice(Integer productId, String newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setCurrentPrice(newPrice);
        productRepository.save(product);

        // ✅ 알림 트리거
        productAlertNotificationService.notifyUsersIfPriceDrops(product);
    }
}