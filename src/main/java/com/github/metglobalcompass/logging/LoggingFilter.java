package com.github.metglobalcompass.logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

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

		//we should use wrapper for request too, because input stream closes after request
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(requestWrapper, responseWrapper);

		String mainUrl = requestWrapper.getRequestURL().toString(); //path included
		String nextPath = requestWrapper.getQueryString();
		nextPath = (nextPath == null) ? "" : "?" + nextPath;
		String requestBody = new String(requestWrapper.getContentAsByteArray()).replaceAll("\\s{2,}|\\n", "");
		System.out.println("URL: " + mainUrl + nextPath + " with request " + requestBody);

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
