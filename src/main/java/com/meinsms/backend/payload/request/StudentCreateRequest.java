package com.meinsms.backend.payload.request;

import lombok.Data;

@Data
public class StudentCreateRequest {
    private String name;
    private String gender;
    private byte[] avatar;
}
