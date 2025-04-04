package com.locket.user.controller.notification;

import com.locket.user.domain.notification.dto.UserAlertDto;
import com.locket.user.service.notification.UserAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
@Tag(
        name = "🔔 사용자 알림 API",
        description = """
                사용자의 알림 정보를 조회하는 API입니다.  
                \uD83D\uDCC8 [저축 목표 알림] - goals_alerts 테이블  
                \uD83C\uDFEA [상품 가격 알림] - products_alerts 테이블  
                \n모든 알림은 최신순 정렬로 제공됩니다.
                """
)
public class NotificationController {

    private final UserAlertService userAlertService;

    @GetMapping("/all")
    @Operation(
            summary = "🔔 사용자 알림 전체 조회",
            description = """
                    📬 사용자의 알림 내역을 조회합니다.  
                    \n📌 **goals_alerts** 테이블(저축 목표 초과 등)  
                    \n🛍️ **products_alerts** 테이블(상품 가격 알림 등)  
                    \n🕐 최신순으로 정렬되어 반환됩니다.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "✅ 알림 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "🚫 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "🔥 서버 오류")
            }
    )
    public ResponseEntity<Map<String, List<UserAlertDto>>> getAllUserAlerts(@RequestParam Long userId) {
        Map<String, List<UserAlertDto>> alerts = userAlertService.getUserAlerts(userId);
        return ResponseEntity.ok(alerts);
    }
}
