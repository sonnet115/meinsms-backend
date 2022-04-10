package com.bezkoder.springjwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CommonResponse {
    private boolean status;
    private String message;
    private Object data;
}

