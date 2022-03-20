package com.macvon.interceptors;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class ProxyHttpInterceptor implements ClientHttpRequestInterceptor, HttpRequestInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, Set<String>> cookies = Collections.synchronizedMap(new HashMap<>());
    private String domain = "";
    @Override
    public void process(org.apache.http.HttpRequest request, HttpContext context) throws HttpException, IOException {
    }

	@Override
	public ClientHttpResponse intercept(org.springframework.http.HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		String uuid = UUID.randomUUID().toString();
		logger.info("[PROXY REQ-ID] " + uuid + " [URL] " + request.getURI().getPath() + " [METHOD] "
				+ request.getMethod().name());
		final ClientHttpResponse response = execution.execute(request, body);
		final String userName = request.getHeaders().getFirst("X-MV-User");

		setHeaders(request, userName);
		logger.info("[PROXY REQ-ID] " + uuid + " [STATUS CODE] " + response.getStatusCode().value());
		return response;
	}
    private void setHeaders(HttpRequest request, String userName) {
        if (userName != null) {
            final Set<String> _cookies = this.cookies.get(userName);
            if (_cookies != null && _cookies.size() > 0) {
                final String headerCookies = _cookies.stream().collect(Collectors.joining(";"));
                request.getHeaders().add("Cookie", headerCookies);
            }
        }
        request.getHeaders().remove("X-MV-User");
        request.getHeaders().set("Accept", "*/*");
        request.getHeaders().set("Accept-Language", "en-US,en;q=0.9");
        request.getHeaders().set("Accept-Encoding", "gzip, deflate, br");
        request.getHeaders().add("Host", domain);
        request.getHeaders().add("Referer", request.getURI().getScheme() + "://" + domain + "/forensics");
    }
}
