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
import LocalConnect.com.Entity.BusinessPost;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.BusinessService;
import LocalConnect.com.Service.UserService;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/business")
public class BusinessController {
    @Autowired
    private  BusinessService businessService;
    @Autowired
    private  UserService userService;

    private User currentUser(UserDetails principal) {
        return userService.getByEmailOrThrow(principal.getUsername());
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails principal,
                        @RequestParam(required = false) String category,
                        Model model) {
        User user = currentUser(principal);
        List<BusinessPost> posts = businessService.filterByCategory(user.getLocality(), category);
        model.addAttribute("posts", posts);
        model.addAttribute("category", category);
        model.addAttribute("currentUser", user);
        return "business-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("businessPost", new BusinessPost());
        return "business-form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal UserDetails principal,
                          @Valid @ModelAttribute("businessPost") BusinessPost businessPost,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "business-form";
        }
        User user = currentUser(principal);
        businessService.createPost(businessPost, user);
        return "redirect:/business";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = currentUser(principal);
        businessService.deletePost(id, user.getId());
        return "redirect:/business";
    }

}
