package LocalConnect.com.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import LocalConnect.com.Entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByStatusOrderByCreatedAtDesc(String status);
    List<Report> findAllByOrderByCreatedAtDesc();
}
