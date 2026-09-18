package LocalConnect.com.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import LocalConnect.com.Entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByGroupIdAndStatus(Long groupId, String status);

    List<GroupMember> findByUserIdAndStatus(Long userId, String status);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
}
