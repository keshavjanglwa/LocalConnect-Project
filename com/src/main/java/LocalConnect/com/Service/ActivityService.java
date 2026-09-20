package LocalConnect.com.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import LocalConnect.com.Entity.ActivityPost;
import LocalConnect.com.Entity.ActivityReply;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.ActivityPostRepository;
import LocalConnect.com.Repository.ActivityReplyRepository;
import jakarta.transaction.Transactional;

@Service
public class ActivityService {
    
    @Autowired
    private ActivityPostRepository activityPostRepository;
    @Autowired
    private ActivityReplyRepository activityReplyRepository;
    @Autowired
    private NotificationService notificationService;

    public ActivityPost createPost(ActivityPost post, User owner) {
        post.setUser(owner);
        post.setLocality(owner.getLocality());
        post.setStatus("OPEN");
        return activityPostRepository.save(post);
    }
     
    public List<ActivityPost> getFeedForLocality(String locality) {
        return activityPostRepository.findByLocalityOrderByCreatedAtDesc(locality);
    }

    public ActivityPost getByIdOrThrow(Long id) {
        return activityPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity post not found"));
    }

    public List<ActivityReply> getRepliesForPost(Long postId) {
        return activityReplyRepository.findByActivityPostIdOrderByCreatedAtAsc(postId);
    }

    public void assertOwner(ActivityPost post, Long currentUserId) {
        if (!post.getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not allowed to modify this post");
        }
    }

    public void deleteRepliesForPost(Long postId) {
        activityReplyRepository.deleteByActivityPostId(postId);
    }

    public ActivityPost updateStatus(Long postId, String status, Long currentUserId) {
        ActivityPost post = getByIdOrThrow(postId);
        assertOwner(post, currentUserId);
        post.setStatus(status);
        return activityPostRepository.save(post);
    }
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        ActivityPost post = getByIdOrThrow(postId);
        assertOwner(post, currentUserId);
        deleteRepliesForPost(postId);
        activityPostRepository.delete(post);
    }
    
    public ActivityReply addReply(Long postId, User replier, String message) {
        ActivityPost post = getByIdOrThrow(postId);
        if (!"OPEN".equals(post.getStatus())) {
            throw new IllegalStateException("This activity is no longer open for replies");
        }
        ActivityReply reply = new ActivityReply();
        reply.setActivityPost(post);
        reply.setUser(replier);
        reply.setMessage(message);
        ActivityReply saved = activityReplyRepository.save(reply);
        notificationService.notify(post.getUser(),
          replier.getName() + " replied to your activity post: " + post.getActivityName());
        return saved;
    }
    public void acceptReply(Long postId, Long replyId, Long currentUserId) {
        ActivityPost post = getByIdOrThrow(postId);
        assertOwner(post, currentUserId);

        ActivityReply reply = activityReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found"));

        reply.setStatus("ACCEPTED");
        activityReplyRepository.save(reply);

        notificationService.notify(reply.getUser(),
         "Your reply was accepted for: " + post.getActivityName());
        
        long acceptedCount = activityReplyRepository.findByActivityPostIdOrderByCreatedAtAsc(postId)
                .stream().filter(r -> "ACCEPTED".equals(r.getStatus())).count();

        if (acceptedCount >= post.getPartnerCount()) {
            post.setStatus("FULL");
            activityPostRepository.save(post);
        }
    }

    public void rejectReply(Long postId, Long replyId, Long currentUserId) {
        ActivityPost post = getByIdOrThrow(postId);
        assertOwner(post, currentUserId);

        ActivityReply reply = activityReplyRepository.findById(replyId)
                .orElseThrow(() -> new IllegalArgumentException("Reply not found"));
        reply.setStatus("REJECTED");
        activityReplyRepository.save(reply);
    }   
    
    public List<ActivityPost> searchByActivityName(String locality, String activityName) {
        if (activityName == null || activityName.isBlank()) {
            return getFeedForLocality(locality);
        }
        return activityPostRepository.findByLocalityAndActivityNameContainingIgnoreCaseOrderByCreatedAtDesc(
                locality, activityName);
    }









 


}

