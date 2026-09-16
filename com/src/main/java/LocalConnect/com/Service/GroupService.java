package LocalConnect.com.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import LocalConnect.com.Entity.Group;
import LocalConnect.com.Entity.User;
import LocalConnect.com.Repository.GroupRepository;

@Service
public class GroupService {
    
    @Autowired
   private GroupRepository groupRepository;
   
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

    public boolean isOwner(Group group, Long userId) {
        return group.getOwner().getId().equals(userId);
    }



}
