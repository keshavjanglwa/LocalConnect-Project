package LocalConnect.com.Controller;

import LocalConnect.com.Repository.GroupPostRepository;
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

import LocalConnect.com.Entity.Group;
import LocalConnect.com.Entity.GroupMember;
import LocalConnect.com.Entity.GroupPost;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Service.GroupService;
import LocalConnect.com.Service.UserService;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/groups")
public class GroupController {
    
    @Autowired
    private GroupPostRepository groupPostRepository;
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

    @GetMapping("/{id}")
    public String details(@AuthenticationPrincipal UserDetails principal,
                           @PathVariable Long id, Model model) {
        User user = currentUser(principal);
        Group group = groupService.getByIdOrThrow(id);
        boolean isOwner = groupService.isOwner(group, user.getId());
        boolean isMember = groupService.isApprovedMember(id, user.getId());

        model.addAttribute("group", group);
        model.addAttribute("currentUser", user);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isMember", isMember);

        if (isMember || isOwner) {
            List<GroupPost> posts = groupService.getPostsForGroup(id);
            model.addAttribute("posts", posts);
        }

        if (isOwner) {
            List<GroupMember> pending = groupService.getPendingRequests(id);
            List<GroupMember> members = groupService.getApprovedMembers(id);
            model.addAttribute("pendingRequests", pending);
            model.addAttribute("members", members);
        }

        return "group-details";
    }
        
    @PostMapping("/{id}/join")
    public String join(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = currentUser(principal);
        groupService.requestToJoin(id, user);
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/members/{memberId}/approve")
    public String approveMember(@AuthenticationPrincipal UserDetails principal,
                                 @PathVariable Long id, @PathVariable Long memberId) {
        User user = currentUser(principal);
        groupService.approveMember(id, memberId, user.getId());
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/members/{memberId}/reject")
    public String rejectMember(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id, @PathVariable Long memberId) {
        User user = currentUser(principal);
        groupService.rejectMember(id, memberId, user.getId());
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/leave")
    public String leave(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = currentUser(principal);
        groupService.leaveGroup(id, user.getId());
        return "redirect:/groups";
    }

    @PostMapping("/{id}/members/{memberId}/remove")
    public String removeMember(@AuthenticationPrincipal UserDetails principal,
                                @PathVariable Long id, @PathVariable Long memberId) {
        User user = currentUser(principal);
        groupService.removeMember(id, memberId, user.getId());
        return "redirect:/groups/" + id;
    }
    
    @Transactional
    @PostMapping("/{id}/delete")
    public String deleteGroup(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
        User user = currentUser(principal);
        groupService.deleteGroup(id, user.getId());
        return "redirect:/groups";
    }

    @PostMapping("/{id}/posts")
    public String createPost(@AuthenticationPrincipal UserDetails principal,
                              @PathVariable Long id, @RequestParam String message) {
        User user = currentUser(principal);
        groupService.createGroupPost(id, user, message);
        return "redirect:/groups/" + id;
    }

    @PostMapping("/{id}/posts/{postId}/delete")
    public String deletePost(@AuthenticationPrincipal UserDetails principal,
                              @PathVariable Long id, @PathVariable Long postId) {
        User user = currentUser(principal);
        groupService.deleteGroupPost(id, postId, user.getId());
        return "redirect:/groups/" + id;
    }

}
