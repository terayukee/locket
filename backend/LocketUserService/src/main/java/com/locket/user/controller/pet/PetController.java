package com.locket.user.controller.pet;

import com.locket.user.domain.pet.dto.*;
import com.locket.user.exception.ErrorResponse;
import com.locket.user.service.pet.CharacterService;
import com.locket.user.service.pet.RewardService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/pet")
@RequiredArgsConstructor
@Tag(name = "🐱Pet", description = "캐릭터 조회, 경험치 관리 API")
public class PetController {

    private final CharacterService characterService;
    private final RewardService rewardService;

    @Operation(summary = "캐릭터 보유 여부 조회", description = "사용자가 캐릭터를 보유하고 있는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캐릭터 보유 여부 조회 성공"),
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
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "사용자를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PetResponseDto> hasPet(@RequestParam("userId") Long userId) {
        PetResponseDto response = characterService.checkPetOwnership(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "캐릭터 정보 조회", description = "사용자의 캐릭터 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캐릭터 정보 조회 성공"),
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
                    description = "캐릭터를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "캐릭터를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CharacterInfoDto> getCharacterInfo(@PathVariable("userId") Long userId) {
        CharacterInfoDto characterInfo = characterService.getCharacterInfo(userId);
        return ResponseEntity.ok(characterInfo);
    }

    @Operation(summary = "캐릭터 경험치 증가", description = "사료 주기 또는 놀아주기를 통해 캐릭터의 경험치를 증가시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "경험치 증가 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청(사료 부족, 장난감 쿨타임 중 등)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "사료가 부족합니다.",
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
                    description = "캐릭터를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "캐릭터를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{userId}/experience")
    public ResponseEntity<ExpActionResponse> addExperience(
            @PathVariable("userId") Long userId,
            @RequestBody ExpActionRequest request) {
        ExpActionResponse response = characterService.addExperience(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기프티콘 목록 조회", description = "사용자가 획득한 기프티콘(리워드) 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기프티콘 목록 조회 성공"),
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
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "사용자를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{userId}/rewards")
    public ResponseEntity<RewardListResponseDto> getRewards(@PathVariable("userId") Long userId) {
        RewardListResponseDto response = rewardService.getUserRewards(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "캐릭터 생성", description = "사용자에게 새로운 캐릭터를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캐릭터 생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 캐릭터를 보유하고 있음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "이미 캐릭터를 보유하고 있습니다.",
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
            )
    })
    @PostMapping
    public ResponseEntity<CharacterInfoDto> createCharacter(@RequestParam("userId") Long userId) {
        CharacterInfoDto characterInfo = characterService.createAndReturnCharacter(userId);
        return ResponseEntity.ok(characterInfo);
    }

    @Operation(summary = "캐릭터 최대 레벨 달성 보상", description = "최대 레벨에 도달한 캐릭터에게 리워드를 부여하고 캐릭터를 리셋합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리워드 부여 및 캐릭터 리셋 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "캐릭터가 최대 레벨에 도달하지 않음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "캐릭터가 아직 최대 레벨이 아닙니다.",
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
                    description = "캐릭터를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "캐릭터를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{userId}/complete")
    public ResponseEntity<RewardDto> completeCharacterAndReset(@PathVariable("userId") Long userId) {
        RewardDto reward = characterService.completeCharacterAndReset(userId);
        return ResponseEntity.ok(reward);
    }

    @Operation(summary = "사료 추가", description = "미션 완료 또는 결제 보상으로 사료를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사료 추가 성공"),
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
                    description = "캐릭터를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "캐릭터를 찾을 수 없습니다.",
                                              "timestamp": "2025-04-01T12:34:56.789Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{userId}/food")
    public ResponseEntity<FoodResponseDto> addFood(@PathVariable("userId") Long userId) {
        FoodResponseDto response = characterService.addFood(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "테스트용: 캐릭터 삭제", description = "개발 테스트용으로 사용자의 캐릭터를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "캐릭터 삭제 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "캐릭터를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "캐릭터를 찾을 수 없습니다.",
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
    @DeleteMapping("/{userId}")
    public ResponseEntity<CharacterDeleteResponseDto> deleteCharacter(@PathVariable("userId") Long userId) {
        CharacterDeleteResponseDto response = characterService.deleteCharacter(userId);
        return ResponseEntity.ok(response);
    }

}