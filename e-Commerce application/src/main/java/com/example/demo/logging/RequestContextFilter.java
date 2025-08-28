package com.example.demo.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestContextFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    MDC.put("path", req.getRequestURI());
    MDC.put("method", req.getMethod());
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) MDC.put("user", auth.getName());
    MDC.put("traceId", UUID.randomUUID().toString());
    try { chain.doFilter(req, res); }
    finally { MDC.clear(); }
  }
}
