package com.github.metglobalcompass.logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

		filterChain.doFilter(request, responseWrapper);

		String method = requestWrapper.getMethod();
		String requestBody = IOUtils.toString(requestWrapper.getInputStream(), "UTF-8");
		System.out.println("Request method: " + method + " - body: " + requestBody);

		HttpStatus status = HttpStatus.valueOf(responseWrapper.getStatusCode());
		HttpHeaders headers = new HttpHeaders();
		responseWrapper.getHeaderNames().forEach(
				header -> headers.add(header, responseWrapper.getHeader(header))
		);

		String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), "UTF-8");
		System.out.println("Status code: " + status + " with response: " + responseBody);
		responseWrapper.copyBodyToResponse();
	}
}
