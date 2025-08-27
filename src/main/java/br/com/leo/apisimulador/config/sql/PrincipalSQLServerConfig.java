package br.com.leo.apisimulador.config.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories(basePackages = "br.com.leo.apisimulador.repository.sqlserver", entityManagerFactoryRef = "principalEntityManagerFactory", transactionManagerRef = "principalTransactionManager")
public class PrincipalSQLServerConfig {

    @Bean
    @Primary
    @Qualifier("principalDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.principal-sqlserver")
    public DataSource principalDataSource() {
        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean principalEntityManagerFactory(
            @Qualifier("principalDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("br.com.leo.apisimulador.model.sqlserver");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        // Configurações específicas do Hibernate para SQL Server
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        properties.put("hibernate.hbm2ddl.auto", "none"); // Não alterar estrutura do BD
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.jdbc.time_zone", "America/Sao_Paulo");
        // Configurações adicionais para estabilidade de transação
        properties.put("hibernate.connection.handling_mode", "delayed_acquisition_and_release_after_transaction");
        properties.put("hibernate.jdbc.batch_size", "20");
        properties.put("hibernate.order_inserts", "true");
        properties.put("hibernate.order_updates", "true");
        properties.put("hibernate.generate_statistics", "false");
        properties.put("hibernate.enable_lazy_load_no_trans", "false");
        // Configurações específicas de timeout para conexões instáveis
        properties.put("hibernate.connection.acquisition_timeout", "30000"); // 30s para obter conexão
        properties.put("hibernate.connection.release_mode", "after_transaction");
        properties.put("hibernate.jdbc.use_streams_for_binary", "false");
        properties.put("hibernate.connection.autocommit", "false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager principalTransactionManager(
            @Qualifier("principalEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(Objects.requireNonNull(entityManagerFactory.getObject()));
        
        // Configurações para lidar com falhas de conectividade
        transactionManager.setFailEarlyOnGlobalRollbackOnly(false);
        transactionManager.setNestedTransactionAllowed(true);
        transactionManager.setRollbackOnCommitFailure(true);
        transactionManager.setDefaultTimeout(30); // 30s timeout
        transactionManager.setValidateExistingTransaction(false); // Não validar transações existentes
        transactionManager.setGlobalRollbackOnParticipationFailure(false); // Não falhar toda transação se um participante falhar
        
        return transactionManager;
    }
}