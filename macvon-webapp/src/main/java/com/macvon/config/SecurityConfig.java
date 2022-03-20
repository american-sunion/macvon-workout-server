package com.macvon.config;

import static com.nimbusds.jose.JWSAlgorithm.RS256;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.macvon.handler.auth.AuthenticationFailureHandler;
import com.macvon.handler.auth.AuthenticationSuccessHandler;
import com.macvon.handler.auth.CustomLogoutSuccessHandler;
import com.macvon.handler.auth.LoginAuthServiceProvider;
import com.macvon.service.auth.SecurityApiService;
import com.macvon.utils.AppUtils;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Autowired
    private AuthenticationSuccessHandler successHandler;
    @Autowired
    private SecurityApiService securityApiService;
	@Autowired
	private Environment env;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/auth/*", "/device/*").permitAll()
				.and().authorizeRequests().antMatchers(HttpMethod.POST, "/api/*").permitAll()
                .and()
                .requestCache()
                .requestCache(new NullRequestCache())
                .and()
                .formLogin()
                .permitAll()
                .loginProcessingUrl("/auth/login")
                .successHandler(successHandler)
                .failureHandler(new AuthenticationFailureHandler())
                .and()
                .logout()
                .permitAll()
                .invalidateHttpSession(false) //will handle in logoutSuccessHandler
                .clearAuthentication(false)
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "POST"))
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
				.and()
	            .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	String loginUrl= AppUtils.getConfigVal("auth.url.base", env) +"/auth/login";
        auth.authenticationProvider(new LoginAuthServiceProvider(securityApiService, loginUrl, configurableJWTProcessor()));
    } 
	@SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever = new DefaultResourceRetriever(30000, 30000);
        String cognitoJWKS= AppUtils.getConfigVal("auth.cognito.jwks", env);
		JWKSource keySource = new RemoteJWKSet(new URL(cognitoJWKS + "/.well-known/jwks.json"), resourceRetriever);
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWSKeySelector keySelector = new JWSVerificationKeySelector(RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }
}
