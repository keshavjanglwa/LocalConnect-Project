package LocalConnect.com.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Turns exceptions that bubble up out of @Controller methods into a friendly
 * error page instead of Spring Boot's generic Whitelabel Error Page.
 *
 * This only handles exceptions that escape a controller method. Controllers
 * that already catch their own exceptions (e.g. AuthController.register,
 * AdminAuthController) are unaffected, since nothing escapes them.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Thrown throughout the service layer for "that record doesn't exist" /
    // "that input isn't valid" cases (see UserService, ActivityService,
    // BusinessService, GroupService). The messages are already
    // user-appropriate (no internals leaked), so it's safe to show them as-is.
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request on {}: {}", request.getRequestURI(), ex.getMessage());
        return errorView(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Thrown when someone tries to do something they're not allowed to
    // (e.g. deleting someone else's post, acting on a group they don't own).
    @ExceptionHandler(SecurityException.class)
    public ModelAndView handleSecurity(SecurityException ex, HttpServletRequest request) {
        log.warn("Forbidden action on {}: {}", request.getRequestURI(), ex.getMessage());
        return errorView(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Thrown for valid-but-not-allowed-right-now actions (e.g. replying to an
    // activity post that's already closed).
    @ExceptionHandler(IllegalStateException.class)
    public ModelAndView handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Conflicting request on {}: {}", request.getRequestURI(), ex.getMessage());
        return errorView(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Thrown by UserService when the logged-in principal no longer matches a
    // row in the database (e.g. the account was deleted mid-session).
    @ExceptionHandler(UsernameNotFoundException.class)
    public ModelAndView handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        log.warn("User lookup failed on {}: {}", request.getRequestURI(), ex.getMessage());
        return errorView(HttpStatus.NOT_FOUND, "We couldn't find your account. Please log in again.");
    }

    // Last-resort catch-all so nothing unhandled ever reaches the default
    // Whitelabel Error Page.
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return errorView(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.");
    }

    private ModelAndView errorView(HttpStatus status, String message) {
        ModelAndView mav = new ModelAndView("error-generic");
        mav.addObject("errorMessage", message);
        mav.setStatus(status);
        return mav;
    }
}
