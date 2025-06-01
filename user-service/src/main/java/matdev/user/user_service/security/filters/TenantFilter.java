package matdev.user.user_service.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import matdev.user.user_service.context.TenantContext;
import matdev.user.user_service.utils.constants.AuthConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class TenantFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tenantId= request.getHeader(AuthConstant.TENANT_HEADER);
        if (tenantId == null || tenantId.isEmpty()) {
            LOGGER.error("Tenant ID is missing in the request header");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tenant ID is required");
            return;
        }
        // Set the tenant ID in the request attribute for further processing
        TenantContext.setTenantId(tenantId);
        LOGGER.info("Tenant ID set in context: {}", tenantId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear the tenant ID from the context after processing the request
            TenantContext.clear();
            LOGGER.info("Tenant ID cleared from context");
        }

    }
}
