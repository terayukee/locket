package com.locket.elasticsearch.common.util;

import com.locket.elasticsearch.common.dto.DateRange;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class DateTimeUtil {

    /**
     * 주어진 연도와 월을 기준으로 UTC 기준의 시작과 끝 Instant 범위를 반환합니다.
     */
    public static DateRange getMonthRangeUtc(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        Instant start = firstDay.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = lastDay.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);

        return new DateRange(start, end);
    }
}
