package com.locket.user.service.product;

import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.domain.product.constant.PaginationConstants;
import com.locket.user.domain.product.dto.*;
import com.locket.user.domain.product.entity.*;
import com.locket.user.domain.product.repository.*;
import com.locket.user.service.notification.ProductNotificationService;
import com.locket.user.exception.CategoryNotFoundException;
import com.locket.user.exception.InvalidRequestException;
import com.locket.user.exception.ProductNotFoundException;
import com.locket.user.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductUserPreferenceRepository productUserPreferenceRepository;
    private final ProductNotificationService productAlertNotificationService;
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Transactional(readOnly = true)
    public ProductListResponseDTO getProductsByCategory(Integer categoryId, Integer page, Integer size) {

        if (categoryId < 1 || categoryId > 11) {
            throw new InvalidRequestException("category 파라미터는 1에서 11 사이의 값이어야 합니다.");
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        int pageNumber = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size <= 0) ? PaginationConstants.DEFAULT_PAGE_SIZE : size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 페이징 처리된 상품 목록 조회
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ProductSummaryDTO> productDTOs = productPage.getContent().stream()
                .map(ProductSummaryDTO::fromEntity)
                .collect(Collectors.toList());

        // 메소드 내부
        log.debug("카테고리 조회: {}", categoryId);
        log.debug("페이지 번호: {}, 페이지 사이즈: {}", pageNumber, pageSize);
        log.debug("조회된 상품 수: {}", productPage.getContent().size());
        log.debug("전체 카테고리 상품 수: {}", productRepository.countByCategoryId(categoryId));

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

        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        List<ProductPriceHistoryResponseDTO> priceHistoryDTOs = priceHistories.stream()
                .sorted(Comparator.comparing(history -> {
                    String[] parts = history.getPriceDate().split("\\.");
                    int month = Integer.parseInt(parts[0]);
                    String day = parts[1];

                    int year = (month <= 4) ? 2025 : 2024;
                    String fullDate = year + "." + String.format("%02d", month) + "." + String.format("%02d", Integer.parseInt(day));

                    return LocalDate.parse(fullDate, fullFormatter);
                }))
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
                .priceHistory(priceHistoryDTOs)
                .build();
    }

    // 상품 찜하기
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ProductLikeResponseDTO toggleProductLike(Integer productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 저장 전 다시 한번 DB에서 조회
        Optional<ProductUserPreference> existingPref = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId);

        ProductUserPreference preference;
        boolean newLikeStatus;

        if (existingPref.isPresent()) {

            preference = existingPref.get();
            newLikeStatus = !preference.isLiked();
            preference.setLiked(newLikeStatus);

        } else {
            preference = new ProductUserPreference(null, product, userId, true, false, null);
            newLikeStatus = true;
        }

        log.debug("찜하기 토글: 상품 ID={}, 사용자 ID={}, 상태 변경={}", productId, userId, newLikeStatus);

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
        int pageSize = (size == null || size <= 0) ? PaginationConstants.DEFAULT_PAGE_SIZE : size;

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

        if (Boolean.TRUE.equals(isAlert)) {
            if (alertPrice == null || alertPrice <= 0) {
                throw new InvalidRequestException("알림 설정 금액은 0원보다 커야 합니다.");
            }
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));


        ProductUserPreference preference = productUserPreferenceRepository
                .findByProductIdAndUserId(productId, userId)
                .orElse(new ProductUserPreference(null, product, userId, false, false, null));

        if (Boolean.TRUE.equals(isAlert)) {
            preference.setAlertPrice(alertPrice);
        }

        preference.setAlert(isAlert);

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

        // ✅ 숫자 추출
        String digitsOnly = newPrice.replaceAll("[^0-9]", "");

        if (digitsOnly.isEmpty()) {
            throw new IllegalArgumentException("가격 정보에 숫자가 포함되어 있지 않습니다: " + newPrice);
        }

        int priceValue = Integer.parseInt(digitsOnly);

        // ✅ "9,000원" 형식으로 변환
        String formattedPrice = NumberFormat.getNumberInstance(Locale.KOREA).format(priceValue) + "원";


        // ✅ 저장
        product.setCurrentPrice(formattedPrice);
        productRepository.save(product);

        // ✅ 알림 트리거
        productAlertNotificationService.notifyUsersIfPriceDrops(product);
    }

    // 만원의 행복
    @Transactional(readOnly = true)
    public ProductListResponseDTO getHappinessProducts() {
        // 만원의 행복 카테고리 ID
        Integer categoryId = 11;

        return getProductsByCategory(categoryId, 1, 10);
    }

    public ProductSearchResponseDTO searchProducts(String productName, int page) {
        int pageIndex = (page < 1) ? 0 : page - 1;
        int pageSize = PaginationConstants.DEFAULT_PAGE_SIZE; // 한 페이지에 보여줄 상품 개수

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = productRepository.findByProductNameContaining(productName, pageable);

        var productDtoList = productPage.getContent().stream()
                .map(p -> new ProductSummaryDTO(
                        p.getId(),
                        p.getProductName(),
                        p.getImageUrl(),
                        p.getCurrentPrice(),
                        p.getDiscountRate()
                ))
                .collect(Collectors.toList());

        ProductSearchResponseDTO response = new ProductSearchResponseDTO();
        response.setSearchKeyword(productName);
        response.setPage(page);
        response.setPageProductCount(productPage.getNumberOfElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setSearchProductCount(productPage.getTotalElements());
        response.setProducts(productDtoList);

        return response;
    }
}