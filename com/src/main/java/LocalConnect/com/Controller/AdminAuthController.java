package LocalConnect.com.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.UserService;

/**
 * Admin registration endpoint.
 *
 * This is intentionally NOT wired to any HTML page/form. It exists only as a
 * JSON API meant to be called from a tool like Postman by someone who already
 * knows the shared admin secret key (see admin.registration.secret-key in
 * application.properties). There is no link in the UI that points here.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    // Configure this in application.properties (or as an env var) and share it
    // out-of-band with whoever is allowed to create admin accounts.
    @Value("${admin.registration.secret-key}")
    private String adminSecretKey;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterRequest request) {

        if (request.getSecretKey() == null || request.getSecretKey().isBlank()
                || !request.getSecretKey().equals(adminSecretKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or missing secret key");
        }

        if (request.getName() == null || request.getName().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("name, email and password are required");
        }

        try {
            User admin = userService.registerAdmin(
                    request.getName(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getLocality());

            // Never send the password hash back in the response.
            admin.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(admin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Request body:
     * {
     *   "name": "Admin Name",
     *   "email": "admin@example.com",
     *   "password": "somePassword",
     *   "locality": "Optional Locality",
     *   "secretKey": "the-secret-key-configured-on-the-server"
     * }
     */
    public static class AdminRegisterRequest {
        private String name;
        private String email;
        private String password;
        private String locality;
        private String secretKey;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
