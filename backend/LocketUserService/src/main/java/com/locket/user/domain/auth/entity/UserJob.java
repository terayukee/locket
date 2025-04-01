package com.locket.user.domain.auth.entity;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum UserJob {
    직장인("직장인"),
    무직("무직"),
    자영업자("자영업자");

    private final String displayName;

    UserJob(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // 문자열에서 열거형 변환
    public static UserJob fromString(String value) {
        if (value == null) {
            return null;
        }

        for (UserJob job : UserJob.values()) {
            if (job.name().equalsIgnoreCase(value) || job.displayName.equals(value)) {
                return job;
            }
        }

        throw new IllegalArgumentException(
                "유효하지 않은 직업입니다. 가능한 값: " +
                        Arrays.stream(UserJob.values())
                                .map(UserJob::getDisplayName)
                                .collect(Collectors.joining(", "))
        );
    }
}