package com.levimartines.learningdevops.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levimartines.learningdevops.error.ErrorDetail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties({SecurityConfigProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ALL_REQUEST_PATH = "/**";
    private final SecurityConfigProperties properties;
    private final ObjectMapper mapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        AuthenticationEntryPoint authenticationEntryPoint =
                (request, response, authException) -> buildResponse(response, authException, HttpStatus.UNAUTHORIZED);
        AccessDeniedHandler accessDeniedHandler =
                (request, response, authException) -> buildResponse(response, authException, HttpStatus.FORBIDDEN);

        return httpSecurity
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .authorizeHttpRequests(this::authorizeRequests)
                .oauth2ResourceServer(oAuth2ResourceServerConfigurer -> {
                    oAuth2ResourceServerConfigurer.jwt();
                    oAuth2ResourceServerConfigurer.authenticationEntryPoint(authenticationEntryPoint);
                })
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and().build();
    }

    private void authorizeRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.antMatchers(properties.getUnsecuredRoutes()).permitAll();
        if (StringUtils.isNotEmpty(properties.getDefaultScope())) {
            auth.antMatchers(ALL_REQUEST_PATH).hasAuthority(properties.getDefaultScope());
        }
        auth.anyRequest().authenticated();
    }

    private void buildResponse(HttpServletResponse response, Exception authException, HttpStatus status)
            throws IOException {
        ErrorDetail errorDetail = ErrorDetail.builder()
                .message(authException.getMessage())
                .timestamp(System.currentTimeMillis())
                .status(status.value())
                .build();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(mapper.writeValueAsString(errorDetail));
    }

}
