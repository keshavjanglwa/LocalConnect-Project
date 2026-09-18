package LocalConnect.com.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import LocalConnect.com.Entity.Group;
import LocalConnect.com.Entity.GroupMember;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.GroupMemberRepository;
import LocalConnect.com.Repository.GroupRepository;

@Service
public class GroupService {
    
    @Autowired
   private GroupRepository groupRepository;
   @Autowired
   private GroupMemberRepository groupMemberRepository;
   
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

    



}
