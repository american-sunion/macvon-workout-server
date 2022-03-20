package com.macvon.handler.auth;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.macvon.utils.AppUtils;

/**
 *
 * @author brian wu
 */
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public AuthenticationFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        try {
            super.onAuthenticationFailure(request, response, exception);
            String userName = request.getParameter("username");
            @SuppressWarnings("deprecation")
			String info = new JSONObject().put("USER_NAME", userName)
                    .put("IP_ADDRESS", AppUtils.getIpAddr(request))
                    .put("DATE", new Date().toGMTString())
                    .put("X-FORWARDED-FOR", request.getHeader("X-Forwarded-For"))
                    .put("USER-AGENT", request.getHeader("User-Agent")).toString();
            logger.error("AUTH_STATUS: FAILED : {}", info);
        } catch (JSONException ex) {
            logger.error("AUTH FAILURE, ", ex);
        }
    }

}
