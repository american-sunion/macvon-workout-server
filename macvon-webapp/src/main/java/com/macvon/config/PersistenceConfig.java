package com.macvon.config;

import java.util.Base64;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macvon.domain.auth.MySqlInfo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Concrete mysql data source configuration...
 * 
 * @author xunwu
 *
 */
@Configuration
@EnableTransactionManagement
public class PersistenceConfig {
	@Autowired
	private Environment env;

	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
		MySqlInfo mysqlInfo = getSecret();
		String jdbcUrl = "jdbc:mysql://" + mysqlInfo.getHost() +":"+ mysqlInfo.getPort()+ "/macvonworkout"
				+ "?autoReconnect=true&useSSL=false";
		hikariConfig.setJdbcUrl(jdbcUrl);
		hikariConfig.setUsername(mysqlInfo.getUsername());
		hikariConfig.setPassword(mysqlInfo.getPassword());

		String prepStmtCacheSize = env.getProperty("jdbc.datasource.prepStmtCacheSize");
		String useServerPrepStmts = env.getProperty("jdbc.datasource.useServerPrepStmts");
		String prepStmtCacheSqlLimit = env.getProperty("jdbc.datasource.prepStmtCacheSqlLimit");
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);
		hikariConfig.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
		HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
		return hikariDataSource;
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setPackagesToScan("com.macvon.domain", "com.macvon.dao", "com.macvon.service");
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		factory.setJpaVendorAdapter(vendorAdapter);
		Properties props = new Properties();
		props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		// props.put("hibernate.show_sql", false);
		String defaultSchema = "macvonworkout";
		props.put("hibernate.default_schema", defaultSchema);
		props.put("hibernate.connection.SetBigStringTryClob", true);
		props.put("hibernate.id.new_generator_mappings", "false");

		factory.setJpaProperties(props);

		factory.afterPropertiesSet();
		return factory;
	}

	/** data source ****/

	@Bean(name = "txManager")
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) throws Exception {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	public MySqlInfo getSecret() {
		String region = env.getProperty("app.db.region");
		String accesskey = System.getProperty("accesskey");
		String secretKey = System.getProperty("secretKey");
		String secretName = System.getProperty("secretName");
		AWSSecretsManager client = null;
		if (accesskey != null && secretKey != null) {
			AWSCredentials credentials = new BasicAWSCredentials(accesskey, secretKey);
			client = AWSSecretsManagerClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
		} else {
			client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
		}
		String secret, decodedBinarySecret;
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
		GetSecretValueResult getSecretValueResult = null;
		try {
			getSecretValueResult = client.getSecretValue(getSecretValueRequest);
			if (getSecretValueResult.getSecretString() != null) {
				secret = getSecretValueResult.getSecretString();
				return new ObjectMapper().readValue(secret, MySqlInfo.class);
			} else {
				decodedBinarySecret = new String(
						Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
				return new ObjectMapper().readValue(decodedBinarySecret, MySqlInfo.class);
			}
		} catch (DecryptionFailureException e) {
			throw new RuntimeException(e);
		} catch (InternalServiceErrorException e) {
			throw new RuntimeException(e);
		} catch (InvalidParameterException e) {
			throw new RuntimeException(e);
		} catch (InvalidRequestException e) {
			throw new RuntimeException(e);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
