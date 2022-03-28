package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentCreateResponse {
    private String message;
    private String name;
    private Long id;
}
