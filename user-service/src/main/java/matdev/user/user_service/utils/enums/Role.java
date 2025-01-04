package matdev.user.user_service.utils.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.utils.constants.RoleConstant;

@RequiredArgsConstructor
public enum Role {
   
    ROLE_ADMIN(RoleConstant.ADMIN),
    ROLE_USER(RoleConstant.USER),
    ROLE_RESTAURANT(RoleConstant.RESTAURANT),
    ROLE_DELIVERY(RoleConstant.DELIVERY);

    private final String roleName;

    public SimpleGrantedAuthority getAuthority(){
        return new SimpleGrantedAuthority(roleName);
    }



}
