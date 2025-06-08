package com.matdev.admin_service.security;

import com.matdev.admin_service.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that extracts the tenant identifier from the HTTP header
 * and stores it in the {@link TenantContext}.
 */
@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilter.class);
    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isEmpty()) {
            LOGGER.error("Tenant ID is missing in the request header");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant ID is required");
            return;
        }
        TenantContext.setTenantId(tenantId);
        LOGGER.info("Tenant ID set in context: {}", tenantId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            LOGGER.info("Tenant ID cleared from context");
        }
    }
}
