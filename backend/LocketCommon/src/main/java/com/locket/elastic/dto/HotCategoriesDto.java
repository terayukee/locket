package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HotCategoriesDto {
    private List<String> categories;
}