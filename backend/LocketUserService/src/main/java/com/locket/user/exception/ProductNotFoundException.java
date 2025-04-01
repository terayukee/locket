package com.locket.user.exception;

public class ProductNotFoundException extends ResourceNotFoundException {
    public ProductNotFoundException(Integer productId) {
        super(String.format("해당 상품을 찾을 수 없습니다. 상품 ID %d는 존재하지 않습니다.", productId));
    }
}