package com.macvon.filter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.macvon.utils.AppUtils;
/**
 * handle CROS origin request
 * @author Brian
 *
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpCrosRequestFilter   implements Filter {
	@Autowired
	private Environment env;
	
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
		String allowsOrigins= AppUtils.getConfigVal("web.allow.origin", env);
		// Get client's origin
        String clientOrigin = request.getHeader("origin");
        if(clientOrigin!=null && allowsOrigins.indexOf(clientOrigin)>=0) {
        	response.setHeader("Access-Control-Allow-Origin", clientOrigin);
            response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, CONNECT");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,authorization,accessToken,refreshToken,idtoken,subject");
            response.setHeader("Access-Control-Max-Age", "7200");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else {
        	String defaultOrigin= AppUtils.getConfigVal("web.allow.defaultOrigin", env);
        	response.setHeader("Access-Control-Allow-Origin", defaultOrigin);
            response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE,CONNECT");
            response.setHeader("Access-Control-Allow-Headers", "x-requested-with,authorization,accessToken,refreshToken,idtoken,subject");
            response.setHeader("Access-Control-Max-Age", "7200");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(req, res);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            //System.out.println("Pre-flight");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,CONNECT");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type," +
                    "access-control-request-headers,access-control-request-method,accept,origin,authentication, x-requested-with,accessToken,refreshToken,idtoken,subject");
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}