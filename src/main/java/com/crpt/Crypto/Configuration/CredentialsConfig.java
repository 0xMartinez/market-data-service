package com.crpt.Crypto.Configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CredentialsConfig {

    @Value("${credentials.api-key}")
    private String apiKey;

    @Value("${credentials.secret-key}")
    private String secretKey;

}
