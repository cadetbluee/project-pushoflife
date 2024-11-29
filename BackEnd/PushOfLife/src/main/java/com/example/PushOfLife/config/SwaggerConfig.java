package com.example.PushOfLife.config;

import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.beans.BeanProperty;
import java.util.Collections;

import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${spring.swagger.server}")
        private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        Info info = new Info()
                .title("Push Of Life API 명세서")
                .version("v.0.1")
                .description("API 명세서");

        String jwtSchemeName = "jwtAuth";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components().addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));

        return new OpenAPI().components(new Components())
                .info(info)
                .servers(Collections.singletonList(new Server().url(serverUrl).description("API Server")))
                .addSecurityItem(securityRequirement)
                .components(components);

    }

}
