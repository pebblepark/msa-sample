package com.example.userservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private String email;
    private String pwd;
    private String name;
    private String userId;
    private Date createdAt;

    private String encryptedPwd;
}
