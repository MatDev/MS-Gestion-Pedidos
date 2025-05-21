package matdev.user.user_service.utils.constants;

public class AuthConstant {
   
        // Constructor privado para evitar la instanciación
        private AuthConstant() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
        public static final String AUTHORIZATION_HEADER = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String ROLE_CLAIM = "role";
    
        public static final String CONTENT_TYPE_JSON = "application/json";
    


}
