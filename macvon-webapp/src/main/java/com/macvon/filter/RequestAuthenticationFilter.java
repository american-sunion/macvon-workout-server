package com.macvon.filter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.common.collect.Maps;
import com.macvon.domain.auth.AuthUser;
import com.macvon.domain.auth.AuthUserProfile;
import com.macvon.service.auth.SecurityApiService;
import com.macvon.utils.AppUtils;
import com.macvon.utils.TokenUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

/**
 * authentication check for all api request via auth micro-service
 * 
 * @author brian
 *
 */
@Component
public class RequestAuthenticationFilter  extends OncePerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SecurityApiService securityApiService;
    @SuppressWarnings("rawtypes")
	@Autowired
    private ConfigurableJWTProcessor configurableJWTProcessor;
	@Autowired
	private Environment env;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.startsWith("/api/")) {
			if(verifyRequestAuth(request, response)) {
				filterChain.doFilter(request, response);
			} else {
				setUnauthorizedResponse(request, response);
				return;
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	public void setUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response) {

		try {
			String userName = request.getParameter("username");
			@SuppressWarnings("deprecation")
			String info = new JSONObject().put("USER_NAME", userName).put("IP_ADDRESS", AppUtils.getIpAddr(request))
					.put("DATE", new Date().toGMTString()).put("X-FORWARDED-FOR", request.getHeader("X-Forwarded-For"))
					.put("UID", request.getHeader("uid"))
					.put("USER-AGENT", request.getHeader("User-Agent")).toString();
			logger.error("AUTH_STATUS: FAILED; " + info);
			HttpSession session = request.getSession(false);
			if (session != null) {
				session.invalidate();
			}
			response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
		} catch (JSONException | IOException ex) {
			logger.error("CT AUTH FAILURE, ", ex);
		}

	}
	public boolean verifyRequestAuth(HttpServletRequest request, HttpServletResponse response) {
		String idToken = request.getHeader("idToken");
		String accessToken = request.getHeader("accessToken");
		String refreshToken = request.getHeader("refreshToken");
		if(StringUtils.isEmpty(idToken) || StringUtils.isEmpty(accessToken) || StringUtils.isEmpty(refreshToken)) {
			logger.error("authroization token is missing");
			return false;
		}
		try {
			JWTClaimsSet idTokenClaimsSet = TokenUtils.getJWTClaimsSet(idToken, configurableJWTProcessor);
			JWTClaimsSet accessTokenClaimsSet = TokenUtils.getJWTClaimsSet(accessToken, configurableJWTProcessor);
			boolean isValid = TokenUtils.isValid(idTokenClaimsSet, accessTokenClaimsSet);
			if(isValid) return true;
//		    if(isValid) {
//		    	long refreshThresholdInsec= AppUtils.getConfigLongVal("auth.refreshThresholdInsec", env);
//		    	boolean isValidForThreshold = TokenUtils.isValidForThreshold(idTokenClaimsSet, refreshThresholdInsec*1000);
//		    	if(!isValidForThreshold) {
//		    		String refreshUrl= AppUtils.getApiUrl("refreshtoken", env);
//					Map<String, String> paramMaps = Maps.newHashMap();
//					paramMaps.put("refreshToken", refreshToken);
//		    		AuthUserProfile ctAuthUserProfile = securityApiService.callApiGateWay(paramMaps, refreshUrl, AuthUserProfile.class);
//					logger.info("refreshed session from ctauth service");
//					if(ctAuthUserProfile!=null && ctAuthUserProfile.getStatusCode()== 200) {
//						return true;
//					} else {
//						return false;
//					}
//		    	} else {
//		    		return true;
//		    	}
//			}

		} catch (Exception ex) {
			logger.error("User AUTH FAILURE, ", ex);
		}
		setUnauthorizedResponse(request, response);
		return false;

	}
	public boolean verifyRequestAuth(HttpServletRequest request, HttpServletResponse response, AuthUser authUser) {
		try {
			AuthUserProfile authUserProfile = authUser.getAuthUserProfile();
			JWTClaimsSet idTokenClaimsSet = TokenUtils.getJWTClaimsSet(authUserProfile.getIdToken(), configurableJWTProcessor);
			JWTClaimsSet accessTokenClaimsSet = TokenUtils.getJWTClaimsSet(authUserProfile.getIdToken(), configurableJWTProcessor);
			boolean isValid = TokenUtils.isValid(idTokenClaimsSet, accessTokenClaimsSet);
		    if(isValid) {
		    	long refreshThresholdInsec= AppUtils.getConfigLongVal("auth.refreshThresholdInsec", env);
		    	boolean isValidForThreshold = TokenUtils.isValidForThreshold(idTokenClaimsSet, refreshThresholdInsec*1000);
		    	if(!isValidForThreshold) {
		    		String refreshUrl= AppUtils.getConfigVal("auth.url.base", env) +"/refreshtoken";
					Map<String, String> paramMaps = Maps.newHashMap();
					paramMaps.put("refreshToken", authUserProfile.getRefreshToken());
		    		AuthUserProfile ctAuthUserProfile = securityApiService.callApiGateWay(paramMaps, refreshUrl, AuthUserProfile.class);
					logger.info("refreshed session from ctauth service");
					if(ctAuthUserProfile!=null && ctAuthUserProfile.getStatusCode()== 200) {
						authUser.getAuthUserProfile().setAccessToken(ctAuthUserProfile.getAccessToken());
						authUser.getAuthUserProfile().setIdToken(ctAuthUserProfile.getIdToken());
						authUser.getAuthUserProfile().setExpiresIn(ctAuthUserProfile.getExpiresIn());
						return true;
					} else {
						return false;
					}
		    	} else {
		    		return true;
		    	}

			}

		} catch (Exception ex) {
			logger.error("User AUTH FAILURE, ", ex);
		}
		setUnauthorizedResponse(request, response);
		return false;

	}
}
