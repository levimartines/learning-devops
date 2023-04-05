package com.levimartines.learningdevops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigProperties {

    private boolean enabled;
    private String defaultScope;
    private String[] unsecuredRoutes;
}

