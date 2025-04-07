package com.locket.user.controller.product;

import com.locket.user.domain.product.constant.PaginationConstants;
import com.locket.user.domain.product.dto.*;
import com.locket.user.exception.ErrorResponse;
import com.locket.user.security.RequiresUser;
import com.locket.user.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "🛒 Product", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "카테고리별 상품 목록 조회", description = "지정된 카테고리의 상품 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 카테고리 또는 페이지 번호)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "유효하지 않은 카테고리 번호입니다. 유효한 범위: 1~11",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 사용자입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "해당 카테고리를 찾을 수 없습니다. 카테고리 ID 15는 존재하지 않습니다. 유효한 범위: 1~11",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "서버 내부 오류가 발생했습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @RequiresUser
    @GetMapping
    public ResponseEntity<ProductListResponseDTO> getProductsByCategory(
            @Parameter(description = "카테고리 번호(1~11)", required = true)
            @RequestParam Integer category,

            @Parameter(description = "페이지 번호")
            @RequestParam(required = false) Integer page
    ) {
        ProductListResponseDTO response = productService.getProductsByCategory(category, page, PaginationConstants.DEFAULT_PAGE_SIZE);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "특정 상품 상세 정보 조회", description = "상품 ID를 기반으로 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "유효하지 않은 요청입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 요청",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "해당 상품을 찾을 수 없습니다. 상품 ID 1은 존재하지 않습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "서버 오류가 발생했습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @RequiresUser
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(
            @PathVariable Integer productId,
            @RequestParam("userId") Long userId
    ) {
        ProductDetailResponseDTO response = productService.getProductDetail(productId, userId);
        return ResponseEntity.ok(response);
    }

    // 상품 찜하기
    @Operation(summary = "상품 찜하기/찜 해제", description = "상품을 찜 목록에 추가하거나 제거합니다. 찜 상태는 토글됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "유효하지 않은 요청입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 요청",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "해당 상품을 찾을 수 없습니다. 상품 ID 1은 존재하지 않습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "서버 오류가 발생했습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductLikeRequestDTO.class),
                    examples = @ExampleObject(
                            value = """
                    {
                        "userId": 123
                    }
                    """
                    )
            )
    )
    @RequiresUser(ownerOnly = false)
    @PostMapping("/{productId}/like")
    public ResponseEntity<ProductLikeResponseDTO> toggleProductLike(
            @PathVariable Integer productId,
            @RequestBody ProductLikeRequestDTO requestDTO
    ) {
        ProductLikeResponseDTO response = productService.toggleProductLike(
                productId,
                requestDTO.getUserId()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 찜 상품 목록 조회", description = "사용자가 찜한 상품 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜 상품 목록 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "유효하지 않은 요청입니다.",
                                          "timestamp": "2025-04-01T12:34:56.789Z"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "인증되지 않은 사용자입니다.",
                                          "timestamp": "2025-04-01T12:34:56.789Z"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 403,
                                          "error": "Forbidden",
                                          "message": "다른 사용자의 찜 목록에 접근할 수 없습니다.",
                                          "timestamp": "2025-04-01T12:34:56.789Z"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "해당 사용자를 찾을 수 없습니다.",
                                          "timestamp": "2025-04-01T12:34:56.789Z"
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 500,
                                          "error": "Internal Server Error",
                                          "message": "서버 내부 오류가 발생했습니다.",
                                          "timestamp": "2025-04-01T12:34:56.789Z"
                                        }
                                        """
                            )
                    )
            )
    })
    @RequiresUser(ownerOnly = true)
    @GetMapping("/liked")
    public ResponseEntity<ProductLikedListResponseDTO> getLikedProducts(
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId,

            @Parameter(description = "페이지 번호")
            @RequestParam(required = false) Integer page
    ) {
        ProductLikedListResponseDTO response = productService.getLikedProducts(userId, page, PaginationConstants.DEFAULT_PAGE_SIZE);
        return ResponseEntity.ok(response);
    }

    // 상품 가격 알림
    @Operation(summary = "상품 가격 알림 설정/해제", description = "상품의 가격 알림을 설정하거나 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS"),
            @ApiResponse(
                    responseCode = "400",
                    description = "BAD_REQUEST",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "유효하지 않은 요청입니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "UNAUTHORIZED",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "인증되지 않은 요청",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "NOT_FOUND",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "해당 상품을 찾을 수 없습니다. 상품 ID 1은 존재하지 않습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "INTERNAL_SERVER_ERROR",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "서버 오류가 발생했습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @RequiresUser(ownerOnly = false)
    @PostMapping("/{productId}/alert")
    public ResponseEntity<ProductAlertResponseDTO> setProductPriceAlert(
            @PathVariable Integer productId,
            @RequestBody ProductAlertRequestDTO requestDTO
    ) {
        ProductAlertResponseDTO response = productService.setProductPriceAlert(
                productId,
                requestDTO.getUserId(),
                requestDTO.getIsAlert(),
                requestDTO.getAlertPrice()
        );
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "상품 가격 변경", description = "상품 ID로 현재 가격을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "가격 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "해당 상품을 찾을 수 없습니다. 상품 ID 1은 존재하지 않습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @RequiresUser(ownerOnly = false)
    @PatchMapping("/{productId}/price")
    public ResponseEntity<Void> updateProductPrice(
            @PathVariable Integer productId,
            @RequestBody ProductPriceUpdateRequestDTO requestDTO
    ) {
        productService.updateProductPrice(productId, requestDTO.getNewPrice());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "만원의 행복 목록 조회", description = "만원 이하 특별 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증되지 않은 사용자입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "message": "서버 내부 오류가 발생했습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                    """
                            )
                    )
            )
    })
    @RequiresUser
    @GetMapping("/happiness")
    public ResponseEntity<ProductListResponseDTO> getHappinessProducts() {
        ProductListResponseDTO response = productService.getHappinessProducts();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "상품 검색 API",
            description = """
                **상품 이름**으로 검색하고, 결과를 **페이지** 단위로 가져옵니다.<br/>
                `Authorization: Bearer <JWT>` 토큰을 헤더에 포함해야 합니다.<br/>
                page 파라미터는 기본값 1입니다.<br/>
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductSearchResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            ---
                            ❌ 잘못된 요청
                          """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                            {
                                "status": 400,
                                "error": "BAD REQUEST",
                                "message": "유효하지 않은 요청입니다.",
                                "details": "category 파라미터는 정수여야 합니다."
                            }
                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            ---
                            \uD83D\uDD12 인증 실패
                          """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                            {
                              "status": 401,
                              "error": "UNAUTHORIZED",
                              "message": "인증되지 않은 요청",
                              "details": "유효한 Access Token 이 필요합니다."
                            }
                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            ---
                            📡 서버 에러
                          """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                            {
                              "status": 500,
                              "error": "INTERNAL SERVER ERROR",
                              "message": "서버 오류가 발생했습니다.",
                              "details": "서버 처리 중 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."
                            }
                            """
                            )
                    )
            )
    })
    @RequiresUser
    @GetMapping("/search")
    public ResponseEntity<ProductSearchResponseDTO> getProducts(
            @Parameter(description = "검색할 상품 이름", required = true)
            @RequestParam(name = "product_name", defaultValue = "삼성전자") String productName,

            @Parameter(description = "페이지 번호 (기본값: 1)")
            @RequestParam(name = "page", required = false, defaultValue = "1") int page
    ) {
        ProductSearchResponseDTO response = productService.searchProducts(productName, page);
        return ResponseEntity.ok(response);
    }
}