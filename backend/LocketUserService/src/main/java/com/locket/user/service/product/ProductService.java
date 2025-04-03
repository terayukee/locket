package com.locket.user.service.product;

import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.product.constant.PaginationConstants;
import com.locket.user.domain.product.dto.*;
import com.locket.user.domain.product.entity.*;
import com.locket.user.domain.product.repository.*;
import com.locket.user.exception.CategoryNotFoundException;
import com.locket.user.exception.InvalidRequestException;
import com.locket.user.exception.ProductNotFoundException;
import com.locket.user.exception.ResourceNotFoundException;
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
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductUserPreferenceRepository productUserPreferenceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProductListResponseDTO getProductsByCategory(Integer categoryId, Integer page, Integer size) {

        if (categoryId < 1 || categoryId > 11) {
            throw new InvalidRequestException("category 파라미터는 1에서 11 사이의 값이어야 합니다.");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 페이징 처리된 상품 목록 조회
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

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 사용자 상품 정보
        ProductUserPreference userPreference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        // 가격 히스토리 조회
        List<PriceHistory> priceHistories = priceHistoryRepository.findByProductIdOrderByPriceDateAsc(productId);

        List<ProductPriceHistoryResponseDTO> priceHistoryDTOs = priceHistories.stream()
                .map(ProductPriceHistoryResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ProductDetailResponseDTO.builder()
                .userId(userId)
                .productId(product.getId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .currentPrice(product.getCurrentPrice())
                .discountRate(product.getDiscountRate())
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
    public ProductLikedListResponseDTO getLikedProducts(Long userId, Integer page, Integer size) {

        userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 페이지 처리
        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<ProductUserPreference> preferencesPage = productUserPreferenceRepository
                .findByUserIdAndIsLikedTrue(userId, pageable);

        // 전체 찜한 상품 수 조회
        long totalLikedProducts = productUserPreferenceRepository.countByUserIdAndIsLikedTrue(userId);

        List<ProductSummaryDTO> likedProducts = preferencesPage.getContent().stream()
                .filter(preference -> preference.getProduct() != null)
                .map(preference -> ProductSummaryDTO.fromEntity(preference.getProduct()))
                .collect(Collectors.toList());

        return ProductLikedListResponseDTO.builder()
                .userId(userId)
                .page(pageNumber + 1)
                .totalPages(preferencesPage.getTotalPages())
                .likedProductCount(likedProducts.size())
                .totalLikedProducts(totalLikedProducts)
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
}