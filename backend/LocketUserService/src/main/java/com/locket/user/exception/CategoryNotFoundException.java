package com.locket.user.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
    public CategoryNotFoundException(Integer categoryId) {
        super(String.format("해당 카테고리를 찾을 수 없습니다. 카테고리 ID %d는 존재하지 않습니다. 유효한 범위: 1~11", categoryId));
    }
}