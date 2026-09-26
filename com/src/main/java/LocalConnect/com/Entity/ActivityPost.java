package LocalConnect.com.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
     
    @NotBlank(message = "Activity name is required")
    private String activityName;

    @NotNull(message = "Date and time are required")
    private LocalDateTime activityTime;

    @Min(value = 1, message = "At least 1 partner is required")
    private int partnerCount;
    private String location;
    
    private String message;
    private String status = "OPEN";
    private String locality;
    private LocalDateTime createdAt = LocalDateTime.now();
}
