package LocalConnect.com.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import LocalConnect.com.Entity.Notification;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.NotificationService;
import LocalConnect.com.Service.UserService;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;

    private User currentUser(UserDetails principal) {
        return userService.getByEmailOrThrow(principal.getUsername());
    }
    
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("currentUser", currentUser(principal));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                 @RequestParam String name,
                                 @RequestParam String locality) {
        User user = currentUser(principal);
        userService.updateProfile(user.getId(), name, locality);
        return "redirect:/home";
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = currentUser(principal);
        List<Notification> notifications = notificationService.getForUser(user.getId());
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", user);
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }
}
