package com.locket.user.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 검색 응답 DTO")
public class ProductSearchResponseDTO {

    @Schema(description = "검색어")
    private String searchKeyword;

    @Schema(description = "현재 페이지 번호")
    private int page;

    @Schema(description = "해당 페이지의 상품 개수")
    private int pageProductCount;

    @Schema(description = "총 페이지 수")
    private int totalPages;

    @Schema(description = "검색된 전체 상품 수")
    private long searchProductCount;

    @Schema(description = "상품 리스트")
    private List<ProductSummaryDTO> products;
}
