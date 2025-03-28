package com.locket.elasticsearch.domain.payment.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "payment_history")
public class PaymentHistory {

    @Id
    private String transactionId;

    @Field(type = FieldType.Long)
    private long buyerId;

    @Field(type = FieldType.Long)
    private long sellerId;

    @Field(type = FieldType.Keyword)
    private String userJob;

    @Field(type = FieldType.Long)
    private long birthDate;

    @Field(type = FieldType.Double)
    private BigDecimal totalAmount;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Keyword)
    private String paymentCategory;

    @Field(type = FieldType.Keyword)
    private String paymentMerchant;

    @Field(type = FieldType.Integer)
    private int cardId;

    @Field(type = FieldType.Keyword)
    private String cardName;

    @Field(type = FieldType.Keyword)
    private String storeName;  // ✅ 매장명 필드 추가

    @Field(type = FieldType.Boolean)
    private boolean receiptUploaded;  // ✅ 영수증 업로드 여부 추가

    @Field(type = FieldType.Keyword)
    private String paymentStatus;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime createdAt;

    // ✅ 날짜별 집계나 필터링을 위한 필드 추가
    @Field(type = FieldType.Integer)
    private int year;

    @Field(type = FieldType.Integer)
    private int month;

    @Field(type = FieldType.Integer)
    private int day;

    @Field(type = FieldType.Nested)
    private List<OrderDetail> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetail {

        @Field(type = FieldType.Keyword)
        private String orderId;

        @Field(type = FieldType.Keyword)
        private String cardNumber;

        @Field(type = FieldType.Double)
        private BigDecimal amount;

        @Field(type = FieldType.Keyword)
        private String paymentOrderStatus;
    }
}
