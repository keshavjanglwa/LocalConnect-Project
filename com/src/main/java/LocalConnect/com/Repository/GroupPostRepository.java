package LocalConnect.com.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import LocalConnect.com.Entity.GroupPost;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {
    List<GroupPost> findByGroupIdOrderByCreatedAtDesc(Long groupId);
}