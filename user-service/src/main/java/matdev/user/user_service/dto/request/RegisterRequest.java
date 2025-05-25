package matdev.user.user_service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import matdev.user.user_service.dto.UsuarioDto;
import matdev.user.user_service.utils.enums.Role;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends UsuarioDto {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private Role role;
    private String tenantId;
}
