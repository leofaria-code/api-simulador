package br.com.leo.apisimulador.config.h2;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "br.com.leo.apisimulador.repository.h2", entityManagerFactoryRef = "localEntityManagerFactory", transactionManagerRef = "localTransactionManager")
public class LocalH2Config {

    @Bean
    @Qualifier("localH2DataSource")
    @ConfigurationProperties(prefix = "spring.second-datasource.local-h2")
    public DataSource localH2DataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localEntityManagerFactory(
            @Qualifier("localH2DataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("br.com.leo.apisimulador.model.h2");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Configurações específicas do Hibernate para H2
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.time_zone", "America/Sao_Paulo");
        // Configurações adicionais para estabilidade de transação
        properties.put("hibernate.connection.handling_mode", "delayed_acquisition_and_release_after_transaction");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");
        properties.put("hibernate.generate_statistics", "false");
        properties.put("hibernate.enable_lazy_load_no_trans", "false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager localTransactionManager(
            @Qualifier("localEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(Objects.requireNonNull(entityManagerFactory.getObject()));
        
        // Configurações para H2 (mais permissivo)
        transactionManager.setFailEarlyOnGlobalRollbackOnly(false);
        transactionManager.setNestedTransactionAllowed(true);
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setDefaultTimeout(60); // 60s timeout para H2
        
        return transactionManager;
    }
}