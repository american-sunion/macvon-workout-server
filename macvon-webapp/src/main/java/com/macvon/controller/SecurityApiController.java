package com.macvon.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.macvon.domain.auth.AuthUserProfile;
import com.macvon.form.WebResponse;
import com.macvon.form.auth.UpdateGroupForm;
import com.macvon.form.auth.UpdatePwdForm;
import com.macvon.form.auth.UpdateUserAttributesForm;
import com.macvon.service.auth.AuthService;

@Controller
@RequestMapping(value = "/api")
public class SecurityApiController {
	@Autowired
	private AuthService authService;
	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> updateUserPassword(HttpServletRequest request,
			@Valid @RequestBody UpdatePwdForm updatePwdForm) throws ServletException {
		String accessToken = request.getHeader("accessToken");
		return authService.updateUserPassword(updatePwdForm, accessToken);
	}
	@RequestMapping(value = "/updateUserGroup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> updateUserGroup(HttpServletRequest request,
			@Valid @RequestBody UpdateGroupForm updateGroupForm) throws ServletException {
		return authService.updateUserGroup(updateGroupForm);

	}
	@RequestMapping(value = "/updateUserAttributes", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> updateUserAttributes(HttpServletRequest request,
			@Valid @RequestBody UpdateUserAttributesForm updateUserAttributesForm) throws ServletException {
	
		return authService.updateUserAttributes(updateUserAttributesForm);
	}
	@RequestMapping(value = "/refreshtoken", method = RequestMethod.POST)
	public @ResponseBody AuthUserProfile refreshtoken(HttpServletRequest request) throws ServletException {
		String refreshToken = request.getHeader("refreshToken");
		return authService.refreshtoken(refreshToken);
	}
//	@RequestMapping(value = "/getUser", method = RequestMethod.POST)
//	public @ResponseBody WebResponse getUser(HttpServletRequest request,
//			@Valid @RequestBody GetUserForm getUserForm) throws ServletException {
//
//		return authService.getUser(getUserForm);
//	}
}
