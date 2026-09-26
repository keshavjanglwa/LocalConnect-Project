package LocalConnect.com.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import LocalConnect.com.Entity.ActivityPost;
import LocalConnect.com.Entity.ActivityReply;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.ActivityService;
import LocalConnect.com.Service.UserService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserService userService;
    
    private User currentUser(UserDetails principal) {
        return userService.getByEmailOrThrow(principal.getUsername());
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        ActivityPost activityPost = new ActivityPost() {};
        model.addAttribute("activityPost", activityPost);
        return "activity-form"; 
    }

    @GetMapping()
    public String list(@AuthenticationPrincipal UserDetails principal,
                        @RequestParam(required = false) String activityName,
                        Model model) {
        User user = currentUser(principal);
        List<ActivityPost> posts = activityService.searchByActivityName(user.getLocality(), activityName);
        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", user);
        model.addAttribute("activityName", activityName);
        return "activity-list";
    }

    @GetMapping("/{id}")
    public String details(@AuthenticationPrincipal UserDetails principal,
                           @PathVariable Long id, Model model) {
        User user = currentUser(principal);
        ActivityPost post = activityService.getByIdOrThrow(id);
        List<ActivityReply> replies = activityService.getRepliesForPost(id);

        model.addAttribute("post", post);
        model.addAttribute("replies", replies);
        model.addAttribute("currentUser", user);
        model.addAttribute("isOwner", post.getUser().getId().equals(user.getId()));
        return "activity-details";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails principal,
                        @Valid @ModelAttribute("activityPost") ActivityPost activityPost,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "activity-form";
        }
        User user = currentUser(principal);
        activityService.createPost(activityPost, user);
        return "redirect:/activities";
    } 
    
    @PostMapping("/{id}/reply")
    public String reply(@AuthenticationPrincipal UserDetails principal,
                         @PathVariable Long id,
                         @RequestParam String message) {
        User user = currentUser(principal);
        activityService.addReply(id, user, message);
        return "redirect:/activities/" + id;
    }
        
    @PostMapping("/{id}/replies/{replyId}/accept")
    public String acceptReply(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable Long id, @PathVariable Long replyId) {
        User user = currentUser(principal);
        activityService.acceptReply(id, replyId, user.getId());
        return "redirect:/activities/" + id;
    }

    @PostMapping("/{id}/replies/{replyId}/reject")
    public String rejectReply(@AuthenticationPrincipal UserDetails principal,
                               @PathVariable Long id, @PathVariable Long replyId) {
        User user = currentUser(principal);
        activityService.rejectReply(id, replyId, user.getId());
        return "redirect:/activities/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id, @RequestParam String status) {
        User user = currentUser(principal);
        activityService.updateStatus(id, status, user.getId());
        return "redirect:/activities/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = currentUser(principal);
        activityService.deletePost(id, user.getId());
        return "redirect:/activities";
    }
}  
