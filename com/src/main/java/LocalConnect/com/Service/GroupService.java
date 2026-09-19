package LocalConnect.com.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import LocalConnect.com.Entity.Group;
import LocalConnect.com.Entity.GroupMember;
import LocalConnect.com.Entity.GroupPost;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.GroupMemberRepository;
import LocalConnect.com.Repository.GroupPostRepository;
import LocalConnect.com.Repository.GroupRepository;

@Service
public class GroupService {
    
    @Autowired
   private GroupRepository groupRepository;
   @Autowired
   private GroupMemberRepository groupMemberRepository;
   @Autowired
   private GroupPostRepository groupPostRepository;
   
    public Group createGroup(Group group, User owner) {
        group.setOwner(owner);
        Group saved = groupRepository.save(group);
        return saved;
    }

    public List<Group> getGroupsForLocality(String locality) {
        return groupRepository.findByLocalityOrderByCreatedAtDesc(locality);
    }

    public Group getByIdOrThrow(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    private void assertOwner(Group group, Long currentUserId) {
        if (!isOwner(group, currentUserId)) {
            throw new SecurityException("Only the group owner can do this");
        }
    }

    public boolean isOwner(Group group, Long userId) {
        return group.getOwner().getId().equals(userId);
    }

    public List<GroupMember> getPendingRequests(Long groupId) {
        return groupMemberRepository.findByGroupIdAndStatus(groupId, "PENDING");
    }

    public List<GroupMember> getApprovedMembers(Long groupId) {
        return groupMemberRepository.findByGroupIdAndStatus(groupId, "APPROVED");
    }

    public boolean isApprovedMember(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .map(m -> "APPROVED".equals(m.getStatus()))
                .orElse(false);
    }

    public void requestToJoin(Long groupId, User user) {
        Optional<GroupMember> existing = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId());
        if (existing.isPresent()) {
            return; // already requested/approved, nothing to do
        }
        Group group = getByIdOrThrow(groupId);
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setStatus("PENDING");
        groupMemberRepository.save(member);
    }

    public void approveMember(Long groupId, Long memberId, Long currentUserId) {
        Group group = getByIdOrThrow(groupId);
        assertOwner(group, currentUserId);

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membership request not found"));
        member.setStatus("APPROVED");
        groupMemberRepository.save(member);
    }

    public void rejectMember(Long groupId, Long memberId, Long currentUserId) {
        Group group = getByIdOrThrow(groupId);
        assertOwner(group, currentUserId);

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Membership request not found"));
        member.setStatus("REJECTED");
        groupMemberRepository.save(member);
    }

    public void leaveGroup(Long groupId, Long userId) {
        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .ifPresent(groupMemberRepository::delete);
    }

    public void removeMember(Long groupId, Long memberId, Long currentUserId) {
        Group group = getByIdOrThrow(groupId);
        assertOwner(group, currentUserId);
        groupMemberRepository.deleteById(memberId);
    }

    public void deleteGroupMembersForGroup(Long groupId) {
        groupMemberRepository.deleteByGroupId(groupId);
    }

    public void deleteGroup(Long groupId ,Long currentUserId) {
        Group group = getByIdOrThrow(groupId);
        assertOwner(group, currentUserId);
        deleteGroupMembersForGroup(groupId);
        groupRepository.delete(group);
    }
    
    public GroupPost createGroupPost(Long groupId, User author, String message) {
        Group group = getByIdOrThrow(groupId);
        GroupPost post = new GroupPost();
        post.setGroup(group);
        post.setUser(author);
        post.setMessage(message);
        return groupPostRepository.save(post);
    }

    public List<GroupPost> getPostsForGroup(Long groupId) {
        return groupPostRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    public void deleteGroupPost(Long groupId, Long postId, Long currentUserId) {
        Group group = getByIdOrThrow(groupId);
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        boolean isAuthor = post.getUser().getId().equals(currentUserId);
        boolean isOwner = isOwner(group, currentUserId);

        if (!isAuthor && !isOwner) {
            throw new SecurityException("You are not allowed to delete this post");
        }
        groupPostRepository.delete(post);
    }


    
    

    



}
