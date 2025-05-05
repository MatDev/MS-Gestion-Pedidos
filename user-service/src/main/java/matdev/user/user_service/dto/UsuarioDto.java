package matdev.user.user_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import matdev.user.user_service.utils.enums.Role;;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDto {
    private Long id;
    private String email;
    private Role role;
    private String username;

}
