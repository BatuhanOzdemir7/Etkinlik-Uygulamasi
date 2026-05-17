package com.works.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Etkinlik Yönetimi API Dokümantasyonu")
                        .version("1.0.0")
                        .description("Bu platform, kullanıcıların etkinlik oluşturması, listelemesi ve etkinliklere katılması için geliştirilmiş RESTful API servislerini içerir."));
    }
}