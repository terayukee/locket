package com.locket.elasticsearch.service.payment;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.locket.elasticsearch.payment.entity.PaymentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    private final ElasticsearchClient elasticsearchClient;

    public List<PaymentHistory> findByUserAndMonth(int userId, int year, int month) throws IOException {
        // ✅ 정확한 범위를 위해 LocalDateTime 사용
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

        // ✅ Elasticsearch에 ISO 8601 포맷 사용
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String startStr = formatter.format(start);
        String endStr = formatter.format(end);

        Query query = bool(b -> b
                .must(term(t -> t.field("buyerId").value(userId)))
                .must(range(r -> r
                        .field("createdAt")
                        .gte(JsonData.of(startStr))
                        .lte(JsonData.of(endStr))
                ))
        );

        SearchRequest request = SearchRequest.of(s -> s
                .index("payment_history")
                .query(query)
        );

        SearchResponse<PaymentHistory> response =
                elasticsearchClient.search(request, PaymentHistory.class);

        return response.hits().hits().stream()
                .map(Hit::source)
                .toList();
    }
}
