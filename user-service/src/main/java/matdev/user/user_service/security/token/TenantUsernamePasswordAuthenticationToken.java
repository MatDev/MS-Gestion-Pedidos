package matdev.user.user_service.security.token;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class TenantUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String tenantId;
    public TenantUsernamePasswordAuthenticationToken(Object principal, Object credentials, String tenantId) {
        super(principal, credentials);
        this.tenantId = tenantId;
    }
    public TenantUsernamePasswordAuthenticationToken(Object principal, Object credentials, String tenantId, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.tenantId = tenantId;
    }
    public String getTenantId() {
        return tenantId;
    }

}
