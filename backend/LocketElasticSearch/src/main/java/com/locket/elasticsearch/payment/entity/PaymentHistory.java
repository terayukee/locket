package com.locket.elasticsearch.payment.entity;

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

    @Field(type = FieldType.Integer)
    private int buyerId;

    @Field(type = FieldType.Integer)
    private int sellerId;

    @Field(type = FieldType.Keyword)
    private String userJob;

    @Field(type = FieldType.Keyword)
    private String birthDate;

    @Field(type = FieldType.Double)
    private BigDecimal totalAmount;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Keyword)
    private String paymentCategory;

    @Field(type = FieldType.Keyword)
    private String paymentMerchant;

    @Field(type = FieldType.Keyword)
    private String paymentStatus;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime createdAt;

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
