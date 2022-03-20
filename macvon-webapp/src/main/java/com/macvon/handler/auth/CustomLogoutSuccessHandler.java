package com.macvon.handler.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.macvon.service.auth.AuthService;

@Component
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
	private final static Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);
	@Autowired
	private AuthService authService;
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String accessToken = request.getHeader("accessToken");
		String userName = request.getParameter("username");
		logger.info("User success logout");
		authService.logout(userName, accessToken);
		response.setStatus(HttpStatus.OK.value());
		response.getWriter().flush();
	}
}