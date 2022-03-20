package com.macvon.config;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLContext;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.macvon.interceptors.RequestInterceptor;
import com.macvon.query.QueryLoader;
import com.macvon.service.email.AppEmailService;

/**
 * define spring configuration.
 * 
 * @author xunwu
 *
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableAsync
@EnableAspectJAutoProxy
@EnableScheduling
@EnableCaching
public class WebAppConfig implements WebMvcConfigurer {

	@Value("${http.maxTotal}")
	private int httpMaxTotal;
	@Value("${http.maxPerRoute}")
	private int httpMaxPerRoute;
	/**
	 * for upload.
	 * 
	 * @return
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(5242880);
		// multipartResolver.setMaxUploadSize(Long.parseLong(env.getProperty("web.max_upload_size")));
		return multipartResolver;
	}

	/**
	 * critical!!
	 * 
	 * @return
	 */
	@Bean
	@Order(0)
	public MultipartFilter multipartFilter() {
		MultipartFilter multipartFilter = new MultipartFilter();
		multipartFilter.setMultipartResolverBeanName("multipartReso‌​lver");
		return multipartFilter;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("/");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
		registry.addResourceHandler("/**", "/assets/**", "/components/**", "/directives/**", "/sections/**",
				"/services/**").addResourceLocations("classpath:/static/", "classpath:/static/assets/",
						"classpath:/static/components/", "classpath:/static/directives/", "classpath:/static/sections/",
						"classpath:/static/services/");
	}

	/**
	 * define email template engine.
	 * 
	 * @return
	 */
	@Bean
	public TemplateEngine emailTemplateEngine() {
		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		// Resolver for HTML emails (except the editable one)
		templateEngine.addTemplateResolver(htmlTemplateResolver());
		return templateEngine;
	}

	/**
	 * detail of email template
	 * 
	 * @return
	 */
	@Bean
	public SpringResourceTemplateResolver htmlTemplateResolver() {
		SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
		emailTemplateResolver.setPrefix("classpath:/templates/");
		emailTemplateResolver.setSuffix(".html");
		emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
		emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());

		return emailTemplateResolver;
	}

	/**
	 * validation bean.
	 * 
	 * @return
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		MethodValidationPostProcessor mvProcessor = new MethodValidationPostProcessor();
		mvProcessor.setValidator(validator());
		return mvProcessor;
	}

	/**
	 * we use Hibernate validator.
	 * 
	 * @return
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setProviderClass(HibernateValidator.class);
		validator.afterPropertiesSet();
		return validator;
	}

	@Bean
	public RestTemplate restTemplate() throws Exception {
		final SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
		final SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslcontext,
				new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", csf).build();

		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		connectionManager.setMaxTotal(httpMaxTotal);
		connectionManager.setDefaultMaxPerRoute(httpMaxPerRoute);
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setSocketTimeout(10000)
				.build();

		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf)
				.setConnectionManager(connectionManager).setKeepAliveStrategy(connectionKeepAliveStrategy())
				.setDefaultRequestConfig(requestConfig).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);

	}

	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, context) -> {
			HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					try {
						return Long.parseLong(value) * 1000;
					} catch (NumberFormatException exception) {
						exception.printStackTrace();
					}
				}
			}
			// If there is no Keep-Alive header. Keep the connection for 30 seconds
			return 30 * 1000;
		};
	}

	/**
	 * add all intercepts.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/**");

	}

	@Bean(name = "appEmailService")
	public AppEmailService appEmailService() {
		return AppEmailService.INSTANCE;
	}

	@Bean(name = "queryLoader")
	public QueryLoader queryLoader() {
		return QueryLoader.INSTANCE;
	}
}
