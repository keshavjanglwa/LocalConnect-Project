package LocalConnect.com.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import LocalConnect.com.Entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByLocalityOrderByCreatedAtDesc(String locality);
    List<Group> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
 
