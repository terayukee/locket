package com.locket.elasticsearch.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class DateRange {
    private Instant start;
    private Instant end;
}
