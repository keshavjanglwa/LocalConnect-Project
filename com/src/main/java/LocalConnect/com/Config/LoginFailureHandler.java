package LocalConnect.com.Config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Routes a failed login to a different query param depending on why it failed,
 * so the login page can show a specific message for a blocked account instead
 * of the generic "invalid username or password" message.
 *
 * DisabledException is thrown by Spring Security's account-status check
 * (see UserService#loadUserByUsername, which sets .disabled(!user.getIsEnable())),
 * and it is thrown BEFORE the password is even checked - so this reliably
 * fires whenever isEnable is false, regardless of whether the password was
 * correct.
 */
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        if (exception instanceof DisabledException) {
            getRedirectStrategy().sendRedirect(request, response, "/login?blocked=true");
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, "/login?error=true");
    }
}
