package com.macvon.service.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macvon.dao.user.UserDAO;
import com.macvon.domain.auth.Login;
import com.macvon.domain.user.User;
import com.macvon.utils.AppUtils;
import com.macvon.utils.DateUtils;
import com.macvon.utils.GlobalConstants;
/**
 * service to connect with security API gateway
 * 
 * @author brianwu
 *
 */
@Service
@Transactional
public class SecurityApiService {

	private final static Logger logger = LoggerFactory.getLogger(SecurityApiService.class);
	private RestTemplate restTemplate;
	@Autowired
	private Environment env;
	@Autowired
	private UserDAO userDAO;
	
	public SecurityApiService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * call api gateway
	 * 
	 * @param <T>
	 * @param paramMaps
	 * @param url
	 * @param clazz
	 * @return
	 */
	public <T> T callApiGateWay(String json, String url, Class<T> clazz) {
		logger.info("callApiGateWay with class: {}", clazz.getName());
		try {
			String apikey= AppUtils.getConfigVal("auth.apikey", env);
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/json");
			headers.add("x-api-key", apikey);
			final HttpEntity<String> body = new HttpEntity<>(json, headers);

			final ResponseEntity<String> resp = this.restTemplate.postForEntity(new URI(url), body, String.class);
			if (resp.getStatusCodeValue() == 200) {
				String resbody = resp.getBody();
				return new ObjectMapper().readValue(resbody, clazz);
			} else {
				throw new RuntimeException("error occurs for restful call: " + url);
			}

		} catch (Exception ex) {
			logger.error("Macvon AUTH FAILURE, ", ex);
			throw new RuntimeException(ex);
		}
	}
	public <T> T callApiGateWay(Map<String, String> paramMaps, String url, Class<T> clazz) {
		logger.info("start callApiGateWay with class: {}", clazz.getName());
		try {
			String apikey= AppUtils.getConfigVal("auth.apikey", env);
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/json");
			headers.add("x-api-key", apikey);

			JSONObject json = new JSONObject();
			paramMaps.forEach((k, v) -> {
				try {
					json.put(k, v);
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			});
			final HttpEntity<String> body = new HttpEntity<>(json.toString(), headers);

			final ResponseEntity<String> resp = this.restTemplate.postForEntity(new URI(url), body, String.class);
			if (resp.getStatusCodeValue() == 200 && !StringUtils.contains(resp.getBody(), "errorType")) {
				logger.info("complete callApiGateWay with class: {}", clazz.getName());
				String resbody = resp.getBody();
				return new ObjectMapper().readValue(resbody, clazz);
			} else {
				throw new RuntimeException("error occurs for restful call: " + url + " error:"+ resp.getBody());
			}

		} catch (Exception ex) {
			logger.error("Macvon AUTH FAILURE, ", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * call api gateway
	 * 
	 * @param <T>
	 * @param paramMaps
	 * @param url
	 * @return
	 */

	public void callApiGateWay(Map<String, String> paramMaps, String url) {
		try {
			String apikey= AppUtils.getConfigVal("auth.apikey", env);
			final HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			headers.add("Accept", "application/json");
			headers.add("x-api-key", apikey);

			JSONObject json = new JSONObject();
			paramMaps.forEach((k, v) -> {
				try {
					json.put(k, v);
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}
			});
			final HttpEntity<String> body = new HttpEntity<>(json.toString(), headers);
			this.restTemplate.postForEntity(new URI(url), body, String.class);
		} catch (Exception ex) {
			logger.error("Macvon AUTH FAILURE, ", ex);
		}
	}

	/**
	 * call api gateway
	 * 
	 * @param <T>
	 * @param paramMaps
	 * @param url
	 * @return
	 * @throws URISyntaxException
	 * @throws Exception
	 */

	public void callApiGateWayWithExp(Map<String, String> paramMaps, String url) throws Exception {
		final HttpHeaders headers = new HttpHeaders();
		String apikey= AppUtils.getConfigVal("auth.apikey", env);
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		headers.add("x-api-key", apikey);

		JSONObject json = new JSONObject();
		paramMaps.forEach((k, v) -> {
			try {
				json.put(k, v);
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		});
		final HttpEntity<String> body = new HttpEntity<>(json.toString(), headers);
		this.restTemplate.postForEntity(new URI(url), body, String.class);
	}

	public HttpHeaders getApiPostHeader(String apikey) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		headers.add("x-api-key", apikey);
		return headers;
	}
	
	public Login saveLoginSuccess(String username) {
		User user = (User) userDAO.findEntityObj("from User u where u.username= ?1 or  u.email=?1", username);
		Login login =  Login.createNewLogin(user.getId(), GlobalConstants.STATUS.ACTIVED.name());
		userDAO.saveWithFlush(login);
		return login;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void handleLoginFailure(String username) {
		User user = (User) userDAO.findEntityObj("from User u where u.username= ?1 or  u.email=?1", username);
		if(user!=null) {
			long userId = user.getId();
			Login login = (Login) userDAO.findLatestActiveLogin(userId);
			if (login != null && login.getLogoutTime() != null) {
				login.addRetry();
				login.setLogoutTime(DateUtils.getCurrentDate());
				userDAO.saveWithFlush(login);
				Login loginAttp = Login.createNewLogin(userId, GlobalConstants.STATUS.FAILED.name());
				userDAO.saveWithFlush(loginAttp);
			} else if (login != null) {
				login.addRetry();
				if (login.getRetry() > 8) {
					login.setStatus(GlobalConstants.STATUS.LOCKED.name());
					login.setLastUpdateDate(DateUtils.getCurrentDateTS());
				}
				userDAO.saveWithFlush(login);
			} else {
				Login loginAttp = Login.createNewLogin(userId, GlobalConstants.STATUS.FAILED.name());
				userDAO.saveWithFlush(loginAttp);
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void checkIfAccountLocked(String username) {
		User user = (User) userDAO.findEntityObj("from User u where u.username= ?1 or  u.email=?1", username);
		if (user == null) {
			logger.error("User Account  {} not found", username);
			throw new UsernameNotFoundException("User " + username + " Account is not found.");
		} else {
			long userId = user.getId();
			Login login = (Login) userDAO.findLatestActiveLogin(userId);
			if (login!=null && login.getStatus().equalsIgnoreCase(GlobalConstants.STATUS.LOCKED.name()) && !DateUtils.isUnlocked(login.getLastUpdateDate())) {
				login.addRetry();
				login.setLastUpdateDate(DateUtils.getCurrentDateTS());
				userDAO.saveWithFlush(login);
				throw new UsernameNotFoundException(GlobalConstants.EXCEPTION.TOO_MANY_LOGIN_ATTEMPTS);
			} 
		}
			

	}
}
