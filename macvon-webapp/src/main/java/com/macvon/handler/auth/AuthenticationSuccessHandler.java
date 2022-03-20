package com.macvon.handler.auth;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.macvon.domain.auth.AuthUser;
import com.macvon.utils.AppUtils;
import com.macvon.utils.GlobalConstants;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final static Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);
	private RequestCache requestCache = new HttpSessionRequestCache();

	@SuppressWarnings({ "deprecation"})
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		final SavedRequest savedRequest = requestCache.getRequest(request, response);
		final Object user = authentication.getPrincipal();

		if (user instanceof AuthUser) {
			try {
				SecurityContext sc = SecurityContextHolder.getContext();
				sc.setAuthentication(authentication);
				String info = new JSONObject().put("USER_NAME", ((AuthUser) user).getUsername())
						.put("IP_ADDRESS", AppUtils.getIpAddr(request)).put("DATE", new Date().toGMTString())
						.put("X-FORWARDED-FOR", request.getHeader("X-Forwarded-For"))
						.put("USER-AGENT", request.getHeader("User-Agent")).toString();
				logger.info("USER LOGIN: -{}", info);
				addResHeader(response, GlobalConstants.WEB.ACCESS_TOKEN, ((AuthUser) user).getAuthUserProfile().getAccessToken());
				addResHeader(response, GlobalConstants.WEB.REFRESH_TOKEN, ((AuthUser) user).getAuthUserProfile().getRefreshToken());
				addResHeader(response, GlobalConstants.WEB.ID_TOKEN, ((AuthUser) user).getAuthUserProfile().getIdToken());
				Set<String> roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toSet());
				StringBuilder sb = new StringBuilder();
				roles.forEach(role -> {
					sb.append(role).append(",");
				});
				String roleSTr = sb.toString();
				int lenth = roleSTr.length();
				if (roleSTr.contains(",")) {
					roleSTr = roleSTr.substring(0, lenth - 1);
				}
				addResHeader(response, GlobalConstants.WEB.ENTITLE, roleSTr);
				addResHeader(response, GlobalConstants.WEB.DISPLAY_NMAE, ((AuthUser) user).getUserInfo().getDisplayName());
			} catch (JSONException ex) {
				logger.info("ERROR to write user info in JSON format, fallback");
				logger.info("USER NAME: \t" + ((AuthUser) user).getUsername());
				logger.info("IP ADDRESS: \t" + AppUtils.getIpAddr(request));
				logger.info("DATE: \t" + new Date().toGMTString());
				logger.info("X-FORWARDED-FOR: \t" + request.getHeader("X-Forwarded-For"));
				logger.info("USER-AGENT:\t" + request.getHeader("User-Agent"));
			}
			logger.debug("\n----------- USER LOGIN ENDS-----------\n");
		}

		if (savedRequest == null) {
			clearAuthenticationAttributes(request);
			return;
		}
		String targetUrlParam = getTargetUrlParameter();
		if (isAlwaysUseDefaultTargetUrl()
				|| (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
			requestCache.removeRequest(request, response);
			clearAuthenticationAttributes(request);
			return;
		}
		clearAuthenticationAttributes(request);
	}

	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}
	public void addResHeader(HttpServletResponse res, String key, String value) {
		res.addHeader(key, value);
		res.addHeader("access-control-expose-headers", key);
	}

}