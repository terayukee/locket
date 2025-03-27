package com.locket.user.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetTestRequest {

    @Schema(description = "사용자 ID", example = "1")
    private long userId;

    @Schema(description = "결제 금액", example = "100000")
    private int amount;

    @Schema(description = "결제 카테고리", example = "식비")
    private String category;

    @Schema(description = "결제 상품명 또는 가맹점", example = "김밥천국")
    private String merchant;

    @Schema(description = "결제가 발생한 매장 이름", example = "강남본점")
    private String store;
}
