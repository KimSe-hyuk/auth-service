package com.example.loginservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "naver")
public class NaverConfig {
    // Getter와 Setter
    private String clientId;
    private String clientSecret;
    private String redirectUrl;

}
