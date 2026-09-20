package LocalConnect.com.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import LocalConnect.com.Entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
