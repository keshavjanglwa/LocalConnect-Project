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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import LocalConnect.com.Entity.Group;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.GroupService;
import LocalConnect.com.Service.UserService;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;

    private User currentUser(UserDetails principal) {
        return userService.getByEmailOrThrow(principal.getUsername());
    }  

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = currentUser(principal);
        List<Group> groups = groupService.getGroupsForLocality(user.getLocality());
        model.addAttribute("groups", groups);
        model.addAttribute("currentUser", user);
        return "groups";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("group", new Group());
        return "group-form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails principal,
                        @ModelAttribute("group") Group group,
                        BindingResult bindingResult) {
                            
        if (bindingResult.hasErrors()) {
            return "group-form";
        }
        User user = currentUser(principal);
        Group saved = groupService.createGroup(group, user);
        return "redirect:/groups/" + saved.getId();
    }
    
}
