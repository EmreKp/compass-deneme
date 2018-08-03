package com.github.metglobalcompass.logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);


		filterChain.doFilter(requestWrapper, responseWrapper);

		String method = requestWrapper.getMethod();
		String url = requestWrapper.getRequestURL().toString();
		Map<String, String[]> reqMap = requestWrapper.getParameterMap();
		String reqJson = new Gson().toJson(reqMap);

		String requestBody = IOUtils.toString(requestWrapper.getInputStream(), "UTF-8");
		System.out.println("Request method: " + method + " - params: " + url + " " + reqJson + " - body: " + requestBody);

		int status = responseWrapper.getStatusCode();
		HttpHeaders headers = new HttpHeaders();
		responseWrapper.getHeaderNames().forEach(
				header -> headers.add(header, responseWrapper.getHeader(header))
		);
		String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), "UTF-8");
		System.out.println("Status code: " + status + " with response: " + responseBody);
		responseWrapper.copyBodyToResponse();
	}
}
