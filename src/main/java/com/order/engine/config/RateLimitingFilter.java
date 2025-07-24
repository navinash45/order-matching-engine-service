package com.order.engine.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

	private final Map<String, Deque<Long>> requestTimestampsPerIp = new ConcurrentHashMap<>();

	private static final int MAX_REQUESTS_PER_MINUTE = 5;
	private static final long WINDOW_SIZE_SECONDS = 60;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		String clientIpAddress = httpServletRequest.getRemoteAddr();
		long now = Instant.now().getEpochSecond();

		Deque<Long> timestamps = requestTimestampsPerIp.computeIfAbsent(clientIpAddress, ip -> new LinkedList<>());
		synchronized (timestamps) {
			while (!timestamps.isEmpty() && (now - timestamps.peekFirst() >= WINDOW_SIZE_SECONDS)) {
				timestamps.pollFirst();
			}

			if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
				httpServletResponse.setStatus(429);
				httpServletResponse.getWriter().write("Too many requests. Please try again later.");
				return;
			}

			timestamps.addLast(now);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
