package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "customAuditorRef")

public class AuditConfiguration {
    @Bean
    public AuditorAware customAuditorRef() {
        return new AuditorAwareImpl();
    }
}
