package com.crpt.Crypto.Model;

import lombok.Data;

@Data
public class RegisterRequest {

    private String userName;
    private String password;
    private String email;
}
