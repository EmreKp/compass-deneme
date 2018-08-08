package com.metglobal.compass.logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.metglobal.compass.logging.model.LogModel;
import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
public class LoggingFilter extends OncePerRequestFilter {

	private final LogHandler handler;

	@Autowired
	public LoggingFilter(LogHandler handler) {
		this.handler = handler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		LogModel model = new LogModel();

		//we should use wrapper for request too, because input stream closes after request
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(requestWrapper, responseWrapper);

		String mainUrl = requestWrapper.getRequestURL().toString(); //path included
		String nextPath = requestWrapper.getQueryString();
		nextPath = (nextPath == null) ? "" : "?" + nextPath;
		model.setUrl(mainUrl + nextPath);

		String requestBody = new String(requestWrapper.getContentAsByteArray()).replaceAll("\\s{2,}|\\n", "");
		model.setRequestBody(requestBody);

		int status = responseWrapper.getStatusCode();
		model.setStatus(status);

		String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), "UTF-8");
		model.setResponseBody(responseBody);

		responseWrapper.copyBodyToResponse();

		handler.handle(model);
	}
}
