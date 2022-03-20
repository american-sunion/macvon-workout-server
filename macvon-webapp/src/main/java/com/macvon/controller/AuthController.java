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

import com.macvon.form.WebResponse;
import com.macvon.form.auth.ConfirmForgotPassword;
import com.macvon.form.auth.PwdResetReqForm;
import com.macvon.form.auth.SignupForm;
import com.macvon.form.auth.SignupMemberForm;
import com.macvon.form.auth.SubscribeForm;
import com.macvon.form.auth.UpdateTmpPwdForm;
import com.macvon.service.auth.AuthService;

@Controller
@RequestMapping(value = "/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> resetPasswordRequest(HttpServletRequest request,
			@Valid @RequestBody PwdResetReqForm pwdResetReqForm) throws ServletException {
		return authService.resetPasssword(pwdResetReqForm);
	}
	@RequestMapping(value = "/confirmForgotPassword", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> confirmForgotPassword(HttpServletRequest request,
			@Valid @RequestBody ConfirmForgotPassword confirmForgotPassword) throws ServletException {
		return authService.confirmForgotPassword(confirmForgotPassword);
	}	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<WebResponse> signup(HttpServletRequest request,
			@Valid @RequestBody SignupForm signupForm) throws ServletException {
		return authService.signup(signupForm);
	}
	@RequestMapping(value = "/signupPrimary", method = RequestMethod.POST)
	public @ResponseBody WebResponse signupPrimary(HttpServletRequest request, @Valid @RequestBody SignupForm signupForm) {
		return authService.signupPrimary(signupForm);
	}
	@RequestMapping(value = "/signupMember", method = RequestMethod.POST)
	public @ResponseBody WebResponse signupMember(HttpServletRequest request,
			@Valid @RequestBody SignupMemberForm signupForm) throws ServletException {
		return authService.signupMember(signupForm);
	}
	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public @ResponseBody WebResponse subscribe(HttpServletRequest request,
			@Valid @RequestBody SubscribeForm subscribeForm) throws ServletException {
		return authService.subscribe(subscribeForm);
	}
	@RequestMapping(value = "/updateTmpPwd", method = RequestMethod.POST)
	public @ResponseBody WebResponse updateTmpPwd(HttpServletRequest request,
			@Valid @RequestBody UpdateTmpPwdForm updateTmpPwdForm) throws ServletException {
		return authService.updateTmpPwd(updateTmpPwdForm);
	}
}
