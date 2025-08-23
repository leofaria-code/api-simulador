package br.com.leo.apisimulador.config;

import org.h2.server.web.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2AuditConfig {

    private static final Logger logger = LoggerFactory.getLogger(H2AuditConfig.class);

    @Bean
    ServletRegistrationBean<WebServlet> h2servletRegistration() {
        ServletRegistrationBean<WebServlet> bean = new ServletRegistrationBean<>(new WebServlet());
        bean.addUrlMappings("/h2-console/*");

        logger.info("H2 Console configurado em: /h2-console");
        return bean;
    }
}