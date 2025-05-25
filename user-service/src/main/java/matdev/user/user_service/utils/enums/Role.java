package matdev.user.user_service.utils.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.RequiredArgsConstructor;
import matdev.user.user_service.utils.constants.RoleConstant;

@RequiredArgsConstructor
public enum Role {
   
    ROLE_ADMIN(RoleConstant.ADMIN),
    ROLE_MESERO(RoleConstant.EMPLEADO),


    private final String roleName;

    public SimpleGrantedAuthority getAuthority(){
        return new SimpleGrantedAuthority(roleName);
    }



}
