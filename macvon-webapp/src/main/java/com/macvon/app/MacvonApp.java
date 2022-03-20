package com.macvon.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan({ "com.macvon.*", "com.macvon.config.*", "com.macvon.auth.*", "com.macvon.auth.service.*", "com.macvon.auth.handler.*",
	"com.macvon.controller.*", "com.macvon.service.*",
		"com.macvon.dao.*", "com.macvon.domain.*", "com.macvon.filter.*"})
public class MacvonApp extends SpringBootServletInitializer {
	
	public static void main(String[] args) {
		// WebAppPreInitializer.initAll();
		SpringApplication.run(MacvonApp.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MacvonApp.class);
	}
}
